package com.github.si9ma.codetimejetbrains.listeners

import com.github.si9ma.codetimejetbrains.Queue
import com.github.si9ma.codetimejetbrains.Util
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.fileEditor.FileDocumentManager


class CodeTimeEditorMouseListener : EditorMouseListener {
    override fun mousePressed(event: EditorMouseEvent) {
        val instance = FileDocumentManager.getInstance()
        val file = instance.getFile(event.editor.document)
        val log: MutableMap<String, String> = HashMap()
        log["file"] = file?.path ?: ""
        log["language"] = file?.let { Util.getLanguage(it) } ?: ""
        log["type"] = "mousePressed"
        Queue.logQueue.add(log)
    }
}