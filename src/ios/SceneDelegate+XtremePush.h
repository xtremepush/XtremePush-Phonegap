#import <UIKit/UIKit.h>

// Category on NSObject to enable swizzling SceneDelegate methods
// This allows the XtremePush plugin to intercept SceneDelegate lifecycle events
// in Cordova iOS 8 apps, which use scene-based architecture by default
@interface NSObject (XtremePushSceneSwizzling)

// Swizzling helper method
+ (void)xtremepush_swizzleSceneMethod:(Class)class
                      originalSelector:(SEL)originalSelector
                   andReplacedSelector:(SEL)replacedSelector
                      andAddedSelector:(SEL)addedSelector;

// Swizzled scene delegate methods
- (void)xtremepushScene_replaced:(UIScene *)scene
              willConnectToSession:(UISceneSession *)session
                           options:(UISceneConnectionOptions *)connectionOptions API_AVAILABLE(ios(13.0));

- (void)xtremepushScene_added:(UIScene *)scene
           willConnectToSession:(UISceneSession *)session
                        options:(UISceneConnectionOptions *)connectionOptions API_AVAILABLE(ios(13.0));

@end
