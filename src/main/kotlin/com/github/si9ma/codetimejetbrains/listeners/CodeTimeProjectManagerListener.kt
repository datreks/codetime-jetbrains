package com.github.si9ma.codetimejetbrains.listeners

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.si9ma.codetimejetbrains.Queue
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import java.util.*
import kotlin.concurrent.timerTask


internal class CodeTimeProjectManagerListener : ProjectManagerListener {
    private val log: Logger = Logger.getInstance("CodeTime")

    override fun projectOpened(project: Project) {
        EditorFactory.getInstance().eventMulticaster.addVisibleAreaListener(CodeTimeVisibleAreaListener())

        Timer().scheduleAtFixedRate(timerTask {
            if (Queue.logQueue.size > 0) {
                while (true) {
                    val h: MutableMap<String, String> = Queue.logQueue.poll() ?: break
                    h["project"] = project.name
                    Fuel.post("https://9fdc56e3a910ba3e4430351c03d9494a.m.pipedream.net").jsonBody(Klaxon().toJsonString(h)).response { result -> println(result.get()) }
                    log.info(h.toString())
                    println(h.toString())
//                    sendHeartbeat(heartbeat, extraHeartbeats)
                }
            }
        }, 100, 100)
    }
}
