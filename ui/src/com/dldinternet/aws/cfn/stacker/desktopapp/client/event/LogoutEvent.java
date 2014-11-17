/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.dldinternet.aws.cfn.stacker.desktopapp.client.event.LogoutEvent.LogoutHandler;

public class LogoutEvent extends GwtEvent<LogoutHandler> {

  public interface LogoutHandler extends EventHandler {
    void onLogout(LogoutEvent event);
  }

  public static Type<LogoutHandler> TYPE = new Type<LogoutHandler>();

  public LogoutEvent() {
  }

  @Override
  public Type<LogoutHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(LogoutHandler handler) {
    handler.onLogout(this);
  }
}