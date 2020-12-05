package com.github.si9ma.codetimejetbrains.listeners

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.si9ma.codetimejetbrains.Queue
import com.google.common.primitives.UnsignedInts.toLong
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.project.guessProjectDir
import com.intellij.util.PlatformUtils
import java.util.Timer
import kotlin.concurrent.timerTask

const val TIMER_DELAY = 100
const val TIMER_PERIOD = 100

internal class CodeTimeProjectManagerListener : ProjectManagerListener {
    private val log: Logger = Logger.getInstance("CodeTime")

    override fun projectOpened(project: Project) {
        EditorFactory.getInstance().eventMulticaster.addVisibleAreaListener(CodeTimeVisibleAreaListener())

        Timer().scheduleAtFixedRate(
            timerTask {
                if (Queue.logQueue.size > 0) {
                    while (true) {
                        val h: MutableMap<String, Any> = Queue.logQueue.poll() ?: break
                        val projectPath: String? = project.guessProjectDir()?.path
                        val absoluteFile = h["absoluteFile"]
                        h["userID"] = 1
                        h["project"] = project.name
                        h["platform"] = System.getProperty("os.name") + " " + System.getProperty("os.version") +
                            " " + System.getProperty("os.arch")
                        h["editor"] = PlatformUtils.getPlatformPrefix() +
                            " " + ApplicationInfo.getInstance().fullVersion
                        h["relativeFile"] = projectPath?.let { absoluteFile.toString().removePrefix(it) } ?: ""
                        Fuel.post("http://codetime.si9ma.com:5000/eventLog")
                            .jsonBody(Klaxon().toJsonString(h)).response { result -> println(result.get()) }
                        log.info(h.toString())
                        println(h.toString())
                    }
                }
            },
            toLong(TIMER_DELAY),
            toLong(TIMER_PERIOD)
        )
    }
}
