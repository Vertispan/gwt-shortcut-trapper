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
package com.vertispan.lib.shortcuttrapper;

import com.vertispan.lib.shortcuttrapper.internal.ShortcutTrapperImpl;

import java.util.Map;

import elemental2.core.JsWeakMap;
import elemental2.dom.DomGlobal;
import elemental2.dom.Node;

/**
 * Traps keyboard shortcuts.
 */
public final class ShortcutTrapper {

  private static final JsWeakMap<Node, ShortcutTrapper> INSTANCES = new JsWeakMap<>();

  /**
   * Gets the global shortcutTrapper.
   * <p>
   * This one binds to the document level.
   *
   * @return the global ShortcutTrapper instance.
   */
  public static ShortcutTrapper getGlobal() {
    return getInstance(DomGlobal.document);
  }

  /**
   * Get a scoped ShortcutTrapper on provided node.
   *
   * @param node the node to scope to.
   * @return the ShortcutTrapper instance scoped to the parameter node.
   */
  public static ShortcutTrapper getInstance(Node node) {
    if (node == null) {
      return getGlobal();
    }
    if (!INSTANCES.has(node)) {
      INSTANCES.set(node, new ShortcutTrapper(node));
    }

    return INSTANCES.get(node);
  }

  private final ShortcutTrapperImpl impl;

  private ShortcutTrapper(Node node) {
    this.impl = new ShortcutTrapperImpl(node);
  }

  public void bind(String shortcut, KeyboardShortcutEventHandler handler) {
    impl.bind(shortcut, handler);
  }

  public void bind(String shortcut, KeyboardShortcutEventHandler handler, BindType bindType) {
    impl.bind(shortcut, handler, bindType);
  }

  public void unbind(String shortcut) {
    impl.unbind(shortcut);
  }

  public Map<String, KeyboardShortcutEventHandler> getBindings() {
    return impl.getBindings();
  }

  public boolean isEnabled() {
    return impl.isEnabled();
  }

  public void setEnabled(boolean enabled) {
    impl.setEnabled(enabled);
  }

  public void removeAll() {
    impl.removeAll();
  }

  public void debugBindings() {
    impl.debugBindings();
  }
}
