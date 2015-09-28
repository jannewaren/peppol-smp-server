/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.smpserver.ui.pub;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.photon.basic.app.menu.IMenuTree;
import com.helger.photon.basic.app.menu.filter.MenuObjectFilterNoUserLoggedIn;

@Immutable
public final class MenuPublic
{
  private MenuPublic ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    aMenuTree.createRootItem (new PagePublicStart (CMenuPublic.MENU_START));

    // Not logged in
    aMenuTree.createRootSeparator ().setDisplayFilter (new MenuObjectFilterNoUserLoggedIn ());
    aMenuTree.createRootItem (new PagePublicLogin (CMenuPublic.MENU_LOGIN))
             .setDisplayFilter (new MenuObjectFilterNoUserLoggedIn ());

    // Set default
    aMenuTree.setDefaultMenuItemID (CMenuPublic.MENU_START);
  }
}