/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.event;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.event.SelectStackModelEvent.SelectStackModelHandler;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class SelectStackModelEvent extends GwtEvent<SelectStackModelHandler> {

  public interface SelectStackModelHandler extends EventHandler {
    void onSetCurrentStackModel(SelectStackModelEvent event);
  }

  public static Type<SelectStackModelHandler> TYPE = new Type<SelectStackModelHandler>();
  private StackModel fileModel;

  public SelectStackModelEvent(StackModel fileModel) {
    this.fileModel = fileModel;
  }

  @Override
  public Type<SelectStackModelHandler> getAssociatedType() {
    return TYPE;
  }

  public StackModel getStackModel() {
    return fileModel;
  }

  @Override
  protected void dispatch(SelectStackModelHandler handler) {
    handler.onSetCurrentStackModel(this);
  }
}