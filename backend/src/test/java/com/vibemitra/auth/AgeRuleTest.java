package com.vibemitra.auth;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgeRuleTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 27);

    @Test
    void exactly18TodayIsAllowed() {
        assertTrue(AgeRule.isAdult(LocalDate.of(2008, 11, 27), TODAY));
    }

    @Test
    void turns18TomorrowIsRejected() {
        assertFalse(AgeRule.isAdult(LocalDate.of(2008, 11, 28), TODAY));
    }

    @Test
    void futureBirthDateIsRejected() {
        assertFalse(AgeRule.isAdult(LocalDate.of(2030, 1, 1), TODAY));
    }

    @Test
    void missingDatesAreRejected() {
        assertFalse(AgeRule.isAdult(null, TODAY));
        assertFalse(AgeRule.isAdult(LocalDate.of(1990, 1, 1), null));
    }
}
