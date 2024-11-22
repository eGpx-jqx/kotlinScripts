#!usr/bin/env kotlin

@file:DependsOn("com.alibaba:easyexcel:4.0.3")
@file:DependsOn("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.annotation.ExcelIgnore
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.annotation.write.style.ColumnWidth
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.converters.Converter
import com.alibaba.excel.event.AnalysisEventListener
import com.alibaba.excel.metadata.GlobalConfiguration
import com.alibaba.excel.metadata.data.DataFormatData
import com.alibaba.excel.metadata.data.WriteCellData
import com.alibaba.excel.metadata.property.ExcelContentProperty
import com.alibaba.excel.read.listener.PageReadListener
import com.alibaba.excel.write.metadata.style.WriteCellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment


class SConverter : Converter<Any?> {
    override fun convertToExcelData(
        value: Any?,
        contentProperty: ExcelContentProperty?,
        globalConfiguration: GlobalConfiguration?
    ): WriteCellData<*> {
        val rs = WriteCellData<String>(value?.toString())
        rs.writeCellStyle = WriteCellStyle().also {
            it.quotePrefix = false
            it.horizontalAlignment = HorizontalAlignment.CENTER
            it.dataFormatData = DataFormatData().also { p ->
                p.index = 49
            }
        }
        return rs
    }
}


class UserExcelListener : AnalysisEventListener<SourceData>() {
    val userList = mutableListOf<SourceData>()


    override fun invoke(p0: SourceData?, p1: AnalysisContext?) {
        if (p0 != null)
            userList.add(p0)
    }

    override fun doAfterAllAnalysed(p0: AnalysisContext?) {
        println("All data is analysed.")
    }
}

fun readExcel(fileName: String): MutableList<SourceData> {
//    val fileName: String = "D:\\Users\\jinqiangxu\\Downloads\\马德里.xlsx"
    val trs = mutableListOf<SourceData>()
    EasyExcel.read(fileName, SourceData::class.java, PageReadListener<SourceData>(trs::addAll, 1000)).sheet().doRead()
    trs.forEach {
        if (it.cityIds != null) {
            it.cityIds = it.cityIds!!.split(",").filter { it.isNotEmpty() && it.toInt() > 0 }.sorted().distinct()
                .joinToString(",")
        }
        if (it.poids != null) {
            it.poids =
                it.poids!!.split(",").filter { it.isNotEmpty() && it.toInt() > 0 }.sorted().distinct().joinToString(",")
        }
        if (it.cityNames != null) {
            it.cityNames = it.cityNames!!.split(",").filter { it.isNotEmpty() }.sorted().distinct().joinToString(",")
        }
    }
    return trs
}

fun handleExcel(type: Int, sources: MutableList<SourceData>): MutableList<TargetData> {
    val rs = mutableListOf<TargetData>()
    sources.forEach {
        val targetData = TargetData(
            ProductID = it.ProductID ?: "",
            ProductType = it.ProductType ?: "",
            TravelDays = it.TravelDays ?: "",
            ProductName = it.ProductName ?: "",
            TourLineID = it.TourLineID ?: "",
            TourLineName = it.TourLineName ?: "",
            cityIds = it.cityIds ?: "",
            cityNames = it.cityNames ?: "",
            poids = it.poids ?: "",
        )
        rs.add(targetData)
    }

    var groupBy: Map<String, List<TargetData>>
    if (type == 1) {
        groupBy = rs.groupBy { it.ProductType + it.TourLineID + it.TravelDays + it.cityIds }
    } else {
        groupBy = rs.groupBy { it.ProductType + it.TourLineID + it.TravelDays + it.cityIds + it.poids }
    }

    for ((index, data) in groupBy.values.withIndex()) {
        data.forEach {
            it.CommonID = (index + 1)
        }
    }
    rs.sortBy { it.CommonID }
    return rs
}


fun main() {
    val mdl = readExcel("D:\\Users\\jinqiangxu\\Downloads\\马德里.xlsx")
    val bhd = readExcel("D:\\Users\\jinqiangxu\\Downloads\\北海道.xlsx")
    val gl = readExcel("D:\\Users\\jinqiangxu\\Downloads\\桂林.xlsx")

    println("马德里: ${mdl.size}, 北海道: ${bhd.size}, 桂林: ${gl.size}")

    val cityMdl = handleExcel(1, mdl).let { it.sortedBy { it.CommonID } }
    val poiMdl = handleExcel(2, mdl).let { it.sortedBy { it.CommonID } }

    val cityBhd = handleExcel(1, bhd).let { it.sortedBy { it.CommonID } }
    val poiBhd = handleExcel(2, bhd).let { it.sortedBy { it.CommonID } }

    val cityGl = handleExcel(1, gl).let { it.sortedBy { it.CommonID } }
    val poiGl = handleExcel(2, gl).let { it.sortedBy { it.CommonID } }

//    println("马德里城市: ${cityMdl.size}, 马德里景点: ${poiMdl.size}, 北海道城市: ${cityBhd.size}, 北海道景点: ${poiBhd.size}, 桂林城市: ${cityGl.size}, 桂林景点: ${poiGl.size}")
//    println("马德里: ${cityMdl.joinToString(",", transform = { it.CommonID.toString() })}")
//    println("北海道: ${cityBhd.joinToString(",", transform = { it.CommonID.toString() })}")
//    println("桂林: ${cityGl.joinToString(",", transform = { it.CommonID.toString() })}")


//    var fileName = "D:\\Users\\jinqiangxu\\Downloads\\产品聚合带景点.xlsx"
//    var excelWriter = EasyExcel.write(fileName, TargetData::class.java).build()
//    excelWriter.write(cityMdl, EasyExcel.writerSheet("马德里城市聚合").build())
//    excelWriter.write(poiMdl, EasyExcel.writerSheet("马德里景点聚合").build())
//    excelWriter.write(cityBhd, EasyExcel.writerSheet("北海道城市聚合").build())
//    excelWriter.write(poiBhd, EasyExcel.writerSheet("北海道景点聚合").build())
//    excelWriter.write(cityGl, EasyExcel.writerSheet("桂林城市聚合").build())
//    excelWriter.write(poiGl, EasyExcel.writerSheet("桂林景点聚合").build())
//    excelWriter.close()
}

main()












data class TargetData(
    @ExcelProperty("合并ID")
    @ColumnWidth(15)
    var CommonID: Int? = null,
    @ExcelProperty("产品ID")
    @ColumnWidth(15)
    var ProductID: String,
//    @ExcelProperty("ProductType")
    @ExcelIgnore
    var ProductType: String,
//    @ExcelProperty("TravelDays")
    @ExcelIgnore
    var TravelDays: String,
    @ExcelProperty("产品名称")
    @ColumnWidth(80)
    var ProductName: String,
//    @ExcelProperty("TourLineName")
    @ExcelIgnore
    var TourLineID: String,
    @ExcelProperty("线路")
    @ColumnWidth(20)
    var TourLineName: String,
//    @ExcelProperty("cityIds")
    @ExcelIgnore
    var cityIds: String,
    @ExcelProperty("途径城市")
    @ColumnWidth(80)
    var cityNames: String,
    @ExcelProperty("景点id")
    @ColumnWidth(80)
//    @ExcelIgnore
    var poids: String? = null,
)


class SourceData {
    @ExcelProperty("ProductID")
    var ProductID: String? = null

    @ExcelProperty("ProductType")
    var ProductType: String? = null

    @ExcelProperty("TravelDays")
    var TravelDays: String? = null

    @ExcelProperty("ProductName")
    var ProductName: String? = null

    @ExcelProperty("TourLineID")
    var TourLineID: String? = null

    @ExcelProperty("TourLineName")
    var TourLineName: String? = null

    @ExcelProperty("cityIds")
    var cityIds: String? = null

    @ExcelProperty("cityNames")
    var cityNames: String? = null

    @ExcelProperty("poids")
    var poids: String? = null

    // 无参构造器
    constructor()

    // 全参构造器
    constructor(
        ProductID: String?,
        ProductType: String?,
        TravelDays: String?,
        ProductName: String?,
        TourLineID: String?,
        TourLineName: String?,
        cityIds: String?,
        cityNames: String?,
        poids: String?
    ) {
        this.ProductID = ProductID
        this.ProductType = ProductType
        this.TravelDays = TravelDays
        this.ProductName = ProductName
        this.TourLineID = TourLineID
        this.TourLineName = TourLineName
        this.cityIds = cityIds
        this.cityNames = cityNames
        this.poids = poids
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SourceData) return false

        if (ProductID != other.ProductID) return false
        if (ProductType != other.ProductType) return false
        if (TravelDays != other.TravelDays) return false
        if (ProductName != other.ProductName) return false
        if (TourLineID != other.TourLineID) return false
        if (TourLineName != other.TourLineName) return false
        if (cityIds != other.cityIds) return false
        if (cityNames != other.cityNames) return false
        if (poids != other.poids) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ProductID?.hashCode() ?: 0
        result = 31 * result + (TravelDays?.hashCode() ?: 0)
        result = 31 * result + (ProductType?.hashCode() ?: 0)
        result = 31 * result + (ProductName?.hashCode() ?: 0)
        result = 31 * result + (TourLineID?.hashCode() ?: 0)
        result = 31 * result + (TourLineName?.hashCode() ?: 0)
        result = 31 * result + (cityIds?.hashCode() ?: 0)
        result = 31 * result + (cityNames?.hashCode() ?: 0)
        result = 31 * result + (poids?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "SourceData(ProductID=$ProductID,ProductType=$ProductType,, TravelDays=$TravelDays, ProductName=$ProductName,TourLineID=$TourLineID, TourLineName=$TourLineName, cityIds=$cityIds, cityNames=$cityNames, poids=$poids)"
    }
}