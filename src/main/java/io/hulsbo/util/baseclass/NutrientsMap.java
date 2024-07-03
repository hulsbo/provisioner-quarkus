package io.hulsbo.util.baseclass;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class NutrientsMap extends AbstractMap<String, Double> {
    private final Map<String, Double> nutrientsMap = new LinkedHashMap<>();

    public NutrientsMap() {
        nutrientsMap.put("protein", 0.0);
        nutrientsMap.put("fat", 0.0);
        nutrientsMap.put("carbs", 0.0);
        nutrientsMap.put("water", 0.0);
        nutrientsMap.put("fiber", 0.0);
        nutrientsMap.put("salt", 0.0);
    }

    @Override
    public Set<Entry<String, Double>> entrySet() {
        return nutrientsMap.entrySet();
    }

    @Override
    public Double put(String key, Double value) {
        if (nutrientsMap.containsKey(key)) {
            nutrientsMap.put(key, value);
        } else {
            throw new IllegalArgumentException("Invalid key: " + key);
        }
        return value;
    }

    @Override
    public Double remove(Object key) {
        throw new UnsupportedOperationException("Keys cannot be removed in a nutrientsMap.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("A nutrientsMaps cannot be cleared.");
    }

}
