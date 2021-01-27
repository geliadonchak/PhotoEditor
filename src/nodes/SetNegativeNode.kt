package nodes

import javafx.fxml.FXMLLoader

class SetNegativeNode: DraggableNode() {
    init {
        DraggableNode()

        nodeType = NodeTypes.SetNegative

        val fxmlLoader = FXMLLoader(javaClass.getResource("../templates/SetNegativeTemplate.fxml"))
        currentWidth = 150.0F
        currentHeight = 50.0F

        setFxml(fxmlLoader)
    }
}