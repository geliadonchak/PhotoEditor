package gson;

import filters.FilterOptions;

public class JsonItem {
    public float layoutX;
    public float layoutY;
    public String nodeType;
    public Integer connectedWith;
    public FilterOptions filterOptions;

    public JsonItem(float layoutX, float layoutY, String nodeType, Integer connectedWith, FilterOptions filterOptions) {
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.nodeType = nodeType;
        this.connectedWith = connectedWith;
        this.filterOptions = filterOptions;
    }
}
