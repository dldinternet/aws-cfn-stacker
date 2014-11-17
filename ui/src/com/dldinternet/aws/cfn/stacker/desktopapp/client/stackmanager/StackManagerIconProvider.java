/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel.StackType;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager.images.Images;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.data.shared.IconProvider;

public class StackManagerIconProvider implements IconProvider<StackModel> {

  @Override
  public ImageResource getIcon(StackModel stackModel) {
    ImageResource icon = null;
    StackType stackType = stackModel.getStackType();
    switch (stackType) {
      case STACK:
        icon = Images.getImageResources().stack();
        break;
      case FOLDER:
        icon = Images.getImageResources().folder();
        break;
    }
    return icon;
  }
}
