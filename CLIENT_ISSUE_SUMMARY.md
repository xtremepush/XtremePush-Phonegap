# Client Issue Summary - Project 1135 (Funstage)

**Date**: 2026-04-15
**Issue**: Local notifications plugin conflict with XtremePush on iOS
**Severity**: High - Blocks local notifications functionality

---

## Executive Summary

The client's iOS app uses both XtremePush and `cordova-plugin-local-notification`. The XtremePush native SDK is intercepting ALL notifications on iOS, preventing the local notifications plugin from working. This occurs even when following the multi-provider documentation and enabling `XPushSwizzlingDisabled`.

**Root Cause**: The XtremePush native SDK (`XPush.xcframework`) sets itself as the iOS notification delegate and processes all notifications without checking if they originated from XtremePush.

**Impact**: Local notification features are completely non-functional on iOS.

---

## Technical Details

### Client Configuration
- **XP SDK Version**: master-geo-beacon branch (commit 2735b8b)
- **Local Plugin**: cordova-plugin-local-notification v1.2.3
- **Platform**: iOS only (Android works correctly)
- **Followed Docs**: Yes - https://docs.xtremepush.com/docs/phonegap-multiple-push-providers-ios

### What the Client Tried
1. ✅ Set `XPushSwizzlingDisabled: true` in Info.plist
2. ✅ Implemented custom AppDelegate notification handlers
3. ❌ None of the above worked

### The Problem

**Evidence from XCode logs**:
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

XPush is logging local notifications, which means it's intercepting them.

### Why XPushSwizzlingDisabled Doesn't Help

The `XPushSwizzlingDisabled` flag only affects the **Cordova plugin's** method swizzling in `AppDelegate+XtremePush.m`. This file only swizzles:
- Old, deprecated notification APIs (UILocalNotification from iOS 9)
- AppDelegate methods

However:
1. Modern plugins use `UNUserNotificationCenter` (iOS 10+)
2. The **native XPush SDK** (`XPush.xcframework`) directly sets itself as the `UNUserNotificationCenterDelegate`
3. This delegate setting happens **inside the compiled framework**, not in the Cordova plugin code
4. The swizzling disable flag doesn't affect the native SDK's behavior

---

## Solutions

### Immediate Workarounds (Client Can Try Now)

#### Option A: Notification Filtering Proxy
Implement a proxy delegate that filters notifications based on their payload.

**Complexity**: Medium
**Success Probability**: 60-70%
**See**: `WORKAROUND_IMPLEMENTATION.md` for full code

**Pros**:
- Can be implemented immediately
- No SDK changes needed

**Cons**:
- Complex implementation
- May be fragile if XPush SDK updates
- Requires native iOS development knowledge

#### Option B: Delayed Initialization
Initialize XtremePush only after local notifications are set up.

**Complexity**: Low
**Success Probability**: 30-40%

```javascript
// Register local notifications first
cordova.plugins.notification.local.on('click', function(notification) {
    // Handle local notification
});

// Then register XtremePush
setTimeout(function() {
    XtremePush.register({ ... });
}, 1000);
```

**Cons**:
- May miss early push notifications
- Doesn't solve the fundamental conflict
- Both still need to be active simultaneously

---

### Recommended Solution (Requires XP SDK Update)

**Owner**: XtremePush SDK team
**ETA**: 2-3 weeks (estimated)
**Priority**: High

#### Changes Required in `lib/ios/XPush.xcframework`

1. **Add Notification Filtering**
   ```objc
   - (BOOL)isXPushNotification:(NSDictionary *)userInfo {
       // Check for XPush-specific payload keys
       return (userInfo[@"xp"] != nil ||
               userInfo[@"xtremepush"] != nil ||
               userInfo[@"campaignId"] != nil);
   }
   ```

2. **Chain Delegates Properly**
   ```objc
   // Before setting XPush as delegate, store the original
   id<UNUserNotificationCenterDelegate> originalDelegate =
       [UNUserNotificationCenter currentNotificationCenter].delegate;

   // Set XPush as delegate
   [UNUserNotificationCenter currentNotificationCenter].delegate = self;

   // Store original for later
   self.previousDelegate = originalDelegate;
   ```

3. **Forward Non-XPush Notifications**
   ```objc
   - (void)userNotificationCenter:(UNUserNotificationCenter *)center
          didReceiveNotificationResponse:(UNNotificationResponse *)response
                   withCompletionHandler:(void (^)(void))completionHandler {

       NSDictionary *userInfo = response.notification.request.content.userInfo;

       if ([self isXPushNotification:userInfo]) {
           // Process XPush notification
           [self handleXPushNotification:response];
           completionHandler();
       } else {
           // Forward to previous delegate
           if ([self.previousDelegate respondsToSelector:@selector(userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:)]) {
               [self.previousDelegate userNotificationCenter:center
                                   didReceiveNotificationResponse:response
                                            withCompletionHandler:completionHandler];
           } else {
               completionHandler();
           }
       }
   }
   ```

#### Alternative: Configuration Option

Add a new option to disable automatic notification handling:

```javascript
XtremePush.register({
    appKey: "...",
    ios: {
        disableAutomaticNotificationHandling: true
    }
});
```

This would require:
- Exposing `[XPush setNotificationHandlingEnabled:NO]` in the SDK
- Updating Cordova plugin to pass this option

---

## Recommendations

### For Client (Immediate)
1. ✅ Try Workaround Option A (Notification Filtering Proxy)
2. ⏸️ If that doesn't work, wait for SDK fix
3. 📝 Document exact XPush notification payload structure for filtering

### For XtremePush Team (Urgent)
1. 🔴 **Priority 1**: Update native iOS SDK with notification filtering
2. 🟠 **Priority 2**: Add configuration option to disable automatic handling
3. 🟡 **Priority 3**: Update documentation with proper multi-provider setup
4. 📚 Document XPush notification payload structure

### For Support Team
1. Escalate to iOS SDK team immediately
2. Track as critical bug affecting multi-provider setups
3. Consider temporary workaround patch release
4. Set up call with client to discuss timeline

---

## Success Criteria

The fix is successful when:
- [ ] Local notifications from cordova-plugin-local-notification work correctly
- [ ] Local notification tap handlers are invoked (not intercepted by XPush)
- [ ] XPush push notifications continue to work correctly
- [ ] XPush analytics and tracking remain functional
- [ ] No conflicts with other notification providers
- [ ] Works with `XPushSwizzlingDisabled` both enabled and disabled

---

## Questions for XP SDK Team

1. **Urgent**: Can you provide a patched version of `XPush.xcframework` with notification filtering?

2. Does the XPush native SDK have any existing APIs to:
   - Disable automatic notification handling?
   - Set a custom notification filter?
   - Chain to a previous delegate?

3. What are the definitive payload keys that identify an XPush notification?
   - `xp`?
   - `xtremepush`?
   - `campaignId`?
   - Others?

4. Can you provide:
   - Sample XPush notification payload (push)
   - Sample XPush notification payload (in-app)
   - Sample XPush notification payload (inbox)

5. Timeline for native SDK fix?

---

## Related Files

- `LOCAL_NOTIFICATIONS_CONFLICT_ANALYSIS.md` - Detailed technical analysis
- `WORKAROUND_IMPLEMENTATION.md` - Complete workaround code
- `src/ios/AppDelegate+XtremePush.m` - Current swizzling implementation
- `src/ios/XtremePushPlugin.m` - Cordova plugin implementation (was accidentally deleted, now restored)

---

## Next Steps

1. **Today**: Send this summary to XP SDK team
2. **This Week**: Client implements workaround while waiting for SDK fix
3. **Next Sprint**: XP SDK team implements proper fix
4. **Follow-up**: Test and verify with client's app
