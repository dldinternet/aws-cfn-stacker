/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel.StackType;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager.images.Images;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class StackManagerToolBar implements IsWidget {

  private static final String STACK_TYPE = "stackType";

  private StackManagerPresenter stackManagerPresenter;

  private ToolBar toolBar;
  private TextButton createFolderTextButton;
  private TextButton createDocumentTextButton;
  private TextButton createSpreadsheetTextButton;
  private TextButton createProgramTextButton;
  private TextButton createBookmarkTextButton;
  private TextButton collapseTextButton;
  private TextButton expandTextButton;
  private TextButton editNameTextButton;
  private TextButton openTextButton;
  private TextButton deleteTextButton;
  private SelectHandler createSelectHandler;
  private SelectHandler expandSelectHandler;
  private SelectHandler collapseSelectHandler;
  private SelectHandler editNameSelectHandler;
  private SelectHandler openSelectHandler;
  private SelectHandler deleteSelectHandler;

  public StackManagerToolBar(StackManagerPresenter stackManagerPresenter) {
    this.stackManagerPresenter = stackManagerPresenter;
  }

  public Widget asWidget() {
    return getToolBar();
  }

  public void setButtonEnabledState() {
    boolean isEnableCreate = getPresenter().isEnableCreate();
    boolean isEnableOpen = getPresenter().isEnableOpen();
    boolean isEnableDelete = getPresenter().isEnableDelete();
    boolean isEnableEditName = getPresenter().isEnableEditName();
    getCreateFolderTextButton().setEnabled(isEnableCreate);
    getCreateStackTextButton().setEnabled(isEnableCreate);
    getExpandTextButton().setEnabled(isEnableCreate);
    getCollapseTextButton().setEnabled(isEnableCreate);
    getEditNameTextButton().setEnabled(isEnableEditName);
    getOpenTextButton().setEnabled(isEnableOpen);
    getDeleteTextButton().setEnabled(isEnableDelete);
  }

  protected StackManagerPresenter getPresenter() {
    return stackManagerPresenter;
  }

  private SelectHandler getCollapseSelectHandler() {
    if (collapseSelectHandler == null) {
      collapseSelectHandler = new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          getPresenter().onCollapse();
        }
      };
    }
    return collapseSelectHandler;
  }

  private TextButton getCollapseTextButton() {
    if (collapseTextButton == null) {
      collapseTextButton = new TextButton();
      collapseTextButton.setToolTip("Collapse");
      collapseTextButton.setIcon(Images.getImageResources().arrow_in());
      collapseTextButton.addSelectHandler(getCollapseSelectHandler());
    }
    return collapseTextButton;
  }

  private TextButton getCreateStackTextButton() {
    if (createDocumentTextButton == null) {
      createDocumentTextButton = new TextButton();
      createDocumentTextButton.setToolTip("New Document");
      createDocumentTextButton.setIcon(Images.getImageResources().stack_add());
      createDocumentTextButton.setData(STACK_TYPE, StackType.STACK);
      createDocumentTextButton.addSelectHandler(getCreateSelectHandler());
    }
    return createDocumentTextButton;
  }

  private TextButton getCreateFolderTextButton() {
    if (createFolderTextButton == null) {
      createFolderTextButton = new TextButton();
      createFolderTextButton.setToolTip("New Folder");
      createFolderTextButton.setIcon(Images.getImageResources().folder_add());
      createFolderTextButton.setData(STACK_TYPE, StackType.FOLDER);
      createFolderTextButton.addSelectHandler(getCreateSelectHandler());
    }
    return createFolderTextButton;
  }

  private SelectHandler getCreateSelectHandler() {
    if (createSelectHandler == null) {
      createSelectHandler = new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          StackType fileType = ((TextButton) event.getSource()).<StackType> getData(STACK_TYPE);
          getPresenter().onCreate(fileType);
        }
      };
    }
    return createSelectHandler;
  }

  private SelectHandler getDeleteSelectHandler() {
    if (deleteSelectHandler == null) {
      deleteSelectHandler = new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          getPresenter().onDelete();
        }
      };
    }
    return deleteSelectHandler;
  }

  private TextButton getDeleteTextButton() {
    if (deleteTextButton == null) {
      deleteTextButton = new TextButton();
      deleteTextButton.setToolTip("Delete");
      deleteTextButton.setIcon(Images.getImageResources().bin_closed());
      deleteTextButton.addSelectHandler(getDeleteSelectHandler());
    }
    return deleteTextButton;
  }

  private SelectHandler getEditNameSelectHandler() {
    if (editNameSelectHandler == null) {
      editNameSelectHandler = new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          getPresenter().onEditName();
        }
      };
    }
    return editNameSelectHandler;
  }

  private TextButton getEditNameTextButton() {
    if (editNameTextButton == null) {
      editNameTextButton = new TextButton();
      editNameTextButton.setToolTip("Edit Name");
      editNameTextButton.setIcon(Images.getImageResources().textfield_rename());
      editNameTextButton.addSelectHandler(getEditNameSelectHandler());
    }
    return editNameTextButton;
  }

  private SelectHandler getExpandSelectHandler() {
    if (expandSelectHandler == null) {
      expandSelectHandler = new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          getPresenter().onExpand();
        }
      };
    }
    return expandSelectHandler;
  }

  private TextButton getExpandTextButton() {
    if (expandTextButton == null) {
      expandTextButton = new TextButton();
      expandTextButton.setToolTip("Expand");
      expandTextButton.addSelectHandler(getExpandSelectHandler());
      expandTextButton.setIcon(Images.getImageResources().arrow_out());
    }
    return expandTextButton;
  }

  private SelectHandler getOpenSelectHandler() {
    if (openSelectHandler == null) {
      openSelectHandler = new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          getPresenter().onOpen();
        }
      };
    }
    return openSelectHandler;
  }

  private TextButton getOpenTextButton() {
    if (openTextButton == null) {
      openTextButton = new TextButton();
      openTextButton.setToolTip("Open");
      openTextButton.setIcon(Images.getImageResources().bullet_go());
      openTextButton.addSelectHandler(getOpenSelectHandler());
    }
    return openTextButton;
  }

  private Widget getToolBar() {
    if (toolBar == null) {
      toolBar = new ToolBar();
      toolBar.add(getCreateFolderTextButton());
      toolBar.add(new SeparatorToolItem());
      toolBar.add(getCreateStackTextButton());
      toolBar.add(new SeparatorToolItem());
      toolBar.add(getExpandTextButton());
      toolBar.add(getCollapseTextButton());
      toolBar.add(new SeparatorToolItem());
      toolBar.add(getEditNameTextButton());
      toolBar.add(new SeparatorToolItem());
      toolBar.add(getOpenTextButton());
      toolBar.add(new SeparatorToolItem());
      toolBar.add(getDeleteTextButton());
    }
    return toolBar;
  }

}
