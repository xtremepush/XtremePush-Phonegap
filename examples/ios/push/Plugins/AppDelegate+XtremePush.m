#import "AppDelegate+XtremePush.h"
#import "XtremePushPlugin.h"
#import <objc/runtime.h>

@implementation AppDelegate (notifications)

+ (void)load {
    [self swizzleMethodWithClass: [self class]
                originalSelector: @selector(application:didRegisterForRemoteNotificationsWithDeviceToken:)
             andReplacedSelector: @selector(xtremepushReplaced:didRegisterForRemoteNotificationsWithDeviceToken:)
                andAddedSelector: @selector(xtremepushAdded:didRegisterForRemoteNotificationsWithDeviceToken:)];
    
    [self swizzleMethodWithClass: [self class]
                originalSelector: @selector(application:didFailToRegisterForRemoteNotificationsWithError:)
             andReplacedSelector: @selector(xtremepushReplaced:didFailToRegisterForRemoteNotificationsWithError:)
                andAddedSelector: @selector(xtremepushAdded:didFailToRegisterForRemoteNotificationsWithError:)];
    
    [self swizzleMethodWithClass: [self class]
                originalSelector: @selector(application:didReceiveRemoteNotification:)
             andReplacedSelector: @selector(xtremepushReplaced:didReceiveRemoteNotification:)
                andAddedSelector: @selector(xtremepushAdded:didReceiveRemoteNotification:)];
    
    [self swizzleMethodWithClass: [self class]
                originalSelector: @selector(application:didReceiveLocalNotification:)
             andReplacedSelector: @selector(xtremepushReplaced:didReceiveLocalNotification:)
                andAddedSelector: @selector(xtremepushAdded:didReceiveLocalNotification:)];
}

+ (void)swizzleMethodWithClass:(Class)class
              originalSelector:(SEL)originalSelector
           andReplacedSelector:(SEL)ReplacedSelector
              andAddedSelector:(SEL)addedSelector {
    Method originalMethod = class_getInstanceMethod(class, originalSelector);
    Method ReplacedMethod = class_getInstanceMethod(class, ReplacedSelector);
    Method addedMethod    = class_getInstanceMethod(class, addedSelector);
    
    BOOL didAddMethod = class_addMethod(class, originalSelector,
                                        method_getImplementation(addedMethod),
                                        method_getTypeEncoding(addedMethod));
    
    if (didAddMethod) {
        class_replaceMethod(class, addedSelector,
                            method_getImplementation(originalMethod),
                            method_getTypeEncoding(originalMethod));
    } else {
        method_exchangeImplementations(originalMethod, ReplacedMethod);
    }
}

- (void)xtremepushReplaced:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    [self xtremepushReplaced:application didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
    [XPush applicationDidRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

- (void)xtremepushAdded:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    [XPush applicationDidRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

- (void)xtremepushReplaced:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    [self xtremepushReplaced:application didFailToRegisterForRemoteNotificationsWithError:error];
    [XPush applicationDidFailToRegisterForRemoteNotificationsWithError:error];
}

- (void)xtremepushAdded:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    [XPush applicationDidFailToRegisterForRemoteNotificationsWithError:error];
}

- (void)xtremepushReplaced:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    [self xtremepushReplaced:application didReceiveRemoteNotification:userInfo];
    [XPush applicationDidReceiveRemoteNotification:userInfo];
}

- (void)xtremepushAdded:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    [XPush applicationDidReceiveRemoteNotification:userInfo];
}

- (void)xtremepushReplaced:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    [self xtremepushReplaced:application didReceiveLocalNotification:notification];
    [XPush applicationDidReceiveLocalNotification:notification];
}

- (void)xtremepushAdded:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    [XPush applicationDidReceiveLocalNotification:notification];
}

@end
