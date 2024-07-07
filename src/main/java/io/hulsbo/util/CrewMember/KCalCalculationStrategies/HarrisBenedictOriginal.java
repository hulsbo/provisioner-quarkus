package io.hulsbo.util.CrewMember.KCalCalculationStrategies;

import jakarta.enterprise.context.ApplicationScoped;
import io.hulsbo.util.CrewMember.Gender;

@ApplicationScoped
public class HarrisBenedictOriginal implements KCalCalculationStrategy {

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
            BMR = 13.7516 * weight + 5.0033 * height - 6.755 * age + 66.473;
        } else BMR = 9.5634 * weight + 1.8496 * height - 4.6756 * age + 655.0955;

        return BMR;
    }

}
