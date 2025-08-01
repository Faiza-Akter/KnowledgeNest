package com.knowledgenest.Model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ModelParameterizedTest {

    // ========== CourseModel Tests ==========

    @ParameterizedTest
    @ValueSource(strings = {"Java Basics", "Kotlin Advanced", "Android Dev"})
    @DisplayName("Test CourseModel Title Setter and Getter")
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
    @DisplayName("Test CourseModel Title & Price")
    void testCourseTitleAndPrice(String title, long price) {
        CourseModel course = new CourseModel();
        course.setTitle(title);
        course.setPrice(price);
        assertEquals(title, course.getTitle());
        assertEquals(price, course.getPrice());
    }

    @ParameterizedTest
    @MethodSource("courseRatingProvider")
    @DisplayName("Test valid rating strings in CourseModel")
    void testCourseRating(String rating) {
        CourseModel course = new CourseModel();
        course.setRating(rating);
        assertEquals(rating, course.getRating());
    }

    static Stream<String> courseRatingProvider() {
        return Stream.of("4.5", "3.0", "5.0");
    }

    // ========== PlayListModel Tests ==========

    @ParameterizedTest
    @CsvSource({
            "Intro Lesson, https://video.com/intro.mp4",
            "Lecture 1, http://example.com/lec1.mp4"
    })
    @DisplayName("Test PlayListModel Title and Video URL")
    void testPlayListModelTitleAndUrl(String title, String url) {
        PlayListModel playlist = new PlayListModel();
        playlist.setTitle(title);
        playlist.setVideoUrl(url);
        assertEquals(title, playlist.getTitle());
        assertEquals(url, playlist.getVideoUrl());
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    @DisplayName("Test PlayListModel Enabled Flag")
    void testPlaylistEnabled(String enabled) {
        PlayListModel model = new PlayListModel();
        model.setEnabled(enabled);
        assertEquals(enabled, model.getEnabled());
    }

    // ========== UserModel Tests ==========

    @ParameterizedTest
    @CsvSource({
            "Alice, alice@example.com",
            "Bob, bob@mail.com",
            "'', ''"
    })
    @DisplayName("Test UserModel Name and Email")
    void testUserModelNameEmail(String name, String email) {
        UserModel user = new UserModel();
        user.setName(name);
        user.setEmail(email);
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = {"password123", "secret", "admin1234"})
    @DisplayName("Test UserModel Password")
    void testUserPassword(String password) {
        UserModel user = new UserModel();
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @ParameterizedTest
    @MethodSource("profileUrlProvider")
    @DisplayName("Test UserModel Profile Image URLs")
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
}
