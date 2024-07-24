package io.hulsbo.model;

import io.hulsbo.util.model.CrewMember.Gender;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.HarrisBenedictOriginal;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.HarrisBenedictRevised;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.KCalCalculationStrategy;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.MifflinStJeor;
import io.hulsbo.util.model.CrewMember.PhysicalActivity;

import io.hulsbo.util.model.SafeID;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CrewMember {
    private final SafeID safeId;
    private final String name;
    private final int age;
    private final int height;
    private final int weight;
    private final Gender gender;
    private final PhysicalActivity activity;
    private final KCalCalculationStrategy kCalCalculationStrategy;
    private final OffsetDateTime creationTime;
    public KCalCalculationStrategy getkCalCalculationStrategy() {
        return kCalCalculationStrategy;
    }

    public CrewMember(String name, int age, int height, int weight, String gender, String activity, String strategy) {

        SafeID id = SafeID.randomSafeID();

        KCalCalculationStrategy someStrategy = switch (strategy.toLowerCase()) {
            case "harris_benedict_original" -> new HarrisBenedictOriginal();
            case "harris_benedict_revised" -> new HarrisBenedictRevised();
            case "mifflin_st_jeor" -> new MifflinStJeor();
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategy);
        };
        this.creationTime = OffsetDateTime.now(ZoneOffset.ofHours(2));
        this.safeId = id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = Gender.valueOf(gender.toUpperCase());
        this.activity = PhysicalActivity.valueOf(activity.toUpperCase());
        this.kCalCalculationStrategy = someStrategy;
        Manager.register(id, this);
    }

    public int getDailyKCalNeed() {
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

    public int getWeight() {
        return weight;
    }
    public int getHeight() {
        return height;
    }

    public SafeID getId() {
        return safeId;
    }

    public OffsetDateTime getCreationTime() {
        return creationTime;
    }

    // Getters and setters if needed
}
