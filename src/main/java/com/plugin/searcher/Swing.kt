@file:JvmName("SwingKt")

package com.plugin.searcher

import javax.swing.SwingUtilities
import javax.swing.tree.MutableTreeNode

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * Swing.
 * @author yzf
 * @version 1.0.0
 */
object Swing {
    
    @JvmStatic
    fun clipboard(text: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
    }
    
    @JvmStatic
    operator fun invoke(runnable: () -> Unit) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable()
        } else {
            SwingUtilities.invokeLater(runnable)
        }
    }
    
    @JvmStatic
    fun addNode(root: MutableTreeNode, node: MutableTreeNode) {
        if (node.parent === root) {
            root.insert(node, root.childCount - 1)
        } else {
            root.insert(node, root.childCount)
        }
    }
}
