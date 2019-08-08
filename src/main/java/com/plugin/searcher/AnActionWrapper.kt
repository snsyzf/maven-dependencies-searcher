package com.plugin.searcher

import javax.swing.Icon

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * AnActionWrapper.
 * @author yzf
 * @version 1.0.0
 */
object AnActionWrapper {
    
    fun of(text: String? = null, description: String? = null, icon: Icon? = null, listener: (e: AnActionEvent)-> Unit): AnAction {
        return object : AnAction(text, description, icon) {
            override fun actionPerformed(e: AnActionEvent) {
                listener(e)
            }
        }
    }
}
