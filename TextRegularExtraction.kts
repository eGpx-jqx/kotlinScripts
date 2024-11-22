#!/usr/env/bin kotlin

import java.io.File


fun extractData(): MutableMap<String, Set<String>> {
    val mutableMapOf = mutableMapOf<String, Set<String>>()
    val fileName = "../resource/textRegularExtraction.txt"
    val file = File(fileName)
    val readText = file.readText(Charsets.UTF_8)
    val regex = Regex(
        """saleRoomId:(\d+).*?date: (.*?)errs""",
        RegexOption.DOT_MATCHES_ALL
    )
    regex.findAll(readText).forEach { match ->
        val saleRoomId = match.groupValues[1]
        val date = match.groupValues[2]
        mutableMapOf.containsKey(saleRoomId).let {
            mutableMapOf[saleRoomId] = mutableMapOf[saleRoomId]?.plus(date) ?: setOf(date)
        }

    }
    return mutableMapOf
}

// 引入它的其他脚本不会执行这个
extractData().entries.forEach { it -> println(it.key);it.value.forEach { println("    $it") } }