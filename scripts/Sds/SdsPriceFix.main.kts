#!/usr/bin/env kotlin

@file:DependsOn("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
@file:DependsOn("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

val client = OkHttpClient()
val mapper = jacksonObjectMapper()
mapper.apply {
    // 序列化时忽略 null 值
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    // 反序列化时忽略未知属性
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}
val fixPriceUrl = "http://"


fun httpGet(url: String): Any? {
    val request = Request.Builder()
        .url(url)
        .build()
    println("${LocalDateTime.now()} url: $url")
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            println("error: ${response.code} ${response.message}")
        } else {
            println("success: ${response.body.string()}")
        }
    }
    return null
}


fun extractData(): Set<String> {
    val mutableListOf = mutableSetOf<String>()
    val fileName = "../resource/errorAlert.txt"
    val file = File(fileName)
    val readText = file.readText(Charsets.UTF_8)
    val regex = Regex("saleRoomId:(\\d+)")
    regex.findAll(readText).forEach { match ->
        mutableListOf.add(match.destructured.component1())
    }
    return mutableListOf
}

fun main() {
    val saleRoomids = this.extractData()
    println("saleRoomids: $saleRoomids, ${saleRoomids.size}")
    saleRoomids.withIndex().forEach { p ->
        p.run {
            val url =
                "${fixPriceUrl}${p.value}"
            httpGet(url)
            if (p.index < saleRoomids.size - 1) {
                TimeUnit.SECONDS.sleep(80)
            }
        }
    }
}


// 以下无效代码
data class User(val name: String?, val age: Int)
val user = User(null, 30)
val jsonString: String? = mapper.writeValueAsString(user)
println("Serialized JSON: $jsonString")
