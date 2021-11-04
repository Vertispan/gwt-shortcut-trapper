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
package com.vertispan.test.lib.shortcuttrapper;

import static elemental2.dom.DomGlobal.console;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

import com.vertispan.lib.shortcuttrapper.internal.ShortcutTrapperImpl;
import com.vertispan.test.lib.shortcuttrapper.view.MainView;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ShortcutTrapperTest implements EntryPoint, UncaughtExceptionHandler {

  @Override
  public void onModuleLoad() {
    GWT.setUncaughtExceptionHandler(this);
    configureLogging();
    new MainView().show();
  }

  @Override
  public void onUncaughtException(Throwable throwable) {
    console.error("Uncaught exception", throwable);
  }

  private void configureLogging() {
    Logger.getGlobal().setLevel(Level.FINEST);
    Logger.getLogger(ShortcutTrapperImpl.class.getName()).setLevel(Level.FINE);
  }
}
