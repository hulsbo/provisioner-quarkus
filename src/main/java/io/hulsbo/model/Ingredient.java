package io.hulsbo.model;

import java.security.SecureRandom;
import java.util.Set;
import java.util.UUID;

public class Ingredient extends BaseClass {


    public Ingredient() {
        SecureRandom random = new SecureRandom();
        for (String nutrient : nutrientsMap.keySet()) {
            UUID key = putChild(new Nutrient(), 0.0, 0.0);
            modifyWeightOfNutrient(key, random.nextInt(1,101));
            Manager.getObject(key).setName(nutrient); // returns found parent msg
        }
        for (UUID key : childMap.keySet()) {
            nutrientsMap.put(childMap.get(key).getChild().getName(), childMap.get(key).getRatio());
        }
    }
    public void modifyWeightOfNutrient(UUID id, double weight) {
        if (childMap.get(id) == null) {
            throw new IllegalArgumentException("No nutrient with such name exist.");
        }
        if (weight == 0) {
            throw new IllegalArgumentException("The weight cannot be 0.");
        }

        childMap.get(id).setRecipeWeight(weight);

        // Calculate the current total weight
        double totalWeight = 0.0;

        Set<UUID> keys = childMap.keySet();

        for (UUID key : keys) {
            totalWeight += childMap.get(key).getRecipeWeight();
        }

        // update all ratios
        for (UUID key : keys) {
            double weightedValue = childMap.get(key).getRecipeWeight() / totalWeight;
            modifyRatio(key, weightedValue);
        }
    }
}

