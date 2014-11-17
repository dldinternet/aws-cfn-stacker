/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.spreadsheet;

import java.util.List;

public interface Row {

  List<String> getColumns();

  int getId();

  void setColumns(List<String> columns);

  void setId(int id);
}
