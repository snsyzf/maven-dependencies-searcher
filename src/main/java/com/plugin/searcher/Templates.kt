@file:JvmName("TemplatesKt")

package com.plugin.searcher

/**
 * Templates.
 * @author yzf
 * @version 1.0.0
 */
object Templates {
    
    const val COPY = "Copy [{0}]"
    const val SEARCH_BY = "Search by [{0}]"
    
    const val QUERY_Q = "https://search.maven.org/solrsearch/select?q={0}&start={1}&rows={2}"
    const val QUERY_GA = "https://search.maven.org/solrsearch/select?q=g:{0}+a:{1}&core=gav&start=0&rows={2}"
    
    const val APACGE_MAVEN = "<dependency>\n    <groupId>{0}</groupId>\n    <artifactId>{1}</artifactId>\n    <version>{2}</version>\n</dependency>"
    const val APACHE_IVY = "<dependency org=\"{0}\" name=\"{1}\" rev=\"{2}\" />"
    const val APACHE_BUILDR = "'{0}:{1}:jar:{2}'"
    const val GRADLE_GROOVY = "implementation ''{0}:{1}:{2}''"
    const val GRADLE_KOTLIN = "implementation(\"{0}:{1}:{2}\")"
    const val GROOVY_GRAPE = "@Grab(''{0}:{1}:{2}'')"
    const val SCALA_SBT = "libraryDependencies += \"{0}\" % \"{1}\" % \"{2}\""
    const val LEININGEN = "[{0}/{1} \"{2}\"]"
    
    const val FILENAME = "{0}/{1}/{2}/{1}-{2}{3}"
    
    
}
