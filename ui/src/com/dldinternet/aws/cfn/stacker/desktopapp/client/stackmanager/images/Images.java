/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public class Images {

    public interface ImageResources extends ClientBundle {
        ImageResource folder();

        ImageResource folder_add();

        ImageResource bin_closed();

        ImageResource stack();

        ImageResource stack_add();

        ImageResource arrow_in();

        ImageResource arrow_out();

        ImageResource textfield_rename();

        ImageResource bullet_go();

    }

  private static ImageResources imageResources;

  public static ImageResources getImageResources() {
    if (imageResources == null) {
      imageResources = GWT.create(ImageResources.class);
    }
    return imageResources;
  }
}
