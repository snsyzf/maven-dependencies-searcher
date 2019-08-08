package com.plugin.searcher.task

import org.intellij.lang.annotations.Language

import java.io.File
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean

import com.github.axet.wget.WGet
import com.github.axet.wget.info.URLInfo
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.plugin.searcher.Browser
import com.plugin.searcher.Values

/**
 * DownloadTask.
 * @author yzf
 * @version 1.0.0
 */
class DownloadTask(
    private val url: URL,
    private val target: File
) : Task.Backgroundable(
    null, "Downloading $url", true
), Runnable {
    
    private val stop = AtomicBoolean()
    
    private lateinit var indicator: ProgressIndicator
    private lateinit var wGet: WGet
    
    override fun run(indicator: ProgressIndicator) {
        this.indicator = indicator
        
        indicator.isIndeterminate = false
        
        Thread(this).start()
        
        this.wGet = WGet(this.url, this.target)
        this.wGet.info.retry = 2
        this.wGet.download(this.stop) {
            when (this.wGet.info.state) {
                URLInfo.States.DONE -> {
                    indicator.fraction = 1.0
                    indicator.stop()
                    stop.set(true)
                }
                URLInfo.States.DOWNLOADING -> indicator.fraction = wGet.info.count.toDouble() / wGet.info.length.toDouble()
                URLInfo.States.ERROR -> {
                    indicator.cancel()
                    stop.set(true)
                }
                URLInfo.States.STOP -> {
                    indicator.stop()
                    stop.set(true)
                }
                else -> {
                }
            }
        }
    }
    
    override fun onSuccess() {
        @Language("HTML")
        val content = """
            <html lang="en">
                <body>
                URL: <a href="url">$url</a><br>
                File: <a href="file">$target</a>
                </body>
            </html>
        """.trimIndent()
        
        Notifications.Bus.notify(Notification(Values.TAG, "下载完成", content, NotificationType.INFORMATION) { _, event ->
            if ("url" == event.description) {
                Browser.url(this.url)
            } else if ("file" == event.description) {
                Browser.file(this.target)
            }
        })
    }
    
    override fun onThrowable(error: Throwable) {
        @Language("HTML")
        val content = """
            <html lang="en">
                <body>
                URL: <a href="url">$url</a><br>
                Error: $error
                </body>
            </html>
        """.trimIndent()
        
        Notifications.Bus.notify(Notification(Values.TAG, "下载失败", content, NotificationType.WARNING) { _, event ->
            if ("url" == event.description) {
                Browser.url(this.url)
            }
        })
    }
    
    override fun run() {
        while (!stop.get()) {
            if (indicator.isCanceled) {
                indicator.cancel()
                stop.set(true)
            }
            Thread.sleep(100)
        }
    }
}
