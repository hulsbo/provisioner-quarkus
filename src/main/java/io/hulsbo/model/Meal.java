package io.hulsbo.model;

import java.util.Set;
import io.hulsbo.util.model.SafeID;

public class Meal extends BaseClass {

public SafeID putChild(Ingredient newIngredient) {
    return super.putChild(newIngredient, 0.0, 0.0);
}

    /**
     * @param id id of ingredient
     * @param weight absolute weight in grams of ingredient
     */
    public void modifyWeightOfIngredient(SafeID id, double weight) {
        if (childMap.get(id) == null) {
            throw new IllegalArgumentException("No Ingredient with such name exist.");
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
