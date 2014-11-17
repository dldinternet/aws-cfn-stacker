/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client;

import com.google.gwt.user.client.ui.IsWidget;

public interface FileBasedMiniAppView extends IsWidget {

  void close();

  String getValue();

  void setTitle(String newTitle);

  void setValue(String value);

}
