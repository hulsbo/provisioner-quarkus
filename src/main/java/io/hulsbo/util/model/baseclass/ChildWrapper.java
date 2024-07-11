package io.hulsbo.util.model.baseclass;

import io.hulsbo.model.BaseClass;

public class ChildWrapper {
    private BaseClass child;
    private double ratio;
    private double recipeWeight;

    public ChildWrapper(BaseClass childObject, double ratio, double recipeWeight) {
        this.child = childObject;
        this.ratio = ratio;
        this.recipeWeight = recipeWeight;
    }

    // Constructor without absWeight (optional parameter)
    public ChildWrapper(BaseClass childObject, double ratio) {
        this.child = childObject;
        this.ratio = ratio;
        // Default value for absWeight
        this.recipeWeight = 0.0; // Or any other default value you want
    }

    public BaseClass getChild() {
        return child;
    }

    public double getRatio() {
        return ratio;
    }

    public double getRecipeWeight() {
        return recipeWeight;
    }

    public void setChild(BaseClass baseclass) {
        this.child = baseclass;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public void setRecipeWeight(double recipeWeight) {
        this.recipeWeight = recipeWeight;
    }
}
