package com.github.si9ma.codetimejetbrains

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "PluginStateComponent",
    storages = [Storage("codetime.xml")]
)
open class PluginStateComponent : PersistentStateComponent<PluginStateComponent.PluginState>, Disposable {
    // this is how we're going to call the component from different classes
    companion object {
        val instance: PluginStateComponent
            get() = ServiceManager.getService(PluginStateComponent::class.java)
    }

    // the component will always keep our state as a variable
    private var pluginState: PluginState = PluginState()

    override fun getState(): PluginState {
        return pluginState
    }

    override fun loadState(state: PluginState) {
        pluginState = state
    }

    class PluginState {
        var token = ""
        var debug = false
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }
}
