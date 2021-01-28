package photo_editor;

import photo_editor.nodes.NodeTypes;
import photo_editor.nodes.DraggableNode;

import java.util.ArrayList;
import java.util.List;

public class StaticResourceManager {
    public static List<DraggableNode> nodesList = new ArrayList<>();
    public static DraggableNode nodeDragged = null;
    public static Integer nextId = 1;
    public static Integer curvesCount = 0;
    public static NodeTypes currentNodeType = NodeTypes.SetImage;
    public static String currentImagePath;

    public static Integer getNextId() {
        return nextId++;
    }
}
