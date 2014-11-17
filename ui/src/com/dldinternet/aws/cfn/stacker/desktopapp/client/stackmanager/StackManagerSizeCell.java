/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackPile;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class StackManagerSizeCell extends AbstractCell<Long> {
  private StackPile fileSystem;

  public StackManagerSizeCell(StackPile fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public void render(Context context, Long size, SafeHtmlBuilder sb) {
    if (size == null) {
      size = Long.valueOf(0);
    }
    sb.append(size);
    String key = (String) context.getKey();
    StackModel fileModel = fileSystem.getTreeStore().findModelWithKey(key);
    switch (fileModel.getStackType()) {
      case FOLDER:
        if (size == 1) {
          sb.appendEscaped(" Stack");
        } else {
          sb.appendEscaped(" Stacks");
        }
        break;
      default:
        if (size == 1) {
          sb.appendEscaped(" Resource");
        } else {
          sb.appendEscaped(" Resources");
        }
        break;
    }
  }
}