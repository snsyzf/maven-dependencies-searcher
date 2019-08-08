package com.plugin.searcher.resp

import com.google.gson.annotations.SerializedName

/**
 * Resp.
 * @author yzf
 * @version 1.0.0
 */
class Resp {
    
    @SerializedName("responseHeader")
    var header: Header? = null
    @SerializedName("response")
    var response: Response? = null
    @SerializedName("spellcheck")
    var spellcheck: Spellcheck? = null
}
