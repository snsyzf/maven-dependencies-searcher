package com.plugin.searcher

import com.intellij.icons.AllIcons
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.labels.LinkLabel
import com.intellij.ui.treeStructure.Tree
import com.plugin.searcher.task.DownloadTask
import com.plugin.searcher.task.QueryTask
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.event.*
import java.io.File
import java.net.URL
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import kotlin.math.min

/**
 * Searcher.
 * @author yzf
 * @version 1.0.0
 */
class Searcher : JPanel() {
    
    private val root = DefaultMutableTreeNode()
    private val more = DefaultMutableTreeNode("---------------------- More ----------------------")
    private val offset = AtomicInteger()
    private val total = AtomicInteger()
    private val lastQuery = AtomicReference<String>()
    
    private val advice = { count: Int, total: Int ->
        println("Update widget: ${LocalDateTime.now()}, count = $count, total = $total")
        
        this.offset.addAndGet(count)
        
        if (total != Integer.MIN_VALUE) {
            this.total.set(total)
        }
        
        this.more.removeFromParent()
        
        if (this.root.childCount > 0 && this.offset.get() < this.total.get()) {
            this.root.add(this.more)
        }
        
        Swing.invoke { this.bWidget.updateUI() }
    }
    
    lateinit var queryInput: JTextField
        private set
    private lateinit var bWidget: Tree
    private lateinit var bPopup: JPopupMenu
    private lateinit var bReload: JMenuItem
    private lateinit var bCopy: JMenu
    private lateinit var bCopyGroup: JMenuItem
    private lateinit var bCopyArtifact: JMenuItem
    private lateinit var bCopyVersion: JMenuItem
    private lateinit var bSearch: JMenu
    private lateinit var bSearchByGroup: JMenuItem
    private lateinit var bSearchByArtifact: JMenuItem
    private lateinit var bSearchByGroupAndArtifact: JMenuItem
    private lateinit var bDownload: JMenu
    
    private val bDownloadItems: List<JMenuItem>
    
    init {
        initComponents()
        
        this.preferredSize = Dimension(800, 600)
        
        this.registerKeyboardAction({ doQueryAction() }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW)
        
        this.bDownloadItems = (0..10).map { JMenuItem() }
        
        val listener = { e: ActionEvent ->
            val it = e.source as JMenuItem
            val node = current()
            
            val url = if (Values.getDownloadSource() == 0) {
                Values.URL_TEMPLATE_DOWNLOAD + it.text
            } else {
                Values.DOWNLOAD_URLS.getOrDefault(node.doc.repositoryId, Values.URL_TEMPLATE_DOWNLOAD) + it.text
            }
            
            val title = "Download " + it.text
            val descriptor = FileSaverDescriptor(title, title, FileUtilRt.getExtension(it.text))
            val dialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, null as Project?)
            val name = File(it.text).name
            val wrapper = dialog.save(null, name)
            
            if (wrapper != null) {
                DownloadTask(URL(url), wrapper.file).queue()
            }
        }
        
        for (it in this.bDownloadItems) {
            this.bDownload.add(it)
            it.addActionListener(listener)
        }
        
        this.bWidget.model = DefaultTreeModel(this.root)
        this.bWidget.add(this.bPopup)
        if (this.bWidget.cellRenderer is JComponent) {
            (this.bWidget.cellRenderer as JComponent).font = Font("Monospaced", Font.PLAIN, 12)
        }
        
        this.bWidget.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                if (bWidget.selectionPath != null && bWidget.selectionPath.lastPathComponent !== root) {
                    if (e.isPopupTrigger && bWidget.selectionPath.lastPathComponent !== more) {
                        
                        val node = current()
                        val doc = node.doc
                        
                        bReload.isVisible = doc.versionCount > 0
                        bDownloadItems.forEach { it.isVisible = false }
                        
                        if (!doc.groupId.isNullOrEmpty()) {
                            bCopyGroup.isVisible = true
                            bCopyGroup.text = Templates.COPY.message(doc.groupId ?: "")
                            bCopyGroup.actionCommand = doc.groupId
                        }
                        
                        if (!doc.artifactId.isNullOrEmpty()) {
                            bCopyArtifact.isVisible = true
                            bCopyArtifact.text = Templates.COPY.message(doc.artifactId ?: "")
                            bCopyArtifact.actionCommand = doc.artifactId
                        }
                        
                        if (!doc.version.isNullOrEmpty()) {
                            bCopyVersion.isVisible = true
                            bCopyVersion.text = Templates.COPY.message(doc.version ?: "")
                            bCopyVersion.actionCommand = doc.version
                        }
                        
                        if (!doc.groupId.isNullOrEmpty()) {
                            bSearchByGroup.isVisible = true
                            bSearchByGroup.actionCommand = "g:${doc.groupId}"
                            bSearchByGroup.text = Templates.SEARCH_BY.message(bSearchByGroup.actionCommand)
                        }
                        
                        if (!doc.artifactId.isNullOrEmpty()) {
                            bSearchByArtifact.isVisible = true
                            bSearchByArtifact.actionCommand = "a:${doc.artifactId}"
                            bSearchByArtifact.text = Templates.SEARCH_BY.message(bSearchByArtifact.actionCommand)
                        }
                        
                        if (!doc.groupId.isNullOrEmpty() and !doc.artifactId.isNullOrEmpty()) {
                            bSearchByGroupAndArtifact.isVisible = true
                            bSearchByGroupAndArtifact.actionCommand = "g:${doc.groupId} + a:${doc.artifactId}"
                            bSearchByGroupAndArtifact.text = Templates.SEARCH_BY.message(bSearchByGroupAndArtifact.actionCommand)
                        }
                        
                        
                        for (i in doc.files.indices) {
                            bDownloadItems[i].isVisible = true
                            bDownloadItems[i].text = Templates.FILENAME.message(doc.groupId?.replace('.', '/'), doc.artifactId, doc.version, doc.files[i])
                        }
                        
                        bPopup.show(e.component, e.x, e.y)
                    }
                    
                    if (SwingUtilities.isLeftMouseButton(e) && e.clickCount == 2 && bWidget.selectionPath.lastPathComponent === more) {
                        loadMore()
                    }
                }
            }
        })
    }
    
    private fun current(): DocNode {
        return this.bWidget.selectionPath.lastPathComponent as DocNode
    }
    
    private fun setDependencies(templet: String) {
        val node = current()
        Swing.clipboard(templet.message(node.doc.groupId, node.doc.artifactId, node.doc.version))
    }
    
    private fun loadMore() {
        val query = queryInput.text
        
        if (lastQuery.get() == query && this.offset.get() < this.total.get()) {
            val url = URL(
                Templates.QUERY_Q.message(
                    query,
                    offset.get(),
                    min(Values.getPageSize(), this.total.get() - this.offset.get())
                )
            )
            QueryTask(root, url, advice).queue()
        } else {
            query(query)
        }
    }
    
    private fun query(query: String) {
        this.queryInput.text = query
        
        this.total.set(0)
        this.offset.set(0)
        
        this.lastQuery.set(query)
        
        this.root.removeAllChildren()
        this.root.userObject = "Queries by $query"
        
        QueryTask(root, URL(Templates.QUERY_Q.message(query, offset.get(), Values.getPageSize())), this.advice).queue()
    }
    
    private fun doQueryAction() {
        
        Values.client.dispatcher().cancelAll()
        
        query(this.queryInput.text)
    }
    
    private fun doExpandAllAction() {
        this.bWidget.expandPath(TreePath(this.root))
    }
    
    private fun doCollapseAllAction() {
        this.bWidget.collapsePath(TreePath(this.root))
    }
    
    private fun doReloadAction() {
        val node = current()
        val doc = node.doc
        
        if (doc.versionCount > 0) {
            val url = URL(Templates.QUERY_GA.message(doc.groupId, doc.artifactId, doc.versionCount))
            QueryTask(node, url, this.advice).queue()
        }
    }
    
    private fun doCopyGroupAction() {
        Swing.clipboard(current().doc.groupId!!)
    }
    
    private fun doCopyArtifactAction() {
        Swing.clipboard(current().doc.artifactId!!)
    }
    
    private fun doCopyVersionAction() {
        Swing.clipboard(current().doc.version!!)
    }
    
    private fun doSearchByGroupAction(e: ActionEvent) {
        query(e.actionCommand)
    }
    
    private fun doSearchByArtifactAction(e: ActionEvent) {
        query(e.actionCommand)
    }
    
    private fun doSearchByGroupAndArtifactAction(e: ActionEvent) {
        query(e.actionCommand)
    }
    
    private fun initComponents() {
        val north = JPanel()
        queryInput = JTextField()
        val toolbar = JPanel()
        val scroll = JBScrollPane()
        bWidget = Tree()
        bPopup = JPopupMenu()
        bReload = JMenuItem()
        val bApacheMaven = JMenuItem()
        val bApacheIvy = JMenuItem()
        val bApacheBuildr = JMenuItem()
        val bGradleGroovyDSL = JMenuItem()
        val bGradleKotlinDSL = JMenuItem()
        val bGroovyGrape = JMenuItem()
        val bScalaSBT = JMenuItem()
        val bLeiningen = JMenuItem()
        bCopy = JMenu()
        bCopyGroup = JMenuItem()
        bCopyArtifact = JMenuItem()
        bCopyVersion = JMenuItem()
        bSearch = JMenu()
        bSearchByGroup = JMenuItem()
        bSearchByArtifact = JMenuItem()
        bSearchByGroupAndArtifact = JMenuItem()
        bDownload = JMenu()
        
        //======== this ========
        layout = BorderLayout()
        
        //======== panel1 ========
        run {
            north.layout = MigLayout(
                "fillx,hidemode 3",
                """
                    []
                    []
                    [grow]
                    []
                """.trimIndent(), //NON-NLS
                // rows
                """
                    []
                    []
                """.trimIndent()
            ) //NON-NLS
            north.add(queryInput, "cell 0 0 4 1,aligny center,grow 100 0") //NON-NLS
            
            //---- bQuery ----
            north.add(
                ActionManager.getInstance().createActionToolbar(
                    Values.TAG, DefaultActionGroup(
                        AnActionWrapper.of(null, "Search", AllIcons.Actions.Search) {
                            this.doQueryAction()
                        }
                    ), true
                ).component, "cell 4 0,aligny center,grow 100 0"
            ) //NON-NLS
            
            //======== toolbar ========
            run {
                toolbar.layout = BorderLayout()
                
                toolbar.add(
                    ActionManager.getInstance().createActionToolbar(
                        Values.TAG, DefaultActionGroup(
                            AnActionWrapper.of(description = "Collapse All", icon = AllIcons.Actions.Collapseall) { this.doCollapseAllAction() },
                            AnActionWrapper.of(description = "Expand All", icon = AllIcons.Actions.Expandall) { this.doExpandAllAction() }
                        ), true
                    ).component, BorderLayout.CENTER
                )
            }
            north.add(toolbar, "cell 0 1") //NON-NLS
            
            north.add(JPanel(FlowLayout(FlowLayout.LEFT)).also {
                it.add(JLabel("Page size:", SwingConstants.RIGHT))
                it.add(JSpinner(SpinnerNumberModel(20, 5, 50, 5)).apply {
                    this.value = Values.getPageSize()
                    
                    this.addChangeListener {
                        Values.setPageSize(this.value as Int)
                    }
                })
            })
            
            north.add(JPanel(FlowLayout(FlowLayout.LEFT)).also {
                it.add(JLabel("Download source:", SwingConstants.RIGHT))
                it.add(JComboBox(arrayOf("Default", "AliYun")).apply {
                    
                    this.selectedIndex = Values.getDownloadSource()
                    
                    this.addActionListener {
                        Values.setDownloadSource(this.selectedIndex)
                    }
                })
            })
            
            //---- bOptions ----
            north.add(LinkLabel.create("Advanced Options") {
                Messages.showMessageDialog(javaClass.getResourceAsStream("/help.html").readBytes().toString(Charsets.UTF_8), "Advanced Options", null)
            }, "cell 3 1 2 1,alignx right,growx 0") //NON-NLS
        }
        add(north, BorderLayout.NORTH)
        
        //======== scroll ========
        run { scroll.setViewportView(bWidget) }
        add(scroll, BorderLayout.CENTER)
        
        //======== bPopup ========
        run {
            
            //---- bReload ----
            bReload.text = "Reload" //NON-NLS
            bReload.icon = ImageIcon(javaClass.getResource("/reload.png")) //NON-NLS
            bReload.addActionListener { this.doReloadAction() }
            bPopup.add(bReload)
            
            val action = ActionListener {
                setDependencies(it.actionCommand)
            }
            
            //---- bApacheMaven ----
            bApacheMaven.text = "Apache Maven" //NON-NLS
            bApacheMaven.icon = ImageIcon(javaClass.getResource("/maven.png")) //NON-NLS
            bApacheMaven.actionCommand = Templates.APACGE_MAVEN
            bApacheMaven.addActionListener(action)
            bPopup.add(bApacheMaven)
            
            //---- bApacheIvy ----
            bApacheIvy.text = "Apache Ivy" //NON-NLS
            bApacheIvy.icon = ImageIcon(javaClass.getResource("/ivy.png")) //NON-NLS
            bApacheIvy.actionCommand = Templates.APACHE_IVY
            bApacheIvy.addActionListener(action)
            bPopup.add(bApacheIvy)
            
            //---- bApacheBuildr ----
            bApacheBuildr.text = "Apache Buildr" //NON-NLS
            bApacheBuildr.icon = ImageIcon(javaClass.getResource("/buildr.png")) //NON-NLS
            bApacheBuildr.actionCommand = Templates.APACHE_BUILDR
            bApacheBuildr.addActionListener(action)
            bPopup.add(bApacheBuildr)
            
            //---- bGradleGroovyDSL ----
            bGradleGroovyDSL.text = "Gradle Groovy DSL" //NON-NLS
            bGradleGroovyDSL.icon = ImageIcon(javaClass.getResource("/gradle.png")) //NON-NLS
            bGradleGroovyDSL.actionCommand = Templates.GRADLE_GROOVY
            bGradleGroovyDSL.addActionListener(action)
            bPopup.add(bGradleGroovyDSL)
            
            //---- bGradleKotlinDSL ----
            bGradleKotlinDSL.text = "Gradle Kotlin DSL" //NON-NLS
            bGradleKotlinDSL.icon = ImageIcon(javaClass.getResource("/kotlin.png")) //NON-NLS
            bGradleKotlinDSL.actionCommand = Templates.GRADLE_KOTLIN
            bGradleKotlinDSL.addActionListener(action)
            bPopup.add(bGradleKotlinDSL)
            
            //---- bGroovyGrape ----
            bGroovyGrape.text = "Groovy Grape" //NON-NLS
            bGroovyGrape.icon = ImageIcon(javaClass.getResource("/groovy.png")) //NON-NLS
            bGroovyGrape.actionCommand = Templates.GROOVY_GRAPE
            bGroovyGrape.addActionListener(action)
            bPopup.add(bGroovyGrape)
            
            //---- bScalaSBT ----
            bScalaSBT.text = "Scala SBT" //NON-NLS
            bScalaSBT.icon = ImageIcon(javaClass.getResource("/scala.png")) //NON-NLS
            bScalaSBT.actionCommand = Templates.SCALA_SBT
            bScalaSBT.addActionListener(action)
            bPopup.add(bScalaSBT)
            
            //---- bLeiningen ----
            bLeiningen.text = "Leiningen" //NON-NLS
            bLeiningen.icon = ImageIcon(javaClass.getResource("/leiningen.png")) //NON-NLS
            bLeiningen.actionCommand = Templates.LEININGEN
            bLeiningen.addActionListener(action)
            bPopup.add(bLeiningen)
            bPopup.addSeparator()
            
            //======== bCopy ========
            run {
                bCopy.text = "Copy" //NON-NLS
                bCopy.icon = AllIcons.Actions.Copy //NON-NLS
                
                //---- bCopyGroup ----
                bCopyGroup.text = "Group" //NON-NLS
                bCopyGroup.addActionListener { this.doCopyGroupAction() }
                bCopy.add(bCopyGroup)
                
                //---- bCopyArtifact ----
                bCopyArtifact.text = "Artifact" //NON-NLS
                bCopyArtifact.addActionListener { this.doCopyArtifactAction() }
                bCopy.add(bCopyArtifact)
                
                //---- bCopyVersion ----
                bCopyVersion.text = "Version" //NON-NLS
                bCopyVersion.addActionListener { this.doCopyVersionAction() }
                bCopy.add(bCopyVersion)
            }
            bPopup.add(bCopy)
            
            //======== bSearch ========
            run {
                bSearch.text = "Search" //NON-NLS
                bSearch.icon = AllIcons.Actions.Search //NON-NLS
                
                //---- bSearchByGroup ----
                bSearchByGroup.text = "Search by group" //NON-NLS
                bSearchByGroup.addActionListener { this.doSearchByGroupAction(it) }
                bSearch.add(bSearchByGroup)
                
                //---- bSearchByArtifact ----
                bSearchByArtifact.text = "Search by artifact" //NON-NLS
                bSearchByArtifact.addActionListener { this.doSearchByArtifactAction(it) }
                bSearch.add(bSearchByArtifact)
                
                //---- bSearchByGroupAndArtifact ----
                bSearchByGroupAndArtifact.text = "Search by group and artifact" //NON-NLS
                bSearchByGroupAndArtifact.addActionListener { this.doSearchByGroupAndArtifactAction(it) }
                bSearch.add(bSearchByGroupAndArtifact)
            }
            bPopup.add(bSearch)
            
            //======== bDownload ========
            run {
                bDownload.text = "Download" //NON-NLS
                bDownload.icon = AllIcons.Actions.Download //NON-NLS
            }
            bPopup.add(bDownload)
        }
    }
}
