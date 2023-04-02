package com.github.si9ma.codetimejetbrains

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.annotations.Nullable
import java.awt.Color
import java.awt.Cursor
import java.awt.Desktop
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField

const val TOKEN_FIELD_COLUMNS = 30
const val TIP_TEXT = "Go to CodeTime to get a token"

class ConfigWindow constructor(project: Project) : DialogWrapper(project, true) {
    private lateinit var tokenField: JTextField
    private lateinit var debugCheckBox: JCheckBox
    private val log: Logger = Logger.getInstance("CodeTime")

    @Nullable
    override fun createCenterPanel(): JComponent {
        val panel = JPanel()

        // token
        panel.layout = GridLayout(0, 2)
        val tokenLabel = JLabel("Token:", JLabel.CENTER)
        tokenField = JTextField(TOKEN_FIELD_COLUMNS)
        panel.add(tokenLabel)
        tokenField.text = PluginStateComponent.instance.state.token
        panel.add(tokenField)

        // debug
        val debugLabel = JLabel("Debug:", JLabel.CENTER)
        panel.add(debugLabel)
        debugCheckBox = JCheckBox()
        debugCheckBox.isSelected = PluginStateComponent.instance.state.debug
        panel.add(debugCheckBox)

        // tip
        val tipLabel = JLabel("No Token?", JLabel.CENTER)
        val tipLink = JLabel(TIP_TEXT)
        tipLink.foreground = Color.BLUE.darker()
        tipLink.cursor = Cursor(Cursor.HAND_CURSOR)
        tipLink.addMouseListener(
            object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    try {
                        Desktop.getDesktop().browse(URI("https://codetime.datreks.com/"))
                    } catch (e1: IOException) {
                        log.warn("click link failed:${e1.printStackTrace()}")
                    } catch (e1: URISyntaxException) {
                        log.warn("click link failed:${e1.printStackTrace()}")
                    }
                }

                override fun mouseExited(e: MouseEvent?) {
                    tipLink.text = TIP_TEXT
                }

                override fun mouseEntered(e: MouseEvent?) {
                    tipLink.text = "<html><a href=''>$TIP_TEXT</a></html>"
                }
            }
        )
        panel.add(tipLabel)
        panel.add(tipLink)

        return panel
    }

    override fun doOKAction() {
        // check if token is valid
        val (_, _, result) = Fuel.get("https://api.codetime.dev/stats?by=time")
            .header("token", tokenField.text)
            .header("User-Agent", "CodeTime Client")
            .responseJson()
        when (result) {
            is Result.Failure<*> -> {
                log.warn("check token failed:${result.error}")
                JOptionPane.showMessageDialog(null, "Invalid token")
            }
            is Result.Success<*> -> {
                log.info("check token success")
                PluginStateComponent.instance.state.token = tokenField.text
                PluginStateComponent.instance.state.debug = debugCheckBox.isSelected
                super.doOKAction()
            }
        }
    }

    init {
        init()
        title = "CodeTime Configuration"
    }
}
