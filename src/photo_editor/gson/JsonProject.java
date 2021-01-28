package photo_editor.gson;

import java.util.ArrayList;

public class JsonProject {
    public ArrayList<JsonItem> items;
    public String imagePath;

    public JsonProject(ArrayList<JsonItem> items, String imagePath) {
        this.items = items;
        this.imagePath = imagePath;
    }
}
