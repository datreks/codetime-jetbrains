package com.github.si9ma.codetimejetbrains.listeners

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.json.responseJson
import com.github.si9ma.codetimejetbrains.ConfigWindow
import com.github.si9ma.codetimejetbrains.PluginStateComponent
import com.github.si9ma.codetimejetbrains.Queue
import com.google.common.primitives.UnsignedInts.toLong
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.ui.Messages
import java.util.Timer
import java.util.UUID
import kotlin.concurrent.timerTask

const val TIMER_DELAY = 100
const val TIMER_PERIOD = 30 * 1000
const val PLUGIN_NAME = "codetime-jetbrains"
const val PLUGIN_VERSION = "0.0.8"

class CodeTimeProjectManagerListener : ProjectManagerListener {
    private val log: Logger = Logger.getInstance("CodeTime")

    override fun projectOpened(project: Project) {
        val uuid = UUID.randomUUID().toString()

        // no token, popup a configuration window
        if (PluginStateComponent.instance.state.token == "") {
            ConfigWindow(project).show()
        }
        if (PluginStateComponent.instance.state.debug) {
            val msg = "Running CodeTime in DEBUG mode. Your IDE may be slow when saving or editing files."
            Messages.showWarningDialog(msg, "Debug")
        }

        Timer().scheduleAtFixedRate(
            timerTask {
                if (Queue.logQueue.size > 0) {
                    val events: MutableList<MutableMap<String, Any>> = ArrayList()
                    val total = Queue.logQueue.size
                    var idx = 0
                    while (idx < total) {
                        val event = Queue.logQueue.poll()
                        events.add(event)
                        idx++
                    }
                    submitEventLog(project, uuid, events)
                }
            },
            toLong(TIMER_DELAY),
            toLong(TIMER_PERIOD)
        )
    }

    init {
        val version = PluginManagerCore.getPlugin(PluginId.getId("com.github.si9ma.codetimejetbrains"))?.version
        log.info("Initializing CodeTime plugin:$version (https://codetime.datreks.com/)")
        EditorFactory.getInstance().eventMulticaster.addVisibleAreaListener(
            CodeTimeVisibleAreaListener(),
            PluginStateComponent.instance
        )
    }

    private fun submitEventLog(project: Project, uuid: String, events: MutableList<MutableMap<String, Any>>) {
        val projectPath: String? = project.guessProjectDir()?.path
        for (event in events) {
            val absoluteFile = event["absoluteFile"]
            event["project"] = project.name
            event["platform"] = System.getProperty("os.name")
            event["platformVersion"] = System.getProperty("os.version")
            event["platformArch"] = System.getProperty("os.arch")
            event["editor"] = ApplicationNamesInfo.getInstance().fullProductName
            event["editorVersion"] = ApplicationInfo.getInstance().fullVersion
            event["sessionID"] = uuid
            event["plugin"] = PLUGIN_NAME
            event["pluginVersion"] = PLUGIN_VERSION
            event["relativeFile"] = projectPath?.let { absoluteFile.toString().removePrefix(it) } ?: ""
        }
        Fuel.post("https://codetime-api.datreks.com/batchEventLog")
            .header("token", PluginStateComponent.instance.state.token)
            .jsonBody(Klaxon().toJsonString(events)).responseJson() { _, _, result ->
                result.fold(
                    success = {
                        if (PluginStateComponent.instance.state.debug) {
                            log.info("submit event log success")
                        }
                    },
                    failure = { error ->
                        if (PluginStateComponent.instance.state.debug) {
                            log.warn("submit event log failed:$error")
                        }
                    }
                )
            }
    }
}
