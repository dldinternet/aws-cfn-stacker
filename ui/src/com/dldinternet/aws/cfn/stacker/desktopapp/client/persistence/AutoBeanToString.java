/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public class AutoBeanToString {
  public static String toString(AutoBean<?> instance) {
    return AutoBeanCodex.encode(instance).getPayload();
  }
}