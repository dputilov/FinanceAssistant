package com.example.financeassistant.main_window

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.financeassistant.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class FinanceAssistanceFlatTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(FinanceManager::class.java)

    @Test
    fun financeAssistanceFlatTest() {
        val floatingActionButton = onView(
            allOf(
                withId(R.id.PageFlatsFloatingActionButton),
                childAtPosition(
                    allOf(
                        withId(R.id.flatMoutionlayout),
                        withParent(withId(R.id.pager))
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.etFlatName),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    0
                )
            )
        )
        appCompatEditText.perform(scrollTo(), replaceText("�����"), closeSoftKeyboard())

        pressBack()

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.etFlatName), withText("�����"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    0
                )
            )
        )
        appCompatEditText2.perform(scrollTo(), replaceText("������ "))

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.etFlatName), withText("������ "),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(closeSoftKeyboard())

        pressBack()

        val appCompatSpinner = onView(
            allOf(
                withId(R.id.spType),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        4
                    ),
                    0
                )
            )
        )
        appCompatSpinner.perform(scrollTo(), click())

//        val appCompatCheckedTextView = onData(anything())
//            .inAdapterView(
//                childAtPosition(
//                    withClassName(`is`("android.widget.PopupWindow$PopupBackgroundView")),
//                    0
//                )
//            )
//            .atPosition(2)
//        appCompatCheckedTextView.perform(click())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.etAdres),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.adresLayout),
                        1
                    ),
                    0
                )
            )
        )
        appCompatEditText4.perform(scrollTo(), replaceText("�����"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.etParam),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    0
                )
            )
        )
        appCompatEditText5.perform(scrollTo(), replaceText("�����������"), closeSoftKeyboard())

        val appCompatSpinner2 = onView(
            allOf(
                withId(R.id.spCredit),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        9
                    ),
                    0
                )
            )
        )
        appCompatSpinner2.perform(scrollTo(), click())

        val appCompatEditText6 = onView(
            allOf(
                withId(R.id.etSumma),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        11
                    ),
                    0
                )
            )
        )
        appCompatEditText6.perform(scrollTo(), replaceText("3333333"), closeSoftKeyboard())

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.action_OK), withContentDescription("OK"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar_actionbar_item),
                        3
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        val actionMenuItemView2 = onView(
            allOf(
                withId(R.id.action_OK), withContentDescription("OK"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar_actionbar_item),
                        3
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView2.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                    && view == parent.getChildAt(position)
            }
        }
    }
}
