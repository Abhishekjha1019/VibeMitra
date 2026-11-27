package com.vibemitra.auth;

/**
 * Launch cities (Delhi NCR). Must stay in sync with the CHECK constraint on users.city
 * in database/migrations. Adding a city requires a migration and a product decision.
 */
public enum City {
    DELHI,
    NOIDA,
    GHAZIABAD,
    GURUGRAM,
    FARIDABAD
}
