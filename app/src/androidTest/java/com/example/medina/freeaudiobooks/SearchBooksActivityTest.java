package com.example.medina.freeaudiobooks;

import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

public class SearchBooksActivityTest {

    @Rule
    public ActivityTestRule<SearchBooksActivity> searchBooksActivityActivityTestRule=
            new ActivityTestRule<>(SearchBooksActivity.class);

    @Before
    public void setUp() {
        searchBooksActivityActivityTestRule.getActivity();
        Intents.init();
    }
    @Test
    public void checkIfBookActivityGetsData() {
        onView(withId(R.id.listView)).perform(click());
        onView(withId(R.id.bookDescription)).check(matches(isDisplayed()));
    }
}