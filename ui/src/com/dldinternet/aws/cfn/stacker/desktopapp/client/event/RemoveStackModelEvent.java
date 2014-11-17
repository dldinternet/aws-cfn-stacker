/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.event;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.event.RemoveStackModelEvent.RemoveStackModelHandler;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RemoveStackModelEvent extends GwtEvent<RemoveStackModelHandler> {

  public interface RemoveStackModelHandler extends EventHandler {
    void onRemoveStackModel(RemoveStackModelEvent event);
  }

  public static Type<RemoveStackModelHandler> TYPE = new Type<RemoveStackModelHandler>();
  private StackModel fileModel;

  public RemoveStackModelEvent(StackModel fileModel) {
    this.fileModel = fileModel;
  }

  @Override
  public Type<RemoveStackModelHandler> getAssociatedType() {
    return TYPE;
  }

  public StackModel getStackModel() {
    return fileModel;
  }

  @Override
  protected void dispatch(RemoveStackModelHandler handler) {
    handler.onRemoveStackModel(this);
  }
}