# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Cordova/PhoneGap plugin (com.xtreme.plugins.XtremePush) that integrates the Xtremepush marketing platform into iOS and Android applications. The plugin supports push notifications, in-app messaging, inbox functionality, location services, iBeacon scanning, user event tracking, and loyalty widget integration.

**Current Version**: 4.7.0

### Loyalty Feature
The plugin includes loyalty widget integration with two implementation options:
- **Built-in webview**: Use `openLoyalty()` to open the widget in the native SDK's webview
- **Custom webview**: Use `getLoyaltyUrl()` to load the widget in a custom webview with full control

See **LOYALTY_FEATURE_GUIDE.md** for complete integration instructions.

## Plugin Architecture

The plugin follows the standard Cordova plugin architecture with three main layers:

### 1. JavaScript Bridge (www/xtremepush.js)
- Exposes all plugin methods to JavaScript/Cordova applications
- Uses Cordova's exec() to communicate with native platforms
- Single entry point for all XtremePush functionality

### 2. iOS Native Implementation (src/ios/)
- **XtremePushPlugin.m/h**: Main plugin class implementing CDVPlugin
- **AppDelegate+XtremePush.m/h**: Category that uses method swizzling to intercept AppDelegate notification methods
- **Storage.m/h**: Local storage utilities
- Native SDK: lib/ios/XPush.xcframework (embedded framework)

**iOS Swizzling**: The plugin automatically swizzles AppDelegate methods to intercept push notification callbacks. This can be disabled by setting `XPushSwizzlingDisabled` to `true` in Info.plist.

### 3. Android Native Implementation (src/android/)
- **XtremePushPlugin.java**: Main plugin class extending CordovaPlugin, implements multiple listeners (InboxBadgeUpdateListener, MessageResponseListener, DeeplinkListener, InboxListListener)
- **XPush.gradle**: Gradle configuration with Firebase and dependency management
- Native SDK: lib/android/xpush_android_lib/XtremePush_lib.aar

## Plugin Configuration (plugin.xml)

The plugin.xml file defines:
- Platform-specific source files and frameworks
- Android manifest permissions and component declarations (activities, services, receivers)
- iOS framework dependencies
- Gradle build configuration via XPush.gradle

## Key Dependencies

### Android
- Firebase Cloud Messaging (FCM) 25.0.0
- Firebase Auth 24.0.1
- Google Play Services Location 21.3.0
- AltBeacon library 2.19.2 for iBeacon support
- AndroidX Security Crypto for secure storage
- WorkManager for background tasks

### iOS
- XPush.xcframework (proprietary SDK)
- System frameworks: CoreLocation, CoreBluetooth, CoreTelephony, SystemConfiguration

## Development Commands

### Installing the Plugin
To install this plugin in a Cordova project:
```bash
cordova plugin add /path/to/XtremePush-Phonegap
# or from GitHub:
cordova plugin add https://github.com/xtremepush/XtremePush-Phonegap
```

### Building for Platforms
```bash
# Add platforms (if not already added)
cordova platform add android
cordova platform add ios

# Build Android
cordova build android

# Build iOS
cordova build ios
```

### Testing Changes
No automated test suite exists. Testing requires:
1. Installing the plugin in a test Cordova app
2. Running on actual devices or emulators
3. Verifying push notifications, inbox, location, and event tracking features

### Updating Native SDKs
- **iOS**: Replace lib/ios/XPush.xcframework with the new version
- **Android**: Replace lib/android/xpush_android_lib/XtremePush_lib.aar with the new version

## Common Modification Patterns

### Adding New JavaScript Methods
1. Add method to XtremePush.prototype in www/xtremepush.js
2. Implement corresponding method in src/ios/XtremePushPlugin.m
3. Implement corresponding method in src/android/XtremePushPlugin.java
4. Ensure method signatures match between platforms

### Modifying Android Permissions
Edit the AndroidManifest.xml config-file section in plugin.xml (lines 38-55)

### Modifying iOS Frameworks
Add or remove framework entries in the iOS platform section of plugin.xml (lines 160-168)

## Platform-Specific Notes

### iOS
- Method swizzling intercepts all notification-related AppDelegate methods
- The plugin loads on startup (onload="true" in plugin.xml)
- Uses categories to extend AppDelegate without modifying the host app
- Push notification handling is automatic via swizzled methods

### Android
- Uses Firebase Cloud Messaging for push notifications
- Requires google-services.json in the host app for Firebase configuration
- The plugin registers multiple BroadcastReceivers for push, location, and beacon events
- Supports both foreground and background notification handling
- Implements LoyaltyTokenHandler interface for loyalty widget token refresh

## Loyalty Feature Implementation

The loyalty feature provides JavaScript methods to integrate the XtremePush loyalty widget:

**JavaScript Methods:**
- `setLoyaltyEndpoint(endpoint)` - Configure loyalty endpoint URL
- `openLoyalty(path, params)` - Open widget in built-in webview
- `getLoyaltyUrl(path, params, callback)` - Get URL for custom webview
- `setLoyaltyTokenHandler(callbackName)` - Register token expiration callback
- `setLoyaltyToken(token)` - Provide fresh JWT token
- `getLoyaltyWebViewInterface()` - Get JavaScript bridge code for custom webviews

**Custom Webview Support:**
For custom webview implementations, inject the JavaScript interface returned by `getLoyaltyWebViewInterface()` to enable:
- Automatic token expiration detection and refresh
- Deeplink handling from loyalty widget to app

The injected interface creates a `window.XtremePush` object that bridges loyalty widget messages back to the Cordova plugin's token and deeplink handlers.

## Branch Variants

The plugin has special branches for different feature sets:
- **master+attributions**: Includes IDFA/Ad ID collection frameworks
- **master-geo-beacon**: Removes geo-location and beacon frameworks for apps that don't need them

## Examples

The examples/ directory contains sample integrations:
- examples/android/: Android Cordova project example
- examples/ios/: iOS Cordova project example
- examples/ionic-app/: Ionic framework integration example
