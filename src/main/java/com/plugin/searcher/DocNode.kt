package com.plugin.searcher

import javax.swing.tree.DefaultMutableTreeNode

import com.plugin.searcher.resp.Doc

/**
 * DocNode.
 * @author yzf
 * @version 1.0.0
 */
class DocNode(val doc: Doc) : DefaultMutableTreeNode() {
    
    init {
        if (doc.versionCount <= 0) {
            setUserObject(TEMPLET_1.format(doc.repositoryId, doc.timestamp, doc.timestamp, doc.groupId, doc.artifactId, doc.version))
        } else {
            setUserObject(TEMPLET_2.format(doc.repositoryId, doc.versionCount, doc.timestamp, doc.timestamp, doc.groupId, doc.artifactId, doc.version))
        }
    }
    
    companion object {
        
        private const val TEMPLET_1 = "[%10s]    %tF %tT    %s:%s:%s"
        private const val TEMPLET_2 = "[%10s] %4d    %tF %tT    %s:%s:%s"
    }
}
