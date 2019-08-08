@file:JvmName("ValuesKt")

package com.plugin.searcher

import com.google.gson.Gson
import com.intellij.ide.util.PropertiesComponent
import okhttp3.OkHttpClient

/**
 * Values.
 * @author yzf
 * @version 1.0.0
 */
object Values {
    
    @JvmStatic
    val TAG = "Maven dependencies searcher"
    
    @JvmStatic
    val GSON = Gson()
    
    @JvmStatic
    val URL_TEMPLATE_DOWNLOAD = "https://search.maven.org/remotecontent?filepath="
    
    @JvmStatic
    val DOWNLOAD_URLS: Map<String, String> = mapOf(
        "central" to "https://maven.aliyun.com/repository/central/",
        "google" to "https://maven.aliyun.com/repository/google/",
        "gradle-plugin" to "https://maven.aliyun.com/repository/gradle-plugin/",
        "jcenter" to "https://maven.aliyun.com/repository/jcenter/",
        "spring" to "https://maven.aliyun.com/repository/spring/",
        "spring-plugin" to "https://maven.aliyun.com/repository/spring-plugin/",
        "grails-core" to "https://maven.aliyun.com/repository/grails-core/",
        "mapr-public" to "https://maven.aliyun.com/repository/mapr-public/",
        "apache snapshots" to "https://maven.aliyun.com/repository/apache snapshots/"
    )
    
    @JvmStatic
    lateinit var client: OkHttpClient
    
    private const val ID = "com.plugin.maven-dependencies-searcher"
    private const val KEY_QUERY_PAGESIZE = "$ID:QueryPageSize"
    private const val KEY_DOWNLOAD_SOURCE = "$ID:DownloadSource"
    
    
    @JvmStatic
    fun getPageSize(): Int = PropertiesComponent.getInstance().getInt(KEY_QUERY_PAGESIZE, 20)
    @JvmStatic
    fun setPageSize(size: Int) = PropertiesComponent.getInstance().setValue(KEY_QUERY_PAGESIZE, size, 20)
    
    @JvmStatic
    fun getDownloadSource(): Int = PropertiesComponent.getInstance().getInt(KEY_DOWNLOAD_SOURCE, 0)
    @JvmStatic
    fun setDownloadSource(id: Int) = PropertiesComponent.getInstance().setValue(KEY_DOWNLOAD_SOURCE, id, 0)
    
}
