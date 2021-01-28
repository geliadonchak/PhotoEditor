package photo_editor.nodes

import photo_editor.filters.FilterOptions
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.TextField
import java.lang.Integer.parseInt

class ChangeSaturationNode : DraggableNode() {
    @FXML
    var saturationFirstParam: TextField? = null

    @FXML
    var saturationSecondParam: TextField? = null

    @FXML
    var saturationThirdParam: TextField? = null


    init {
        DraggableNode()

        nodeType = NodeTypes.ChangeSaturation

        val fxmlLoader = FXMLLoader(javaClass.getResource("../templates/ChangeSaturationTemplate.fxml"))
        currentWidth = 200.0F
        currentHeight = 90.0F

        setFxml(fxmlLoader)
    }

    override fun getOptions(): FilterOptions {
        return FilterOptions()
                .add("val1", parseInt(saturationFirstParam?.text))
                .add("val2", parseInt(saturationSecondParam?.text))
                .add("val3", parseInt(saturationThirdParam?.text))
    }

    override fun setOptions(options: FilterOptions) {
        super.setOptions(options)

        this.saturationFirstParam?.text = options.getOption("val1").toString()
        this.saturationSecondParam?.text = options.getOption("val2").toString()
        this.saturationThirdParam?.text = options.getOption("val3").toString()
    }

    override fun interactions() {
        super.interactions()

        val saturationChangeListener = { parameter: TextField? ->
            parameter?.textProperty()?.addListener { _, _, newValue ->
                try {
                    parameter.text = parseInt(newValue).toString()
                    if (parseInt(parameter.text) > 255)
                        parameter.text = "255"
                } catch (e: NumberFormatException) {
                    parameter.text = "0"
                }

                isChangedProperty.set(true)
            }
        }

        saturationChangeListener(saturationFirstParam)
        saturationChangeListener(saturationSecondParam)
        saturationChangeListener(saturationThirdParam)
    }
}