/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.messages.client;

import com.google.gwt.core.client.GWT;

/**
 * Default locale-sensitive messages for GXT. This class uses
 * {@link com.google.gwt.core.client.GWT#create(Class)} to create an instance of an automatically generated
 * subclass that implements the {@link com.sencha.gxt.messages.client.XMessages} interface. See the package
 * containing {@link com.sencha.gxt.messages.client.XMessages} for the property files containing the translated
 * messages. See {@link com.google.gwt.i18n.client.Messages} for more information.
 */
public class DesktopMessagesApi {

  private static final DesktopMessages instance = GWT.create(DesktopMessages.class);

  /**
   * Returns an instance of an automatically generated subclass that implements
   * the {@link com.sencha.gxt.messages.client.XMessages} interface containing default locale-sensitive
   * messages for GXT.
   *
   * @return locale-sensitive messages for GXT
   */
  public static DesktopMessages getMessages() {
    return instance;
  }

}
