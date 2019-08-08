@file:JvmName("BrowserKt")

package com.plugin.searcher

import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.net.URL

import com.intellij.openapi.util.SystemInfo

/**
 * Browser.
 * @author yzf
 * @version 1.0.0
 */
object Browser {
    
    @JvmStatic
    fun url(url: URL) {
        try {
            Desktop.getDesktop().browse(url.toURI())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        
    }
    
    @JvmStatic
    fun file(file: File): Process? {
        try {
            if (SystemInfo.isWindows) {
                return Runtime.getRuntime().exec("explorer.exe /select," + file.absolutePath)
            }
            if (SystemInfo.isMac) {
                return Runtime.getRuntime().exec(arrayOf("open", "-R", file.absolutePath))
            }
            if (SystemInfo.isLinux) {
                if (SystemInfo.isGNOME || checkProcessNames("gnome-session", "cinnamon-session")) {
                    return if (checkProcessNames("nemo")) {
                        //mint
                        Runtime.getRuntime().exec(arrayOf("nemo", file.absolutePath))
                    } else Runtime.getRuntime().exec(arrayOf("nautilus", file.absolutePath))
                    //LINUX_GNOME
                }
                if (checkProcessNames("ksmserver")) {
                    //LINUX_KDE
                    return Runtime.getRuntime().exec(arrayOf("konqueror", file.absolutePath))
                }
                if (checkProcessNames("xfce4-session", "xfce-mcs-manage")) {
                    //LINUX_XFCE
                    return Runtime.getRuntime().exec(arrayOf("thunar", file.absolutePath))
                }
                if (checkProcessNames("lxsession")) {
                    //LINUX_LXDE
                    return Runtime.getRuntime().exec(arrayOf("pcmanfm", file.absolutePath))
                }
                if (checkProcessNames("mate-session")) {
                    //LINUX_MATE
                    return Runtime.getRuntime().exec(arrayOf("caja", file.absolutePath))
                }
            }
        } catch (e: IOException) {
            Desktop.getDesktop().open(file)
        }
        return null
    }
    
    @JvmStatic
    private fun checkProcessNames(vararg names: String): Boolean {
        return names.any { executePidOnCommand(it) }
    }
    
    @JvmStatic
    private fun executePidOnCommand(name: String): Boolean {
        try {
            return Runtime.getRuntime().exec(arrayOf("pidof", name)).waitFor() == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }
}
