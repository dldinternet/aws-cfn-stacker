/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.DesktopBus;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.event.OpenStackModelEvent;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.event.SelectStackModelEvent;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel.StackType;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackPile;
import com.google.gwt.user.client.ui.HasWidgets;

import java.util.Date;
import java.util.List;

public class StackManagerPresenterImpl implements StackManagerPresenter {

  private StackPile stackPile;
  private DesktopBus desktopBus;

  private StackManagerView stackManagerView;
  private boolean isNewlyCreated;

  public StackManagerPresenterImpl(StackPile stackPile, DesktopBus desktopBus) {
    this.stackPile = stackPile;
    this.desktopBus = desktopBus;
  }

  public DesktopBus getDesktopBus() {
    return desktopBus;
  }

  @Override
  public void go(HasWidgets hasWidgets) {
    hasWidgets.add(getStackManagerView().asWidget());
  }

  @Override
  public boolean isEnableCreate() {
    StackModel selectedItem = getStackManagerView().getSelectedItem();
    StackType fileType = selectedItem == null ? null : selectedItem.getStackType();
    return fileType == null || fileType == StackType.FOLDER;
  }

  @Override
  public boolean isEnableDelete() {
    StackModel selectedItem = getStackManagerView().getSelectedItem();
    return selectedItem != null;
  }

  @Override
  public boolean isEnableEditName() {
    StackModel selectedItem = getStackManagerView().getSelectedItem();
    return selectedItem != null;
  }

  @Override
  public boolean isEnableOpen() {
    boolean isEnableOpen = false;
    StackModel selectedItem = getStackManagerView().getSelectedItem();
    if (selectedItem != null) {
      StackType fileType = selectedItem.getStackType();
      switch (fileType) {
        case STACK:
          isEnableOpen = true;
          break;
        case FOLDER:
          // do nothing
          break;
      }
    }
    return isEnableOpen;
  }

  @Override
  public void onCollapse() {
    getStackManagerView().collapse();
  }

  @Override
  public void onCreate(StackType stackType) {
    StackModel parentStackModel = getStackManagerView().getSelectedItem();
    String name = getStackPile().getNextUntitledStackName(parentStackModel, stackType);
    StackModel childStackModel = getStackPile().createStackModel(parentStackModel, name, stackType);
    getStackManagerView().selectStackModel(childStackModel);
    isNewlyCreated = true;
    getStackManagerView().editName(childStackModel);
  }

  @Override
  public void onDelete() {
    List<StackModel> fileModels = getStackManagerView().getSelectedItems();
    for (StackModel fileModel : fileModels) {
      getStackPile().remove(fileModel);
    }
  }

  @Override
  public void onEditStackNameComplete(boolean isSaved) {
    StackModel fileModel = getStackManagerView().getSelectedItem();
    if (fileModel != null) {
      fileModel.setLastModified(new Date());
    }
    if (isNewlyCreated) {
      isNewlyCreated = false;
      if (isSaved) {
        onOpen();
      } else {
        onDelete();
      }
    }
  }

  @Override
  public void onEditName() {
    StackModel selectedItem = getStackManagerView().getSelectedItem();
    if (selectedItem != null) {
      getStackManagerView().editName(selectedItem);
    }
  }

  @Override
  public void onExpand() {
    getStackManagerView().expand();
  }

  @Override
  public void onOpen() {
    List<StackModel> fileModels = getStackManagerView().getSelectedItems();
    for (StackModel fileModel : fileModels) {
      getDesktopBus().fireOpenStackModelEvent(new OpenStackModelEvent(getStackPile(), fileModel));
    }
  }

  @Override
  public void onSelect(StackModel stackModel) {
    getDesktopBus().fireSelectStackModelEvent(new SelectStackModelEvent(stackModel));
  }

  private StackManagerView getStackManagerView() {
    if (stackManagerView == null) {
      stackManagerView = new StackManagerViewImpl(this, getStackPile());
    }
    return stackManagerView;
  }

  private StackPile getStackPile() {
    return stackPile;
  }
}
