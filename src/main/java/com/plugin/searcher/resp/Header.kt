package com.plugin.searcher.resp

import com.google.gson.annotations.SerializedName

/**
 * Header.
 * @author yzf
 * @version 1.0.0
 */
class Header {
    
    @SerializedName("status")
    var status: Int = 0
    @SerializedName("QTime")
    var time: Int = 0
    @SerializedName("params")
    var param: Param? = null
}
