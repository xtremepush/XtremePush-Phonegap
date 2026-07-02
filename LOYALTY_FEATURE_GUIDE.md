# Loyalty Feature Integration Guide

This guide covers the loyalty widget integration options added to the XtremePush Cordova plugin.

## Prerequisites

- XtremePush Cordova plugin v4.7.0+
- Loyalty endpoint from XtremePush dashboard
- Native SDKs with loyalty support (Android SDK with LoyaltyActivity, iOS SDK with loyalty methods)

## Basic Setup

### 1. Set Loyalty Endpoint

Configure your loyalty endpoint during plugin initialization:

```javascript
xtremepush.setLoyaltyEndpoint("p12345.p.loyalty.prod.xtremepush.com");
```

### 2. Register Token Handler

The loyalty widget requires JWT tokens that may expire. Register a handler to provide fresh tokens:

```javascript
// Register the handler
xtremepush.setLoyaltyTokenHandler("onLoyaltyTokenExpired");

// Implement the callback function
function onLoyaltyTokenExpired() {
    console.log("Loyalty token expired, fetching new token...");

    // Fetch fresh token from your backend
    fetchTokenFromYourBackend().then(function(newToken) {
        xtremepush.setLoyaltyToken(newToken);
    });
}
```

### 3. Register Deeplink Handler (Optional)

If you want to handle deeplinks from the loyalty widget:

```javascript
xtremepush.register({
    appKey: "your-app-key",
    deeplinkCallback: "onDeeplinkReceived",
    // ... other options
});

function onDeeplinkReceived(deeplink) {
    console.log("Deeplink received:", deeplink);
    // Navigate to appropriate screen in your app
}
```

## Implementation Options

### Option 1: Built-in Webview (Recommended)

The simplest approach - use the native SDK's built-in webview:

```javascript
// Open loyalty widget with default options
xtremepush.openLoyalty();

// Open specific path with parameters
xtremepush.openLoyalty("/rewards", {
    "color-mode": "dark",  // "auto", "light", or "dark"
    "lang": "en"           // "en", "es", or "pt"
});
```

**Advantages:**
- Automatic token handling
- Automatic deeplink handling
- Native UI integration
- No additional setup required

### Option 2: Custom Webview

For more control over the webview presentation, use a custom webview:

#### Step 1: Get the Loyalty URL

```javascript
xtremepush.getLoyaltyUrl("/rewards", { "color-mode": "light" }, function(url) {
    console.log("Loyalty URL:", url);
    // Use this URL in your custom webview
    loadInCustomWebview(url);
});
```

#### Step 2: Inject JavaScript Interface

To enable token handling and deeplinks in your custom webview, inject the interface:

```javascript
function loadInCustomWebview(url) {
    var webview = document.getElementById('my-webview');

    // Get the interface code
    var interfaceCode = xtremepush.getLoyaltyWebViewInterface();

    // Inject it into the webview
    webview.addEventListener('loadstop', function() {
        webview.executeScript({
            code: interfaceCode
        }, function() {
            console.log("Loyalty interface injected successfully");
        });
    });

    // Load the loyalty URL
    webview.src = url;
}
```

#### Example with InAppBrowser Plugin

```javascript
function openLoyaltyInBrowser() {
    xtremepush.getLoyaltyUrl(null, null, function(url) {
        var ref = cordova.InAppBrowser.open(url, '_blank', 'location=yes');

        ref.addEventListener('loadstop', function() {
            // Inject the interface when page loads
            ref.executeScript({
                code: xtremepush.getLoyaltyWebViewInterface()
            });
        });
    });
}
```

**Advantages:**
- Full control over webview presentation
- Can customize webview options (toolbar, navigation, etc.)
- Can use InAppBrowser or custom webview components

**Requirements:**
- Must inject JavaScript interface for token handling to work
- Must inject JavaScript interface for deeplinks to work
- WebView must have JavaScript enabled

## API Reference

### `setLoyaltyEndpoint(endpoint)`

Sets the loyalty endpoint URL.

**Parameters:**
- `endpoint` (String): Loyalty endpoint from XtremePush dashboard

**Example:**
```javascript
xtremepush.setLoyaltyEndpoint("p12345.p.loyalty.prod.xtremepush.com");
```

---

### `openLoyalty([path], [params])`

Opens the loyalty widget in the native SDK's built-in webview.

**Parameters:**
- `path` (String, optional): URL path extension (e.g., "/rewards", "/profile")
- `params` (Object, optional): Configuration parameters
  - `color-mode`: "auto" | "light" | "dark"
  - `lang`: "en" | "es" | "pt"

**Examples:**
```javascript
// Open default view
xtremepush.openLoyalty();

// Open specific section
xtremepush.openLoyalty("/rewards");

// Open with custom parameters
xtremepush.openLoyalty("/profile", {
    "color-mode": "dark",
    "lang": "es"
});
```

---

### `getLoyaltyUrl([path], [params], callback)`

Gets the loyalty widget URL for use in a custom webview.

**Parameters:**
- `path` (String, optional): URL path extension
- `params` (Object, optional): Configuration parameters
- `callback` (Function): Called with the URL string

**Example:**
```javascript
xtremepush.getLoyaltyUrl("/rewards", { "lang": "en" }, function(url) {
    console.log("URL:", url);
    // Load in custom webview
});
```

---

### `setLoyaltyTokenHandler(callbackFunctionName)`

Registers a callback function that will be called when the loyalty token expires.

**Parameters:**
- `callbackFunctionName` (String): Name of the JavaScript function to call

**Example:**
```javascript
xtremepush.setLoyaltyTokenHandler("onLoyaltyTokenExpired");

function onLoyaltyTokenExpired() {
    // Fetch new token and call setLoyaltyToken()
}
```

---

### `setLoyaltyToken(token)`

Provides a fresh JWT token to the loyalty widget.

**Parameters:**
- `token` (String): New JWT token

**Example:**
```javascript
function onLoyaltyTokenExpired() {
    fetchNewToken().then(function(token) {
        xtremepush.setLoyaltyToken(token);
    });
}
```

---

### `getLoyaltyWebViewInterface()`

Returns JavaScript code to inject into custom webviews to enable token handling and deeplinks.

**Returns:** String containing JavaScript code

**Example:**
```javascript
var interfaceCode = xtremepush.getLoyaltyWebViewInterface();
webview.executeScript({ code: interfaceCode });
```

## Platform-Specific Notes

### Android

- Built-in webview uses `LoyaltyActivity` from the native SDK
- Custom webviews must have JavaScript enabled
- The injected interface provides the `window.XtremePush` object that the loyalty widget expects

### iOS

- Built-in webview is handled internally by the XPush framework
- Token handler uses completion blocks internally but is bridged to match Android's callback pattern
- WKWebView is recommended for custom webview implementations

## Common Use Cases

### Use Case 1: Simple Integration

Just open the loyalty widget with defaults:

```javascript
// Setup
xtremepush.setLoyaltyEndpoint("p12345.p.loyalty.prod.xtremepush.com");
xtremepush.setLoyaltyTokenHandler("refreshToken");

function refreshToken() {
    getTokenFromBackend().then(xtremepush.setLoyaltyToken);
}

// Usage
xtremepush.openLoyalty();
```

### Use Case 2: Custom Branding

Open with specific theme and language:

```javascript
function openRewards() {
    xtremepush.openLoyalty("/rewards", {
        "color-mode": "dark",
        "lang": getUserPreferredLanguage()
    });
}
```

### Use Case 3: Embedded in App Screen

Use custom webview for full control:

```javascript
function embedLoyaltyWidget() {
    var container = document.getElementById('loyalty-container');
    var webview = document.createElement('iframe');

    xtremepush.getLoyaltyUrl(null, null, function(url) {
        // Note: iframe may have limitations, InAppBrowser recommended
        webview.src = url;
        container.appendChild(webview);

        // Inject interface (method depends on webview type)
        injectInterface(webview);
    });
}
```

## Troubleshooting

### Token Handler Not Called

**Symptoms:** Token expires but callback never fires

**Solutions:**
- Verify token handler is registered before opening loyalty widget
- Check that callback function exists in global scope
- For custom webviews, ensure JavaScript interface is injected

### Deeplinks Not Working

**Symptoms:** Clicking deeplinks in loyalty widget does nothing

**Solutions:**
- Verify deeplink callback is registered in plugin initialization
- For custom webviews, ensure JavaScript interface is injected
- Check browser console for JavaScript errors

### Custom Webview Blank or Error

**Symptoms:** Loyalty widget doesn't load in custom webview

**Solutions:**
- Ensure JavaScript is enabled in webview settings
- Verify the URL returned from `getLoyaltyUrl()` is valid
- Check that token is valid and not expired
- Inject JavaScript interface after page load completes

## Security Considerations

1. **Token Security**: Never hardcode JWT tokens. Always fetch from your secure backend.
2. **HTTPS Only**: Loyalty endpoints must use HTTPS
3. **Token Refresh**: Implement proper token refresh logic to avoid service interruption
4. **Deeplink Validation**: Validate deeplinks before navigating to prevent malicious redirects

## Examples

See the `examples/` directory for complete integration examples:
- `examples/loyalty-basic/` - Simple built-in webview example
- `examples/loyalty-custom/` - Custom webview with InAppBrowser
