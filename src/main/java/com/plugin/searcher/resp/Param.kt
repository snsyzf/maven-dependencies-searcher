package com.plugin.searcher.resp

import com.google.gson.annotations.SerializedName

/**
 * Param.
 * @author yzf
 * @version 1.0.0
 */
class Param {
    
    @SerializedName("q")
    var query: String? = null
    @SerializedName("defType")
    var defType: String? = null
    @SerializedName("indent")
    var indent: String? = null
    @SerializedName("gf")
    var gf: String? = null
    @SerializedName("spellcheck")
    var isSpellcheck: Boolean = false
    @SerializedName("fl")
    var fl: String? = null
    @SerializedName("start")
    var start: Int = 0
    @SerializedName("spellcheck.count")
    var spellcheckCount: Int = 0
    @SerializedName("sort")
    var sort: String? = null
    @SerializedName("rows")
    var rows: Int = 0
    @SerializedName("version")
    var version: String? = null
    @SerializedName("wt")
    var wt: String? = null
}
