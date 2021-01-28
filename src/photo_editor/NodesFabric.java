package photo_editor;

import photo_editor.nodes.*;

public class NodesFabric {
    public static DraggableNode createNode(NodeTypes type) {
        return switch (type) {
            case ChangeSaturation -> new ChangeSaturationNode();
            case SetSharpness -> new SetSharpnessNode();
            case ChangeBrightness -> new ChangeBrightnessNode();
            case SetImage -> new SetImageNode();
            case SetSepia -> new SetSepiaNode();
            case SetNegative -> new SetNegativeNode();
            case SetGray -> new SetGrayNode();
            case SetBlackWhite -> new SetBlackWhiteNode();
            case ChangeContrast -> new ChangeContrastNode();
            default -> new DraggableNode();
        };
    }

    public static DraggableNode createNode(String typeString) {
        NodeTypes nodeType;

        switch (typeString) {
            case "SetImage" -> nodeType = NodeTypes.SetImage;
            case "ChangeSaturation" -> nodeType = NodeTypes.ChangeSaturation;
            case "SetNegative" -> nodeType = NodeTypes.SetNegative;
            case "SetSepia" -> nodeType = NodeTypes.SetSepia;
            case "SetSharpness" -> nodeType = NodeTypes.SetSharpness;
            case "ChangeBrightness" -> nodeType = NodeTypes.ChangeBrightness;
            case "SetGray" -> nodeType = NodeTypes.SetGray;
            case "SetBlackWhite" -> nodeType = NodeTypes.SetBlackWhite;
            case "ChangeContrast" -> nodeType = NodeTypes.ChangeContrast;
            default -> nodeType = NodeTypes.BaseNode;
        }

        return createNode(nodeType);
    }
}
