package io.hulsbo.model;

import java.security.SecureRandom;
import java.util.Set;
import io.hulsbo.util.model.SafeID;

public class Ingredient extends BaseClass {

    public void modifyNutrient(String nutrient, Double ratio) {
        if (ratio != null) {
            nutrientsMap.put(nutrient, ratio);
        }
    }

}

