/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.filemanager.images.Images;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel.StackType;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;

public class StackManagerMenu implements IsWidget {

  private static final String STACK_TYPE = "stackType";

  private StackManagerPresenter stackManagerPresenter;

  private Menu fileMenu;
  private MenuItem createFolderMenuItem;
  private MenuItem createDocumentMenuItem;
  private MenuItem createSpreadsheetMenuItem;
  private MenuItem createProgramMenuItem;
  private MenuItem createBookmarkMenuItem;
  private MenuItem collapseMenuItem;
  private MenuItem expandMenuItem;
  private MenuItem editNameMenuItem;
  private MenuItem openMenuItem;
  private MenuItem deleteMenuItem;
  private BeforeShowHandler beforeShowHandler;
  private SelectionHandler<MenuItem> createSelectionHandler;
  private SelectionHandler<MenuItem> expandSelectionHandler;
  private SelectionHandler<MenuItem> collapseSelectionHandler;
  private SelectionHandler<MenuItem> editNameSelectionHandler;
  private SelectionHandler<MenuItem> openSelectionHandler;
  private SelectionHandler<MenuItem> deleteSelectionHandler;

  public StackManagerMenu(StackManagerPresenter stackManagerPresenter) {
    this.stackManagerPresenter = stackManagerPresenter;
  }

  public Widget asWidget() {
    getStackMenu();
    return fileMenu;
  }

  public Menu getStackMenu() {
    if (fileMenu == null) {
      fileMenu = new Menu();
      fileMenu.add(getCreateFolderMenuItem());
      fileMenu.add(new SeparatorMenuItem());
      fileMenu.add(getCreateStackMenuItem());
      fileMenu.add(new SeparatorMenuItem());
      fileMenu.add(getExpandMenuItem());
      fileMenu.add(getCollapseMenuItem());
      fileMenu.add(new SeparatorMenuItem());
      fileMenu.add(getEditNameMenuItem());
      fileMenu.add(new SeparatorMenuItem());
      fileMenu.add(getOpenMenuItem());
      fileMenu.add(new SeparatorMenuItem());
      fileMenu.add(getDeleteMenuItem());
      fileMenu.addBeforeShowHandler(getBeforeShowHandler());
    }
    return fileMenu;
  }

  protected StackManagerPresenter getPresenter() {
    return stackManagerPresenter;
  }

  private BeforeShowHandler getBeforeShowHandler() {
    if (beforeShowHandler == null) {
      beforeShowHandler = new BeforeShowHandler() {
        @Override
        public void onBeforeShow(BeforeShowEvent event) {
          boolean isEnableCreate = getPresenter().isEnableCreate();
          boolean isEnableOpen = getPresenter().isEnableOpen();
          boolean isEnableDelete = getPresenter().isEnableDelete();
          boolean isEnableEditName = getPresenter().isEnableEditName();
          getCreateFolderMenuItem().setEnabled(isEnableCreate);
          getCreateStackMenuItem().setEnabled(isEnableCreate);
          getExpandMenuItem().setEnabled(isEnableCreate);
          getCollapseMenuItem().setEnabled(isEnableCreate);
          getEditNameMenuItem().setEnabled(isEnableEditName);
          getOpenMenuItem().setEnabled(isEnableOpen);
          getDeleteMenuItem().setEnabled(isEnableDelete);
        }
      };
    }
    return beforeShowHandler;
  }

  private MenuItem getCollapseMenuItem() {
    if (collapseMenuItem == null) {
      collapseMenuItem = new MenuItem("Collapse", getCollapseSelectionHandler());
      collapseMenuItem.setIcon(Images.getImageResources().arrow_in());
    }
    return collapseMenuItem;
  }

  private SelectionHandler<MenuItem> getCollapseSelectionHandler() {
    if (collapseSelectionHandler == null) {
      collapseSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          getPresenter().onCollapse();
        }
      };
    }
    return collapseSelectionHandler;
  }

  private MenuItem getCreateStackMenuItem() {
    if (createDocumentMenuItem == null) {
      createDocumentMenuItem = new MenuItem("New Document", getCreateSelectionHandler());
      createDocumentMenuItem.setIcon(Images.getImageResources().page_white_add());
      createDocumentMenuItem.setData(STACK_TYPE, StackType.STACK);
    }
    return createDocumentMenuItem;
  }

  private MenuItem getCreateFolderMenuItem() {
    if (createFolderMenuItem == null) {
      createFolderMenuItem = new MenuItem("New Folder", getCreateSelectionHandler());
      createFolderMenuItem.setIcon(Images.getImageResources().folder_add());
      createFolderMenuItem.setData(STACK_TYPE, StackType.FOLDER);
    }
    return createFolderMenuItem;
  }

  private SelectionHandler<MenuItem> getCreateSelectionHandler() {
    if (createSelectionHandler == null) {
      createSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          StackType fileType = event.getSelectedItem().<StackType> getData(STACK_TYPE);
          getPresenter().onCreate(fileType);
        }
      };
    }
    return createSelectionHandler;
  }

  private MenuItem getDeleteMenuItem() {
    if (deleteMenuItem == null) {
      deleteMenuItem = new MenuItem("Delete", getDeleteSelectionHandler());
      deleteMenuItem.setIcon(Images.getImageResources().bin_closed());
    }
    return deleteMenuItem;
  }

  private SelectionHandler<MenuItem> getDeleteSelectionHandler() {
    if (deleteSelectionHandler == null) {
      deleteSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          getPresenter().onDelete();
        }
      };
    }
    return deleteSelectionHandler;
  }

  private MenuItem getEditNameMenuItem() {
    if (editNameMenuItem == null) {
      editNameMenuItem = new MenuItem("Edit Name", getEditNameSelectionHandler());
      editNameMenuItem.setIcon(Images.getImageResources().textfield_rename());
    }
    return editNameMenuItem;
  }

  private SelectionHandler<MenuItem> getEditNameSelectionHandler() {
    if (editNameSelectionHandler == null) {
      editNameSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          getPresenter().onEditName();
        }
      };
    }
    return editNameSelectionHandler;
  }

  private MenuItem getExpandMenuItem() {
    if (expandMenuItem == null) {
      expandMenuItem = new MenuItem("Expand", getExpandSelectionHandler());
      expandMenuItem.setIcon(Images.getImageResources().arrow_out());
    }
    return expandMenuItem;
  }

  private SelectionHandler<MenuItem> getExpandSelectionHandler() {
    if (expandSelectionHandler == null) {
      expandSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          getPresenter().onExpand();
        }
      };
    }
    return expandSelectionHandler;
  }

  private MenuItem getOpenMenuItem() {
    if (openMenuItem == null) {
      openMenuItem = new MenuItem("Open", getOpenSelectionHandler());
      openMenuItem.setIcon(Images.getImageResources().bullet_go());
    }
    return openMenuItem;
  }

  private SelectionHandler<MenuItem> getOpenSelectionHandler() {
    if (openSelectionHandler == null) {
      openSelectionHandler = new SelectionHandler<MenuItem>() {
        @Override
        public void onSelection(SelectionEvent<MenuItem> event) {
          getPresenter().onOpen();
        }
      };
    }
    return openSelectionHandler;
  }

}
