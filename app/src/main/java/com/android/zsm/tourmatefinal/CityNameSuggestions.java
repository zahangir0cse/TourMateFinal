package com.android.zsm.tourmatefinal;

import android.content.SearchRecentSuggestionsProvider;

public class CityNameSuggestions extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.android.shamim.taketour.CityNameSuggestions";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public CityNameSuggestions() {
        setupSuggestions(AUTHORITY,MODE);
    }
}
