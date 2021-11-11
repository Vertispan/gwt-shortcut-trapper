# Shortcut-Trapper

Simple GWT library for handling keyboard shortcuts.

## Getting Started

1. Include Vertispan Shortcut-Trapper in your application

```xml
<dependencies>
  <dependency>
    <groupId>com.vertispan</groupId>
    <artifactId>shortcut-trapper</artifactId>
    <version>LATEST</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

2. Include Shortcut-Trapper in your gwt.xml

```xml
<inherits name="com.vertispan.lib.shortcuttrapper.ShortcutTrapper"/>
```

3. Add some shortcuts to listen for

Shortcuts are added by using modifier+key combinations, such as ctrl+c, alt+a, shift+f.
```java
private void initKeyboardShortcuts() {
    // The library supports global shortcuts 
    //   - listens as the document level
    ShortcutTrapper globalTrapper = ShortcutTrapper.getGlobal();
    globalTrapper.bind("ctrl+p", this::onPrint);
    
    // The library also supports scoping shortcuts to other DOM elements
    ShortcutTrapper editorShortcuts = ShortcutTrapper.getInstance(editorElement);
    editorShortcuts.bind("ctrl+c", this::onCopy);
}

private boolean onPrint(KeyboardShortcutEvent event) {
    console.log("Handle custom print dialog");
    return false; // stops further bubbling and prevents default
}

private boolean onCopy(KeyboardShortcutEvent event) {
    console.log("Custom copy code");
    return false; // stops further bubbling and prevents default
}
```


### Sequence Capture
This library also supports gmail-like sequence shortcuts.
```java
ShortcutTrapper globalTrapper = ShortcutTrapper.getGlobal();
globalTrapper.bind("g i", this::onNavInbox);

// emacs-like bindings
globalTrapper.bind("ctrl+x ctrl+f", this::onOpenFile);
globalTrapper.bind("ctrl+x h", this::onMarkWholeBuffer);
globalTrapper.bind("ctrl+x r m", this::onCreateBookmark);
// secret codes... 
globalTrapper.bind("up up down down left right left right b a enter", this::onKonami);
```



### Shortcut names
For many of the shortcut names, we have alternatives/aliases to
make it easier to code with.  All of these can be used in the binding
and will be normalized, so using up or uparrow will both perform the 
same binding.

Here is a list of them:
* up - uparrow
* down - downarrow
* left - leftarrow
* right - rightarrow
* option - alt
* command - meta
* return - enter
* escape - esc
* plus - +
* mod - this one will be either "meta" or "ctrl"; 
  * "meta" if navigator platform matches iphone, mac, ipad, ipod


### Information about the KeyboardShortcutEventHandler

It was mentioned briefly above in the getting started section, but should be
mentioned again about the handler method return type.  The 
`KeyboardShortcutEventHandler.onShortcut()` returns boolean.  Returning false will 
prevent default browser behavior and stop event bubbling.  Returning true makes the 
handler more of a 'peek' and allows other handlers above to receive the event as well.

This may seem obvious with certain shortcut bindings that overlap with browser, such as
`ctrl+p`, `ctr+r`, `f5`, etc, but can also be handy when orchestrating multiple 
`ShortcutTrapper` instances in nested divs.

For example, in the following DOM structure, we have a simple nesting of `div` elements.
```html
<div id="grandparent">
  <div>
    <div id="grandchild"/>
  </div>
</div>
```
 We can bind identical shortcuts to both `grandparent` and `grandchild` elements. We
 can allow certain events to bubble if we also want the `grandparent` handler to be triggered as well.
 
```java
private void initKeyboardShortcuts(){
    ShortcutTrapper grandparentTrapper = ShortcutTrapper.getInstance(grandparentEl);
    ShortcutTrapper grandchildTrapper = ShortcutTrapper.getInstance(grandchildEl);

    grandparentTrapper.bind("ctrl+c", this::onGrandparentCopy);
    grandchildTrapper.bind("ctrl+c", this::onGrandchildCopy);
}

private boolean onGrandparentCopy(KeyboardShortcutEvent event) {
    console.log("Copy in Grandparent");
    return false; // stops further processing
}
    
private boolean onGrandchildCopy(KeyboardShortcutEvent event) {
    console.log("Copy in Grandchild");
    return true; // allow to bubble
}
```
             
When focus is within the `grandchild` element, and the user presses `ctrl+c`, 
the console will log both:
```text
Copy in Grandchild
Copy in Grandparent
```


### Specify Event Type
For the most part, Shortcut-Trapper can determine, from the binding combo, what event 
to listen on; keydown, keyup, keypress.   However, if you want to bind the shortcut on
a specific event type, this can be done through the binding
```java
shortcutTrapper.bind("ctrl+c", this::onCopy, BindType.KEYDOWN);
```


### Other Functions

#### Pausing Handlers
A `ShortcutTrapper` instance can be paused by enabling/disabling.   By disabling
a `ShortcutTrapper`, it will not listen for combo sequences and fire events.

```java
shortcutTrapper.setEnabled(false);   // stops listening
shortcutTrapper.setEnabled(true);    // resumes listening
```

#### Get All Bindings
All configured combo bindings can be retrieved as a map.  Any changes to the resulting
Map will **not** impact the actual bindings.  This is more for informational purposes.
```java
Map<String, KeyboardShortcutEventHandler> bindings = shortcutTrapper.getBindings();
```

#### Delete All Shortcuts
All combinations bound to a `ShortcutTrapper` can be removed by using the 
`removeAll` method.
```java
shortcutTrapper.removeAll();
```


#### Remove Specific Binding
Individual bindings can be removed by passing the exact same combo String.
```java
shortcutTrapper.unbind("ctrl+c");
```