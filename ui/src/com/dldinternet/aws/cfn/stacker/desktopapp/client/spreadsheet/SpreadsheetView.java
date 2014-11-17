/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.spreadsheet;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.FileBasedMiniAppView;

public interface SpreadsheetView extends FileBasedMiniAppView {

  int getSelectedColumn();

  int getSelectedRow();

  void refresh();

  void setValue(Worksheet worksheet);

  void updateDetails(String cellName, String cellValue);

  void updateGrid();

  void updateSelectedCells(String value);

}