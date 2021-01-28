package photo_editor.nodes

import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.SimpleDoubleProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.CubicCurve
import java.io.IOException
import java.util.*

class LinkNode : AnchorPane() {
    @FXML
    var nodeLink: CubicCurve? = null

    private val offsetX = SimpleDoubleProperty()
    private val offsetY = SimpleDoubleProperty()
    private val offsetDirX1 = SimpleDoubleProperty()
    private val offsetDirX2 = SimpleDoubleProperty()
    private val offsetDirY1 = SimpleDoubleProperty()
    private val offsetDirY2 = SimpleDoubleProperty()

    @FXML
    private fun initialize() {
        offsetX.set(100.0)
        offsetY.set(50.0)

        offsetDirX1.bind(When(nodeLink!!.startXProperty().greaterThan(nodeLink!!.endXProperty())).then(-1.0).otherwise(1.0))
        offsetDirX2.bind(When(nodeLink!!.startXProperty().greaterThan(nodeLink!!.endXProperty())).then(1.0).otherwise(-1.0))

        nodeLink!!.controlX1Property().bind(Bindings.add(nodeLink!!.startXProperty(), offsetX.multiply(offsetDirX1)))
        nodeLink!!.controlX2Property().bind(Bindings.add(nodeLink!!.endXProperty(), offsetX.multiply(offsetDirX2)))
        nodeLink!!.controlY1Property().bind(Bindings.add(nodeLink!!.startYProperty(), offsetY.multiply(offsetDirY1)))
        nodeLink!!.controlY2Property().bind(Bindings.add(nodeLink!!.endYProperty(), offsetY.multiply(offsetDirY2)))
    }

    fun setStart(point: Point2D) {
        nodeLink!!.startX = point.x
        nodeLink!!.startY = point.y
    }

    fun setEnd(point: Point2D) {
        nodeLink!!.endX = point.x
        nodeLink!!.endY = point.y
    }

    fun bindStartEnd(source1: DraggableNode, source2: DraggableNode) {
        nodeLink!!.startXProperty().bind(Bindings.add(source1.layoutXProperty(), source1.currentWidth / 2.0))
        nodeLink!!.startYProperty().bind(Bindings.add(source1.layoutYProperty(), source1.currentHeight / 2.0))
        nodeLink!!.endXProperty().bind(Bindings.add(source2.layoutXProperty(), source2.currentWidth / 2.0))
        nodeLink!!.endYProperty().bind(Bindings.add(source2.layoutYProperty(), source2.currentHeight / 2.0))
    }

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("../templates/NodeLink.fxml"))

        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)

        try {
            fxmlLoader.load<Any>()
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }

        id = UUID.randomUUID().toString()
    }
}