package io.hulsbo.model;

import io.hulsbo.util.model.CrewMember.Gender;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.KCalCalculationStrategy;
import io.hulsbo.util.model.CrewMember.PhysicalActivity;

public class CrewMember {
    private final String name;
    private final int age;
    private final double height;
    private final double weight;
    private final Gender gender;
    private final PhysicalActivity activity;
    private final KCalCalculationStrategy kCalCalculationStrategy;

    public KCalCalculationStrategy getkCalCalculationStrategy() {
        return kCalCalculationStrategy;
    }

    public CrewMember(String name, int age, double height, double weight, Gender gender, PhysicalActivity activity, KCalCalculationStrategy kCalCalculationStrategy) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.activity = activity;
        this.kCalCalculationStrategy = kCalCalculationStrategy;
    }

    public double getDailyKCalNeed() {
        double BMR = kCalCalculationStrategy.determineBMR(age, height, weight, gender);
        return kCalCalculationStrategy.determineKCal(BMR, activity);
    }

    // NOTE: used in template 
    public int getDailyKCalNeed(boolean asInt) {
        double BMR = kCalCalculationStrategy.determineBMR(age, height, weight, gender);
        return (int) kCalCalculationStrategy.determineKCal(BMR, activity);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Gender getGender() {
        return gender;
    }

    public PhysicalActivity getActivity() {
        return activity;
    }

    // Getters and setters if needed
}
