package photo_editor.nodes

import photo_editor.filters.FilterOptions
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.TextField
import java.lang.Integer.parseInt

class ChangeContrastNode : DraggableNode() {
    @FXML
    var contrastParam: TextField? = null

    init {
        DraggableNode()

        nodeType = NodeTypes.ChangeContrast

        val fxmlLoader = FXMLLoader(javaClass.getResource("../templates/ChangeContrastTemplate.fxml"))
        currentWidth = 200.0F
        currentHeight = 80.0F

        setFxml(fxmlLoader)
    }

    override fun getOptions(): FilterOptions {
        return FilterOptions().add("value", parseInt(contrastParam?.text))
    }

    override fun setOptions(options: FilterOptions) {
        super.setOptions(options)

        this.contrastParam?.text = options.getOption("value").toString()
    }

    override fun interactions() {
        super.interactions()

        contrastParam?.textProperty()?.addListener { _, _, newValue ->
            try {
                contrastParam?.text = parseInt(newValue).toString()
                if (parseInt(contrastParam?.text) < 1)
                    contrastParam?.text = "1"
                if (parseInt(contrastParam?.text) > 100)
                    contrastParam?.text = "100"
            } catch (e: NumberFormatException) {
                contrastParam?.text = "1"
            }

            isChangedProperty.set(true)
        }
    }
}