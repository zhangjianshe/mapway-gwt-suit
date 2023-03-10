package cn.mapway.ace.client;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents GWT wrapper around an Ace Selection object
 */
public class AceSelection {
	private final JavaScriptObject selection;
	private final AceSelectionListener rootListener;
	private final List<AceSelectionListener> subListeners = new ArrayList<AceSelectionListener>();
	
	/**
	 * Constructor for AceSelection
	 * @param selection Ace Selection java-script object (returned from editor.getSession().getSelection())
	 */
	public AceSelection(JavaScriptObject selection) {
		this.selection = selection;
		this.rootListener = new AceSelectionListener() {
			@Override
			public void onChangeSelection(AceSelection ignore) {
				for (AceSelectionListener lst : subListeners)
					lst.onChangeSelection(AceSelection.this);
			}
		};
		registerRootListener(selection, rootListener);
	}
	
	private static native void registerRootListener(JavaScriptObject selection, AceSelectionListener rootListener) /*-{
		selection.addEventListener("changeSelection", function() {
			rootListener.@cn.mapway.ace.client.AceSelectionListener::onChangeSelection(Lcn/mapway/ace/client/AceSelection;)(null);
		});
	}-*/;

	/**
	 * Register listener for selection change events.
	 * @param listener implementation of a listener for selection change events
	 */
	public void addSelectionListener(AceSelectionListener listener) {
		subListeners.add(listener);
	}
	
	/**
	 * Unregister listener for selection change events.
	 * @param listener implementation of a listener for selection change events
	 */
	public void removeSelectionListener(AceSelectionListener listener) {
		subListeners.remove(listener);
	}

	/**
	 * Check if selection is empty.
	 * @return true in case selection is empty
	 */
	public native boolean isEmpty() /*-{
		return this.@cn.mapway.ace.client.AceSelection::selection.isEmpty();
	}-*/;
	
	/**
	 * Check if selection contains several lines.
	 * @return true in case selection contains several lines
	 */
	public native boolean isMultiLine() /*-{
		return this.@cn.mapway.ace.client.AceSelection::selection.isMultiLine();
	}-*/;

	private AceEditorCursorPosition getCursorPositionImpl(final double row, final double column) {
		return new AceEditorCursorPosition((int) row, (int) column);
	}

	/**
	 * Give position of leading end of selection (where cursor is located).
	 * @return position of leading end of selection
	 */
	public native AceEditorCursorPosition getSelectionLead() /*-{
		var pos = this.@cn.mapway.ace.client.AceSelection::selection.getSelectionLead();
		return this.@cn.mapway.ace.client.AceSelection::getCursorPositionImpl(DD)(pos.row, pos.column);
	}-*/;
	
	/**
	 * Give starting position of selection (opposite to position where cursor is located).
	 * @return starting position of selection
	 */
	public native AceEditorCursorPosition getSelectionAnchor() /*-{
		var pos = this.@cn.mapway.ace.client.AceSelection::selection.getSelectionAnchor();
		return this.@cn.mapway.ace.client.AceSelection::getCursorPositionImpl(DD)(pos.row, pos.column);
	}-*/;
	
	/**
	 * Check if selection leading position is located before anchor position
	 * @return true in case selection leading position is located before anchor position
	 */
	public native boolean isBackwards() /*-{
		return this.@cn.mapway.ace.client.AceSelection::selection.isBackwards();
	}-*/;

	/**
	 * Clear selection.
	 */
	public native void clearSelection() /*-{
		this.@cn.mapway.ace.client.AceSelection::selection.clearSelection();
	}-*/;
	
	/**
	 * Select the whole text.
	 */
	public native void selectAll() /*-{
		this.@cn.mapway.ace.client.AceSelection::selection.selectAll();
	}-*/;

	/**
	 * Select text fragment between two positions
	 * @param fromRow row of selection anchor
	 * @param fromColumn columns of selection anchor
	 * @param toRow row of selection leading position
	 * @param toColumn column of selection leading position
	 */
	public native void select(int fromRow, int fromColumn, int toRow, int toColumn) /*-{
		this.@cn.mapway.ace.client.AceSelection::selection.setSelectionAnchor(fromRow, fromColumn);
		this.@cn.mapway.ace.client.AceSelection::selection.selectTo(toRow, toColumn);
	}-*/;

	/**
	 * Select line where cursor is located.
	 */
	public native void selectLine() /*-{
		var s = this.@cn.mapway.ace.client.AceSelection::selection;
		var pos = s.getSelectionLead();
		var len = s.doc.getLine(pos.row).length;
		s.setSelectionAnchor(pos.row, 0);
		s.selectTo(pos.row, len);
	}-*/;
}