package io.hulsbo.model;

import io.hulsbo.util.model.CrewMember.Gender;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.KCalCalculationStrategy;
import io.hulsbo.util.model.CrewMember.PhysicalActivity;

import io.hulsbo.util.model.SafeID;

public class CrewMember {
    private final SafeID safeId;
    private final String name;
    private final int age;
    private final int height;
    private final int weight;
    private final Gender gender;
    private final PhysicalActivity activity;
    private final KCalCalculationStrategy kCalCalculationStrategy;

    public KCalCalculationStrategy getkCalCalculationStrategy() {
        return kCalCalculationStrategy;
    }

    public CrewMember(String name, int age, int height, int weight, Gender gender, PhysicalActivity activity, KCalCalculationStrategy kCalCalculationStrategy) {
        SafeID id = SafeID.randomSafeID();
        this.safeId = id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.activity = activity;
        this.kCalCalculationStrategy = kCalCalculationStrategy;
        Manager.register(id, this);
    }

    public double getDailyKCalNeed() {
        double BMR = kCalCalculationStrategy.determineBMR(age, height, weight, gender);
        return Math.round(kCalCalculationStrategy.determineKCal(BMR, activity));
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

    public int getWeight() {
        return weight;
    }
    public int getHeight() {
        return weight;
    }

    public SafeID getId() {
        return safeId;
    }

    // Getters and setters if needed
}
