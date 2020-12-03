package com.github.si9ma.codetimejetbrains.listeners

import com.github.si9ma.codetimejetbrains.Queue
import com.github.si9ma.codetimejetbrains.Util
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager


class CodeTimeDocumentListener : DocumentListener {
    override fun documentChanged(event: DocumentEvent) {
        val document: Document = event.document
        val instance = FileDocumentManager.getInstance()
        val file = instance.getFile(document)
        val log: MutableMap<String, String> = HashMap()
        log["file"] = file?.path ?: ""
        log["language"] = file?.let { Util.getLanguage(it) } ?: ""
        log["type"] = "documentChanged"
        Queue.logQueue.add(log)
    }
}