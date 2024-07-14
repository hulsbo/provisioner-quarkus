package io.hulsbo.model;

import java.security.SecureRandom;
import java.util.Set;
import io.hulsbo.util.model.SafeID;

public class Ingredient extends BaseClass {


    public Ingredient() {
        SecureRandom random = new SecureRandom();
        for (String nutrient : nutrientsMap.keySet()) {
            SafeID key = putChild(new Nutrient(), 0.0, 0.0);
            modifyWeightOfNutrient(key, random.nextInt(1,101));
            Manager.getBaseClass(key).setName(nutrient); // returns found parent msg
        }
        for (SafeID key : childMap.keySet()) {
            nutrientsMap.put(childMap.get(key).getChild().getName(), childMap.get(key).getRatio());
        }
    }
    public void modifyWeightOfNutrient(SafeID id, double weight) {
        if (childMap.get(id) == null) {
            throw new IllegalArgumentException("No nutrient with such name exist.");
        }
        if (weight == 0) {
            throw new IllegalArgumentException("The weight cannot be 0.");
        }

        childMap.get(id).setRecipeWeight(weight);

        // Calculate the current total weight
        double totalWeight = 0.0;

        Set<SafeID> keys = childMap.keySet();

        for (SafeID key : keys) {
            totalWeight += childMap.get(key).getRecipeWeight();
        }

        // update all ratios
        for (SafeID key : keys) {
            double weightedValue = childMap.get(key).getRecipeWeight() / totalWeight;
            modifyRatio(key, weightedValue);
        }
    }
}

