/*
 * Copyright 2021 Vertispan LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vertispan.lib.shortcuttrapper.internal;

import com.vertispan.lib.shortcuttrapper.KeyboardShortcutEventHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import elemental2.dom.DomGlobal;
import elemental2.dom.KeyboardEvent;

/**
 * Normalizes the key information.
 *
 * <p>Parses strings into KeyInfo or sequence of KeyInfo.</p>
 * <p>Normalizes KeyboardEvent objects.</p>
 */
public class ComboNormalizer {

  private static final class Modifier {
    boolean shift;
    boolean ctrl;
    boolean alt;
    boolean meta;

    private void setModifier(String key) {
      if (key == null) {
        return;
      }
      switch (key.toLowerCase()) {
        case "shift":
          shift = true;
          break;
        case "ctrl":
          ctrl = true;
          break;
        case "alt":
          alt = true;
          break;
        case "meta":
          meta = true;
          break;
      }
    }

    private static boolean isModifier(String key) {
      if (key == null) {
        return false;
      }
      key = key.toLowerCase();
      return "shift".equals(key) || "ctrl".equals(key) || "alt".equals(key) || "meta".equals(key);
    }

    public boolean any() {
      return shift || ctrl || alt || meta;
    }
  }

  private static final Map<String, String> SHIFT_MAP = new HashMap<>();
  private static final Map<String, String> SPECIAL_ALIASES = new HashMap<>();


  static {
    SHIFT_MAP.put("~", "`");
    SHIFT_MAP.put("!", "1");
    SHIFT_MAP.put("@", "2");
    SHIFT_MAP.put("#", "3");
    SHIFT_MAP.put("$", "4");
    SHIFT_MAP.put("%", "5");
    SHIFT_MAP.put("^", "6");
    SHIFT_MAP.put("&", "7");
    SHIFT_MAP.put("*", "8");
    SHIFT_MAP.put("(", "9");
    SHIFT_MAP.put(")", "0");
    SHIFT_MAP.put("_", "_");// doesn't change... Browser still sends _
    SHIFT_MAP.put("+", "=");
    SHIFT_MAP.put(":", ";");
    SHIFT_MAP.put("\"", "'");
    SHIFT_MAP.put("<", ",");
    SHIFT_MAP.put(">", ".");
    SHIFT_MAP.put("?", "/");
    SHIFT_MAP.put("|", "\\");

    SPECIAL_ALIASES.put("up", "arrowup");
    SPECIAL_ALIASES.put("down", "arrowdown");
    SPECIAL_ALIASES.put("left", "arrowleft");
    SPECIAL_ALIASES.put("right", "arrowright");
    SPECIAL_ALIASES.put("option", "alt");
    SPECIAL_ALIASES.put("command", "meta");
    SPECIAL_ALIASES.put("return", "enter");
    SPECIAL_ALIASES.put("escape", "esc");
    SPECIAL_ALIASES.put("plus", "+");
    SPECIAL_ALIASES.put("mod",
        DomGlobal.window.navigator.platform.matches("^.*(iPhone|Mac|iPad|iPod).*$") ? "meta"
            : "ctrl");
  }

  @Nonnull
  static KeyInfo getKeyInfo(KeyboardEvent event) {

    String key = event.key.toLowerCase();

    if (event.shiftKey && SHIFT_MAP.containsKey(key)) {
      key = SHIFT_MAP.get(key);
    }

    // so.. at least on mac, if you have alt as a modifier without ctrl,
    // it sends different.  For example, alt/option+3 == £
    // however, it also sends:
    // keyCode: 51
    // code:   "Digit3"
    // TODO - implement
    //if (event.altKey && !event.ctrlKey) {
    //}

    return new KeyInfo(event.type, key, event.shiftKey, event.altKey, event.ctrlKey, event.metaKey);
  }

  /**
   * Parses a String combination.
   */
  @Nonnull
  static Optional<Binding> parse(String combination, KeyboardShortcutEventHandler handler,
      String action) {
    if (combination == null) {
      return Optional.empty();
    }
    // lowercase it all
    combination = combination.toLowerCase();

    // reduce spaces
    combination = combination.replaceAll("\\s+", " ");
    String[] sequence = combination.split(" ");

    // pattern is a sequence of key bindings.   each one should be processed one at a time.

    Collection<KeyInfo> keyInfos = new ArrayList<>();

    if (sequence.length > 1) {
      keyInfos.addAll(getKeyInfo(sequence, action));
    } else {
      keyInfos.add(getKeyInfo(combination, action));
    }

    return Optional.of(new Binding(combination, keyInfos, handler));
  }

  private static KeyInfo getKeyInfo(String combo, String action) {
    Modifier modifiers = new Modifier();

    String[] parts = keysFromString(combo);

    // everything up until the last index should be a modifier
    // if someone does something weird, like ctrl-a-a ... then let's throw an exception.
    if (parts.length > 1) {
      for (int i = 0; i < parts.length - 1; i++) {
        String part = parts[i];

        // normalize any key names
        if (SPECIAL_ALIASES.containsKey(part)) {
          part = SPECIAL_ALIASES.get(part);
        }

        if (!Modifier.isModifier(part)) {
          throw new IllegalArgumentException("Invalid binding: " + combo);
        }

        modifiers.setModifier(part);
      }
    }

    String key = parts[parts.length - 1];
    if (SPECIAL_ALIASES.containsKey(key)) {
      key = SPECIAL_ALIASES.get(key);
    }
    if (SHIFT_MAP.containsKey(key)) {
      modifiers.shift = true;
      key = SHIFT_MAP.get(key);
    }

    // depending on the key combo, choose the best event type
    action = pickBestAction(key, modifiers, action);

    return new KeyInfo(action, key, modifiers.shift, modifiers.alt, modifiers.ctrl, modifiers.meta);
  }

  @Nonnull
  private static Collection<KeyInfo> getKeyInfo(String[] sequence, String action) {
    Collection<KeyInfo> result = new ArrayList<>();
    for (String seq : sequence) {
      result.add(getKeyInfo(seq, action));
    }
    return result;
  }

  private static String[] keysFromString(String combination) {
    if ("+".equals(combination)) {
      return new String[]{combination};
    }
    combination = combination.replaceAll("\\+{2}", "+plus");
    return combination.split("\\+");
  }

  private static String pickBestAction(String key, Modifier modifiers, String action) {

    // if no action is specified, let's decide if we're looking at a key other than single characters
    // this will be for things like backspace, F1-F12, etc.
    if (action == null) {
      action = key.length() > 1 ? "keydown" : "keypress";
    }

    // modifier keys don't work as expected with keypress, switch to keydown
    if ("keypress".equals(action) && modifiers.any()) {
      action = "keydown";
    }

    return action;
  }
}
