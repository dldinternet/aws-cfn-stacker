/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.interpreter;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.FileBasedMiniAppView;

public interface InterpreterView extends FileBasedMiniAppView {

  void print(String value);

}