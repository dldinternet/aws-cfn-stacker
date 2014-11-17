/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * Presents the login view to the user.
 */
public interface LoginPresenter {

  /**
   * Creates a login view and connects it to the user interface.
   * 
   * @param hasWidgets the user interface
   */
  void go(HasWidgets hasWidgets);

  /**
   * Handles a login request from the view. Attempts to log a user into the
   * application.
   */
  void onLogin();

}