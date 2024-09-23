#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC.2")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0-RC.2")

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


fun main() {
    println("main: ++++ Thread here is ${Thread.currentThread().name}")
    GlobalScope.launch {
        println("onCreate: ++++ Dispatchers.Main Thread is ${Thread.currentThread().name}")
//    withContext(Dispatchers.IO) {
//        println("onCreate: ++++ Dispatchers.IO Thread is ${Thread.currentThread().name}")
////        TimeUnit.SECONDS.sleep(5)
//    }
//        println("onCreate: ++++ Thread here is ${Thread.currentThread().name}")
        TimeUnit.SECONDS.sleep(5)
    }
}

main()