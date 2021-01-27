package nodes

import StaticResourceManager
import filters.FilterOptions
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.*
import javafx.scene.layout.AnchorPane

var stateAddLink = DataFormat("linkAdd")
var stateAddNode = DataFormat("nodeAdd")

// TODO TOWATCH
open class DraggableNode : AnchorPane() {

    var isChangedProperty: BooleanProperty = SimpleBooleanProperty(false)
    var nodeType: NodeTypes = NodeTypes.BaseNode
    var currentWidth: Float = 0.0F
    var currentHeight: Float = 0.0F

    @FXML
    var leftLinkHandle: AnchorPane? = null

    @FXML
    var rightLinkHandle: AnchorPane? = null

    @FXML
    var titleBar: Label? = null


    fun setFxml(fxmlLoader: FXMLLoader) {
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<Any>()
        id = StaticResourceManager.getNextId().toString()
        titleBar?.text = nodeType.toString()
    }

    @FXML
    var deleteTemplate: Button? = null

    private lateinit var contextDragOver: EventHandler<DragEvent>
    private lateinit var contextDragDropped: EventHandler<DragEvent>

    private lateinit var linkDragDetected: EventHandler<MouseEvent>
    private lateinit var linkDeleteDragDetected: EventHandler<MouseEvent>
    private lateinit var linkDragDropped: EventHandler<DragEvent>
    private lateinit var contextLinkDragOver: EventHandler<DragEvent>
    private lateinit var contextLinkDragDropped: EventHandler<DragEvent>

    private var myLink = LinkNode()
    private var offset = Point2D(0.0, 0.0)

    var nextActionNode: DraggableNode? = null
    var prevActionNode: DraggableNode? = null

    var superParent: AnchorPane? = null

    @FXML
    protected fun initialize() {
        nodeHandlers()
        linkHandlers()
        interactions()

        leftLinkHandle?.onDragDetected = linkDeleteDragDetected
        leftLinkHandle?.onDragDropped = linkDragDropped
        rightLinkHandle?.onDragDetected = linkDragDetected

        parentProperty().addListener { _, _, _ -> superParent = parent as AnchorPane? }
    }

    private fun updatePoint(p: Point2D) {
        val local = parent.sceneToLocal(p)
        relocate(
                (local.x - offset.x),
                (local.y - offset.y)
        )
    }

    private fun nodeHandlers() {
        contextDragOver = EventHandler { event ->
            updatePoint(Point2D(event.sceneX, event.sceneY))
            event.consume()
        }

        contextDragDropped = EventHandler { event ->
            parent.onDragDropped = null
            parent.onDragOver = null
            event.isDropCompleted = true
            event.consume()
        }

        titleBar!!.onDragDetected = EventHandler { event ->
            parent.onDragOver = contextDragOver
            parent.onDragDropped = contextDragDropped

            offset = Point2D(event.x, event.y)
            updatePoint(Point2D(event.sceneX, event.sceneY))

            val content = ClipboardContent()
            content[stateAddNode] = "node"
            startDragAndDrop(*TransferMode.ANY).setContent(content)
        }
    }

    private fun linkHandlers() {
        // Add new curve
        linkDragDetected = EventHandler { event ->
            parent.onDragOver = null
            parent.onDragDropped = null

            StaticResourceManager.nodeDragged = this

            if (this.nextActionNode != null) {
                this.nextActionNode?.prevActionNode = null
                this.nextActionNode = null
                isChangedProperty.set(true)
                refreshCurves()

                parent.onDragOver = contextLinkDragOver
                parent.onDragDropped = contextLinkDragDropped

                superParent!!.children.add(0, myLink)
                myLink.isVisible = true

                myLink.setStart(Point2D(this.layoutX + width / 2, this.layoutY + height / 4))

                val content = ClipboardContent()
                content[stateAddLink] = "link"
                startDragAndDrop(*TransferMode.ANY).setContent(content)
            } else {
                parent.onDragOver = contextLinkDragOver
                parent.onDragDropped = contextLinkDragDropped

                superParent!!.children.add(0, myLink)
                myLink.isVisible = true

                myLink.setStart(Point2D(layoutX + width / 2, layoutY + height / 2))

                val content = ClipboardContent()
                content[stateAddLink] = "link"
                startDragAndDrop(*TransferMode.ANY).setContent(content)
            }

            event.consume()
        }

        // Remove an already attached curve
        linkDeleteDragDetected = EventHandler { event ->
            if (this.prevActionNode == null)
                return@EventHandler

            parent.onDragOver = null
            parent.onDragDropped = null

            // Remove and recount curves
            StaticResourceManager.nodeDragged = this.prevActionNode
            this.prevActionNode?.nextActionNode = null
            this.prevActionNode = null
            isChangedProperty.set(true)
            refreshCurves()

            parent.onDragOver = contextLinkDragOver
            parent.onDragDropped = contextLinkDragDropped

            superParent!!.children.add(0, myLink)
            myLink.isVisible = true

            myLink.setStart(Point2D(StaticResourceManager.nodeDragged.layoutX + width / 2,
                    StaticResourceManager.nodeDragged.layoutY + height / 4))

            val content = ClipboardContent()
            content[stateAddLink] = "link"
            startDragAndDrop(*TransferMode.ANY).setContent(content)
            event.consume()

            isChangedProperty.set(true)
        }

        // The end of curve on another node
        linkDragDropped = EventHandler { event ->
            parent.onDragOver = null
            parent.onDragDropped = null

            superParent!!.children.removeAt(0)

            if (this.prevActionNode != null) {
                return@EventHandler
            } else {
                val link = LinkNode()
                link.bindStartEnd(StaticResourceManager.nodeDragged, this)
                StaticResourceManager.curvesCount++
                superParent!!.children.add(0, link)

                StaticResourceManager.nodeDragged.nextActionNode = this
                this.prevActionNode = StaticResourceManager.nodeDragged
                isChangedProperty.set(true)
                StaticResourceManager.nodeDragged = null
            }

            event.isDropCompleted = true
            event.consume()
        }

        contextLinkDragOver = EventHandler { event ->
            event.acceptTransferModes(*TransferMode.ANY)
            if (!myLink.isVisible) myLink.isVisible = true
            myLink.setEnd(Point2D(event.x, event.y))

            event.consume()
        }

        // The end of curve is not on another node
        contextLinkDragDropped = EventHandler { event ->
            parent.onDragDropped = null
            parent.onDragOver = null

            myLink.isVisible = false
            superParent!!.children.removeAt(0)
            StaticResourceManager.nodeDragged = null

            event.isDropCompleted = true
            event.consume()
        }
    }

    private fun refreshCurves() {
        while (StaticResourceManager.curvesCount > 0) {
            superParent!!.children.removeAt(0)
            StaticResourceManager.curvesCount--
        }

        StaticResourceManager.nodesList.forEach {
            if (it.nextActionNode != null) {
                val link = LinkNode()
                link.bindStartEnd(it, it.nextActionNode!!)
                StaticResourceManager.curvesCount++
                superParent!!.children.add(0, link)
            }
        }
    }

    protected open fun interactions() {
        deleteTemplate?.setOnAction {
            val prevTemplate = prevActionNode
            prevTemplate?.nextActionNode = null

            val nextTemplate = nextActionNode
            nextTemplate?.prevActionNode = null

            this.nextActionNode = null
            this.prevActionNode = null

            refreshCurves()

            this.isVisible = false
            var counter = 1
            while (counter < StaticResourceManager.nodesList.size) {
                if (StaticResourceManager.nodesList[counter].id == this.id) {
                    StaticResourceManager.nodesList.remove(StaticResourceManager.nodesList[counter])
                    break
                }

                counter++
            }

            isChangedProperty.set(true)
        }
    }

    open fun getOptions(): FilterOptions {
        return FilterOptions()
    }

    open fun setOptions(options: FilterOptions) {}

    init {
        AnchorPane()

        nodeType = StaticResourceManager.currentNodeType
    }
}