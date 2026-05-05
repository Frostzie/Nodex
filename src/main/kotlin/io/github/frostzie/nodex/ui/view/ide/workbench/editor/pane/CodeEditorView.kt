package io.github.frostzie.nodex.ui.view.ide.workbench.editor.pane

import javafx.scene.layout.StackPane
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory

class CodeEditorView(
    initialContent: String,
    private val tabId: String,
    private val onTextChange: (String, String) -> Unit
) : StackPane() {

    private val codeArea = CodeArea()
    private val scrollPane = VirtualizedScrollPane(codeArea)
    private var suppressChangeEvent = false
    private var lastKnownContent: String = initialContent

    init {
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)
        codeArea.replaceText(initialContent)

        children.add(scrollPane)

        codeArea.textProperty().addListener { _, _, newText ->
            if (suppressChangeEvent) {
                return@addListener
            }
            if (newText != lastKnownContent) {
                lastKnownContent = newText
                onTextChange(tabId, newText)
            }
        }
    }

    fun renderContent(content: String) {
        if (codeArea.text == content) {
            lastKnownContent = content
            return
        }

        suppressChangeEvent = true
        try {
            codeArea.replaceText(content)
            lastKnownContent = content
        } finally {
            suppressChangeEvent = false
        }
    }
}
