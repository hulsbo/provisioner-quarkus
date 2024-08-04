package io.hulsbo.util.model.baseclass;

import java.util.*;

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
// TODO: Finish implementation below where put only adapts if > 100 ratio is achieved.
//  If < 100 the rest should be unallocated, and this unallocated space communicated to front-end
//  so user knows at input of ratio how much space is available.

//        // Calculating the sum
//        Collection<Double> values = nutrientsMap.values();
//        double sum = 0.0;
//
//        for (Double v : values) {
//            sum = sum + v;
//        }
//
//        double oldValue = nutrientsMap.get(key);
//
//        if (sum - oldValue + value > 100.0) {
//            System.out.println("The ratio sum exceeded 100.0, shrinking all to be equal to 100.");
//
//        }
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
