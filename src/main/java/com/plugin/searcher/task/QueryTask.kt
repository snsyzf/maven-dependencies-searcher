package com.plugin.searcher.task

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.intellij.lang.annotations.Language

import javax.swing.tree.MutableTreeNode

import java.io.IOException
import java.net.URL
import java.text.MessageFormat
import java.util.concurrent.atomic.AtomicBoolean

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.plugin.searcher.Browser
import com.plugin.searcher.DocNode
import com.plugin.searcher.Swing
import com.plugin.searcher.Templates
import com.plugin.searcher.Values
import com.plugin.searcher.resp.Doc
import com.plugin.searcher.resp.Resp

/**
 * QueryTask.
 * @author yzf
 * @version 1.0.0
 */
class QueryTask(
    private val root: MutableTreeNode,
    private val url: URL,
    private val advice: ((count: Int, total: Int) -> Unit)?
) : Task.Backgroundable(null, "Queries by $url", true), Callback {
    
    private val stop = AtomicBoolean()
    private lateinit var indicator: ProgressIndicator
    
    override fun run(indicator: ProgressIndicator) {
        this.indicator = indicator
        
        indicator.isIndeterminate = true
        indicator.fraction = 0.0
        
        Values.client.newCall(request(this.url, this.root, null)).enqueue(this)
        
        while (!stop.get()) {
            if (indicator.isCanceled) {
                indicator.cancel()
                stop.set(true)
            }
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
    
    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
        
        println("Query failure: url = " + call.request().url())
        
        stop.set(true)
        
        indicator.fraction = 1.0
        indicator.cancel()
        
        @Language("HTML")
        val content = """
            <html lang="en">
                <body>
                URL: <a href="url">${call.request().url()}</a><br>
                Reason: $e
                </body>
            </html>
        """.trimIndent()
        
        Notifications.Bus.notify(Notification(Values.TAG, "Query failed", content, NotificationType.WARNING) { _, event ->
            if ("url" == event.description) {
                Browser.url(call.request().url().url())
            }
        })
    }
    
    @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {
        
        println("Query response: url = " + call.request().url())
        
        stop.set(true)
        
        indicator.fraction = 1.0
        indicator.stop()
        
        if (this.advice != null) {
            this.advice.invoke(0, Integer.MIN_VALUE)
        }
        
        if (response.body() != null) {
            val text = response.body()!!.string()
            val resp = Values.GSON.fromJson(text, Resp::class.java)
            val root = call.request().tag(MutableTreeNode::class.java)
            val parent = call.request().tag(Doc::class.java)
            
            println("Text: $text")
            println("Root: $root")
            println("Parent: $parent")
            
            if (root != null) {
                for (doc in resp.response!!.docs!!) {
                    
                    if (doc.repositoryId == null) {
                        if (parent?.repositoryId != null) {
                            doc.repositoryId = parent.repositoryId
                        } else {
                            doc.repositoryId = "central"
                        }
                    }
                    
                    val node = DocNode(doc)
                    
                    Swing.addNode(root, node)
                    
                    if (parent == null && doc.versionCount > 0) {
                        requestAll(node, doc)
                    }
                }
                
                if (this.advice != null && parent == null) {
                    this.advice.invoke(resp.response!!.docs!!.size, resp.response!!.count)
                }
            }
        }
    }
    
    private fun requestAll(node: MutableTreeNode, doc: Doc) {
        val url = URL(MessageFormat.format(Templates.QUERY_GA, doc.groupId, doc.artifactId, doc.versionCount))
        val request = request(url, node, doc)
        Values.client.newCall(request).enqueue(this)
    }
    
    
    private fun request(url: URL, root: MutableTreeNode, doc: Doc?): Request {
        println("New request: $url")
        
        return Request.Builder()
            .url(url)
            .get()
            .header("Content-Type", "application/json")
            .header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0"
            )
            .tag(MutableTreeNode::class.java, root)
            .tag(Doc::class.java, doc)
            .build()
    }
}
