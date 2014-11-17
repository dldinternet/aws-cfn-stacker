/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.utility.Utility;

import java.util.Date;

public interface StackModel {
    public enum StackType {
        FOLDER, STACK;

        @Override
        public String toString() {
            return Utility.capitalize(super.toString());
        }
    }

    public String getContent();

    public String getId();

    public Date getLastModified();

    public String getName();

    public Long getSize();

    public StackType getStackType();

    public void setContent(String content);

    public void setId(String id);

    public void setLastModified(Date lastModified);

    public void setName(String name);

    public void setSize(Long size);

    public void setStackType(StackType stackType);

}