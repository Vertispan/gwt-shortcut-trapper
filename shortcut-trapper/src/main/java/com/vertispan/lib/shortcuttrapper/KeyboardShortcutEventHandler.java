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

/**
 * Handler for ShortcutTrapper events.
 */
@FunctionalInterface
public interface KeyboardShortcutEventHandler {
  /**
   * Executes when a ShortcutTrapper traps a configured binding.
   *
   * @return false to prevent default browser behavior and stop event bubbling.
   */
  boolean onShortcut(KeyboardShortcutEvent event);
}
