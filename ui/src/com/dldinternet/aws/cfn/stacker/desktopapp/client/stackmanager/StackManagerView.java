/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface StackManagerView extends IsWidget {

  public void collapse();

  public void editName(StackModel childStackModel);

  public void expand();

  public StackModel getSelectedItem();

  public List<StackModel> getSelectedItems();

  public void selectStackModel(StackModel fileModel);

}