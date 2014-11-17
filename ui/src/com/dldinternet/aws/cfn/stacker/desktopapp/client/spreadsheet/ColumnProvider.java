/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.spreadsheet;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;

public interface ColumnProvider {

  ColumnConfig<Row, Object> getColumn(int cellIndex);

  void setColumnHeader(int columnIndex, SafeHtml fromString);

}