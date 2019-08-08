package com.plugin.searcher.resp

import com.google.gson.annotations.SerializedName

/**
 * Doc.
 * @author yzf
 * @version 1.0.0
 */
class Doc {
    
    @SerializedName("id")
    var id: String? = null
    @SerializedName("g")
    var groupId: String? = null
    @SerializedName("a")
    var artifactId: String? = null
    @SerializedName(value = "v", alternate = ["latestVersion"])
    var version: String? = null
    @SerializedName("repositoryId")
    var repositoryId: String? = null
    @SerializedName("p")
    var packaging: String? = null
    @SerializedName("timestamp")
    var timestamp: Long = 0
    @SerializedName("versionCount")
    var versionCount: Int = 0
    @SerializedName("text")
    var text: List<String>? = null
    @SerializedName("ec")
    var files: List<String> = listOf()
    @SerializedName("tags")
    var tags: List<String>? = null
    
}
