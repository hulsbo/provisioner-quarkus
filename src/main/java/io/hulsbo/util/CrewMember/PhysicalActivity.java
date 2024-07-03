package io.hulsbo.util.CrewMember;

/**
 * <p>The activity factor calculates the  on the basal metabolic rate (BMR).
 *  The activity factor is determined by the five pre-defined levels of physical activity.</p>
 *
 <ol>
 * <li>Sedentary: If you are sedentary (little or no exercise) : Calorie-Calculation = BMR x 1.2</li>
 * <li>Mild: If you are lightly active (light exercise/sports 1-3 days/week) : Calorie-Calculation = BMR x 1.375</li>
 * <li>Moderate: If you are moderately active (moderate exercise/sports 3-5 days/week) : Calorie-Calculation = BMR x 1.55</li>
 * <li>Heavy: If you are very active (hard exercise/sports 6-7 days a week) : Calorie-Calculation = BMR x 1.725</li>
 * <li>Very heavy: If you are extra active (very hard exercise/sports &amp; physical job or 2x training) : Calorie-Calculation = BMR x 1.9</li>
 * </ol>
 *
 *  * <p>Source: <a href="https://globalrph.com/medcalcs/harris-benedict-equation-updated-basal-metabolic-rate/">HARRIS-BENEDICT EQUATION (UPDATED)</a>.</p>
 */
public enum PhysicalActivity {
    SEDENTARY,
    MILD,
    MODERATE,
    HEAVY,
    VERY_HEAVY
}
