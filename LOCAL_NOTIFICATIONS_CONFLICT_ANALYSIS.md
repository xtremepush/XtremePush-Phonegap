# iOS Local Notifications Conflict Analysis

## Problem Summary

Client (Project 1135 - Funstage) is using:
- XtremePush Cordova SDK (master-geo-beacon branch, commit 2735b8b)
- `cordova-plugin-local-notification` v1.2.3

**Issue**: XtremePush is intercepting ALL notifications on iOS, including local notifications from the other plugin. Even when `XPushSwizzlingDisabled` is set, the issue persists.

**Log Evidence**:
```
[XPush] - userNotificationCenter didReceiveNotificationResponse: {
    ...
    meta = {
        plugin = "cordova-plugin-local-notification";
        version = "1.2.3";
    };
    ...
}
```

## Root Cause Analysis

### 1. **The Native SDK is Using UNUserNotificationCenter (iOS 10+)**

The log shows `userNotificationCenter didReceiveNotificationResponse:`, which is the **modern** notification API introduced in iOS 10. This is NOT handled by the Cordova plugin's swizzling code.

### 2. **Current Swizzling Only Covers Deprecated APIs**

In `src/ios/AppDelegate+XtremePush.m`, the plugin only swizzles these **deprecated** methods:
- `application:didReceiveLocalNotification:` (deprecated iOS 10+)
- `application:handleActionWithIdentifier:forLocalNotification:completionHandler:` (deprecated iOS 10+)
- `application:didReceiveRemoteNotification:fetchCompletionHandler:`

These methods are NOT used by modern plugins like `cordova-plugin-local-notification`, which uses the UNUserNotificationCenter framework.

### 3. **XPushSwizzlingDisabled Only Affects AppDelegate Swizzling**

The `XPushSwizzlingDisabled` flag (checked on line 10 of `AppDelegate+XtremePush.m`) only prevents the Cordova plugin from swizzling AppDelegate methods. It does NOT prevent the **native XPush.xcframework** from:
- Setting itself as the `UNUserNotificationCenterDelegate`
- Intercepting `userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:`
- Intercepting `userNotificationCenter:willPresentNotification:withCompletionHandler:`

### 4. **The Problem is in the Native SDK**

The compiled `lib/ios/XPush.xcframework` is directly setting itself as the notification center delegate and processing ALL notifications, regardless of their source.

## Solutions

### Solution 1: Native SDK Filter (Recommended - Requires XPush SDK Update)

**Change Location**: `lib/ios/XPush.xcframework` (native SDK)

**Approach**: Modify the native SDK to check if a notification is from XtremePush before processing it.

**Implementation**:
```objc
// In XPush.xcframework's UNUserNotificationCenterDelegate implementation
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       didReceiveNotificationResponse:(UNNotificationResponse *)response
                withCompletionHandler:(void (^)(void))completionHandler {

    NSDictionary *userInfo = response.notification.request.content.userInfo;

    // Check if this is an XPush notification
    // XPush notifications typically contain specific keys
    BOOL isXPushNotification = [self isXPushNotification:userInfo];

    if (isXPushNotification) {
        // Process XPush notification
        [self handleXPushNotification:userInfo response:response];
        completionHandler();
    } else {
        // Call original delegate if one was set before XPush
        if (self.originalDelegate &&
            [self.originalDelegate respondsToSelector:@selector(userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:)]) {
            [self.originalDelegate userNotificationCenter:center
                                 didReceiveNotificationResponse:response
                                          withCompletionHandler:completionHandler];
        } else {
            completionHandler();
        }
    }
}

- (BOOL)isXPushNotification:(NSDictionary *)userInfo {
    // XPush notifications have specific identifiers
    // Check for XPush-specific keys in the payload
    return (userInfo[@"xp"] != nil ||
            userInfo[@"xtremepush"] != nil ||
            userInfo[@"aps"][@"xp"] != nil ||
            // Add other XPush-specific markers
            [userInfo[@"gcm.message_id"] containsString:@"xtremepush"]);
}
```

**Pros**:
- Clean solution
- Works for all notification types
- Properly chains delegates

**Cons**:
- Requires native SDK update from XtremePush team
- Not immediately available to client

### Solution 2: Cordova Plugin Swizzling Enhancement (Immediate Workaround)

**Change Location**: `src/ios/AppDelegate+XtremePush.m`

**Approach**: Add swizzling for UNUserNotificationCenterDelegate methods in the Cordova plugin.

**Implementation**:

Add to the `+load` method in `AppDelegate+XtremePush.m`:

```objc
#import <UserNotifications/UserNotifications.h>

+ (void)load {

    BOOL disabled = [[[[NSBundle mainBundle] infoDictionary] objectForKey:@"XPushSwizzlingDisabled"] boolValue];
    if (disabled) {
        return;
    }

    // Existing AppDelegate swizzling...
    [self swizzleMethodWithClass: [self class]
                originalSelector: @selector(application:didRegisterForRemoteNotificationsWithDeviceToken:)
             andReplacedSelector: @selector(xtremepushReplaced:didRegisterForRemoteNotificationsWithDeviceToken:)
                andAddedSelector: @selector(xtremepushAdded:didRegisterForRemoteNotificationsWithDeviceToken:)];

    // ... other existing swizzles ...

    // NEW: Swizzle UNUserNotificationCenterDelegate methods
    // This requires that AppDelegate implements UNUserNotificationCenterDelegate
    [self swizzleMethodWithClass: [self class]
                originalSelector: @selector(userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:)
             andReplacedSelector: @selector(xtremepushReplaced_userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:)
                andAddedSelector: @selector(xtremepushAdded_userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:)];

    [self swizzleMethodWithClass: [self class]
                originalSelector: @selector(userNotificationCenter:willPresentNotification:withCompletionHandler:)
             andReplacedSelector: @selector(xtremepushReplaced_userNotificationCenter:willPresentNotification:withCompletionHandler:)
                andAddedSelector: @selector(xtremepushAdded_userNotificationCenter:willPresentNotification:withCompletionHandler:)];
}

// NEW: Implement swizzled methods for UNUserNotificationCenter
- (void)xtremepushReplaced_userNotificationCenter:(UNUserNotificationCenter *)center
                       didReceiveNotificationResponse:(UNNotificationResponse *)response
                                withCompletionHandler:(void (^)(void))completionHandler {

    NSDictionary *userInfo = response.notification.request.content.userInfo;

    // Check if this is an XPush notification
    BOOL isXPushNotification = [self isXPushNotification:userInfo];

    if (isXPushNotification && Storage.store.isRegistered) {
        // Let XPush handle it
        // Note: This assumes XPush SDK has a method to handle UNNotificationResponse
        // You may need to convert to the old format or use a different XPush method
        [XPush handleNotificationResponse:response];
    }

    // Always call the original implementation to allow other plugins to work
    [self xtremepushReplaced_userNotificationCenter:center
                         didReceiveNotificationResponse:response
                                  withCompletionHandler:completionHandler];
}

- (void)xtremepushAdded_userNotificationCenter:(UNUserNotificationCenter *)center
                    didReceiveNotificationResponse:(UNNotificationResponse *)response
                             withCompletionHandler:(void (^)(void))completionHandler {

    NSDictionary *userInfo = response.notification.request.content.userInfo;

    // Check if this is an XPush notification
    BOOL isXPushNotification = [self isXPushNotification:userInfo];

    if (isXPushNotification && Storage.store.isRegistered) {
        [XPush handleNotificationResponse:response];
    }

    completionHandler();
}

// Helper method to identify XPush notifications
- (BOOL)isXPushNotification:(NSDictionary *)userInfo {
    // Check for XPush-specific keys in the notification payload
    // Adjust these checks based on actual XPush notification structure
    return (userInfo[@"xp"] != nil ||
            userInfo[@"xtremepush"] != nil ||
            userInfo[@"aps"][@"xp"] != nil);
}

// Similar implementation for willPresentNotification...
```

**Pros**:
- Can be implemented in the Cordova plugin immediately
- Allows filtering of XPush vs non-XPush notifications

**Cons**:
- Complex and may not work if XPush SDK directly sets the UNUserNotificationCenter delegate
- Requires identifying XPush notification payloads correctly
- The native XPush SDK might still override this

### Solution 3: Expose SDK Configuration Option (Recommended for Client)

**Change Location**: `src/ios/XtremePushPlugin.m` + `lib/ios/XPush.xcframework`

**Approach**: Add a new configuration option to disable XPush's automatic notification handling.

**Implementation**:

1. Add to `www/xtremepush.js` documentation:
```javascript
ios: {
    disableNotificationHandling: true  // NEW option
}
```

2. In `src/ios/XtremePushPlugin.m`, in the `register:` method:
```objc
id disableNotificationHandling = [iosOptions objectForKey:@"disableNotificationHandling"];
if (disableNotificationHandling != nil && [disableNotificationHandling boolValue]) {
    [XPush setNotificationHandlingEnabled:NO];
}
```

3. Requires native XPush SDK to expose `setNotificationHandlingEnabled:` method.

## Recommended Action Plan

### Immediate (For Client):

1. **Verify XPush Notification Payload Structure**:
   - Ask client to log the payload of actual XPush push notifications
   - Identify unique keys that distinguish XPush notifications from local notifications

2. **Temporary Workaround - Don't Initialize XPush Until Needed**:
   - Delay calling `XtremePush.register()` until after local notifications are set up
   - This might not work if both need to be active simultaneously

3. **Test with Manual Delegate Implementation**:
   ```objc
   // In AppDelegate
   - (void)userNotificationCenter:(UNUserNotificationCenter *)center
          didReceiveNotificationResponse:(UNNotificationResponse *)response
                   withCompletionHandler:(void (^)(void))completionHandler {

       NSDictionary *userInfo = response.notification.request.content.userInfo;

       // Check for local notification plugin marker
       if (userInfo[@"meta"][@"plugin"]) {
           // Handle via local notification plugin
           // Don't call XPush methods
       } else {
           // Assume XPush and handle accordingly
       }

       completionHandler();
   }
   ```

### Long-term (Requires XPush Team):

1. **Update Native SDK** to implement Solution 1 (filtering in the native SDK)
2. **Expose Configuration API** to disable/enable notification handling
3. **Chain Delegates Properly** - Store original delegate before setting XPush as delegate
4. **Document Notification Payload Structure** so plugins can identify XPush notifications

## Files to Investigate

1. `lib/ios/XPush.xcframework` - Check how it sets up UNUserNotificationCenterDelegate
2. Identify XPush notification payload markers by logging actual push notifications
3. Check if XPush SDK has any existing APIs to disable notification handling

## Questions for XPush SDK Team

1. Does the XPush native SDK check if notifications are from XPush before processing them?
2. Is there an API to disable automatic notification handling in the SDK?
3. Does the SDK store the original UNUserNotificationCenterDelegate before setting itself?
4. What are the unique payload keys that identify an XPush notification?
5. Can the SDK be updated to chain delegates properly for multi-provider support?
