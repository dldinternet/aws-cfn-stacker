/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.spreadsheet;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.AutoBeanToString;

@Category({AutoBeanToString.class, TableCategory.class})
public interface TableFactory extends AutoBeanFactory {

  AutoBean<Row> row();

  AutoBean<Table> table();
}