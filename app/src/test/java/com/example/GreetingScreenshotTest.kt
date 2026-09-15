package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.SeatInfo
import com.example.ui.components.SuzukiSeatSelector
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleSeats = listOf(
      SeatInfo(1, "الأمام", isSelected = true, isOccupied = false),
      SeatInfo(2, "وسط يمين", isSelected = false, isOccupied = true),
      SeatInfo(3, "وسط", isSelected = false, isOccupied = false),
      SeatInfo(4, "وسط يسار", isSelected = false, isOccupied = false),
      SeatInfo(5, "خلف يمين", isSelected = false, isOccupied = false),
      SeatInfo(6, "خلف وسط", isSelected = false, isOccupied = false),
      SeatInfo(7, "خلف يسار", isSelected = false, isOccupied = false)
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        SuzukiSeatSelector(seats = sampleSeats, onSeatClick = {})
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
