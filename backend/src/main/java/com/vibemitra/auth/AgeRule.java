package com.vibemitra.auth;

import java.time.LocalDate;
import java.time.Period;

/** Minimum-age rule for matchmaking features. Kept as a pure function so it is easy to unit-test. */
public final class AgeRule {

    public static final int MINIMUM_AGE = 18;

    private AgeRule() {
    }

    public static boolean isAdult(LocalDate dateOfBirth, LocalDate today) {
        if (dateOfBirth == null || today == null || dateOfBirth.isAfter(today)) {
            return false;
        }
        return Period.between(dateOfBirth, today).getYears() >= MINIMUM_AGE;
    }
}
