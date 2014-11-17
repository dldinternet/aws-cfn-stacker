package com.dldinternet.aws.cfn.stacker.api;

/**
 * Created by cdelange on 11/7/14.
 *
 * Description:
 */
public abstract class ApiBase {

    public abstract String toString(String br);

    public String toHTML() {
        return toString("<br>\n");
    }

    @Override
    public String toString() {
        return toString("\n");
    }
}
