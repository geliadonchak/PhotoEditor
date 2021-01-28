package photo_editor.nodes

import javafx.fxml.FXMLLoader

class SetSharpnessNode : DraggableNode() {
    init {
        DraggableNode()

        nodeType = NodeTypes.SetSharpness

        val fxmlLoader = FXMLLoader(javaClass.getResource("../templates/SetNegativeTemplate.fxml"))
        currentWidth = 200.0F
        currentHeight = 70.0F

        setFxml(fxmlLoader)
    }
}