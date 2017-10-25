#import "AppDelegate+XtremePush.h"
#import "XtremePushPlugin.h"
#import <objc/runtime.h>
#import "Storage.h"

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
                originalSelector: @selector(application:handleActionWithIdentifier:)
             andReplacedSelector: @selector(xtremepushReplaced:handleActionWithIdentifier:)
                andAddedSelector: @selector(xtremepushAdded:handleActionWithIdentifier:)];
    
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

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    [XPush applicationDidRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    [XPush applicationDidFailToRegisterForRemoteNotificationsWithError:error];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    if(Storage.store.isRegistered == true){
        [XPush applicationDidReceiveRemoteNotification:userInfo fetchCompletionHandler:completionHandler];
    } else{
        Storage.store.tempUserStuff = userInfo;
        Storage.store.identifier = nil;
    }
}

- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    [XPush applicationDidReceiveLocalNotification:notification];
}

- (void) application:(UIApplication *)application handleActionWithIdentifier:(NSString *)identifier forRemoteNotification:(NSDictionary *)userInfo
   completionHandler:(void (^)())completionHandler {
    
    if(Storage.store.isRegistered){
        [XPush application:application handleActionWithIdentifier:identifier forRemoteNotification:userInfo completionHandler:completionHandler];
    } else{
        Storage.store.tempUserStuff = userInfo;
        Storage.store.identifier = identifier;
    }
}

- (void) application:(UIApplication *)application handleActionWithIdentifier:(NSString *)identifier forLocalNotification:(UILocalNotification *)notification completionHandler:(void (^)())completionHandler{
    [XPush application:application handleActionWithIdentifier:identifier forLocalNotification:notification completionHandler:completionHandler];
}





// - (void)xtremepushReplaced:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
//     [self xtremepushReplaced:application didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
//     [XPush applicationDidRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
// }

// - (void)xtremepushAdded:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
//     [XPush applicationDidRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
// }

// - (void)xtremepushReplaced:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
//     [self xtremepushReplaced:application didFailToRegisterForRemoteNotificationsWithError:error];
//     [XPush applicationDidFailToRegisterForRemoteNotificationsWithError:error];
// }

// - (void)xtremepushAdded:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
//     [XPush applicationDidFailToRegisterForRemoteNotificationsWithError:error];
// }

// - (void)xtremepushReplaced:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
//     [self xtremepushReplaced:application didReceiveRemoteNotification:userInfo];
//     [XPush applicationDidReceiveRemoteNotification:userInfo];
// }

// - (void)xtremepushAdded:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
//     [XPush applicationDidReceiveRemoteNotification:userInfo];
// }

// - (void)xtremepushReplaced:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
//     [self xtremepushReplaced:application didReceiveLocalNotification:notification];
//     [XPush applicationDidReceiveLocalNotification:notification];
// }

// - (void)xtremepushAdded:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
//     [XPush applicationDidReceiveLocalNotification:notification];
// }

@end

