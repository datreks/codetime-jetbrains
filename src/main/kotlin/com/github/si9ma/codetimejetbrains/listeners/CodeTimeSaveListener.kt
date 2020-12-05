package com.github.si9ma.codetimejetbrains.listeners

import com.github.si9ma.codetimejetbrains.Queue
import com.github.si9ma.codetimejetbrains.Util
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener

class CodeTimeSaveListener : FileDocumentManagerListener {
    override fun beforeDocumentSaving(document: Document) {
        val instance = FileDocumentManager.getInstance()
        val file = instance.getFile(document)
        val log: MutableMap<String, Any> = HashMap()
        log["absoluteFile"] = file?.path ?: ""
        log["language"] = file?.let { Util.getLanguage(it) } ?: ""
        log["eventType"] = "beforeDocumentSaving"
        log["eventTime"] = System.currentTimeMillis()
        Queue.logQueue.add(log)
    }
}
