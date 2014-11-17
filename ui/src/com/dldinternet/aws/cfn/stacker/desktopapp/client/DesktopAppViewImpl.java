/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.dldinternet.aws.cfn.stacker.desktop.client.layout.DesktopLayoutType;
import com.dldinternet.aws.cfn.stacker.desktop.client.widget.Desktop;
import com.dldinternet.aws.cfn.stacker.desktop.client.widget.Shortcut;
import com.dldinternet.aws.cfn.stacker.desktop.client.widget.StartMainMenuItem;
import com.dldinternet.aws.cfn.stacker.desktop.client.widget.StartToolMenuItem;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.images.DesktopImages;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;

import java.util.Iterator;

/**
 * Provides a desktop application view.
 * 
 * @see DesktopAppView
 */
public class DesktopAppViewImpl implements DesktopAppView {

  private Desktop desktop;
  private Shortcut fileManagerShortcut;
  private Shortcut stackManagerShortcut;
  private StartMainMenuItem fileManagerStartMenuItem;
  private StartMainMenuItem stackManagerStartMenuItem;
  private StartToolMenuItem cascadeToolMenuItem;
  private StartToolMenuItem tileToolMenuItem;
  private StartToolMenuItem updateProfileToolMenuItem;
  private StartToolMenuItem logoutToolMenuItem;
  private SelectionHandler<Item> fileManagerStartMenuListener;
  private SelectionHandler<Item> stackManagerStartMenuListener;
  private SelectHandler fileManagerShortcutListener;
  private SelectHandler stackManagerShortcutListener;
  private DesktopAppPresenter desktopAppPresenter;

  /**
   * Creates a desktop application view that interacts with the rest of the application using the specified presenter.
   * 
   * @param desktopAppPresenter the source of commands and data and the target of user initiated events.
   */
  public DesktopAppViewImpl(DesktopAppPresenter desktopAppPresenter) {
    this.desktopAppPresenter = desktopAppPresenter;
  }

  @Override
  public void add(Widget widget) {
    getDesktop().activate(widget);
  }

  @Override
  public Widget asWidget() {
    return getDesktop().asWidget();
  }

  @Override
  public void clear() {
    // TODO: implement
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<Widget> iterator() {
    // TODO: implement
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Widget w) {
    // TODO: implement
    throw new UnsupportedOperationException();
  }

  @Override
  public void setDesktopLayoutType(DesktopLayoutType desktopLayoutType) {
    getDesktop().setDesktopLayoutType(desktopLayoutType);
  }

  @Override
  public void setScaleOnResize(boolean scaleOnResize) {
    getDesktop().setScaleOnResize(scaleOnResize);
  }

  private StartToolMenuItem getCascadeToolMenuItem() {
    if (cascadeToolMenuItem == null) {
      cascadeToolMenuItem = new StartToolMenuItem("Cascade");
      cascadeToolMenuItem.setIcon(DesktopImages.INSTANCE.application_cascade());
      cascadeToolMenuItem.addSelectionHandler(new SelectionHandler<Item>() {
        @Override
        public void onSelection(SelectionEvent<Item> event) {
          getDesktop().layout(DesktopLayoutType.CASCADE);
        }
      });
    }
    return cascadeToolMenuItem;
  }

  private Desktop getDesktop() {
    if (desktop == null) {
      desktop = new Desktop();
      desktop.addShortcut(getFileManagerShortcut());
      desktop.addShortcut(getStackManagerShortcut());
      desktop.addStartMenuItem(getFileManagerStartMenuItem());
      desktop.addStartMenuItem(getStackManagerStartMenuItem());
      desktop.setStartMenuHeading(getPresenter().getCurrentUser());
      desktop.setStartMenuIcon(DesktopImages.INSTANCE.user());
      desktop.addToolMenuItem(getCascadeToolMenuItem());
      desktop.addToolMenuItem(getTileToolMenuItem());
      desktop.addToolSeparator();
      desktop.addToolMenuItem(getUpdateProfileToolMenuItem());
      desktop.addToolSeparator();
      desktop.addToolMenuItem(getLogoutToolMenuItem());
    }
    return desktop;
  }

  private DesktopAppPresenter getDesktopAppPresenter() {
    return desktopAppPresenter;
  }

  private Shortcut getFileManagerShortcut() {
    if (fileManagerShortcut == null) {
      fileManagerShortcut = new Shortcut();
      fileManagerShortcut.setText("File Manager");
      fileManagerShortcut.setIcon(DesktopImages.INSTANCE.folder_shortcut());
      fileManagerShortcut.addSelectHandler(getFileManagerShortcutListener());
    }
    return fileManagerShortcut;
  }

  private Shortcut getStackManagerShortcut() {
    if (stackManagerShortcut == null) {
      stackManagerShortcut = new Shortcut();
      stackManagerShortcut.setText("Stack Manager");
      stackManagerShortcut.setIcon(DesktopImages.INSTANCE.stack_shortcut());
      stackManagerShortcut.addSelectHandler(getStackManagerShortcutListener());
    }
    return stackManagerShortcut;
  }

  private SelectHandler getFileManagerShortcutListener() {
    if (fileManagerShortcutListener == null) {
      fileManagerShortcutListener = new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          getDesktopAppPresenter().onOpenFileManager();
        }
      };
    }
    return fileManagerShortcutListener;
  }

  private SelectHandler getStackManagerShortcutListener() {
    if (stackManagerShortcutListener == null) {
      stackManagerShortcutListener = new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          getDesktopAppPresenter().onOpenStackManager();
        }
      };
    }
    return stackManagerShortcutListener;
  }

  private StartMainMenuItem getFileManagerStartMenuItem() {
    if (fileManagerStartMenuItem == null) {
      fileManagerStartMenuItem = new StartMainMenuItem("File Manager");
      fileManagerStartMenuItem.setIcon(DesktopImages.INSTANCE.folder());
      fileManagerStartMenuItem.addSelectionHandler(getFileManagerStartMenuListener());
    }
    return fileManagerStartMenuItem;
  }

  private StartMainMenuItem getStackManagerStartMenuItem() {
    if (stackManagerStartMenuItem == null) {
      stackManagerStartMenuItem = new StartMainMenuItem("Stack Manager");
      stackManagerStartMenuItem.setIcon(DesktopImages.INSTANCE.stack());
      stackManagerStartMenuItem.addSelectionHandler(getStackManagerStartMenuListener());
    }
    return stackManagerStartMenuItem;
  }

  private SelectionHandler<Item> getFileManagerStartMenuListener() {
    if (fileManagerStartMenuListener == null) {
      fileManagerStartMenuListener = new SelectionHandler<Item>() {
        @Override
        public void onSelection(SelectionEvent<Item> event) {
          getDesktopAppPresenter().onOpenFileManager();
        }
      };
    }
    return fileManagerStartMenuListener;
  }

  private SelectionHandler<Item> getStackManagerStartMenuListener() {
    if (stackManagerStartMenuListener == null) {
      stackManagerStartMenuListener = new SelectionHandler<Item>() {
        @Override
        public void onSelection(SelectionEvent<Item> event) {
          getDesktopAppPresenter().onOpenStackManager();
        }
      };
    }
    return stackManagerStartMenuListener;
  }

  private StartToolMenuItem getLogoutToolMenuItem() {
    if (logoutToolMenuItem == null) {
      logoutToolMenuItem = new StartToolMenuItem("Logout");
      logoutToolMenuItem.setIcon(DesktopImages.INSTANCE.door_out());
      logoutToolMenuItem.addSelectionHandler(new SelectionHandler<Item>() {
        @Override
        public void onSelection(SelectionEvent<Item> event) {
          getPresenter().onLogout();
        }
      });
    }
    return logoutToolMenuItem;
  }

  private DesktopAppPresenter getPresenter() {
    return desktopAppPresenter;
  }

  private StartToolMenuItem getTileToolMenuItem() {
    if (tileToolMenuItem == null) {
      tileToolMenuItem = new StartToolMenuItem("Tile");
      tileToolMenuItem.setIcon(DesktopImages.INSTANCE.application_tile_horizontal());
      tileToolMenuItem.addSelectionHandler(new SelectionHandler<Item>() {
        @Override
        public void onSelection(SelectionEvent<Item> event) {
          getDesktop().layout(DesktopLayoutType.TILE);
        }
      });
    }
    return tileToolMenuItem;
  }

  private StartToolMenuItem getUpdateProfileToolMenuItem() {
    if (updateProfileToolMenuItem == null) {
      updateProfileToolMenuItem = new StartToolMenuItem("Settings");
      updateProfileToolMenuItem.setIcon(DesktopImages.INSTANCE.user_edit());
      updateProfileToolMenuItem.addSelectionHandler(new SelectionHandler<Item>() {
        @Override
        public void onSelection(SelectionEvent<Item> event) {
          getDesktopAppPresenter().onOpenProfile();
        }
      });
    }
    return updateProfileToolMenuItem;
  }

}
