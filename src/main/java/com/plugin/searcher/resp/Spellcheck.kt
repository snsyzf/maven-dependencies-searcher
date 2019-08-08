package com.plugin.searcher.resp

import com.google.gson.annotations.SerializedName

/**
 * Spellcheck.
 * @author yzf
 * @version 1.0.0
 */
class Spellcheck {
    
    @SerializedName("suggestions")
    var suggestions: List<Any>? = null
   
}
