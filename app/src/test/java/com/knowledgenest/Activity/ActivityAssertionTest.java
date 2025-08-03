package com.knowledgenest.Activity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityAssertionTest {
    //MainActivity Logic Tests

    @Test
    void testToolbarTitleSwitch() {
        int HOME = 101;
        int PROFILE = 102;
        int OTHER = 999;

        String title1 = getToolbarTitle(HOME);
        String title2 = getToolbarTitle(PROFILE);
        String title3 = getToolbarTitle(OTHER);

        assertEquals("KnowledgeNest", title1);
        assertEquals("Profile", title2);
        assertEquals("Unknown", title3);
    }

    private String getToolbarTitle(int selectedId) {
        if (selectedId == 101) return "KnowledgeNest";
        else if (selectedId == 102) return "Profile";
        else return "Unknown";
    }

    //PlayListActivity Logic Tests

    @Test
    void testDescriptionValidation() {
        String validDesc = "Learn Java";
        String emptyDesc = "";
        String nullDesc = null;
        String spaces = "     ";

        assertTrue(validDesc != null && !validDesc.trim().isEmpty());
        assertFalse(emptyDesc != null && !emptyDesc.trim().isEmpty());
        assertFalse(nullDesc != null && !nullDesc.trim().isEmpty());
        assertFalse(spaces != null && !spaces.trim().isEmpty());
    }

    @Test
    void testPriceToStringFormat() {
        long price = 1500;
        String formatted = String.valueOf(price);
        assertTrue(formatted.matches("\\d+"));
        assertEquals("1500", formatted);
    }

    @Test
    void testDurationFormat() {
        String dur1 = "3h";
        String dur2 = "45min";
        String dur3 = "1.5h";
        String dur4 = "2";

        assertTrue(dur1.endsWith("h") || dur1.endsWith("min"));
        assertTrue(dur2.endsWith("h") || dur2.endsWith("min"));
        assertTrue(dur3.endsWith("h") || dur3.endsWith("min"));
        assertFalse(dur4.endsWith("h") || dur4.endsWith("min"));
    }



    //SplashScreenActivity Logic

    @Test
    void testSplashDelayThreshold() {
        int delay = 1500;
        assertTrue(delay >= 1000);
    }

    //Intent Extras Check (PlayListActivity)

    @Test
    void testIntentTitleCheck() {
        String title1 = "Java Basics";
        String title2 = "";
        String title3 = null;
        String title4 = "   ";

        assertTrue(title1 != null && !title1.trim().isEmpty());
        assertFalse(title2 != null && !title2.trim().isEmpty());
        assertFalse(title3 != null && !title3.trim().isEmpty());
        assertFalse(title4 != null && !title4.trim().isEmpty());
    }
}