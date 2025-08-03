package com.knowledgenest.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

public class ModelParameterizedTest {

    @BeforeAll
    static void beforeAllTests() {
        System.out.println(" [BEFORE ALL] Starting ModelParameterizedTest class...");
    }

    @BeforeEach
    void beforeEachTest() {
        System.out.println(" [BEFORE EACH] Starting a new test case...");
    }

    @AfterEach
    void afterEachTest() {
        System.out.println(" [AFTER EACH] Finished test case.\n");
    }

    @AfterAll
    static void afterAllTests() {
        System.out.println(" [AFTER ALL] All tests completed for ModelParameterizedTest.");
    }


    // ========== CourseModel Tests ==========

    @ParameterizedTest
    @ValueSource(strings = {"Java Basics", "Kotlin Advanced", "Android Dev"})
    void testCourseTitle(String title) {
        CourseModel course = new CourseModel();
        course.setTitle(title);
        assertEquals(title, course.getTitle());
    }

    @ParameterizedTest
    @CsvSource({
            "Basic Java, 500",
            "Advanced Android, 1000",
            "ML with Python, 1200"
    })
    void testCourseTitleAndPrice(String title, long price) {
        CourseModel course = new CourseModel();
        course.setTitle(title);
        course.setPrice(price);
        assertEquals(title, course.getTitle());
        assertEquals(price, course.getPrice());
    }

    @ParameterizedTest
    @MethodSource("courseRatingProvider")
    void testCourseRating(String rating) {
        CourseModel course = new CourseModel();
        course.setRating(rating);
        assertEquals(rating, course.getRating());
    }

    static Stream<String> courseRatingProvider() {
        return Stream.of("4.5", "3.0", "5.0");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/course_data.csv", numLinesToSkip = 1)
    void testCourseModelFromCSV(String title, String duration, String rating, String description, long price,
                                String thumbnail, String introvideo, String postedBy, String enable) {
        CourseModel course = new CourseModel(title, duration, rating, description, price, thumbnail, introvideo, postedBy, enable);

        assertEquals(title, course.getTitle());
        assertEquals(duration, course.getDuration());
        assertEquals(rating, course.getRating());
        assertEquals(description, course.getDescription());
        assertEquals(price, course.getPrice());
        assertEquals(thumbnail, course.getThumbnail());
        assertEquals(introvideo, course.getIntrovideo());
        assertEquals(postedBy, course.getPostedBy());
        assertEquals(enable, course.getEnable());
    }

    // ========== PlayListModel Tests ==========

    @ParameterizedTest
    @CsvSource({
            "Intro Lesson, https://video.com/intro.mp4",
            "Lecture 1, http://example.com/lec1.mp4"
    })
    void testPlayListModelTitleAndUrl(String title, String url) {
        PlayListModel playlist = new PlayListModel();
        playlist.setTitle(title);
        playlist.setVideoUrl(url);
        assertEquals(title, playlist.getTitle());
        assertEquals(url, playlist.getVideoUrl());
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void testPlaylistEnabled(String enabled) {
        PlayListModel model = new PlayListModel();
        model.setEnabled(enabled);
        assertEquals(enabled, model.getEnabled());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/playlist_data.csv", numLinesToSkip = 1)
    void testPlayListModelFromCSV(String title, String videoUrl, String enabled, String key) {
        PlayListModel model = new PlayListModel();
        model.setTitle(title);
        model.setVideoUrl(videoUrl);
        model.setEnabled(enabled);
        model.setKey(key);

        assertEquals(title, model.getTitle());
        assertEquals(videoUrl, model.getVideoUrl());
        assertEquals(enabled, model.getEnabled());
        assertEquals(key, model.getKey());
    }

    // ========== UserModel Tests ==========

    @ParameterizedTest
    @CsvSource({
            "Alice, alice@example.com",
            "Bob, bob@mail.com",
            "'', ''"
    })
    void testUserModelNameEmail(String name, String email) {
        UserModel user = new UserModel();
        user.setName(name);
        user.setEmail(email);
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = {"password123", "secret", "admin1234"})
    void testUserPassword(String password) {
        UserModel user = new UserModel();
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @ParameterizedTest
    @MethodSource("profileUrlProvider")
    void testUserProfile(String profileUrl) {
        UserModel user = new UserModel();
        user.setProfile(profileUrl);
        assertEquals(profileUrl, user.getProfile());
    }

    static Stream<String> profileUrlProvider() {
        return Stream.of(
                "https://example.com/img1.jpg",
                "http://mycdn.com/profile.png",
                ""
        );
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/user_data.csv", numLinesToSkip = 1)
    void testUserModelFromCSV(String name, String email, String password, String profile) {
        UserModel user = new UserModel(name, email, password, profile);

        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(profile, user.getProfile());
    }
}
