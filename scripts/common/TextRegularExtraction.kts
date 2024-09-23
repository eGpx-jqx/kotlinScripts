#!/usr/bin/env kotlin

import java.io.File


fun extractData(): Set<String> {
    val mutableListOf = mutableSetOf<String>()
    val fileName = "../resource/textRegularExtraction.txt"
    val file = File(fileName)
    val readText = file.readText(Charsets.UTF_8)
    val regex = Regex("saleRoomId:(\\d+)")
    regex.findAll(readText).forEach { match ->
        mutableListOf.add(match.destructured.component1())
    }
    return mutableListOf
}

// 引入它的其他脚本不会执行这个
extractData().joinToString(", ")

