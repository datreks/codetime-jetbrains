package com.github.si9ma.codetimejetbrains

import java.util.concurrent.ConcurrentLinkedQueue

object Queue {
    val logQueue: ConcurrentLinkedQueue<MutableMap<String, Any>> = ConcurrentLinkedQueue()
}
