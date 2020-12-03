package com.github.si9ma.codetimejetbrains.listeners

import com.github.si9ma.codetimejetbrains.Queue
import com.github.si9ma.codetimejetbrains.Util
import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener
import com.intellij.openapi.fileEditor.FileDocumentManager


class CodeTimeVisibleAreaListener : VisibleAreaListener {
    override fun visibleAreaChanged(e: VisibleAreaEvent) {
        val instance = FileDocumentManager.getInstance()
        val file = instance.getFile(e.editor.document)
        val log: MutableMap<String, String> = HashMap()
        log["file"] = file?.path ?: ""
        log["language"] = file?.let { Util.getLanguage(it) } ?: ""
        log["type"] = "visibleAreaChanged"
        Queue.logQueue.add(log)
    }
}