# Edit Profile Feature Implementation

## Overview
A fully functional edit profile section has been implemented for the profile page, matching the app's design theme and style.

## Files Created/Modified

### 1. New Layout File
**File:** `app/src/main/res/layout/dialog_edit_profile.xml`
- Bottom sheet dialog with rounded corners
- Profile picture with change photo option
- Editable fields:
  - Full Name
  - Email Address
  - Phone Number
  - Target Exam (dropdown)
- Action buttons: Cancel and Save Changes

### 2. Updated Fragment
**File:** `app/src/main/java/com/osmiumai/app/ui/profile/ProfileFragment.kt`
- Added `setupEditProfile()` method
- Added `showEditProfileDialog()` method with:
  - Bottom sheet dialog initialization
  - Save functionality that updates profile display
  - Cancel/Close functionality
  - Exam dropdown selection

### 3. Updated Layout
**File:** `app/src/main/res/layout/fragment_profile.xml`
- Added IDs to TextViews:
  - `tvUserName` - for displaying user name
  - `tvUserEmail` - for displaying user email
  - `btnEditProfile` - for the edit profile button

## Design Features

### Consistent Theme
- Background: `#F7F5F3` (app's beige background)
- Card background: `#FFFFFF` (white)
- Text colors: `#1E1E1E` (dark), `#616161` (medium), `#757575` (light)
- Border radius: 16dp (consistent with app cards)
- Spacing: 24dp padding, consistent margins

### UI Components
- Rounded profile picture (80dp circle)
- Outlined input fields with rounded corners
- Dropdown for exam selection
- Two-button layout (Cancel + Save)
- Close button in header

### User Experience
- Bottom sheet dialog for smooth appearance
- Auto-populated fields with current data
- Real-time profile update on save
- Easy dismissal (close button, cancel button, or swipe down)

## How It Works

1. User clicks "Edit profile" button on profile page
2. Bottom sheet dialog slides up from bottom
3. User can:
   - Change profile photo
   - Edit name, email, phone
   - Select target exam from dropdown
4. Click "Save Changes" to update profile
5. Profile display updates immediately
6. Dialog closes automatically

## Styling Consistency

All elements match the app's existing design:
- Same button styles (`bg_btn_outline_gray`, `bg_btn_black`)
- Same input field style (`bg_outline_rounded`)
- Same typography (sizes: 12sp, 13sp, 14sp, 20sp)
- Same color palette
- Same spacing and padding patterns
- Same rounded corners (16dp for cards, 40dp for profile pic)
