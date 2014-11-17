/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel.StackType;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

public class StackManagerTreeGrid extends TreeGrid<StackModel> {

  public StackManagerTreeGrid(TreeStore<StackModel> store, ColumnModel<StackModel> cm,
                              ColumnConfig<StackModel, ?> treeColumn) {
    super(store, cm, treeColumn);
  }

  @Override
  public boolean isLeaf(StackModel model) {
    return model.getStackType() != StackType.FOLDER;
  }

  public void unbind() {
    if (storeHandlerRegistration != null) {
      storeHandlerRegistration.removeHandler();
    }
  }

  @Override
  protected void onClick(Event event) {
    super.onClick(event);
    EventTarget eventTarget = event.getEventTarget();
    if (Element.is(eventTarget)) {
      StackModel m = store.get(getView().findRowIndex(Element.as(eventTarget)));
      if (m == null) {
        getSelectionModel().deselectAll();
      }
    }
  }
}