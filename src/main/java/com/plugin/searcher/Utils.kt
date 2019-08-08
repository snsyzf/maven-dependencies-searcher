@file:JvmName("UtilsKt")

package com.plugin.searcher

import java.text.MessageFormat

/**
 * Utils.
 * @author yzf
 * @version 1.0.0
 */

fun String.message(vararg args: Any?): String {
    return MessageFormat.format(this, *args)
}
