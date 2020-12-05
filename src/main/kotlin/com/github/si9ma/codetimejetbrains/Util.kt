package com.github.si9ma.codetimejetbrains

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile

object Util {
    fun getLanguage(file: VirtualFile): String {
        val type: FileType = file.fileType
        return type.name
    }
}
