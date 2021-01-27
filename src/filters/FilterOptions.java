package filters;

import java.util.HashMap;

public class FilterOptions {
    public FilterOptions() {
        options = new HashMap<>();
    }

    public FilterOptions add(String name, Integer value){
        options.put(name, value);
        return this;
    }

    public Integer getOption(String name) {
        return options.get(name);
    }

    private final HashMap<String, Integer> options;
}
