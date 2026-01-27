
# ShakeLog Android SDK

**ShakeLog** is a powerful bug reporting SDK for Android applications. It allows users and QA testers to report bugs instantly by simply **shaking the device**. The SDK captures the screen, collects device logs, tracks user steps (breadcrumbs), and uploads everything to the ShakeLog Dashboard.

## Features

-   **Shake to Report:** Automatically detects aggressive shaking to trigger the reporting flow.
    
-   **Smart Screenshot:** Captures the current screen state instantly.
    
-   **Built-in Editor:** Allows users to draw, annotate, and highlight issues directly on the screenshot.
    
-   **Breadcrumbs & Logs:** Automatically tracks user journey (screen transitions) and system events leading up to the crash.
    
-   **Network Logging:** Intercepts and records HTTP requests/responses (using OkHttp) to debug API errors.
    
-   **Device Metadata:** Automatically collects OS version, battery level, device model, and screen resolution.
    

## Installation

### Step 1. Add the JitPack repository

Add it in your root `settings.gradle.kts` at the end of repositories:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("[https://jitpack.io](https://jitpack.io)") } // <--- Add this line
    }
}

```

### Step 2. Add the dependency

Add the SDK to your module's `build.gradle.kts` dependencies block:

```
dependencies {
    implementation("com.github.OmerMel:shakelog-android:1.0.0")
}

```

## Usage

### 1. Initialization

Initialize the SDK in your `Application` class or main `Activity`. You will need your **Project API Key** from the ShakeLog Portal.

```
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize ShakeLog
        ShakeLog.init(this, "sk_live_YOUR_API_KEY_HERE")
    }
}

```

### 2. User Identification (Optional)

To see _who_ reported the bug, set the user identifier when they log in:

```
// Identify the user by email, ID, or username
ShakeLog.setUserIdentifier("john.doe@example.com")

// Add custom metadata (e.g., plan type, app version)
ShakeLog.setMetadata("Subscription", "Premium")
ShakeLog.setMetadata("Team", "QA-Beta")

```

### 3. Network Logging (Optional but Recommended)

To automatically capture network errors (404, 500) and latency, add the `ShakeLogNetworkInterceptor` to your OkHttp client:

```
val client = OkHttpClient.Builder()
    .addInterceptor(ShakeLogNetworkInterceptor()) // <--- Magic happens here
    .build()

val retrofit = Retrofit.Builder()
    .client(client)
    // ...
    .build()

```

### 4. Manual Breadcrumbs

You can manually log important user actions to help reproduce the bug:

```
btnPurchase.setOnClickListener {
    ShakeLog.log("User clicked 'Buy Now' button")
    // ... your logic
}

```

## Screenshots
<img src="https://github.com/user-attachments/assets/d935889a-a303-4ca7-81df-8a8fcd41506d" alt="WhatsApp Image 1" width="400"/>
<br>Built-in annotation tool allows users to highlight and draw on the screenshot to pinpoint the issue.

<br> <img src="https://github.com/user-attachments/assets/bd5075f3-e428-48bd-ab86-03a011732cdd" alt="WhatsApp Image 2" width="400"/>
<br>A user-friendly form collects bug descriptions and user contact details before submission.

## Technology

The SDK is built using modern Android technologies:

-   **Kotlin** - First-class language.
    
-   **Retrofit & OkHttp** - For robust networking.
    
-   **Firebase Storage** - For efficient image and log file hosting.
    
-   **SensorManager** - For optimized shake detection with debounce logic.

## Documentation
Detailed documentation is available on our [Project Website](https://omermel.github.io/shakelog-android/).

## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/OmerMel/shakelog-android/blob/master/LICENSE) file for details.
