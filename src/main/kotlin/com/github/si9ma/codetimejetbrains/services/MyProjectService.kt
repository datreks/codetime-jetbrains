package com.github.si9ma.codetimejetbrains.services

import com.intellij.openapi.project.Project
import com.github.si9ma.codetimejetbrains.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
