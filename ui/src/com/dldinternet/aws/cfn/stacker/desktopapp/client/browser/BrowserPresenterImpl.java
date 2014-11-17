/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.browser;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.DesktopBus;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.FileBasedMiniAppPresenterImpl;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.FileBasedMiniAppView;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.FileModel;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.FileSystem;

public class BrowserPresenterImpl extends FileBasedMiniAppPresenterImpl implements BrowserPresenter {

  public BrowserPresenterImpl(DesktopBus desktopBus, FileSystem fileSystem, FileModel fileModel) {
    super(desktopBus, fileSystem, fileModel);
  }

  @Override
  protected FileBasedMiniAppView createFileBasedMiniAppView() {
    return new BrowserViewImpl(this);
  }

  @Override
  protected String getDisplayedContent(FileModel fileModel) {
    return fileModel.getName();
  }

  @Override
  protected String getTitle() {
    return "Browser - " + super.getTitle();
  }

  protected boolean isModified() {
    return false;
  }

}
