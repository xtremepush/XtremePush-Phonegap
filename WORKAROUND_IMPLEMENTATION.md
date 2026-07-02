# Workaround Implementation for Local Notifications Conflict

## Quick Fix for Client (Project 1135)

This workaround helps the `cordova-plugin-local-notification` plugin work alongside XtremePush by implementing custom notification filtering in the AppDelegate.

## Step 1: Create Custom AppDelegate Category

Create a new file in your Cordova project's iOS platform:

**File**: `platforms/ios/YourApp/Classes/AppDelegate+NotificationFix.h`

```objc
#import "AppDelegate.h"
#import <UserNotifications/UserNotifications.h>

@interface AppDelegate (NotificationFix) <UNUserNotificationCenterDelegate>

@end
```

**File**: `platforms/ios/YourApp/Classes/AppDelegate+NotificationFix.m`

```objc
#import "AppDelegate+NotificationFix.h"
#import <objc/runtime.h>
#import <UserNotifications/UserNotifications.h>

@implementation AppDelegate (NotificationFix)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        // Store original delegate
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];

        // Set up notification center with proper delegate
        [[NSNotificationCenter defaultCenter] addObserverForName:UIApplicationDidFinishLaunchingNotification
                                                          object:nil
                                                           queue:[NSOperationQueue mainQueue]
                                                      usingBlock:^(NSNotification *note) {
            // This runs after XPush sets itself as delegate
            // We'll intercept at a higher level
            [self setupNotificationInterception];
        }];
    });
}

+ (void)setupNotificationInterception {
    // Swizzle the UNUserNotificationCenter's delegate setter
    Method originalMethod = class_getInstanceMethod([UNUserNotificationCenter class],
                                                    @selector(setDelegate:));
    Method swizzledMethod = class_getInstanceMethod([self class],
                                                    @selector(xp_setDelegate:));

    method_exchangeImplementations(originalMethod, swizzledMethod);
}

// This will be called instead of the original setDelegate:
- (void)xp_setDelegate:(id<UNUserNotificationCenterDelegate>)delegate {
    // Create our proxy delegate that filters notifications
    NotificationDelegateProxy *proxy = [[NotificationDelegateProxy alloc] initWithOriginalDelegate:delegate];
    [self xp_setDelegate:proxy];  // Call original (swizzled) method
}

@end

// MARK: - Proxy Delegate

@interface NotificationDelegateProxy : NSObject <UNUserNotificationCenterDelegate>
@property (nonatomic, weak) id<UNUserNotificationCenterDelegate> originalDelegate;
@property (nonatomic, weak) id<UNUserNotificationCenterDelegate> localNotificationDelegate;
@end

@implementation NotificationDelegateProxy

- (instancetype)initWithOriginalDelegate:(id<UNUserNotificationCenterDelegate>)delegate {
    self = [super init];
    if (self) {
        _originalDelegate = delegate;
        // Try to get the local notification plugin's delegate
        // The local notification plugin usually stores its delegate somewhere accessible
        _localNotificationDelegate = [self findLocalNotificationDelegate];
    }
    return self;
}

- (id<UNUserNotificationCenterDelegate>)findLocalNotificationDelegate {
    // Attempt to find the delegate set by cordova-plugin-local-notification
    // This may require inspecting the plugin's implementation
    // For now, return nil and we'll handle locally
    return nil;
}

- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       didReceiveNotificationResponse:(UNNotificationResponse *)response
                withCompletionHandler:(void (^)(void))completionHandler {

    NSDictionary *userInfo = response.notification.request.content.userInfo;

    NSLog(@"[NotificationProxy] Received notification: %@", userInfo);

    // Check if this is from local notification plugin
    if ([self isLocalNotification:userInfo]) {
        NSLog(@"[NotificationProxy] Handling as local notification");

        // Handle local notification
        if (self.localNotificationDelegate &&
            [self.localNotificationDelegate respondsToSelector:@selector(userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:)]) {
            [self.localNotificationDelegate userNotificationCenter:center
                                      didReceiveNotificationResponse:response
                                               withCompletionHandler:completionHandler];
        } else {
            // Fallback: Post notification for local plugin to handle
            [[NSNotificationCenter defaultCenter] postNotificationName:@"CDVLocalNotification"
                                                                object:response];
            completionHandler();
        }
    } else {
        NSLog(@"[NotificationProxy] Handling as XPush notification");

        // Handle as XPush notification
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

- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {

    NSDictionary *userInfo = notification.request.content.userInfo;

    if ([self isLocalNotification:userInfo]) {
        // Handle local notification
        if (self.localNotificationDelegate &&
            [self.localNotificationDelegate respondsToSelector:@selector(userNotificationCenter:willPresentNotification:withCompletionHandler:)]) {
            [self.localNotificationDelegate userNotificationCenter:center
                                           willPresentNotification:notification
                                             withCompletionHandler:completionHandler];
        } else {
            // Default: show the notification
            completionHandler(UNNotificationPresentationOptionAlert |
                            UNNotificationPresentationOptionSound |
                            UNNotificationPresentationOptionBadge);
        }
    } else {
        // Handle as XPush notification
        if (self.originalDelegate &&
            [self.originalDelegate respondsToSelector:@selector(userNotificationCenter:willPresentNotification:withCompletionHandler:)]) {
            [self.originalDelegate userNotificationCenter:center
                                  willPresentNotification:notification
                                    withCompletionHandler:completionHandler];
        } else {
            completionHandler(UNNotificationPresentationOptionNone);
        }
    }
}

- (BOOL)isLocalNotification:(NSDictionary *)userInfo {
    // Check for cordova-plugin-local-notification markers
    // The plugin adds specific keys to identify its notifications

    // Method 1: Check for plugin metadata
    NSDictionary *meta = userInfo[@"meta"];
    if (meta && [meta[@"plugin"] isEqualToString:@"cordova-plugin-local-notification"]) {
        return YES;
    }

    // Method 2: Check for local notification specific keys
    // cordova-plugin-local-notification uses these keys:
    if (userInfo[@"id"] != nil &&
        userInfo[@"trigger"] != nil &&
        userInfo[@"foreground"] != nil) {
        // Likely a local notification
        return YES;
    }

    // Method 3: Check for absence of XPush markers
    // XPush notifications typically have these keys:
    BOOL hasXPushMarkers = (userInfo[@"xp"] != nil ||
                           userInfo[@"xtremepush"] != nil ||
                           userInfo[@"aps"][@"xp"] != nil ||
                           userInfo[@"campaignId"] != nil ||
                           userInfo[@"messageId"] != nil);

    if (!hasXPushMarkers) {
        // No XPush markers, assume local notification
        return YES;
    }

    return NO;
}

// Forward any other delegate methods to the original delegate
- (BOOL)respondsToSelector:(SEL)aSelector {
    if ([super respondsToSelector:aSelector]) {
        return YES;
    }
    return [self.originalDelegate respondsToSelector:aSelector];
}

- (id)forwardingTargetForSelector:(SEL)aSelector {
    return self.originalDelegate;
}

@end
```

## Step 2: Add Files to Xcode Project

1. Open `platforms/ios/YourApp.xcworkspace` in Xcode
2. Add both files to the project (right-click on Classes folder → Add Files)
3. Make sure they're included in the build target

## Step 3: Build and Test

```bash
cordova build ios
```

## Step 4: Verify in Logs

When you tap a local notification, you should see:
```
[NotificationProxy] Received notification: {...}
[NotificationProxy] Handling as local notification
```

When you tap an XPush notification, you should see:
```
[NotificationProxy] Received notification: {...}
[NotificationProxy] Handling as XPush notification
```

## Alternative Simpler Approach: Hook-Based Script

If the above is too complex, create a simpler Cordova hook that modifies the XtremePush plugin at build time.

**File**: `hooks/after_plugin_install/modify_xpush.js`

```javascript
#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

module.exports = function(context) {
    const platformRoot = path.join(context.opts.projectRoot, 'platforms/ios');
    const pluginFile = path.join(
        context.opts.projectRoot,
        'plugins/com.xtreme.plugins.XtremePush/src/ios/XtremePushPlugin.m'
    );

    if (!fs.existsSync(pluginFile)) {
        console.log('XtremePush plugin not found, skipping...');
        return;
    }

    // Add helper method to check if notification is from XPush
    const helperMethod = `
// CUSTOM: Check if notification is from XPush
- (BOOL)isXPushNotification:(NSDictionary *)userInfo {
    // Check for XPush-specific keys
    return (userInfo[@"xp"] != nil ||
            userInfo[@"xtremepush"] != nil ||
            userInfo[@"aps"][@"xp"] != nil ||
            userInfo[@"campaignId"] != nil);
}
`;

    let content = fs.readFileSync(pluginFile, 'utf8');

    if (!content.includes('isXPushNotification')) {
        // Find the @implementation line and add our method
        content = content.replace(
            '@implementation XtremePushPlugin',
            '@implementation XtremePushPlugin\n' + helperMethod
        );

        fs.writeFileSync(pluginFile, content, 'utf8');
        console.log('✓ Added notification filtering helper to XtremePush plugin');
    }
};
```

## Step 5: Contact XtremePush Support

While implementing the workaround, contact XtremePush support with:

1. **Request**: Ask for native SDK update to filter notifications properly
2. **Reference**: Share the analysis document (`LOCAL_NOTIFICATIONS_CONFLICT_ANALYSIS.md`)
3. **Ask about**: Any existing APIs to disable automatic notification handling

## Notes for XtremePush SDK Team

The core issue is in `lib/ios/XPush.xcframework`. The SDK needs to:

1. **Check notification source** before processing:
   ```objc
   - (BOOL)isXPushNotification:(NSDictionary *)userInfo {
       return (userInfo[@"xp"] != nil || /* other XPush markers */);
   }
   ```

2. **Chain delegates properly**:
   ```objc
   // Store the original delegate before setting XPush as delegate
   self.originalDelegate = [UNUserNotificationCenter currentNotificationCenter].delegate;
   [UNUserNotificationCenter currentNotificationCenter].delegate = self;
   ```

3. **Call original delegate** for non-XPush notifications:
   ```objc
   if (![self isXPushNotification:userInfo]) {
       if ([self.originalDelegate respondsToSelector:@selector(userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:)]) {
           [self.originalDelegate userNotificationCenter:center
                                didReceiveNotificationResponse:response
                                         withCompletionHandler:completionHandler];
           return;
       }
   }
   ```

## Testing Checklist

- [ ] Local notifications from cordova-plugin-local-notification are received correctly
- [ ] Local notification tap handlers are called (not intercepted by XPush)
- [ ] XPush push notifications still work correctly
- [ ] XPush push notification handlers are still called
- [ ] No crashes or errors in logs
- [ ] Both types of notifications can coexist

## If This Doesn't Work

The issue may require changes at the native SDK level. In that case:

1. Request urgent SDK update from XtremePush
2. Consider temporarily removing XPush or local notifications feature
3. Wait for proper SDK fix that implements delegate chaining
