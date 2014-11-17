/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.Presenter;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel.StackType;

public interface StackManagerPresenter extends Presenter {

  boolean isEnableCreate();

  boolean isEnableDelete();

  boolean isEnableEditName();

  boolean isEnableOpen();

  void onCollapse();

  void onCreate(StackType stackType);

  void onDelete();

  void onEditStackNameComplete(boolean isSaved);

  void onEditName();

  void onExpand();

  void onOpen();

  void onSelect(StackModel stackModel);

}