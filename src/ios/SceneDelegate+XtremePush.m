#import "SceneDelegate+XtremePush.h"
#import "Storage.h"
#import <objc/runtime.h>
#import <UserNotifications/UserNotifications.h>
#import <XPush/XPush.h>

@implementation NSObject (XtremePushSceneSwizzling)

+ (void)load {
    // Check if swizzling is disabled via Info.plist
    BOOL disabled = [[[[NSBundle mainBundle] infoDictionary] objectForKey:@"XPushSwizzlingDisabled"] boolValue];
    if (disabled) {
        return;
    }

    // Only swizzle on iOS 13+ where SceneDelegate exists
    if (@available(iOS 13.0, *)) {
        // Try to find the SceneDelegate class used by Cordova iOS 8
        Class sceneDelegateClass = NSClassFromString(@"CDVSceneDelegate");

        // Fallback to other common SceneDelegate class names
        if (!sceneDelegateClass) {
            sceneDelegateClass = NSClassFromString(@"MainSceneDelegate");
        }
        if (!sceneDelegateClass) {
            sceneDelegateClass = NSClassFromString(@"SceneDelegate");
        }

        // If we found a SceneDelegate class, swizzle the scene connection method
        if (sceneDelegateClass) {
            [self xtremepush_swizzleSceneMethod:sceneDelegateClass
                                originalSelector:@selector(scene:willConnectToSession:options:)
                             andReplacedSelector:@selector(xtremepushScene_replaced:willConnectToSession:options:)
                                andAddedSelector:@selector(xtremepushScene_added:willConnectToSession:options:)];
        }
    }
}

+ (void)xtremepush_swizzleSceneMethod:(Class)class
                      originalSelector:(SEL)originalSelector
                   andReplacedSelector:(SEL)replacedSelector
                      andAddedSelector:(SEL)addedSelector {
    Method originalMethod = class_getInstanceMethod(class, originalSelector);
    Method replacedMethod = class_getInstanceMethod(class, replacedSelector);
    Method addedMethod = class_getInstanceMethod(class, addedSelector);

    BOOL didAddMethod = class_addMethod(class, originalSelector,
                                        method_getImplementation(addedMethod),
                                        method_getTypeEncoding(addedMethod));

    if (didAddMethod) {
        class_replaceMethod(class, addedSelector,
                            method_getImplementation(originalMethod),
                            method_getTypeEncoding(originalMethod));
    } else {
        method_exchangeImplementations(originalMethod, replacedMethod);
    }
}

- (void)xtremepushScene_replaced:(UIScene *)scene
              willConnectToSession:(UISceneSession *)session
                           options:(UISceneConnectionOptions *)connectionOptions API_AVAILABLE(ios(13.0)) {
    // Call original implementation first
    [self xtremepushScene_replaced:scene willConnectToSession:session options:connectionOptions];

    // Handle cold boot notification - extract notification response from connection options
    if (connectionOptions.notificationResponse) {
        UNNotificationResponse *response = connectionOptions.notificationResponse;
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];

        if (Storage.store.isRegistered) {
            // XPush is already registered, process notification immediately
            [XPush userNotificationCenter:center
          didReceiveNotificationResponse:response
                   withCompletionHandler:nil];
        } else {
            // XPush not registered yet, store notification data for later processing
            Storage.store.tempUserInfo = response.notification.request.content.userInfo;
            Storage.store.identifier = response.actionIdentifier;
        }
    }
}

- (void)xtremepushScene_added:(UIScene *)scene
           willConnectToSession:(UISceneSession *)session
                        options:(UISceneConnectionOptions *)connectionOptions API_AVAILABLE(ios(13.0)) {
    // Handle cold boot notification - extract notification response from connection options
    if (connectionOptions.notificationResponse) {
        UNNotificationResponse *response = connectionOptions.notificationResponse;
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];

        if (Storage.store.isRegistered) {
            // XPush is already registered, process notification immediately
            [XPush userNotificationCenter:center
          didReceiveNotificationResponse:response
                   withCompletionHandler:nil];
        } else {
            // XPush not registered yet, store notification data for later processing
            Storage.store.tempUserInfo = response.notification.request.content.userInfo;
            Storage.store.identifier = response.actionIdentifier;
        }
    }
}

@end
