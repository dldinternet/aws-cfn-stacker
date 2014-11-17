/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel.StackType;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import java.util.Date;

public interface StackModelProperties extends PropertyAccess<StackModel> {
  
  @Path("id")
  ModelKeyProvider<StackModel> key();

  ValueProvider<StackModel, StackType> stackType();

  ValueProvider<StackModel, Date> lastModified();

  ValueProvider<StackModel, String> name();

  ValueProvider<StackModel, Long> size();
  
}

