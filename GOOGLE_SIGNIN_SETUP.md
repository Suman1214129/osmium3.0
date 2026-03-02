# Google Sign-In Setup Guide for Osmium App

## Overview
This guide will help you set up Google Sign-In for your Android app.

## Step 1: Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Note your project name

## Step 2: Configure OAuth Consent Screen

1. In Google Cloud Console, go to **APIs & Services** > **OAuth consent screen**
2. Select **External** user type (or Internal if using Google Workspace)
3. Fill in the required information:
   - App name: Osmium
   - User support email: Your email
   - Developer contact email: Your email
4. Click **Save and Continue**
5. Add scopes (optional for basic sign-in)
6. Click **Save and Continue**

## Step 3: Create OAuth 2.0 Credentials

### Create Web Client ID (Required)
1. Go to **APIs & Services** > **Credentials**
2. Click **Create Credentials** > **OAuth client ID**
3. Select **Web application**
4. Name it "Osmium Web Client"
5. Click **Create**
6. **IMPORTANT**: Copy the **Client ID** - you'll need this!

### Create Android Client ID (Optional but Recommended)
1. Click **Create Credentials** > **OAuth client ID** again
2. Select **Android**
3. Name it "Osmium Android"
4. Get your SHA-1 fingerprint:
   
   **For Debug Build:**
   ```bash
   cd C:\Users\suman\OneDrive\Desktop\osmiumapp-master
   keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
   ```
   
   **For Release Build:**
   ```bash
   keytool -list -v -keystore path\to\your\release.keystore -alias your_alias
   ```

5. Copy the SHA-1 fingerprint from the output
6. Paste it in the Google Cloud Console
7. Enter package name: `com.osmiumai.app`
8. Click **Create**

## Step 4: Update Your App Code

1. Open `GoogleAuthHelper.kt`
2. Replace `YOUR_WEB_CLIENT_ID.apps.googleusercontent.com` with your actual Web Client ID from Step 3
3. The line should look like:
   ```kotlin
   private const val WEB_CLIENT_ID = "123456789-abcdefghijklmnop.apps.googleusercontent.com"
   ```

## Step 5: Sync Gradle

1. Open Android Studio
2. Click **File** > **Sync Project with Gradle Files**
3. Wait for the sync to complete

## Step 6: Test the Implementation

1. Build and run the app
2. Navigate to the Welcome Screen 4
3. Click "Continue with Google"
4. Select a Google account
5. Grant permissions
6. You should be signed in and redirected to MainActivity

## Features Implemented

### 1. Google Sign-In on Welcome Screen
- Users can sign in with Google from the welcome screen
- Seamless authentication flow

### 2. Google Sign-In on Signup Screen
- Alternative entry point for Google authentication
- Consistent user experience

### 3. User Data Storage
The following user data is saved to SharedPreferences:
- `user_email`: User's email address
- `user_name`: User's display name
- `user_profile_pic`: Profile picture URL
- `is_logged_in`: Login status (true/false)
- `login_method`: "google" for Google sign-in

### 4. Error Handling
- Proper error messages for failed sign-in attempts
- User-friendly Toast notifications

## Retrieving User Data

To access the signed-in user's data anywhere in your app:

```kotlin
val prefs = getSharedPreferences("OsmiumPrefs", Context.MODE_PRIVATE)
val isLoggedIn = prefs.getBoolean("is_logged_in", false)
val userName = prefs.getString("user_name", "")
val userEmail = prefs.getString("user_email", "")
val profilePic = prefs.getString("user_profile_pic", "")
val loginMethod = prefs.getString("login_method", "")
```

## Sign Out Implementation

To implement sign-out functionality:

```kotlin
val prefs = getSharedPreferences("OsmiumPrefs", Context.MODE_PRIVATE)
prefs.edit().clear().apply()
// Redirect to login screen
```

## Troubleshooting

### Issue: "Sign-in failed: 16"
- **Solution**: Make sure you've added the correct SHA-1 fingerprint to Google Cloud Console

### Issue: "Sign-in failed: 10"
- **Solution**: Check that your Web Client ID is correct in GoogleAuthHelper.kt

### Issue: "Sign-in failed: Developer Error"
- **Solution**: Verify package name matches in Google Cloud Console and your app

### Issue: Credentials not showing
- **Solution**: Make sure Google Play Services is updated on your device/emulator

## Security Notes

1. **Never commit your Web Client ID to public repositories** if it's sensitive
2. Use environment variables or local.properties for production
3. Implement proper backend verification of ID tokens for production apps
4. Consider adding Firebase Authentication for enhanced security

## Next Steps

1. Implement backend verification of Google ID tokens
2. Add user profile management
3. Implement sign-out functionality
4. Add account linking (if user signs up with email then tries Google)
5. Handle edge cases (network errors, account conflicts, etc.)

## Additional Resources

- [Google Sign-In Documentation](https://developers.google.com/identity/sign-in/android/start)
- [Credential Manager API](https://developer.android.com/training/sign-in/credential-manager)
- [OAuth 2.0 for Mobile Apps](https://developers.google.com/identity/protocols/oauth2/native-app)
