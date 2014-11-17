/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.event;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.event.AddStackModelEvent.AddStackModelHandler;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class AddStackModelEvent extends GwtEvent<AddStackModelHandler> {

  public interface AddStackModelHandler extends EventHandler {
    void onAddStackModel(AddStackModelEvent event);
  }

  public static Type<AddStackModelHandler> TYPE = new Type<AddStackModelHandler>();
  private StackModel stackModel;

  public AddStackModelEvent(StackModel stackModel) {
    this.stackModel = stackModel;
  }

  @Override
  public Type<AddStackModelHandler> getAssociatedType() {
    return TYPE;
  }

  public StackModel getStackModel() {
    return stackModel;
  }

  @Override
  protected void dispatch(AddStackModelHandler handler) {
    handler.onAddStackModel(this);
  }
}