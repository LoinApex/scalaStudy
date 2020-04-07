package util

import java.io.{File, FileInputStream, InputStream}

import javax.xml.parsers.ParserConfigurationException
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener
import org.apache.poi.hssf.eventusermodel._
import org.apache.poi.hssf.eventusermodel.dummyrecord.{LastCellOfRowDummyRecord, MissingCellDummyRecord}
import org.apache.poi.hssf.model.HSSFFormulaParser
import org.apache.poi.hssf.record._
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ooxml.util.SAXHelper
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.util.{CellAddress, CellReference}
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler
import org.apache.poi.xssf.eventusermodel.{ReadOnlySharedStringsTable, XSSFReader, XSSFSheetXMLHandler}
import org.apache.poi.xssf.model.StylesTable
import org.apache.poi.xssf.usermodel.XSSFComment
import org.xml.sax.{InputSource, XMLReader}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object PoiUtil {

  def main(args: Array[String]): Unit = {
    val filePath: String = "/Users/xiaofengfu/Desktop/product_compare汉子.xlsx"
    val excelPath: String = "/Users/xiaofengfu/Desktop/product_compare汉子.xls"
    println(parseExcel(excelPath))
  }

  def parseExcel(filePath: String): Map[String, List[mutable.LinkedHashMap[String, String]]] = {

    try {
      if (filePath.endsWith("xls")) {
        parse2003ExceltoMap(filePath)
      } else {
        parse2007ExceltoMap(filePath)
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
        null
      }
    }
  }

  def parse2003ExceltoMap(filePath: String): Map[String, List[mutable.LinkedHashMap[String, String]]] = {
    val myHSSFListener = new MyHSSFListener(filePath)
    myHSSFListener.processExcel()
    myHSSFListener.result
  }


  def parse2007ExceltoMap(excelPath: String): Map[String, List[mutable.LinkedHashMap[String, String]]] = {
    var result: Map[String, List[mutable.LinkedHashMap[String, String]]] = Map()
    try {
      val pkg: OPCPackage = OPCPackage.open(new File(excelPath))
      val strings: ReadOnlySharedStringsTable = new ReadOnlySharedStringsTable(pkg)
      val reader = new XSSFReader(pkg)
      val stylesTable: StylesTable = reader.getStylesTable
      val sheetsData: XSSFReader.SheetIterator = reader.getSheetsData.asInstanceOf[XSSFReader.SheetIterator]
      while (sheetsData.hasNext) {
        val sheetContentsHandler = new MySheetContentsHandler()
        val sheet: InputStream = sheetsData.next()
        val sheetSource: InputSource = new InputSource(sheet)
        try {
          val sheetParser: XMLReader = SAXHelper.newXMLReader
          val handler = new XSSFSheetXMLHandler(stylesTable, strings, sheetContentsHandler, new DataFormatter, false)
          sheetParser.setContentHandler(handler)
          sheetParser.parse(sheetSource)
        } catch {
          case e: ParserConfigurationException =>
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage)
        }
        result += (sheetsData.getSheetName -> sheetContentsHandler.result.toList)
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
    result
  }


  private class MySheetContentsHandler() extends SheetContentsHandler {
    var result = ListBuffer[mutable.LinkedHashMap[String, String]]()
    private var tempResult: mutable.LinkedHashMap[String, String] = _
    private var head: List[String] = _
    private var currentRow = -1
    private var currentCol = -1

    override def startRow(rowNum: Int): Unit = {
      tempResult = mutable.LinkedHashMap[String, String]()
      currentRow = rowNum
      currentCol = -1
      //默认第一行为表头，表头数据一定不能为空，否则会IndexOutOfBoundsException
      if (0 == rowNum) {
        head = List[String]()
      }
    }

    override def endRow(rowNum: Int): Unit = {
      if (1 <= rowNum) {
        //处理空尾行数据
        if (head.size != tempResult.size) {
          for (i <- tempResult.size to head.size - 1) {
            tempResult += (head(i) -> "")
          }
        }
        result += tempResult
      }
    }

    override def cell(cellReference: String, formattedValue: String, comment: XSSFComment): Unit = {
      var cellRefer: String = cellReference
      if (null == cellReference) {
        cellRefer = new CellAddress(currentRow, currentCol).formatAsString
      }
      val thisCol: Short = new CellReference(cellRefer).getCol

      // 处理空列数据，进行列对齐，空列数据处理为 ""
      val missedCols = thisCol - currentCol - 1
      var i = 0
      while (i < missedCols) {
        i += 1;
        //tempResult = tempResult :+ ""
        if (0 == currentRow) {
          head = head :+ ""
        } else {
          tempResult += (head(currentCol + i) -> "")
        }
      }
      if (0 == currentRow) {
        head = head :+ formattedValue
      } else {
        tempResult += (head(thisCol) -> formattedValue)
      }

      //tempResult = tempResult :+ formattedValue
      currentCol = thisCol
    }
  }


  private class MyHSSFListener(filePath: String) extends HSSFListener {

    var result: Map[String, List[mutable.LinkedHashMap[String, String]]] = Map[String, List[mutable.LinkedHashMap[String, String]]]()

    private var sstRecord: SSTRecord = _
    private var formatListener: FormatTrackingHSSFListener = _

    private var workbookBuildingListener: SheetRecordCollectingListener = _
    private var stuWorkbook: HSSFWorkbook = _

    private var nextRow: Int = _
    private var nextColumn: Int = _
    private var outputNextStringRecord: Boolean = _

    private var lastRowNumber: Int = _
    private var lastColumnNumber: Int = _
    private val outputFormulaValues: Boolean = true

    private var sheetIndex: Int = -1
    private var orderedBSRs: Array[BoundSheetRecord] = _
    private var boundSheetRecords: List[BoundSheetRecord] = List[BoundSheetRecord]()

    private var head: List[String] = List[String]()
    private var recordMap: mutable.LinkedHashMap[String, String] = _
    private var sheetResult: ListBuffer[mutable.LinkedHashMap[String, String]] = _

    private var totalColumn: Int = _
    private var totalRow: Int = _
    private var sheetName: String = _

    override def processRecord(record: Record): Unit = {
      var thisRow: Int = -1
      var thisColumn: Int = -1
      var thisStr: String = ""

      record.getSid match {
        case BoundSheetRecord.sid => {
          val boundSheetRecord: BoundSheetRecord = record.asInstanceOf[BoundSheetRecord]
          boundSheetRecords = boundSheetRecords :+ boundSheetRecord
        }
        case BOFRecord.sid => {
          // handle sheet
          val br = record.asInstanceOf[BOFRecord]
          if (BOFRecord.TYPE_WORKSHEET.equals(br.getType)) {
            if (null != workbookBuildingListener && null == stuWorkbook) {
              stuWorkbook = workbookBuildingListener.getStubHSSFWorkbook
            }
            sheetIndex += 1
            if (null == orderedBSRs) {
              orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords.asJava)
            }
            sheetName = orderedBSRs(sheetIndex).getSheetname
            sheetResult = ListBuffer[mutable.LinkedHashMap[String, String]]()
            head = List[String]()
          }
        }
        case SSTRecord.sid => {
          sstRecord = record.asInstanceOf[SSTRecord]
        }
        case BlankRecord.sid => {
          val brec = record.asInstanceOf[BlankRecord]
          thisRow = brec.getRow
          thisColumn = brec.getColumn
          thisStr = "blankRecord"
        }
        case BoolErrRecord.sid => {
          val berec = record.asInstanceOf[BoolErrRecord]
          thisRow = berec.getRow
          thisColumn = berec.getColumn
          thisStr = "BoolErrRecord"
        }
        case FormulaRecord.sid => {
          val frec = record.asInstanceOf[FormulaRecord]
          thisRow = frec.getRow
          thisColumn = frec.getColumn
          if (outputFormulaValues) {
            if (frec.getValue.isNaN) {
              outputNextStringRecord = true
              nextRow = frec.getRow
              nextColumn = frec.getColumn
            } else {
              thisStr = formatListener.formatNumberDateCell(frec)
            }
          } else {
            thisStr = HSSFFormulaParser.toFormulaString(stuWorkbook, frec.getParsedExpression)
          }
        }
        case StringRecord.sid => {
          if (outputNextStringRecord) {
            val srec = record.asInstanceOf[StringRecord]
            thisStr = srec.getString
            thisRow = nextRow
            thisColumn = nextColumn
            outputNextStringRecord = false
          }

        }
        case LabelRecord.sid => {
          val lrec = record.asInstanceOf[LabelRecord]
          thisRow = lrec.getRow
          thisColumn = lrec.getColumn
          thisStr = lrec.getValue
        }
        case LabelSSTRecord.sid => {
          val lsrec = record.asInstanceOf[LabelSSTRecord]
          thisRow = lsrec.getRow
          thisColumn = lsrec.getColumn
          if (null == sstRecord) {
            thisStr = "noLabelSSTRecord"
          } else {
            thisStr = sstRecord.getString(lsrec.getSSTIndex).toString
          }
        }
        case NoteRecord.sid => {
          val nrec = record.asInstanceOf[NoteRecord]
          thisRow = nrec.getRow
          thisColumn = nrec.getColumn
          thisStr = "NoteRecord"
        }
        case NumberRecord.sid => {
          val numrec = record.asInstanceOf[NumberRecord]
          thisRow = numrec.getRow
          thisColumn = numrec.getColumn
          thisStr = formatListener.formatNumberDateCell(numrec)
        }
        case RKRecord.sid => {
          val rkrec = record.asInstanceOf[RKRecord]
          thisRow = rkrec.getRow
          thisColumn = rkrec.getColumn
          thisStr = "RKRecord"
        }
        case ColumnInfoRecord.sid => {
          record.asInstanceOf[ColumnInfoRecord]
        }
        case RowRecord.sid => {
          val rowRecord = record.asInstanceOf[RowRecord]
          totalColumn = rowRecord.getLastCol
          totalRow = rowRecord.getRowNumber
        }
        case _ => {
        }
      }
      if (-1 != thisRow && thisRow != lastRowNumber) {
        lastColumnNumber = -1
      }
      // handle missing column
      if (record.isInstanceOf[MissingCellDummyRecord]) {
        val mc = record.asInstanceOf[MissingCellDummyRecord]
        thisRow = mc.getRow
        thisColumn = mc.getColumn
        thisStr = ""
      }
      // If we got something to print out, do so// If we got something to print out, do so
      if (thisStr != null) {
        //if (thisColumn > 0) print(',')
        //print(thisStr)
        if (0 == thisRow) {
          head = head :+ thisStr
        } else if (thisRow >= 1 && null != head && !head.isEmpty) {
          recordMap += (head(thisColumn) -> thisStr)
        }
      }

      // Update column and row count
      if (thisRow > -1) lastRowNumber = thisRow
      if (thisColumn > -1) lastColumnNumber = thisColumn

      // Handle end of row
      if (record.isInstanceOf[LastCellOfRowDummyRecord]) { // Print out any missing commas if needed
        val lastCellOfRowDummyRecord = record.asInstanceOf[LastCellOfRowDummyRecord]
        thisRow = lastCellOfRowDummyRecord.getRow
        if (-1 == lastColumnNumber) {
          lastColumnNumber = 0
        }
        for (i <- lastColumnNumber + 1 to totalColumn - 1) {
          if (0 == thisRow) {
            head = head :+ ""
          } else if (thisRow >= 1 && null != head && !head.isEmpty) {
            recordMap += (head(i) -> "")
          }
          //print(",")
        }
        lastColumnNumber = -1
        if (null != recordMap && !recordMap.isEmpty) {
          sheetResult += recordMap
        }
        recordMap = mutable.LinkedHashMap[String, String]()
        if (totalRow == thisRow) {
          result += (sheetName -> sheetResult.toList)
        }
      }
    }

    def processExcel(): Unit = {
      val excelListener = new MissingRecordAwareHSSFListener(this)
      formatListener = new FormatTrackingHSSFListener(excelListener)
      val eventFactory = new HSSFEventFactory
      val request = new HSSFRequest
      if (outputFormulaValues) {
        request.addListenerForAllRecords(formatListener)
      } else {
        workbookBuildingListener = new SheetRecordCollectingListener(formatListener)
        request.addListenerForAllRecords(workbookBuildingListener)
      }
      eventFactory.processWorkbookEvents(request, new POIFSFileSystem(new FileInputStream(filePath)))
    }

  }

}
