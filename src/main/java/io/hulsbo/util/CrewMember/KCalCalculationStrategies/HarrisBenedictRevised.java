package io.hulsbo.util.CrewMember.KCalCalculationStrategies;

import io.hulsbo.util.CrewMember.Gender;

public class HarrisBenedictRevised implements KCalCalculationStrategy {

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
            BMR = 13.397 * weight + 4.799 * height - 5.677 * age + 88.362;
        } else BMR = 9.247 * weight + 3.098 * height - 4.330 * age + 447.593;
        return BMR;
    }
}
