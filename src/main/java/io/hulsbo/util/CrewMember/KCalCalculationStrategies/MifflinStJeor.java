package io.hulsbo.util.CrewMember.KCalCalculationStrategies;

import io.hulsbo.util.CrewMember.Gender;

public class MifflinStJeor implements KCalCalculationStrategy {

    /**
     * Source: <a href=
     * "https://en.wikipedia.org/wiki/Harris%E2%80%93Benedict_equation">...</a>
     *
     * @param age    of crew member
     * @param height of crew member
     * @param weight of crew member
     * @param gender of crew member
     * @return BMR
     */
    @Override
    public double determineBMR(int age, double height, double weight, Gender gender) {
        double BMR;
        if (gender == Gender.MALE) {
            BMR = 10 * weight + 6.25 * height - 5 * age + 5;
        } else BMR = 10 * weight + 6.25 * height - 5 * age - 161;

        return BMR;
    }
}
