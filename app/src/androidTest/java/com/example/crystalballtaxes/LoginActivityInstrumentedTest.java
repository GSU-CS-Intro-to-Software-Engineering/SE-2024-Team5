package com.example.crystalballtaxes;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        // Initialize Firebase if needed
        FirebaseApp.initializeApp(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    public void testLoginUIElements() {
        // Verify all UI elements are displayed
        onView(withId(R.id.emailEditTxt)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordEditTxt)).check(matches(isDisplayed()));
        onView(withId(R.id.loginBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.signUpBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.forgotPassTxt)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyEmailValidation() {
        // Enter only password and try to login
        onView(withId(R.id.passwordEditTxt))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.loginBtn)).perform(click());

        // Verify error message
        onView(withId(R.id.emailEditTxt))
                .check(matches(hasErrorText("Email cannot be empty")));
    }

    @Test
    public void testEmptyPasswordValidation() {
        // Enter only email and try to login
        onView(withId(R.id.emailEditTxt))
                .perform(typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.loginBtn)).perform(click());

        // Verify error message
        onView(withId(R.id.passwordEditTxt))
                .check(matches(hasErrorText("Password cannot be empty")));
    }

    @Test
    public void testLoginInputInteraction() {
        // Type email
        onView(withId(R.id.emailEditTxt))
                .perform(typeText("test@example.com"), closeSoftKeyboard());

        // Type password
        onView(withId(R.id.passwordEditTxt))
                .perform(typeText("password123"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.loginBtn)).perform(click());
    }

    @Test
    public void testForgotPasswordFlow() {
        // Enter email first
        onView(withId(R.id.emailEditTxt))
                .perform(typeText("test@example.com"), closeSoftKeyboard());

        // Click forgot password
        onView(withId(R.id.forgotPassTxt)).perform(click());
    }

    @Test
    public void testNavigationToSignUp() {
        // Click sign up button
        onView(withId(R.id.signUpBtn)).perform(click());
    }
}