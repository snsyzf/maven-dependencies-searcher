package com.plugin.searcher

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.util.Disposer
import okhttp3.OkHttpClient

/**
 * MavenDependenciesSearcher.
 * @author yzf
 * @version 1.0.0
 */
class MavenDependenciesSearcher : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        
        Values.client = OkHttpClient()
        
        val searcher = Searcher()
        
        val builder = DialogBuilder()
            .centerPanel(searcher)
            .title("Maven dependencies searcher")
        
        builder.removeAllActions()
        builder.setPreferredFocusComponent(searcher.queryInput)
        builder.showModal(false)
        
        e.presentation.isEnabled = false
        
        Disposer.register(builder.dialogWrapper.disposable, Disposable {
            Values.client.dispatcher().cancelAll()
            Values.client.dispatcher().executorService().shutdown()
            e.presentation.isEnabled = true
        })
    }
}
