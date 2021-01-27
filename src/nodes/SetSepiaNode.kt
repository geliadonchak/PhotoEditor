package nodes

import javafx.fxml.FXMLLoader

class SetSepiaNode: DraggableNode() {
    init {
        DraggableNode()

        nodeType = NodeTypes.SetSepia

        val fxmlLoader = FXMLLoader(javaClass.getResource("../templates/SetNegativeTemplate.fxml"))
        currentWidth = 155.0F
        currentHeight = 50.0F

        setFxml(fxmlLoader)
    }
}