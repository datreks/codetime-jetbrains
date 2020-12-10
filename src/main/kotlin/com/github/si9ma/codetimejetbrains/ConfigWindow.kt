package com.github.si9ma.codetimejetbrains

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.annotations.Nullable
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

const val TOKEN_FIELD_COLUMNS = 30

class ConfigWindow constructor(project: Project) : DialogWrapper(project, true) {
    private lateinit var tokenField: JTextField
    private lateinit var debugCheckBox: JCheckBox

    @Nullable
    protected override fun createCenterPanel(): JComponent {
        val panel = JPanel()

        // token
        panel.layout = GridLayout(0, 2)
        var tokenLabel = JLabel("Token:", JLabel.CENTER)
        tokenField = JTextField(TOKEN_FIELD_COLUMNS)
        panel.add(tokenLabel)
        tokenField.text = PluginStateComponent.instance.state.token
        panel.add(tokenField)

        // debug
        var debugLabel = JLabel("Debug:", JLabel.CENTER)
        panel.add(debugLabel)
        debugCheckBox = JCheckBox()
        debugCheckBox.isSelected = PluginStateComponent.instance.state.debug
        panel.add(debugCheckBox)
        return panel
    }

    override fun doOKAction() {
        PluginStateComponent.instance.state.token = tokenField.text
        PluginStateComponent.instance.state.debug = debugCheckBox.isSelected
        super.doOKAction()
    }

    init {
        init()
        setTitle("CodeTime Configuration")
    }
}
