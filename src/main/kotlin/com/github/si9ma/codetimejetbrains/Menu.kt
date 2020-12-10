package com.github.si9ma.codetimejetbrains

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class Menu : AnAction("CodeTime Configuration") {
    override fun actionPerformed(e: AnActionEvent) {
        val configWindow = e.project?.let { ConfigWindow(it) }
        configWindow?.show()
    }
}
