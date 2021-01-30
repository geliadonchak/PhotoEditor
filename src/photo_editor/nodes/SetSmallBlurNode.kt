package photo_editor.nodes

import javafx.fxml.FXMLLoader

class SetSmallBlurNode: DraggableNode() {
    init {
        DraggableNode()

        nodeType = NodeTypes.SetSmallBlur

        val fxmlLoader = FXMLLoader(javaClass.getResource("../templates/SetNegativeTemplate.fxml"))
        currentWidth = 155.0F
        currentHeight = 50.0F

        setFxml(fxmlLoader)
    }
}