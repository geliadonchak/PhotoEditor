package nodes

import StaticResourceManager
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.stage.FileChooser
import java.io.File

class SetImageNode: DraggableNode() {

    @FXML
    var changeImagePath: Button? = null

    var imagePath: String = ""

    init {
        DraggableNode()

        nodeType = NodeTypes.SetImage

        val fxmlLoader = FXMLLoader(javaClass.getResource("../templates/SetImageTemplate.fxml"))
        currentWidth = 125.0F
        currentHeight = 50.0F

        setFxml(fxmlLoader);
    }

    override fun interactions() {
        super.interactions()

        changeImagePath?.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.addAll(
                    FileChooser.ExtensionFilter("Image", "*.png", "*.jpg", "*.webp")
            )
            val selectedFile: File? = fileChooser.showOpenDialog(null)

            if (selectedFile != null) {
                imagePath = selectedFile.toURI().toString().drop(6)
                StaticResourceManager.currentImagePath = imagePath

                isChangedProperty.set(true)
            }
        }
    }


}