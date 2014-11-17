/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.spreadsheet;

import com.sencha.gxt.core.client.ValueProvider;

public class RowValueProvider implements ValueProvider<Row, String> {
  private int columnIndex;

  public RowValueProvider(int columnIndex) {
    this.columnIndex = columnIndex;
  }

  @Override
  public String getPath() {
    return Integer.toString(columnIndex);
  }

  @Override
  public String getValue(Row row) {
    return row.getColumns().get(columnIndex);
  }

  @Override
  public void setValue(Row row, String value) {
    row.getColumns().set(columnIndex, value);
  }
}