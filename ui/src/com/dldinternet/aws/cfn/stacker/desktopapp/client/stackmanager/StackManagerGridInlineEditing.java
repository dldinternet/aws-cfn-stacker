/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.dldinternet.aws.cfn.stacker.desktopapp.client.stackmanager;

import com.dldinternet.aws.cfn.stacker.desktopapp.client.persistence.StackModel;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

/**
 * Works around a minor issue with GridInlineEditing in which any update
 * operation that does not change the value is reported as a cancel.
 */
public class StackManagerGridInlineEditing extends GridInlineEditing<StackModel> {

  private boolean isEnter;

  StackManagerGridInlineEditing(Grid<StackModel> editableGrid) {
    super(editableGrid);
  }

  public boolean isEnter() {
    return isEnter;
  }

  @Override
  public void startEditing(GridCell cell) {
    isEnter = false;
    super.startEditing(cell);
  }

  /**
   * The original work around used onEnter to set the flag. However this no
   * longer works because onEnter is invoked after onCancel has been invoked.
   */
  @Override
  protected void onEnter(NativeEvent evt) {
    isEnter = true;
    super.onEnter(evt);
  }

  public TextField getTextField() {
    StackManagerGridInlineEditingTextField textField = new StackManagerGridInlineEditingTextField();
    textField.setSelectOnFocus(true);
    return textField;
  }

  /**
   * The new improved work around uses a special TextField override to set the
   * flag. When the underlying issue is resolved, this TextField can be replaced
   * with a plain old TextField.
   */
  public class StackManagerGridInlineEditingTextField extends TextField {

    @Override
    public void onBrowserEvent(Event event) {
      if ("keydown".equals(event.getType()) && event.getKeyCode() == KeyCodes.KEY_ENTER) {
        isEnter = true;
      }
      super.onBrowserEvent(event);
    }

  }
}