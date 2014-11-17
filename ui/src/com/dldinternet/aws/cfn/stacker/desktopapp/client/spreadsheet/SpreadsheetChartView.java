/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.spreadsheet;

import com.google.gwt.user.client.ui.IsWidget;

public interface SpreadsheetChartView extends IsWidget {

  void configure(Worksheet worksheet);

  void setTitle(String newTitle);

}
