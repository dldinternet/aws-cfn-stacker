/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.event.SelectFileModelEvent.SelectFileModelHandler;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.FileModel;

public class SelectFileModelEvent extends GwtEvent<SelectFileModelHandler> {

  public interface SelectFileModelHandler extends EventHandler {
    void onSetCurrentFileModel(SelectFileModelEvent event);
  }

  public static Type<SelectFileModelHandler> TYPE = new Type<SelectFileModelHandler>();
  private FileModel fileModel;

  public SelectFileModelEvent(FileModel fileModel) {
    this.fileModel = fileModel;
  }

  @Override
  public Type<SelectFileModelHandler> getAssociatedType() {
    return TYPE;
  }

  public FileModel getFileModel() {
    return fileModel;
  }

  @Override
  protected void dispatch(SelectFileModelHandler handler) {
    handler.onSetCurrentFileModel(this);
  }
}