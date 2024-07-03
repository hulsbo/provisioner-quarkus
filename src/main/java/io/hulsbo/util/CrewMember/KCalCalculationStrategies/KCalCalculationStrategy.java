package io.hulsbo.util.CrewMember.KCalCalculationStrategies;
import io.hulsbo.util.CrewMember.Gender;
import io.hulsbo.util.CrewMember.PhysicalActivity;


public interface KCalCalculationStrategy {
    double determineBMR(int age, double height, double weight, Gender gender);

    default double determineKCal(double BMR, PhysicalActivity physicalActivity) {
        double factor = 0;
        switch (physicalActivity) {
            case SEDENTARY -> factor = 1.2;
            case MILD -> factor = 1.375;
            case MODERATE -> factor = 1.55;
            case HEAVY -> factor = 1.7;
            case VERY_HEAVY -> factor = 1.9;
        }
        return BMR * factor;
    }
}
