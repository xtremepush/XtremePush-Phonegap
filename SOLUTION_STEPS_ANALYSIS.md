# Solution Steps Analysis: Will This Work?

## Proposed Solution Steps

1. ✅ **Disable swizzling** - `XPushSwizzlingDisabled: YES`
2. ❓ **Unset XPush as default UNUserNotificationCenter**
3. ✅ **Custom handle push** - Check for `xpush` key as shown in docs

## Analysis

### Step 1: Disable Swizzling ✅ DOCUMENTED

**What it does:**
- Prevents `AppDelegate+XtremePush.m` from swizzling AppDelegate methods
- Stops automatic injection of deprecated notification handlers

**What it DOESN'T do:**
- Does NOT prevent native `XPush.xcframework` from setting itself as `UNUserNotificationCenterDelegate`
- Does NOT affect the native SDK's behavior at all

**Verdict:** ✅ Necessary but insufficient

---

### Step 2: Unset XPush as Default Delegate ❌ NOT DOCUMENTED

**The Problem:**
The native `XPush.xcframework` automatically sets itself as the notification center delegate when initialized, likely in the `applicationDidFinishLaunchingWithOptions:` method:

```objc
// Inside XPush.xcframework (compiled, not accessible)
[UNUserNotificationCenter currentNotificationCenter].delegate = self;
```

**Looking for API to prevent this:**

✅ **Found:** `enableManualPushRegistration` option
```javascript
XtremePush.register({
    ios: {
        enableManualPushRegistration: true
    }
});
```

Which calls:
```objc
[XPush enableManualPushRegistration:YES];
```

**Question:** Does this prevent the SDK from setting itself as the delegate?

**Documentation Status:** ❌ NOT DOCUMENTED
- No explanation of what `enableManualPushRegistration` actually does
- No API documented to unset XPush as delegate
- No method like `[XPush setNotificationHandlingEnabled:NO]`

---

### Step 3: Custom Handle Push ✅ PARTIALLY DOCUMENTED

**From the docs:**
```objc
if ([userInfo objectForKey:@"xpush"] != nil) {
    [XPush applicationDidReceiveRemoteNotification:userInfo
        fetchCompletionHandler:nil];
}
```

**Key information provided:**
- ✅ XPush notifications have a `"xpush"` key in their payload
- ✅ Shows how to conditionally call XPush methods

**Issues:**
1. **Wrong API for modern iOS:**
   - Shows: `applicationDidReceiveRemoteNotification:fetchCompletionHandler:`
   - Should show: `userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:`

2. **Incomplete for UNUserNotificationCenter:**
   ```objc
   // What's needed for modern iOS (not shown in docs):
   - (void)userNotificationCenter:(UNUserNotificationCenter *)center
          didReceiveNotificationResponse:(UNNotificationResponse *)response
                   withCompletionHandler:(void (^)(void))completionHandler {

       NSDictionary *userInfo = response.notification.request.content.userInfo;

       if ([userInfo objectForKey:@"xpush"] != nil) {
           // XPush notification
           [XPush userNotificationCenter:center
                  didReceiveNotificationResponse:response
                           withCompletionHandler:completionHandler];
       } else {
           // Local notification or other provider
           // Handle accordingly
           completionHandler();
       }
   }
   ```

---

## Will This Approach Work?

### Scenario A: If `enableManualPushRegistration` Prevents Delegate Setting

**IF** `[XPush enableManualPushRegistration:YES]` prevents the SDK from setting itself as the UNUserNotificationCenter delegate, then:

✅ **YES, this approach would work:**

```javascript
// 1. Enable manual push registration
XtremePush.register({
    appKey: "YOUR_KEY",
    ios: {
        enableManualPushRegistration: true,
        pushPermissionsRequest: false
    }
});
```

```objc
// 2. In AppDelegate.h
@interface AppDelegate : UIResponder <UIApplicationDelegate, UNUserNotificationCenterDelegate>

// 3. In AppDelegate.m - didFinishLaunchingWithOptions:
[UNUserNotificationCenter currentNotificationCenter].delegate = self;

// 4. Implement delegate method with filtering
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       didReceiveNotificationResponse:(UNNotificationResponse *)response
                withCompletionHandler:(void (^)(void))completionHandler {

    NSDictionary *userInfo = response.notification.request.content.userInfo;

    // Check for XPush marker
    if ([userInfo objectForKey:@"xpush"] != nil) {
        // Handle XPush notification
        [XPush userNotificationCenter:center
               didReceiveNotificationResponse:response
                        withCompletionHandler:completionHandler];
    } else {
        // Handle local notification or other provider
        // Let cordova-plugin-local-notification handle it
        completionHandler();
    }
}

- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {

    NSDictionary *userInfo = notification.request.content.userInfo;

    if ([userInfo objectForKey:@"xpush"] != nil) {
        [XPush userNotificationCenter:center
              willPresentNotification:notification
                withCompletionHandler:completionHandler];
    } else {
        // Show local notification
        completionHandler(UNNotificationPresentationOptionAlert |
                         UNNotificationPresentationOptionSound);
    }
}
```

### Scenario B: If `enableManualPushRegistration` Doesn't Prevent Delegate Setting

**IF** the SDK still sets itself as delegate even with `enableManualPushRegistration:YES`, then:

❌ **NO, additional steps needed:**

The native SDK will still intercept all notifications. You would need:

**Option 1: Override after XPush initialization**
```objc
// After calling XtremePush.register()
// Re-set AppDelegate as the delegate
[UNUserNotificationCenter currentNotificationCenter].delegate = self;
```

**Option 2: Create delegate proxy** (as shown in `WORKAROUND_IMPLEMENTATION.md`)

**Option 3: Wait for SDK fix** (as outlined in `CLIENT_ISSUE_SUMMARY.md`)

---

## Testing the Approach

### Test 1: Check if enableManualPushRegistration works

```javascript
XtremePush.register({
    appKey: "YOUR_KEY",
    debugLogsEnabled: true,
    ios: {
        enableManualPushRegistration: true,
        pushPermissionsRequest: false
    }
});
```

Then in Xcode, check logs:
```
// If you see this, the SDK is still setting itself as delegate:
[XPush] Setting self as UNUserNotificationCenter delegate

// If you DON'T see that, it's working
```

### Test 2: Verify delegate ownership

Add to AppDelegate:
```objc
- (BOOL)application:(UIApplication *)application
didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    // Set AppDelegate as delegate
    [UNUserNotificationCenter currentNotificationCenter].delegate = self;

    // Initialize XPush
    // (Cordova will call XtremePush.register())

    // After a delay, check who the delegate is
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 2 * NSEC_PER_SEC),
                   dispatch_get_main_queue(), ^{
        id delegate = [UNUserNotificationCenter currentNotificationCenter].delegate;
        NSLog(@"Current delegate: %@", delegate);
        // Should log: "Current delegate: <AppDelegate: 0x...>"
        // If it logs XPush, then the SDK overrode it
    });

    return YES;
}
```

### Test 3: Verify notification filtering

Send a local notification and an XPush push notification:

```objc
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       didReceiveNotificationResponse:(UNNotificationResponse *)response
                withCompletionHandler:(void (^)(void))completionHandler {

    NSDictionary *userInfo = response.notification.request.content.userInfo;

    NSLog(@"Received notification: %@", userInfo);
    NSLog(@"Has xpush key: %@", [userInfo objectForKey:@"xpush"] ? @"YES" : @"NO");

    // ... filtering logic ...
}
```

Expected logs:
```
// Local notification:
Received notification: {meta = {plugin = "cordova-plugin-local-notification"}}
Has xpush key: NO

// XPush notification:
Received notification: {xpush = {...}, aps = {...}}
Has xpush key: YES
```

---

## Additional Identifier: The "xpush" Key

**From documentation:**
```objc
if ([userInfo objectForKey:@"xpush"] != nil) {
    // This is an XPush notification
}
```

**Alternative checks** (based on common push payload structures):
```objc
- (BOOL)isXPushNotification:(NSDictionary *)userInfo {
    // Primary marker
    if ([userInfo objectForKey:@"xpush"] != nil) {
        return YES;
    }

    // Alternative markers (adjust based on actual payload)
    if ([userInfo objectForKey:@"campaignId"] != nil ||
        [userInfo objectForKey:@"messageId"] != nil) {
        return YES;
    }

    // Check APS payload
    NSDictionary *aps = [userInfo objectForKey:@"aps"];
    if (aps && [aps objectForKey:@"xp"] != nil) {
        return YES;
    }

    return NO;
}

- (BOOL)isLocalNotification:(NSDictionary *)userInfo {
    // cordova-plugin-local-notification markers
    NSDictionary *meta = [userInfo objectForKey:@"meta"];
    if (meta && [[meta objectForKey:@"plugin"] isEqualToString:@"cordova-plugin-local-notification"]) {
        return YES;
    }

    // Other local notification markers
    if ([userInfo objectForKey:@"trigger"] != nil) {
        return YES;
    }

    return NO;
}
```

---

## Recommended Implementation Path

### Attempt 1: Try enableManualPushRegistration (Quick Test)

```javascript
XtremePush.register({
    appKey: "YOUR_KEY",
    debugLogsEnabled: true,
    ios: {
        enableManualPushRegistration: true,  // Try this first
        pushPermissionsRequest: false
    }
});
```

```objc
// Disable swizzling in Info.plist
<key>XPushSwizzlingDisabled</key>
<true/>

// Implement AppDelegate delegates with filtering
// (as shown in Scenario A above)
```

**Test:** Send local notification and XPush notification
- ✅ If both work correctly → SUCCESS
- ❌ If local notifications still intercepted → Try Attempt 2

### Attempt 2: Force Delegate Ownership (If Attempt 1 Fails)

```objc
- (BOOL)application:(UIApplication *)application
didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    // Let XPush initialize
    [super application:application didFinishLaunchingWithOptions:launchOptions];

    // Force AppDelegate to be the delegate AFTER XPush initialization
    dispatch_async(dispatch_get_main_queue(), ^{
        [UNUserNotificationCenter currentNotificationCenter].delegate = self;
    });

    return YES;
}
```

### Attempt 3: Use Workaround Proxy (If Both Fail)

Implement the proxy delegate from `WORKAROUND_IMPLEMENTATION.md`

---

## Questions to Answer

Before implementing, client should ask XtremePush support:

1. ❓ Does `enableManualPushRegistration:YES` prevent the SDK from setting itself as `UNUserNotificationCenterDelegate`?

2. ❓ Is there an API to completely disable XPush's notification handling?

3. ❓ Does the SDK check for the `"xpush"` key before processing notifications, or does it process all notifications regardless?

4. ❓ What are ALL the possible keys that identify an XPush notification?
   - `xpush`
   - `campaignId`
   - `messageId`
   - Others?

5. ❓ Can we see sample payloads for:
   - XPush push notification
   - XPush in-app message
   - XPush inbox message

---

## Success Criteria

The solution is successful when:

- ✅ Local notifications from cordova-plugin-local-notification work
- ✅ Local notification tap handlers are called correctly
- ✅ XPush push notifications still work
- ✅ XPush analytics and tracking remain functional
- ✅ No "[XPush]" logs for local notifications
- ✅ Both notification types can coexist without conflict

---

## Conclusion

**The approach SHOULD work, BUT:**

1. ✅ Step 1 (disable swizzling) - Documented and straightforward
2. ❓ Step 2 (unset delegate) - **NOT documented**, need to test if `enableManualPushRegistration` does this
3. ✅ Step 3 (custom handling) - Documented but needs updating for UNUserNotificationCenter

**Recommendation:**
- Try `enableManualPushRegistration: true` with custom delegate implementation
- Test thoroughly with logs to verify delegate ownership
- Have workaround proxy ready as backup
- Contact XtremePush support for clarification on `enableManualPushRegistration` behavior
