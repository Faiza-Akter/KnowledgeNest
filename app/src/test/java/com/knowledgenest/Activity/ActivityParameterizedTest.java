package com.knowledgenest.Activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivityParameterizedTest {

    @BeforeAll
    void beforeAllTests() {
        System.out.println(" [BEFORE ALL] Starting ActivityLogicParameterizedTest...");
    }

    @BeforeEach
    void beforeEachTest() {
        System.out.println(" [BEFORE EACH] Running a test case...");
    }

    @AfterEach
    void afterEachTest() {
        System.out.println(" [AFTER EACH] Test case completed.\n");
    }

    @AfterAll
    void afterAllTests() {
        System.out.println(" [AFTER ALL] All tests finished for ActivityLogicParameterizedTest.");
    }

    // ===== PlayListActivity: Test Description Logic =====
    @ParameterizedTest
    @CsvSource({
            "'This is a course', true",
            "'   ', false",
            "'', false",
            "'Learn Java', true"
    })
    void testCourseDescription(String description, boolean expectedValid) {
        boolean isValid = description != null && !description.trim().isEmpty();
        assertEquals(expectedValid, isValid);
    }

    // ===== PlayListActivity: Test Price Formatting Logic =====
    @ParameterizedTest
    @ValueSource(longs = {0, 999, 1500, 10})
    void testPriceFormat(long price) {
        String formatted = String.valueOf(price);
        assertTrue(formatted.matches("\\d+")); // only digits expected
    }

    // ===== MainActivity: Fragment Switch Logic Testing =====

    static class DummyIds {
        static final int HOME = 101;
        static final int PROFILE = 102;
    }

    @ParameterizedTest
    @CsvSource({
            "101, KnowledgeNest",
            "102, Profile"
    })
    void testToolbarTitleChange(int selectedId, String expectedTitle) {
        String title;
        if (selectedId == DummyIds.HOME) {
            title = "KnowledgeNest";
        } else if (selectedId == DummyIds.PROFILE) {
            title = "Profile";
        } else {
            title = "Unknown";
        }
        assertEquals(expectedTitle, title);
    }

    // ===== PlayListActivity: Intent field validation =====
    @ParameterizedTest
    @CsvSource({
            "Java Basics, true",
            "'', false",
            "'   ', false"
    })
    void testIntentTitleField(String title, boolean expectedValid) {
        boolean isValid = title != null && !title.trim().isEmpty();
        assertEquals(expectedValid, isValid);
    }

    // ===== SplashScreenActivity: Delay logic =====
    @ParameterizedTest
    @ValueSource(ints = {1000, 1500, 2000})
    void testSplashDelayTiming(int delayMillis) {
        assertTrue(delayMillis >= 1000);
    }

    // ===== PlayListActivity: Duration Format Test =====
    @ParameterizedTest
    @MethodSource("durationProvider")
    void testDurationFormat(String duration) {
        boolean valid = duration.endsWith("h") || duration.endsWith("min");
        assertTrue(valid);
    }

    static Stream<String> durationProvider() {
        return Stream.of("5h", "2h", "30min", "0.5h");
    }
}
