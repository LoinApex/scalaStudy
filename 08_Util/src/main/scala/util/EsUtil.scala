package util

import java.net.InetAddress
import java.util

import com.alibaba.fastjson.{JSON, JSONObject}
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.search.{SearchResponse, SearchType}
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.sort.SortOrder
import org.elasticsearch.transport.client.PreBuiltTransportClient

object EsUtil {

  private val esClusterName = YamlUtil.getNested("elasticsearch.cluster.name")
  private val esNodes = YamlUtil.getNested("elasticsearch.nodes")
  private val settings = Settings.builder.put("cluster.name", esClusterName).put("client.transport.sniff", true).build

  val transportClient = new PreBuiltTransportClient(settings)
  esNodes.split(",").map(item => {
    val hostAndPort = item.split(":")
    transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName(hostAndPort(0)), hostAndPort(1).toInt))
  })

  /**
    * 批量入库ES
    *
    * @param list
    * @param indexName
    * @param indexType
    */
  def saveToEs(list: java.util.List[JSONObject], indexName: String, indexType: String): Unit = {
    val bulkRequest = transportClient.prepareBulk
    import scala.collection.JavaConversions._
    for (jsonObject <- list) {
      bulkRequest.add(transportClient.prepareIndex(indexName, indexType, jsonObject.getString("id")).setSource(jsonObject))
    }
    var bulkResponse = None: Option[BulkResponse]
    if (bulkRequest.numberOfActions > 0) {
      bulkResponse = Some(bulkRequest.execute().actionGet())
    }
    bulkResponse.get
  }

  def isIndexExisys(index: String) = {
    val inExistsRequest = new IndicesExistsRequest(index)
    transportClient.admin.indices.exists(inExistsRequest).actionGet.isExists
  }

  def createIndex(indexName: String, shards: Int, replicas: Int): Boolean = {
    val settings = Settings.builder.put("index.number_of_shards", shards).put("index.number_of_replicas", replicas).build
    val createIndexResponse = transportClient.admin.indices.prepareCreate(indexName.toLowerCase).setSettings(settings).execute.actionGet
    val isIndexCreated = createIndexResponse.isAcknowledged
    isIndexCreated
  }

  def deleteIndex(indexName: String): Boolean = {
    val deleteResponse = transportClient.admin.indices.prepareDelete(indexName.toLowerCase).execute.actionGet
    val isIndexDeleted = deleteResponse.isAcknowledged
    isIndexDeleted
  }

  def setMapping(indexName: String, typeName: String, mapping: String): Unit = {
    transportClient.admin.indices.preparePutMapping(indexName).setType(typeName).setSource(mapping, XContentType.JSON).get
  }

  /**
    * 获取索引文档总数
    *
    * @param indexName
    * @param indexType
    * @param queryCondition
    * @return
    */
  def getIndexCount(indexName: String, indexType: String, queryCondition: QueryBuilder): Long = {
    val response = transportClient.prepareSearch(indexName).setTypes(indexType).setQuery(queryCondition).execute.actionGet
    response.getHits.getTotalHits
  }

  def clearScroll(scrollId: String): Boolean = {
    val clearScrollRequestBuilder = transportClient.prepareClearScroll
    clearScrollRequestBuilder.addScrollId(scrollId)
    val response = clearScrollRequestBuilder.get
    response.isSucceeded
  }

  /**
    * 查询单个索引全部数据
    *
    * @param indexName
    * @param typeName
    * @param queryCondition
    * @return
    */
  def searchAllData(indexName: String, typeName: String, queryCondition: QueryBuilder): util.List[JSONObject] = {
    val list: util.List[JSONObject] = new util.ArrayList[JSONObject]
    var searchResponse: SearchResponse = transportClient.prepareSearch(indexName).setTypes(typeName).setQuery(queryCondition).setSearchType(SearchType.DEFAULT).setSize(1000).setScroll(TimeValue.timeValueMinutes(8)).execute.actionGet
    if (searchResponse.getHits.getHits.length > 0) {
      for (hit <- searchResponse.getHits.getHits) {
        val dataJson: JSONObject = JSON.parseObject(hit.getSourceAsString)
        list.add(dataJson)
      }
      var i = 1
      do {
        searchResponse = transportClient.prepareSearchScroll(searchResponse.getScrollId).setScroll(TimeValue.timeValueMinutes(8)).execute.actionGet
        val hits: SearchHits = searchResponse.getHits
        i = hits.getHits.length
        for (hit <- hits.getHits) {
          val dataJson: JSONObject = JSON.parseObject(hit.getSourceAsString)
          list.add(dataJson)
        }
      } while (i != 0)
    }
    clearScroll(searchResponse.getScrollId)
    list
  }

  /**
    * 指定起始点获取文档
    *
    * @param indexName
    * @param typeName
    * @param queryCondition
    * @param from 查询起始点，值为 0 是第1条
    * @param size
    * @return
    */
  def searchData(indexName: String, typeName: String, queryCondition: QueryBuilder, from: Int, size: Int): util.List[JSONObject] = {
    val resultList = new util.ArrayList[JSONObject]
    if (from < 0 || size <= 0) return resultList
    val response = transportClient.prepareSearch(indexName).setTypes(typeName).setQuery(queryCondition).setFrom(from).setSize(size).execute.actionGet
    val hits = response.getHits
    for (hit <- hits.getHits) {
      resultList.add(JSON.parseObject(hit.getSourceAsString))
    }
    resultList
  }

  /**
    * 分页查询
    *
    * @param indexName
    * @param typeName
    * @param queryCondition
    * @param page
    * @param size
    * @param fieldOrder 排序
    * @return
    */
  def searchDataPaging(indexName: String, typeName: String, queryCondition: QueryBuilder, page: Int, size: Int, fieldOrder: Map[String, SortOrder]): util.List[JSONObject] = {
    val resultList = new util.ArrayList[JSONObject]
    if (page <= 0 || size <= 0) return resultList
    val searchRequestBuilder = transportClient.prepareSearch(indexName).setTypes(typeName).setQuery(queryCondition).setFrom((page - 1) * size).setSize(size)
    fieldOrder.foreach(item => {
      searchRequestBuilder.addSort(item._1, item._2)
    })
    val searchResponse = searchRequestBuilder.execute.actionGet
    val hits = searchResponse.getHits
    for (hit <- hits.getHits) {
      resultList.add(JSON.parseObject(hit.getSourceAsString))
    }
    resultList
  }

  def updateIndexData(indexName: String, typeName: String, jsonData: JSONObject, id: String): UpdateResponse = {
    transportClient.prepareUpdate(indexName, typeName, id).setDoc(jsonData).get
  }
}
