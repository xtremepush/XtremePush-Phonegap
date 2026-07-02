# iOS SDK Source Code Analysis - Notification Handling

## Critical Findings from Source Code Review

Location: `~/Documents/ios/Framework/code/Managers/Legacy/XPPushManager.m`

---

## Finding #1: SDK Always Sets Itself as Delegate ⚠️

**Location**: `XPPushManager.m` lines 56-59

```objc
if (UNUserNotificationCenter.class) {
    UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
    center.delegate = self;  // ← ALWAYS sets itself as delegate
}
```

**Impact**:
- The SDK sets itself as `UNUserNotificationCenterDelegate` in the `init` method
- This happens **regardless** of any configuration options
- There is **NO way to prevent this** through configuration

---

## Finding #2: enableManualPushRegistration Does NOT Prevent Delegate Setting ❌

**Location**: `XPPushManager.m` lines 82-91

```objc
- (void)applicationDidFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [self resetBadgeNumber];

    if (!_enableManualPushRegistration) {  // ← ONLY affects permission request
        if (!UIApplication.isRegisteredForRemoteNotifications) {
            [self registrationWithToken:nil];
        } else {
            [self registerForRemoteNotificationTypes:XPNotificationType_Alert |
                                                      XPNotificationType_Badge |
                                                      XPNotificationType_Sound
                            provisionalAuthorization:YES];
        }
    }
    // ... delegate is ALREADY set in init, regardless of this flag
}
```

**What `enableManualPushRegistration` does**:
- ✅ Prevents automatic push permission request
- ✅ Allows manual control of when to ask for permissions
- ❌ Does NOT prevent delegate setting
- ❌ Does NOT disable notification processing

---

## Finding #3: Partial Notification Filtering Exists ⚠️

### didReceiveNotificationResponse (User Taps Notification) - ✅ HAS FILTERING

**Location**: `XPPushManager.m` lines 361-372

```objc
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
didReceiveNotificationResponse:(UNNotificationResponse *)response
         withCompletionHandler:(void (^)(void))completionHandler {

    [[XPCore defaultInstance] log:[NSString stringWithFormat:
        @"userNotificationCenter didReceiveNotificationResponse: %@ withActionIdentifier: %@",
        response.notification.request.content.userInfo, response.actionIdentifier]];

    if (!response.notification.request.content.userInfo[@"xpush"]) {
        return;  // ← FILTERING: Returns early for non-XPush notifications
    }

    [self.pushActionManager handlePushPayload:response.notification.request.content.userInfo
                         withActionIdentifier:response.actionIdentifier
                             optionalCallback:completionHandler];
}
```

**Analysis**:
- ✅ Checks for `"xpush"` key in notification payload
- ✅ Returns immediately if key is missing
- ✅ Does NOT process non-XPush notifications
- ❌ **BUT** still logs ALL notifications before checking!

### willPresentNotification (Notification Arrives in Foreground) - ❌ NO FILTERING

**Location**: `XPPushManager.m` lines 341-359

```objc
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {

    [[XPCore defaultInstance] log:[NSString stringWithFormat:
        @"userNotificationCenter willPresentNotification: %@",
        notification.request.content.userInfo]];

    if ([XPCore defaultInstance].inboxEnabled) {
        [[XPCore defaultInstance].inboxManager loadBadgeWithCompletion:nil];
    }

    [self.pushActionManager handlePushReceivedInForeground:notification.request.content.userInfo];

    XPMessageResponse* res = [XPMessageResponse messageFromPushPayload:notification.request.content.userInfo
                                                      actionIdentifier:nil];

    XPNotificationType types = self.foregroundNotificationOptions(res.message);
    UNNotificationPresentationOptions options = [self.notificationTypesManager unPresentationOptions:types];
    completionHandler(options);
}
```

**Analysis**:
- ❌ NO filtering - processes ALL notifications
- ❌ Logs all notifications
- ❌ Attempts to handle all notifications as XPush messages
- ❌ Calls `handlePushReceivedInForeground` for ALL notifications
- ❌ Calls `messageFromPushPayload` for ALL notifications

**This is the problem!**

---

## Finding #4: SDK Warns But Doesn't Prevent Delegate Override

**Location**: `XPPushManager.m` lines 534-541

```objc
- (void) verifyUNDelegate {
    if (!UNUserNotificationCenter.class) { return; }

    UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
    if (center.delegate != self) {
        [XPCore.defaultInstance log:@"WARNING. We advice not to use your own implementation of UNUserNotificationCenter's delegate. In case you need it, please make sure to forward UNUserNotificationCenter's callback to XPush SDK"];
    }
}
```

**Analysis**:
- ⚠️ Only logs a warning if delegate changes
- ❌ Does NOT prevent delegate from being changed
- ✅ Acknowledges that users might need their own delegate
- ✅ Suggests forwarding callbacks to XPush

---

## Root Cause of Client's Issue

Based on the source code, here's what's happening to the client:

### Issue 1: Delegate Override
1. XPush SDK sets itself as delegate in `init` (line 58)
2. Client tries to set AppDelegate as delegate (following docs)
3. XPush's `verifyUNDelegate` shows warning
4. Both try to handle notifications

### Issue 2: willPresentNotification Processes All Notifications
1. Local notification arrives while app is in foreground
2. SDK's `willPresentNotification` is called (no filtering)
3. SDK logs: `"userNotificationCenter willPresentNotification: {...}"`
4. SDK tries to process it as XPush notification
5. SDK calls foreground notification handler
6. Local notification plugin never gets the callback

### Issue 3: didReceiveNotificationResponse Logs Before Filtering
1. User taps local notification
2. SDK's `didReceiveNotificationResponse` is called
3. SDK logs: `"userNotificationCenter didReceiveNotificationResponse: {...}"` (line 365)
4. **THEN** checks for `xpush` key and returns (line 367)
5. Local notification plugin gets the callback (because of return)

**This explains the client's logs!**
```
[XPush] - userNotificationCenter didReceiveNotificationResponse: {
    meta = {
        plugin = "cordova-plugin-local-notification";
    };
}
```

The SDK logs the local notification before checking if it should process it.

---

## Why Documentation Approach Won't Work

### Problem 1: Can't Prevent Delegate Setting
```javascript
// This won't help:
XtremePush.register({
    ios: {
        enableManualPushRegistration: true  // Only affects permission request, not delegate
    }
});
```

### Problem 2: SDK Still Processes Foreground Notifications
Even if you:
1. Set `XPushSwizzlingDisabled: true`
2. Use `enableManualPushRegistration: true`
3. Implement AppDelegate delegate methods

**The SDK still:**
- Sets itself as delegate
- Processes ALL foreground notifications (`willPresentNotification`)
- Logs ALL notifications before filtering (`didReceiveNotificationResponse`)

---

## Solutions

### Solution 1: Override Delegate AFTER SDK Initialization ✅

**This should work:**

```objc
// In AppDelegate.m
- (BOOL)application:(UIApplication *)application
didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    // Let Cordova and XPush initialize
    [super application:application didFinishLaunchingWithOptions:launchOptions];

    // Override delegate AFTER XPush has initialized
    // XPush will have already set itself, but we take control back
    dispatch_async(dispatch_get_main_queue(), ^{
        [UNUserNotificationCenter currentNotificationCenter].delegate = self;
        NSLog(@"AppDelegate took control of notification center delegate");
    });

    return YES;
}

// Implement delegate methods with filtering
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       didReceiveNotificationResponse:(UNNotificationResponse *)response
                withCompletionHandler:(void (^)(void))completionHandler {

    NSDictionary *userInfo = response.notification.request.content.userInfo;

    // Check if it's an XPush notification
    if (userInfo[@"xpush"] != nil) {
        // Forward to XPush SDK
        [[XPCore defaultInstance].pushManager userNotificationCenter:center
                                      didReceiveNotificationResponse:response
                                               withCompletionHandler:completionHandler];
    } else {
        // Let local notification plugin handle it
        // Don't call XPush - just call completion handler
        completionHandler();
    }
}

- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {

    NSDictionary *userInfo = notification.request.content.userInfo;

    // Check if it's an XPush notification
    if (userInfo[@"xpush"] != nil) {
        // Forward to XPush SDK
        [[XPCore defaultInstance].pushManager userNotificationCenter:center
                                             willPresentNotification:notification
                                               withCompletionHandler:completionHandler];
    } else {
        // Show local notification
        completionHandler(UNNotificationPresentationOptionAlert |
                         UNNotificationPresentationOptionSound |
                         UNNotificationPresentationOptionBadge);
    }
}
```

**Why this works:**
- Takes delegate control back from XPush
- Implements proper filtering for BOTH methods
- Forwards XPush notifications to the SDK
- Lets local notifications work normally

### Solution 2: SDK Fix (Recommended for XtremePush Team) 🔧

**File**: `XPPushManager.m`

**Change 1**: Add filtering to `willPresentNotification` (line 341)

```objc
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {

    NSDictionary *userInfo = notification.request.content.userInfo;

    // ADD FILTERING
    if (!userInfo[@"xpush"]) {
        // Not an XPush notification - don't process
        // Call completion with default options or let other handler deal with it
        if (self.previousDelegate &&
            [self.previousDelegate respondsToSelector:@selector(userNotificationCenter:willPresentNotification:withCompletionHandler:)]) {
            [self.previousDelegate userNotificationCenter:center
                                 willPresentNotification:notification
                                   withCompletionHandler:completionHandler];
        } else {
            // Default: show the notification
            completionHandler(UNNotificationPresentationOptionAlert |
                            UNNotificationPresentationOptionSound |
                            UNNotificationPresentationOptionBadge);
        }
        return;
    }

    // Original XPush processing code
    [[XPCore defaultInstance] log:[NSString stringWithFormat:@"userNotificationCenter willPresentNotification: %@", userInfo]];
    // ... rest of existing code
}
```

**Change 2**: Store previous delegate (line 56)

```objc
if (UNUserNotificationCenter.class) {
    UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
    self.previousDelegate = center.delegate;  // Store before overriding
    center.delegate = self;
}
```

**Change 3**: Add configuration option

```objc
// XPPushManager.h
@property (nonatomic, assign) BOOL processOnlyXPushNotifications;

// XPPushManager.m - update filtering logic
if (self.processOnlyXPushNotifications && !userInfo[@"xpush"]) {
    return; // Don't process non-XPush notifications
}
```

---

## Testing the Solution

### Test 1: Verify Delegate Ownership

```objc
dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 2 * NSEC_PER_SEC),
               dispatch_get_main_queue(), ^{
    id delegate = [UNUserNotificationCenter currentNotificationCenter].delegate;
    NSLog(@"Current delegate: %@", delegate);
    // Should log: "Current delegate: <AppDelegate: 0x...>"
});
```

### Test 2: Send Local Notification

```objc
// Should NOT see "[XPush]" logs for local notifications
// Should see your own logs
```

### Test 3: Send XPush Notification

```objc
// Should see "[XPush]" logs
// Should still process correctly
```

---

## Answers to Original Questions

### Q1: Does `enableManualPushRegistration` prevent delegate setting?
**A: NO** ❌
- It only prevents automatic permission request
- Delegate is set in `init`, before this flag is checked
- No way to prevent delegate setting through configuration

### Q2: Will the documented approach work?
**A: NO** ❌
- Step 1 (disable swizzling) - helps but not enough
- Step 2 (unset delegate) - NOT possible with `enableManualPushRegistration`
- Step 3 (custom handling) - Required, but must OVERRIDE delegate

### Q3: Why is client still seeing XPush logs?
**A:**
- SDK logs ALL notifications before filtering (line 365)
- SDK processes ALL foreground notifications (no filtering in `willPresentNotification`)
- SDK is set as delegate and intercepts everything

### Q4: What's the correct solution?
**A:**
1. Set `XPushSwizzlingDisabled: true`
2. Override delegate AFTER XPush initialization
3. Implement both delegate methods with filtering
4. Forward XPush notifications to SDK
5. Handle local notifications normally

---

## Recommendation for Client

Use Solution 1 (Override Delegate) immediately while waiting for SDK fix.

```objc
// Info.plist
<key>XPushSwizzlingDisabled</key>
<true/>

// AppDelegate.m
- (BOOL)application:(UIApplication *)application
didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [super application:application didFinishLaunchingWithOptions:launchOptions];

    // Take control of delegate AFTER XPush initialization
    dispatch_async(dispatch_get_main_queue(), ^{
        [UNUserNotificationCenter currentNotificationCenter].delegate = self;
    });

    return YES;
}

// Implement delegate methods with filtering (as shown in Solution 1)
```

---

## Recommendation for SDK Team

1. Add filtering to `willPresentNotification` method
2. Store and chain to previous delegate
3. Add `processOnlyXPushNotifications` configuration option
4. Update documentation with correct multi-provider setup
5. Add unit tests for multi-provider scenarios
