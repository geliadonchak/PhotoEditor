package photo_editor.nodes

import javafx.fxml.FXMLLoader

class SetBigBlurNode: DraggableNode() {
    init {
        DraggableNode()

        nodeType = NodeTypes.SetBigBlur

        val fxmlLoader = FXMLLoader(javaClass.getResource("../templates/SetNegativeTemplate.fxml"))
        currentWidth = 155.0F
        currentHeight = 50.0F

        setFxml(fxmlLoader)
    }
}