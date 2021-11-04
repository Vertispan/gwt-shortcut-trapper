package com.vertispan.test.lib.shortcuttrapper;

import static elemental2.dom.DomGlobal.console;

import com.google.gwt.core.client.EntryPoint;

import com.vertispan.lib.shortcuttrapper.ShortcutTrapper;

/**
 *
 */
public class ShortcutTrapperTest implements EntryPoint {

  @Override
  public void onModuleLoad() {
    ShortcutTrapper trapper = new ShortcutTrapper();
    console.log(trapper);
  }
}
