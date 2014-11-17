/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.event;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.event.OpenStackModelEvent.OpenStackModelHandler;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackPile;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class OpenStackModelEvent extends GwtEvent<OpenStackModelHandler> {

  public interface OpenStackModelHandler extends EventHandler {
    void onOpenStackModel(OpenStackModelEvent event);
  }

  public static Type<OpenStackModelHandler> TYPE = new Type<OpenStackModelHandler>();
  private StackPile stackPile;
  private StackModel stackModel;

  public OpenStackModelEvent(StackPile stackPile, StackModel stackModel) {
    this.stackPile = stackPile;
    this.stackModel = stackModel;
  }

  @Override
  public Type<OpenStackModelHandler> getAssociatedType() {
    return TYPE;
  }

  public StackModel getStackModel() {
    return stackModel;
  }

  public StackPile getStackPile() {
    return stackPile;
  }

  @Override
  protected void dispatch(OpenStackModelHandler handler) {
    handler.onOpenStackModel(this);
  }
}