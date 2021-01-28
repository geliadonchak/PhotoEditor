package photo_editor.nodes

import photo_editor.filters.FilterOptions
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.TextField
import java.lang.Integer.parseInt

class ChangeBrightnessNode : DraggableNode() {
    @FXML
    var brightnessParam: TextField? = null

    init {
        DraggableNode()

        nodeType = NodeTypes.ChangeBrightness

        val fxmlLoader = FXMLLoader(javaClass.getResource("../templates/ChangeBrightnessTemplate.fxml"))
        currentWidth = 200.0F
        currentHeight = 80.0F

        setFxml(fxmlLoader)
    }

    override fun getOptions(): FilterOptions {
        return FilterOptions().add("value", parseInt(brightnessParam?.text))
    }

    override fun setOptions(options: FilterOptions) {
        super.setOptions(options)

        this.brightnessParam?.text = options.getOption("value").toString()
    }

    override fun interactions() {
        super.interactions()

        brightnessParam?.textProperty()?.addListener { _, _, newValue ->
            try {
                brightnessParam?.text = parseInt(newValue).toString()
                if (parseInt(brightnessParam?.text) < 0)
                    brightnessParam?.text = "0"
            } catch (e: NumberFormatException) {
                brightnessParam?.text = "0"
            }

            isChangedProperty.set(true)
        }
    }
}