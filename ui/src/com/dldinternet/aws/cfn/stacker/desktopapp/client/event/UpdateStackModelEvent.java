/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.event;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.event.UpdateStackModelEvent.UpdateStackModelHandler;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class UpdateStackModelEvent extends GwtEvent<UpdateStackModelHandler> {

  public interface UpdateStackModelHandler extends EventHandler {
    void onUpdateStackModel(UpdateStackModelEvent event);
  }

  public static Type<UpdateStackModelHandler> TYPE = new Type<UpdateStackModelHandler>();
  private StackModel fileModel;

  public UpdateStackModelEvent(StackModel fileModel) {
    this.fileModel = fileModel;
  }

  @Override
  public Type<UpdateStackModelHandler> getAssociatedType() {
    return TYPE;
  }

  public StackModel getStackModel() {
    return fileModel;
  }

  @Override
  protected void dispatch(UpdateStackModelHandler handler) {
    handler.onUpdateStackModel(this);
  }

}