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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import elemental2.dom.DomGlobal;

/**
 *
 */
public class Binding {
  private static final double SEQUENCE_DELAY = 1000;
  private static final Logger LOGGER = Logger.getLogger(Binding.class.getName());

  private final String combo;
  private final LinkedList<KeyInfo> linkedList;
  private final KeyboardShortcutEventHandler handler;

  private KeyInfo current;
  private Iterator<KeyInfo> iterator;
  private double timerId;

  public Binding(@Nonnull String combo, @Nonnull KeyInfo keyInfo,
      @Nonnull KeyboardShortcutEventHandler handler) {
    this(combo, Collections.singletonList(keyInfo), handler);
  }

  public Binding(String combo, @Nonnull Collection<KeyInfo> keyInfos,
      @Nonnull KeyboardShortcutEventHandler handler) {
    this.combo = combo;
    this.linkedList = new LinkedList<>(keyInfos);
    this.handler = handler;

    iterator = linkedList.iterator();
    current = iterator.next();
  }

  @Nonnull
  Optional<KeyboardShortcutEventHandler> visit(KeyInfo keyInfo) {
    // do quick check and return
    if (!current.equals(keyInfo)) {
      // doesn't match, reset and move on.
      return Optional.empty();
    }

    // matches current key info.
    // if we still have items in the sequence, let's do that.
    if (iterator.hasNext()) {
      // increment to next KeyInfo
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.fine("Started binding sequence");
      }
      current = iterator.next();
      resetTimer();

      // that's all we can do right now.
      return Optional.empty();
    }

    // no more items in sequence... reset and run handler
    reset();
    return Optional.of(handler);
  }

  String getCombo() {
    return combo;
  }

  KeyboardShortcutEventHandler getHandler() {
    return handler;
  }

  private void reset() {
    iterator = linkedList.iterator();
    current = iterator.next();
  }

  private void resetTimer() {
    DomGlobal.clearTimeout(timerId);
    timerId = DomGlobal.setTimeout(arg -> reset(), SEQUENCE_DELAY);
  }

  @Override
  public String toString() {
    return "Binding{" +
        "linkedList=" + linkedList +
        ", combo='" + combo + '\'' +
        '}';
  }
}
