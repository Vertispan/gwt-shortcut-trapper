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

import java.util.Objects;

/**
 * Information about the current key event.
 *
 * <p>This is basically the same as a KeyboardEvent, but with better control
 * over equals/hashcode for comparability of things we care about.
 */
public class KeyInfo {

  final String type;
  final String key;
  final boolean shiftKey;
  final boolean altKey;
  final boolean ctrlKey;
  final boolean metaKey;

  KeyInfo(String type, String key, boolean shiftKey, boolean altKey,
      boolean ctrlKey, boolean metaKey) {
    this.type = type;
    this.key = key;
    this.shiftKey = shiftKey;
    this.altKey = altKey;
    this.ctrlKey = ctrlKey;
    this.metaKey = metaKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    KeyInfo keyInfo = (KeyInfo) o;

    return shiftKey == keyInfo.shiftKey &&
        altKey == keyInfo.altKey &&
        ctrlKey == keyInfo.ctrlKey &&
        metaKey == keyInfo.metaKey &&
        type.equals(keyInfo.type) &&
        key.equals(keyInfo.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, key, shiftKey, altKey, ctrlKey, metaKey);
  }

  @Override
  public String toString() {
    return "KeyInfo{" +
        "type='" + type + '\'' +
        ", key='" + key + '\'' +
        ", shiftKey=" + shiftKey +
        ", altKey=" + altKey +
        ", ctrlKey=" + ctrlKey +
        ", metaKey=" + metaKey +
        '}';
  }
}
