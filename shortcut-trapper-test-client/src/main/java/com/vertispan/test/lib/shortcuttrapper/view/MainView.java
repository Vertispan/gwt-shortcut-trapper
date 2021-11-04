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
package com.vertispan.test.lib.shortcuttrapper.view;

import static elemental2.dom.DomGlobal.document;
import static org.jboss.elemento.Elements.button;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.input;
import static org.jboss.elemento.Elements.option;
import static org.jboss.elemento.Elements.select;
import static org.jboss.elemento.Elements.span;
import static org.jboss.elemento.Elements.textarea;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import com.vertispan.lib.shortcuttrapper.KeyboardShortcutEvent;
import com.vertispan.lib.shortcuttrapper.ShortcutTrapper;

import org.jboss.elemento.EventType;
import org.jboss.elemento.InputType;

import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLTextAreaElement;

/**
 * The main view for the shortcut trapper tests.
 */
public class MainView {

  private static final Resources RESOURCES = GWT.create(Resources.class);
  private static final Style STYLE;

  static {
    STYLE = RESOURCES.style();
    STYLE.ensureInjected();
  }

  private final HTMLDivElement mainView;
  private HTMLDivElement console;
  private HTMLTextAreaElement field;

  private final ShortcutTrapper globalTrapper;
  private final ShortcutTrapper scopedTrapper;

  public MainView() {
    STYLE.ensureInjected();
    mainView = initUi();

    globalTrapper = ShortcutTrapper.getGlobal();
    scopedTrapper = ShortcutTrapper.getInstance(field);

    globalTrapper.bind("mod+=", this::onGlobalShortcut);
    globalTrapper.bind("ctrl+i", this::onGlobalShortcut);
    globalTrapper.bind("alt+left", this::onGlobalShortcut);
    globalTrapper.bind("command+i", this::onGlobalShortcut);
    globalTrapper.bind("meta+u", this::onGlobalShortcut);
    globalTrapper.bind("mod+y", this::onGlobalShortcut);
    globalTrapper.bind("g i", this::onGlobalShortcut);
    globalTrapper.bind("meta+2", this::onGlobalShortcut);
    globalTrapper.bind("up up down down left right left right b a enter", e -> {
      log("Konami code entered: " + e.getCombo());
      return true;
    });

    scopedTrapper.bind("ctrl+j", this::onScopedShortcut);
    scopedTrapper.bind("meta+2", this::onScopedShortcut);
  }

  public void show() {
    document.body.append(mainView);

    log("Ready");
  }

  private boolean onGlobalShortcut(KeyboardShortcutEvent event) {
    log("Received global shortcut keybinding: " + event.getCombo());
    return false;
  }

  private boolean onScopedShortcut(KeyboardShortcutEvent event) {
    log("Received scoped shortcut keybinding: " + event.getCombo());
    return false;
  }


  private void displayGlobalBindings(Event e) {
    log("-- Global Bindings ---------------");
    globalTrapper.getBindings().forEach((key, value) -> log(key));
    log("----------------------------------");
  }

  private void displayScopedBindings(Event e) {
    log("-- Scoped Bindings ---------------");
    scopedTrapper.getBindings().forEach((key, value) -> log(key));
    log("----------------------------------");
  }

  private void onAddBinding(Event e) {
    HTMLSelectElement bindingScope = (HTMLSelectElement) document.getElementById("bindingScope");
    HTMLInputElement combo = (HTMLInputElement) document.getElementById("comboCharacters");
    if ("Global".equals(bindingScope.value)) {
      globalTrapper.bind(combo.value, this::onGlobalShortcut);
      displayGlobalBindings(e);
    } else {
      scopedTrapper.bind(combo.value, this::onScopedShortcut);
      displayScopedBindings(e);
    }
  }

  private void onClearClick(Event e) {
    clearConsole();
  }

  private void toggle(ShortcutTrapper trapper) {
    trapper.setEnabled(!trapper.isEnabled());
    log((trapper == globalTrapper ? "Global" : "Scoped") + " is now "
        + (trapper.isEnabled() ? "enabled" : "disabled"));
  }

  private void removeAll(ShortcutTrapper trapper) {
    trapper.removeAll();
    if (trapper == globalTrapper) {
      displayGlobalBindings(null);
    } else {
      displayScopedBindings(null);
    }
  }

  private void log(String message) {
    HTMLElement newLine;
    console.append(
        newLine = span()
            .css(STYLE.consoleLogLine())
            .textContent(message)
            .element());
    newLine.scrollIntoView();
  }

  private void clearConsole() {
    console.innerHTML = "";
  }

  private HTMLDivElement initUi() {
    return div()
        .css(STYLE.container())
        .add(div()
            .css(STYLE.top())
            .add(div()
                .css(STYLE.menuArea())
                .add(div()
                    .css(STYLE.toolbar())
                    .add(button()
                        .on(EventType.click, this::onClearClick)
                        .textContent("Clear Console"))
                    .add(button()
                        .on(EventType.click, this::displayGlobalBindings)
                        .textContent("Display Global Bindings"))
                    .add(button()
                        .on(EventType.click, this::displayScopedBindings)
                        .textContent("Display Scoped Bindings"))
                    .add(button()
                        .title("Toggles enable/disable listening to bindings")
                        .textContent("Toggle Global")
                        .on(EventType.click, e -> this.toggle(globalTrapper)))
                    .add(button()
                        .title("Toggles enable/disable listening to bindings")
                        .textContent("Toggle Scoped")
                        .on(EventType.click, e -> this.toggle(scopedTrapper)))
                    .add(button()
                        .textContent("Flush all Global")
                        .on(EventType.click, e -> this.removeAll(globalTrapper)))
                    .add(button()
                        .textContent("Flush all scoped")
                        .on(EventType.click, e -> this.removeAll(scopedTrapper)))
                )
                .add(div()
                    .css(STYLE.fieldLabel())
                    .style("padding-top: 15px;")
                    .textContent("Add new Bindings")
                )
                .add(div()
                    .css(STYLE.form())
                    .add(input(InputType.text)
                        .id("comboCharacters"))
                    .add(select()
                        .id("bindingScope")
                        .add(option()
                            .textContent("Global"))
                        .add(option()
                            .textContent("Scoped")))
                    .add(button()
                        .on(EventType.click, this::onAddBinding)
                        .textContent("Add Binding"))
                )
            )
            .add(div()
                .css(STYLE.fieldHolder())
                .add(div()
                    .css(STYLE.fieldLabel())
                    .textContent("Focus on below textarea for scoped shortcuts"))
                .add(field = textarea()
                    .css(STYLE.field())
                    .element())))
        .add(console = div()
            .css(STYLE.console())
            .element())
        .element();
  }


  interface Resources extends ClientBundle {
    @Source("MainView.gss")
    Style style();
  }

  interface Style extends CssResource {
    String container();

    String console();

    String top();

    String field();

    String menuArea();

    String toolbar();

    String form();

    String fieldHolder();

    String fieldLabel();

    String consoleLogLine();
  }
}
