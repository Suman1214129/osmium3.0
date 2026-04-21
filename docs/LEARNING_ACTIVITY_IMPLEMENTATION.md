# Learning Activity Card - Functional Implementation

## Overview
The Learning Activity card in the Profile page now properly responds to dropdown selections with month-wise and year-wise filtering.

## Changes Made

### 1. ProfileFragment.kt

#### Added Data Models
```kotlin
data class LearningActivity(
    val daysActive: Int,
    val dayData: List<DayActivity>
)

data class DayActivity(
    val day: Int,
    val type: DayType
)

enum class DayType {
    NEUTRAL, GREEN, BLUE, ORANGE
}
```

#### Added Activity Data
- **This month**: 18 active days with specific day-by-day activity
- **Last month**: 22 active days with different activity pattern
- **Last 3 months**: 65 active days (90 days total)
- **Last 6 months**: 128 active days (180 days total)
- **This year**: 245 active days (365 days total)

#### Key Functions

**setupMonthDropdown()**
- Handles dropdown menu clicks
- Updates `selectedTimePeriod` variable
- Calls `updateLearningActivity()` to refresh the UI

**updateLearningActivity()**
- Fetches activity data based on selected time period
- Updates the summary text (e.g., "You showed up on 18 days this month 🔥")
- Calls `updateActivityGrid()` to refresh the calendar view

**updateActivityGrid()**
- Dynamically generates day boxes based on activity data
- Applies appropriate styling (Green/Blue/Orange/Neutral) based on activity type
- Removes old views and adds new ones

**generateMonthlyData()**
- Helper function to generate random activity data for longer periods
- Used for "Last 3 months", "Last 6 months", and "This year"

### 2. fragment_profile.xml

#### Added IDs
- `android:id="@+id/tvActivitySummary"` - For the activity summary text
- `android:id="@+id/activityGrid"` - For the GridLayout containing day boxes

## How It Works

1. **Initial Load**: When the profile page loads, it displays "This month" data by default
2. **Dropdown Selection**: User clicks the dropdown and selects a time period
3. **Data Update**: The system fetches corresponding activity data
4. **UI Refresh**: 
   - Summary text updates to show correct days and period
   - Calendar grid regenerates with new day data
   - Each day box shows appropriate color based on activity type

## Activity Types

- **Green**: User logged in
- **Orange**: 3+ test attempts
- **Blue**: Started new course
- **Neutral**: No activity

## Time Period Options

1. **This month** - Current month's daily activity
2. **Last month** - Previous month's daily activity
3. **Last 3 months** - 90 days of activity data
4. **Last 6 months** - 180 days of activity data
5. **This year** - Full year (365 days) of activity data

## Testing

To test the functionality:
1. Open the Profile page
2. Click on the dropdown (currently shows "This month")
3. Select different time periods
4. Observe:
   - Summary text changes
   - Calendar grid updates with new data
   - Day count reflects the selected period

## Future Enhancements

- Connect to actual backend API for real user activity data
- Add smooth animations when switching between time periods
- Implement horizontal scrolling for longer time periods
- Add tooltips showing detailed activity on day click
- Cache activity data to reduce API calls
