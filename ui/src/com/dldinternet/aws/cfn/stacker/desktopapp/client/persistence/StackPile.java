/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel.StackType;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.utility.Utility;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.web.bindery.autobean.shared.*;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StackPile {

  public interface StorageItem {

    List<String> getChildIds();

    Splittable getData();

    String getId();

    void setChildIds(List<String> children);

    void setData(Splittable data);

    void setId(String id);
  }

  @Category(AutoBeanToString.class)
  public interface StorageItemFactory extends AutoBeanFactory {
    AutoBean<StorageItem> storageItem();
  }

  public class TreeStoreHandlers implements StoreHandlers<StackModel> {

    @Override
    public void onAdd(StoreAddEvent<StackModel> event) {
      List<StackModel> stacks = event.getItems();
      StackModel parent = treeStore.getParent(stacks.get(0));
      if (parent == null) {
        persistRoot();
      } else {
        parent.setLastModified(new Date());
        update(parent);
      }
      persist(stacks);
    }

    @Override
    public void onClear(StoreClearEvent<StackModel> event) {
      StackPile.this.backingStore.clear();
    }

    @Override
    public void onDataChange(StoreDataChangeEvent<StackModel> event) {
      // no-op, we won't be replacing whole sets of items
    }

    @Override
    public void onFilter(StoreFilterEvent<StackModel> event) {
      // no-op, we're going to ignore this
    }

    @Override
    public void onRecordChange(StoreRecordChangeEvent<StackModel> event) {
      // no-op, can't happen since we are autocommit=true
    }

    @Override
    public void onRemove(StoreRemoveEvent<StackModel> event) {
      TreeStoreRemoveEvent<StackModel> treeStoreRemoveEvent = (TreeStoreRemoveEvent<StackModel>) event;
      StackModel parent = treeStoreRemoveEvent.getParent();
      if (parent == null) {
        persistRoot();
      } else {
        parent.setLastModified(new Date());
        update(parent);
      }

      // get the old child and clean it and its children from storage
      remove(getStorageItem(event.getItem().getId()));
    }

    @Override
    public void onSort(StoreSortEvent<StackModel> event) {
      // no-op, we're going to ignore this
    }

    @Override
    public void onUpdate(StoreUpdateEvent<StackModel> event) {
      for (StackModel item : event.getItems()) {
        update(item);
      }
    }
  }

  private static final String ROOT_ID = "0";
  private static final String ID_KEY = "id";
  private static final String PT_PREFIX = "sp";

  private BackingStore backingStore;
  private TreeStore<StackModel> treeStore;
  private StackModelProperties stackModelProperties;
  private StackModelFactory dataFactory;
  private StorageItemFactory storageFactory;

  public StackPile(BackingStore backingStore) {
    this.backingStore = backingStore;
  }

  public StackModel createStackModel(StackModel parentStackModel, String name, StackType stackType) {

    StackModel childStackModel = getDataFactory().stackModel().as();
    childStackModel.setName(name);
    childStackModel.setStackType(stackType);
    childStackModel.setLastModified(new Date());
    childStackModel.setId(allocateId(PT_PREFIX));
    childStackModel.setSize(0l);

    if (parentStackModel == null) {
      getTreeStore().add(childStackModel);
    } else {
      getTreeStore().add(parentStackModel, childStackModel);
    }

    return childStackModel;
  }

  public StackModelProperties getStackModelProperties() {
    if (stackModelProperties == null) {
      stackModelProperties = GWT.create(StackModelProperties.class);
    }
    return stackModelProperties;
  }

  public String getNextUntitledStackName(StackModel parentStackModel, StackType fileType) {
    String nextUntitledStackName;
    List<StackModel> children;
    if (parentStackModel == null) {
      children = getTreeStore().getRootItems();
    } else {
      children = getTreeStore().getChildren(parentStackModel);
    }
    int index = 1;
    if (fileType == StackType.STACK) {
      nextUntitledStackName = "https://aws.amazon.com";
    } else {
      String fileNameTemplate = Utility.capitalize(fileType.toString()) + " ";
      do {
        nextUntitledStackName = fileNameTemplate + index;
        index++;
      } while (containsName(children, nextUntitledStackName));
    }
    return nextUntitledStackName;
  }

  /**
   * Returns the parent of the specified file model or null if the parent is
   * root or the file model does not exist. This method is not necessary if
   * TreeStore.getParent is modified so that it does not assert the stackModel
   * exists.
   * 
   * @param stackModel the file model to return the parent of
   * @return the parent of the file model or null if the parent is root or the
   *         file model does not exist
   */
  public StackModel getParent(StackModel stackModel) {
    return getTreeStore().findModel(stackModel) == null ? null : getTreeStore().getParent(stackModel);
  }

  public String getPath(StackModel stackModel) {
    StringBuilder s = new StringBuilder();
    while (stackModel != null) {
      String name = stackModel.getName();
      s.insert(0, "/" + name);
      stackModel = getParent(stackModel);
    }
    return s.toString();
  }

  public StorageItem getStorageItem(StackModel model) {
    String id = model.getId();
    StorageItem item = createItem(id);
    item.setData(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(model)));
    for (StackModel child : getTreeStore().getChildren(model)) {
      item.getChildIds().add(child.getId());
    }
    return item;
  }

  public TreeStore<StackModel> getTreeStore() {
    if (treeStore == null) {
      treeStore = new TreeStore<StackModel>(getStackModelProperties().key());
      treeStore.setAutoCommit(true);
      addChildrenToStore(getRootStorageItem(), null, treeStore);
      treeStore.addStoreHandlers(new TreeStoreHandlers());
    }
    return treeStore;
  }

  /**
   * Removes the specified file model if it exists. This method is not necessary
   * if TreeStore.remove is modified so that it does not assert the stackModel
   * exists.
   * 
   * @param stackModel the file model to remove
   * @return true if the file model existed and was removed
   */
  public boolean remove(StackModel stackModel) {
    return getTreeStore().findModel(stackModel) != null && getTreeStore().remove(stackModel);
  }

  protected String allocateId(String prefix) {
    String thisId = backingStore.getItem(ID_KEY);
    if (thisId == null) {
      thisId = ROOT_ID;
    }
    String nextId = Integer.toString(Integer.parseInt(thisId) + 1);
    backingStore.setItem(ID_KEY, nextId);
    return prefix + thisId;
  }

  protected boolean containsName(List<StackModel> children, String name) {
    for (StackModel stackModel : children) {
      if (name.equals(stackModel.getName())) {
        return true;
      }
    }
    return false;
  }

  private void addChildrenToStore(StorageItem item, StackModel parent, TreeStore<StackModel> treeStore) {
    for (String childId : item.getChildIds()) {
      StorageItem child = getStorageItem(childId);
      StackModel model = AutoBeanCodex.decode(getDataFactory(), StackModel.class, child.getData()).as();
      if (parent != null) {
        treeStore.add(parent, model);
      } else {
        treeStore.add(model);
      }
      addChildrenToStore(child, model, treeStore);
    }
  }

  private StorageItem createItem(String id) {
    StorageItem item = getStorageFactory().storageItem().as();
    item.setId(id);
    item.setChildIds(new ArrayList<String>());
    return item;
  }

  private StackModelFactory getDataFactory() {
    if (dataFactory == null) {
      dataFactory = GWT.create(StackModelFactory.class);
    }
    return dataFactory;
  }

  private StorageItem getRootStorageItem() {
    StorageItem root = getStorageItem(PT_PREFIX + ROOT_ID);
    if (root == null) {
      root = createItem(allocateId(PT_PREFIX));
      persist(root);
    }
    return root;
  }

  private StorageItemFactory getStorageFactory() {
    if (storageFactory == null) {
      storageFactory = GWT.create(StorageItemFactory.class);
    }
    return storageFactory;
  }

  private StorageItem getStorageItem(String string) {
    String payload = backingStore.getItem(string);
    if (payload == null) {
      return null;
    }
    return AutoBeanCodex.decode(getStorageFactory(), StorageItem.class, payload).as();
  }

  private void persist(StackModel model) {
    persist(getStorageItem(model));
  }

  private void persist(List<StackModel> stackModels) {
    for (StackModel stack : stackModels) {
      persist(stack);
      persist(getTreeStore().getChildren(stack));
    }
  }

  private void persist(StorageItem item) {
    assert item.getData() != null || item.getId().equals(PT_PREFIX + ROOT_ID) : "No data in non-root item";

    String payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(item)).getPayload();
    backingStore.setItem(item.getId(), payload);
  }

  private void persistRoot() {
    StorageItem item = getRootStorageItem();
    item.getChildIds().clear();
    for (StackModel child : getTreeStore().getRootItems()) {
      item.getChildIds().add(child.getId());
    }
    persist(item);
  }

  private void remove(StorageItem item) {
    backingStore.removeItem(item.getId());
    for (String child : item.getChildIds()) {
      remove(getStorageItem(child));
    }
  }

  private void update(final StackModel stackModel) {
    final long length;
    if (stackModel.getStackType() == StackType.FOLDER) {
      length = (long) getTreeStore().getChildCount(stackModel);
    } else {
      String content = stackModel.getContent();
      length = (long) (content == null ? 0 : content.length());
    }
    if (length != stackModel.getSize()) {
      stackModel.setSize(length);
      // prevents recursive update via onUpdate handler
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        @Override
        public void execute() {
          getTreeStore().update(stackModel);
        }
      });
    }
    persist(stackModel);
  }

}
