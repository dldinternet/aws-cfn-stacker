/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.filemanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.Presenter;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.FileModel;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.FileModel.FileType;

public interface FileManagerPresenter extends Presenter {

  boolean isEnableCreate();

  boolean isEnableDelete();

  boolean isEnableEditName();

  boolean isEnableOpen();

  void onCollapse();

  void onCreate(FileType fileType);

  void onDelete();

  void onEditFileNameComplete(boolean isSaved);

  void onEditName();

  void onExpand();

  void onOpen();

  void onSelect(FileModel fileModel);

}