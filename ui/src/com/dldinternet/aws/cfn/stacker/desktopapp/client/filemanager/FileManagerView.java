/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.filemanager;

import com.google.gwt.user.client.ui.IsWidget;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.FileModel;

import java.util.List;

public interface FileManagerView extends IsWidget {

  public void collapse();

  public void editName(FileModel childFileModel);

  public void expand();

  public FileModel getSelectedItem();

  public List<FileModel> getSelectedItems();

  public void selectFileModel(FileModel fileModel);

}