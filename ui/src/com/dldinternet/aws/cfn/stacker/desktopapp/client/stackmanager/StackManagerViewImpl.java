/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.filemanager.images.Images;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel.StackType;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModelProperties;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackPile;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.TreeGridDragSource;
import com.sencha.gxt.dnd.core.client.TreeGridDropTarget;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent.CancelEditHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StackManagerViewImpl implements StackManagerView, HideHandler {

  private static final String TITLE = "Stack Manager";

  private StackPile                             stackPile;
  private StackManagerPresenter                 stackManagerPresenter;

  private Window                                window;
  private VerticalLayoutContainer               verticalLayoutContainer;
  private StackManagerTreeGrid                  treeGrid;
  private ColumnConfig<StackModel, StackType>   typeConfig;
  private ColumnConfig<StackModel, String>      nameConfig;
  private ColumnConfig<StackModel, Date>        dateConfig;
  private ColumnConfig<StackModel, Long>        sizeConfig;
  private ColumnModel<StackModel>               columnModel;
  private StackManagerIconProvider              stackManagerIconProvider;
  private StackManagerSizeCell                  stackManagerSizeCell;
  private StackManagerGridInlineEditing         gridEditing;
  private StackManagerToolBar                   stackManagerToolBar;
  private StackManagerMenu                      stackManagerMenu;
  private SelectionChangedHandler<StackModel>   selectionChangedHandler;
  private TreeGridDragSource<StackModel>        treeGridDragSource;
  private TreeGridDropTarget<StackModel>        treeGridDropTarget;
  private RowDoubleClickHandler                 rowDoubleClickHandler;
  private CompleteEditHandler<StackModel>       completeEditHandler;
  private CancelEditHandler<StackModel>         cancelEditHandler;
  private StackModel                            editStackModel;

  public StackManagerViewImpl(StackManagerPresenter stackManagerPresenter, StackPile stackPile) {
    this.stackManagerPresenter = stackManagerPresenter;
    this.stackPile = stackPile;
  }

  @Override
  public Widget asWidget() {
    return getWindow();
  }

  @Override
  public void collapse() {
    StackModel fileModel = getTreeGrid().getSelectionModel().getSelectedItem();
    if (fileModel == null) {
      getTreeGrid().collapseAll();
    } else {
      getTreeGrid().setExpanded(fileModel, false, true);
    }
  }

  @Override
  public void editName(StackModel fileModel) {
    editSaveStackModel(fileModel);
    Element row = getTreeGrid().getView().getRow(fileModel);
    int rowIndex = getTreeGrid().getView().findRowIndex(row);
    getGridEditing().startEditing(new GridCell(rowIndex, 0));
  }

  @Override
  public void expand() {
    StackModel fileModel = getTreeGrid().getSelectionModel().getSelectedItem();
    if (fileModel == null) {
      getTreeGrid().expandAll();
    } else {
      getTreeGrid().setExpanded(fileModel, true, true);
    }
  }

  @Override
  public StackModel getSelectedItem() {
    return getTreeGrid().getSelectionModel().getSelectedItem();
  }

  @Override
  public List<StackModel> getSelectedItems() {
    return getTreeGrid().getSelectionModel().getSelectedItems();
  }

  @Override
  public void onHide(HideEvent event) {
    getTreeGrid().unbind();
  }

  @Override
  public void selectStackModel(StackModel fileModel) {
    getTreeGrid().setExpanded(fileModel, true);
    getTreeGrid().getSelectionModel().select(fileModel, false);
  }

  StackPile getStackPile() {
    return stackPile;
  }

  /**
   * Works around a minor issue with TreeGrid and program initiated GridEditing in which the selection is lost if the
   * user clicks in the active EditField.
   */
  private void editRestoreStackModel() {
    if (editStackModel != null) {
      getTreeGrid().getSelectionModel().select(editStackModel, false);
    }
  }

  private void editSaveStackModel(StackModel editStackModel) {
    this.editStackModel = editStackModel;
  }

  private CancelEditHandler<StackModel> getCancelEditHandler() {
    if (cancelEditHandler == null) {
      cancelEditHandler = new CancelEditHandler<StackModel>() {
        @Override
        public void onCancelEdit(CancelEditEvent<StackModel> event) {
          /*
           * Works around a minor issue with GridInlineEditing in which any update operation that does not change the
           * value is reported as a cancel.
           */
          if (gridEditing.isEnter()) {
            completeEditing();
          } else {
            getStackManagerPresenter().onEditStackNameComplete(false);
          }
        }
      };
    }
    return cancelEditHandler;
  }

  private ColumnModel<StackModel> getColumnModel() {
    if (columnModel == null) {
      List<ColumnConfig<StackModel, ?>> columnConfigs = new ArrayList<ColumnConfig<StackModel, ?>>();
      columnConfigs.add(getNameConfig());
      columnConfigs.add(getDateConfig());
      columnConfigs.add(getTypeConfig());
      columnConfigs.add(getSizeConfig());
      columnModel = new ColumnModel<StackModel>(columnConfigs);
    }
    return columnModel;
  }

  private CompleteEditHandler<StackModel> getCompleteEditHandler() {
    if (completeEditHandler == null) {
      completeEditHandler = new CompleteEditHandler<StackModel>() {
        @Override
        public void onCompleteEdit(CompleteEditEvent<StackModel> event) {
          completeEditing();
        }
      };
    }
    return completeEditHandler;
  }

  private ColumnConfig<StackModel, Date> getDateConfig() {
    if (dateConfig == null) {
      dateConfig = new ColumnConfig<StackModel, Date>(getStackModelProperties().lastModified(), 100, "Date");
      dateConfig.setCell(new DateCell(DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM)));
    }
    return dateConfig;
  }

  private IconProvider<StackModel> getStackManagerIconProvider() {
    if (stackManagerIconProvider == null) {
      stackManagerIconProvider = new StackManagerIconProvider();
    }
    return stackManagerIconProvider;
  }

  private StackManagerMenu getStackManagerMenu() {
    if (stackManagerMenu == null) {
      stackManagerMenu = new StackManagerMenu(getStackManagerPresenter());
    }
    return stackManagerMenu;
  }

  private StackManagerPresenter getStackManagerPresenter() {
    return stackManagerPresenter;
  }

  private StackManagerSizeCell getStackManagerSizeCell() {
    if (stackManagerSizeCell == null) {
      stackManagerSizeCell = new StackManagerSizeCell(getStackPile());
    }
    return stackManagerSizeCell;
  }

  private StackManagerToolBar getStackManagerToolBar() {
    if (stackManagerToolBar == null) {
      stackManagerToolBar = new StackManagerToolBar(getStackManagerPresenter());
      stackManagerToolBar.setButtonEnabledState();
    }
    return stackManagerToolBar;
  }

  private StackModelProperties getStackModelProperties() {
    return getStackPile().getStackModelProperties();
  }

  private StackManagerGridInlineEditing getGridEditing() {
    if (gridEditing == null) {
      gridEditing = new StackManagerGridInlineEditing(getTreeGrid());
      gridEditing.setClicksToEdit(null);
      gridEditing.addEditor(getNameConfig(), gridEditing.getTextField());
      gridEditing.addCompleteEditHandler(getCompleteEditHandler());
      gridEditing.addCancelEditHandler(getCancelEditHandler());
    }
    return gridEditing;
  }

  private ColumnConfig<StackModel, String> getNameConfig() {
    if (nameConfig == null) {
      nameConfig = new ColumnConfig<StackModel, String>(getStackModelProperties().name(), 200, "Name");
    }
    return nameConfig;
  }

  private RowDoubleClickHandler getRowDoubleClickHandler() {
    if (rowDoubleClickHandler == null) {
      rowDoubleClickHandler = new RowDoubleClickHandler() {
        @Override
        public void onRowDoubleClick(RowDoubleClickEvent event) {
          getStackManagerPresenter().onOpen();
        }
      };
    }
    return rowDoubleClickHandler;
  }

  private SelectionChangedHandler<StackModel> getSelectionChangedHandler() {
    if (selectionChangedHandler == null) {
      selectionChangedHandler = new SelectionChangedHandler<StackModel>() {
        @Override
        public void onSelectionChanged(SelectionChangedEvent<StackModel> event) {
          StackModel fileModel = treeGrid.getSelectionModel().getSelectedItem();
          if (fileModel != null) {
            getStackManagerPresenter().onSelect(fileModel);
          }
          getStackManagerToolBar().setButtonEnabledState();
          getWindow().setHeadingText(getTitle(fileModel));
        }
      };
    }
    return selectionChangedHandler;
  }

  private ColumnConfig<StackModel, Long> getSizeConfig() {
    if (sizeConfig == null) {
      sizeConfig = new ColumnConfig<StackModel, Long>(getStackModelProperties().size(), 100, "Size");
      sizeConfig.setCell(getStackManagerSizeCell());
    }
    return sizeConfig;
  }

  private String getTitle(StackModel fileModel) {
    return fileModel == null ? TITLE : TITLE + " - " + getStackPile().getPath(fileModel);
  }

  private StackManagerTreeGrid getTreeGrid() {
    if (treeGrid == null) {
      treeGrid = new StackManagerTreeGrid(getStackPile().getTreeStore(), getColumnModel(), getNameConfig());
      treeGrid.getView().setEmptyText("Use tool bar or context menu to create files and folders.");
      treeGrid.setBorders(false);
      treeGrid.getView().setTrackMouseOver(false);
      treeGrid.getView().setForceFit(true);
      treeGrid.getView().setAutoFill(true);
      treeGrid.setIconProvider(getStackManagerIconProvider());
      treeGrid.setContextMenu(getStackManagerMenu().getStackMenu());
      treeGrid.getSelectionModel().addSelectionChangedHandler(getSelectionChangedHandler());
      treeGrid.addRowDoubleClickHandler(getRowDoubleClickHandler());
      getGridEditing();
      getTreeGridDragSource();
      getTreeGridDropTarget();
    }
    return treeGrid;
  }

  private TreeGridDragSource<StackModel> getTreeGridDragSource() {
    if (treeGridDragSource == null) {
      treeGridDragSource = new TreeGridDragSource<StackModel>(getTreeGrid());
    }
    return treeGridDragSource;
  }

  private TreeGridDropTarget<StackModel> getTreeGridDropTarget() {
    if (treeGridDropTarget == null) {
      treeGridDropTarget = new TreeGridDropTarget<StackModel>(getTreeGrid());
      treeGridDropTarget.setAllowSelfAsSource(true);
      treeGridDropTarget.setFeedback(Feedback.BOTH);
    }
    return treeGridDropTarget;
  }

  private ColumnConfig<StackModel, ?> getTypeConfig() {
    if (typeConfig == null) {
      typeConfig = new ColumnConfig<StackModel, StackType>(getStackModelProperties().stackType(), 100, "Type");
    }
    return typeConfig;
  }

  private VerticalLayoutContainer getVerticalLayoutContainer() {
    if (verticalLayoutContainer == null) {
      verticalLayoutContainer = new VerticalLayoutContainer();
      verticalLayoutContainer.setBorders(true);
      verticalLayoutContainer.add(getStackManagerToolBar(), new VerticalLayoutData(1, -1));
      verticalLayoutContainer.add(getTreeGrid(), new VerticalLayoutData(1, 1));
    }
    return verticalLayoutContainer;
  }

  private Window getWindow() {
    if (window == null) {
      window = new Window();
      window.setHeadingText(getTitle(null));
      window.getHeader().setIcon(Images.getImageResources().folder());
      window.setMinimizable(true);
      window.setMaximizable(true);
      window.setPixelSize(500, 400);
      window.setOnEsc(false);
      window.addHideHandler(this);
      window.setWidget(getVerticalLayoutContainer());
    }
    return window;
  }

  private void completeEditing() {
    editRestoreStackModel();
    getStackManagerPresenter().onEditStackNameComplete(true);
    // Give the change a chance to propagate to model and store
    Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
      @Override
      public boolean execute() {
        getWindow().setHeadingText(getTitle(getSelectedItem()));
        return false;
      }
    }, 250);
  }
}
