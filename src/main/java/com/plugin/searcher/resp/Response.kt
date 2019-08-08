package com.plugin.searcher.resp

import com.google.gson.annotations.SerializedName

/**
 * Response.
 * @author yzf
 * @version 1.0.0
 */
class Response {
    
    @SerializedName("numFound")
    var count: Int = 0
    @SerializedName("start")
    var start: Int = 0
    @SerializedName("docs")
    var docs: List<Doc>? = null
    
}
