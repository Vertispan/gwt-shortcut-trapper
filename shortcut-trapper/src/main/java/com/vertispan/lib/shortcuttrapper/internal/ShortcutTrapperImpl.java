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

import static elemental2.dom.DomGlobal.console;
import static java.util.stream.Collectors.toMap;

import com.vertispan.lib.shortcuttrapper.BindType;
import com.vertispan.lib.shortcuttrapper.KeyboardShortcutEvent;
import com.vertispan.lib.shortcuttrapper.KeyboardShortcutEventHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.KeyboardEvent;
import elemental2.dom.Node;

/**
 *
 */
public class ShortcutTrapperImpl {
  private static final Logger LOGGER = Logger.getLogger(ShortcutTrapperImpl.class.getName());

  private final Collection<Binding> bindings = new ArrayList<>();
  private boolean enabled = true;

  public ShortcutTrapperImpl(Node node) {
    node.addEventListener("keypress", this::onBrowserEvent);
    node.addEventListener("keydown", this::onBrowserEvent);
    node.addEventListener("keyup", this::onBrowserEvent);
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.fine("Ready to receive events on " + node);
    }
  }


  public void bind(String shortcut, KeyboardShortcutEventHandler handler) {
    this.bind(shortcut, handler, null);
  }

  public void bind(String shortcut, KeyboardShortcutEventHandler handler, BindType bindType) {
    Optional<Binding> binding = ComboNormalizer.parse(shortcut, handler,
        bindType == null ? null : bindType.name().toLowerCase());
    binding.ifPresent(bindings::add);
  }

  public Map<String, KeyboardShortcutEventHandler> getBindings() {
    return bindings.stream()
        .collect(toMap(Binding::getCombo, Binding::getHandler));
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void removeAll() {
    bindings.clear();
  }

  public void debugBindings() {
    for (Binding binding : bindings) {
      console.log(binding.toString());
    }
  }


  private void handleKey(KeyInfo keyInfo, KeyboardEvent e) {
    for (Binding binding : bindings) {
      Optional<KeyboardShortcutEventHandler> optional = binding.visit(keyInfo);
      optional.ifPresent(
          keyboardShortcutEventHandler ->
              fireCallback(keyboardShortcutEventHandler, e, binding.getCombo()));
    }
  }

  private void fireCallback(KeyboardShortcutEventHandler handler, KeyboardEvent e, String combo) {
    if (stopCallback(e, (Element) e.target)) {
      return;
    }
    if (!handler.onShortcut(new KeyboardShortcutEvent(combo))) {
      e.preventDefault();
      e.stopPropagation();
    }
  }

  private boolean stopCallback(KeyboardEvent e, Element target) {
    // TODO implement
    return false;
  }

  private void onBrowserEvent(Event event) {
    if (!(event instanceof KeyboardEvent)) {
      // not sure how we got here
      throw new IllegalStateException("Invalid event type: " + event.type);
    }

    if (!enabled || bindings.isEmpty()) {
      return;
    }

    KeyboardEvent e = (KeyboardEvent) event;
    KeyInfo keyInfo = ComboNormalizer.getKeyInfo(e);
    if (LOGGER.isLoggable(Level.FINEST)) {
      // not exactly logger, but don't want toString()
      console.trace("ShortcutTrapper.onBrowserEvent", keyInfo, e);
    }
    handleKey(keyInfo, e);
  }
}
