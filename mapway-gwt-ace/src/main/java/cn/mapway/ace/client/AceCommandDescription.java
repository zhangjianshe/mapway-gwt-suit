package cn.mapway.ace.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Rules describing how editor runs this command (from keyboard, 
 * from command line or by {@link AceEditor#execCommand(String)} API calls).
 */
public class AceCommandDescription {
	private final String name;
	private final ExecAction exec;
	private KeyBinding bindKey = null;
	private boolean readOnly = false;
	private boolean passEvent = false;
	private ScrollIntoView scrollIntoView = null;
	private MultiSelectAction multiSelectAction = null;
	private String aceCommandGroup = null;
	
	/**
	 * Define Ace command with command line name and execution action
	 * @param name command line name
	 * @param exec execution action
	 */
	public AceCommandDescription(String name, ExecAction exec) {
		this.name = name;
		this.exec = exec;
	}

	/**
	 * Give command line name.
	 * @return command line name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Give execution action of this command.
	 * @return execution action
	 */
	public ExecAction getExec() {
		return exec;
	}

	/**
	 * Give key bindings.
	 * @return key bindings
	 */
	public KeyBinding getBindKey() {
		return bindKey;
	}

	/**
	 * Describe does this command change editor document or not.
	 * @return whether or not editor document could be changed
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * In case this parameter is true keyboard event will be passed to 
	 * original listener.
	 * @return true in case event shouldn't be stopped after command execution
	 */
	public boolean isPassEvent() {
		return passEvent;
	}

	/**
	 * Give scroll settings for this command. In case it's null command is 
	 * executed without scroll adjustment. Otherwise see 
	 * {@link ScrollIntoView} for details.
	 * @return scroll settings
	 */
	public ScrollIntoView getScrollIntoView() {
		return scrollIntoView;
	}
	
	/**
	 * Give multi-select action for this command. In case it's null command is 
	 * executed once without virtual changes in selection ranges. Otherwise 
	 * see {@link MultiSelectAction} for details.
	 * @return multi-select action
	 */
	public MultiSelectAction getMultiSelectAction() {
		return multiSelectAction;
	}

	/**
	 * Give Ace command group name of this command.
	 * @return Ace command group name
	 */
	public String getAceCommandGroup() {
		return aceCommandGroup;
	}

	/**
	 * Chainable setter method for bindKey property.
	 * @param bindKey key binding (e.g. "shift-esc|ctrl-`" or "Command+Alt+C")
	 * @return reference to this command description
	 */
	public AceCommandDescription withBindKey(KeyBinding bindKey) {
		this.bindKey = bindKey;
		return this;
	}

	/**
	 * Chainable setter method for bindKey property for all platforms.
	 * @param bindKeyForAllPlatforms key binding (e.g. "shift-esc|ctrl-`" or "Command+Alt+C")
	 * @return reference to this command description
	 */
	public AceCommandDescription withBindKey(String bindKeyForAllPlatforms) {
		this.bindKey = new KeyBinding(bindKeyForAllPlatforms);
		return this;
	}

	/**
	 * Chainable setter method for bindKey property for Mac and other platforms separately.
	 * @param bindKeyForAllPlatformsExceptMac key binding for everything except Mac (e.g. "shift-esc|ctrl-`")
	 * @param bindKeyForMac key binding for Mac (e.g. "shift-esc|ctrl-`" or "Command+Alt+C")
	 * @return reference to this command description
	 */
	public AceCommandDescription withBindKey(String bindKeyForAllPlatformsExceptMac, 
			String bindKeyForMac) {
		this.bindKey = new KeyBinding(bindKeyForAllPlatformsExceptMac, bindKeyForMac);
		return this;
	}

	/**
	 * Chainable setter method for readOnly property.
	 * @param readOnly describes does this command change editor document or not
	 * @return reference to this command description
	 */
	public AceCommandDescription withReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	/**
	 * Chainable setter method for passEvent property.
	 * @param passEvent in case it's true keyboard event will not be stopped 
	 * 		and will be passed to original listener after command execution
	 * @return reference to this command description
	 */
	public AceCommandDescription withPassEvent(boolean passEvent) {
		this.passEvent = passEvent;
		return this;
	}

	/**
	 * Chainable setter method for scrollIntoView property.
	 * @param scrollIntoView scroll settings for this command (in case it's 
	 * 		null command is executed without scroll adjustment; otherwise 
	 * 		see {@link ScrollIntoView} for details)
	 * @return reference to this command description
	 */
	public AceCommandDescription withScrollIntoView(ScrollIntoView scrollIntoView) {
		this.scrollIntoView = scrollIntoView;
		return this;
	}
	
	/**
	 * Chainable setter method for multiSelectAction property.
	 * @param multiSelectAction multi-select action for this command (in 
	 * 		case it's null command is executed once without virtual changes 
	 * 		in selection ranges; otherwise see {@link MultiSelectAction} for 
	 * 		details)
	 * @return reference to this command description
	 */
	public AceCommandDescription withMultiSelectAction(MultiSelectAction multiSelectAction) {
		this.multiSelectAction = multiSelectAction;
		return this;
	}

	/**
	 * Chainable setter method for aceCommandGroup property.
	 * @param aceCommandGroup Ace command group name of this command
	 * @return reference to this command description
	 */
	public AceCommandDescription withAceCommandGroup(String aceCommandGroup) {
		this.aceCommandGroup = aceCommandGroup;
		return this;
	}

	@Override
	public String toString() {
		return "AceCommandDescription [name=" + name + ", exec=" + exec
				+ ", bindKey=" + bindKey + ", readOnly=" + readOnly
				+ ", passEvent=" + passEvent + ", scrollIntoView="
				+ scrollIntoView + ", multiSelectAction=" + multiSelectAction
				+ ", aceCommandGroup=" + aceCommandGroup + "]";
	}

	/**
	 * Create Ace command from javascript object.
	 * @param obj javascript object describing Ace command
	 * @return Ace command description object
	 */
	public static native AceCommandDescription fromJavaScript(JavaScriptObject obj) /*-{
		var name = obj.name;
		var exec = @cn.mapway.ace.client.AceCommandDescription::createJavaScriptWrapper(Ljava/lang/Object;)(obj.exec);
		var ret = @cn.mapway.ace.client.AceCommandDescription::new(Ljava/lang/String;Lcn/mapway/ace/client/AceCommandDescription$ExecAction;)(name, exec);
		var bindKey = @cn.mapway.ace.client.AceCommandDescription.KeyBinding::fromJavaScript(Ljava/lang/Object;)(obj.bindKey);
		if (bindKey)
			ret.@cn.mapway.ace.client.AceCommandDescription::withBindKey(Lcn/mapway/ace/client/AceCommandDescription$KeyBinding;)(bindKey);
		var readOnly = obj.readOnly;
		if (readOnly)
			ret.@cn.mapway.ace.client.AceCommandDescription::withReadOnly(Z)(readOnly);
		var passEvent = obj.passEvent;
		if (passEvent)
			ret.@cn.mapway.ace.client.AceCommandDescription::withPassEvent(Z)(passEvent);
		var scrollIntoView = @cn.mapway.ace.client.AceCommandDescription.ScrollIntoView::fromString(Ljava/lang/String;)(obj.scrollIntoView);
		if (scrollIntoView)
			ret.@cn.mapway.ace.client.AceCommandDescription::withScrollIntoView(Lcn/mapway/ace/client/AceCommandDescription$ScrollIntoView;)(scrollIntoView);
		var objMultiSelectAction = obj.multiSelectAction;
		var multiSelectAction = null;
		if (typeof objMultiSelectAction === "string")
			multiSelectAction = @cn.mapway.ace.client.AceCommandDescription.MultiSelectAction::fromString(Ljava/lang/String;)(objMultiSelectAction);
		if (multiSelectAction)
			ret.@cn.mapway.ace.client.AceCommandDescription::withMultiSelectAction(Lcn/mapway/ace/client/AceCommandDescription$MultiSelectAction;)(multiSelectAction);
		var aceCommandGroup = obj.aceCommandGroup;
		if (aceCommandGroup)
			ret.@cn.mapway.ace.client.AceCommandDescription::withAceCommandGroup(Ljava/lang/String;)(aceCommandGroup);
		return ret;
	}-*/;

	private static ExecAction createJavaScriptWrapper(final Object jsFunction) {
		return new ExecAction() {
			@Override
			public Object exec(AceEditor editor) {
				return invokeJavaScriptCommand(jsFunction, editor);
			}
			
			@Override
			public String toString() {
				return "ExecAction [javascript=" + jsFunction + "]";
			}
		};
	}
	
	private static native Object invokeJavaScriptCommand(Object jsFunction, AceEditor javaWrapper) /*-{
		var jsEditor = javaWrapper.@cn.mapway.ace.client.AceEditor::editor;
		return jsFunction(jsEditor);
	}-*/;

	/**
	 * Create javascript object from Ace command description.
	 * @param editor Ace editor java wrapper
	 * @return Ace command javascript object
	 */
	public native JavaScriptObject toJavaScript(AceEditor editor) /*-{
		var ret = {};
		ret['name'] = this.@cn.mapway.ace.client.AceCommandDescription::name;
		var javaExec = this.@cn.mapway.ace.client.AceCommandDescription::exec;
		ret['exec'] = function() {
			javaExec.@cn.mapway.ace.client.AceCommandDescription.ExecAction::exec(Lcn/mapway/ace/client/AceEditor;)(editor);
		};
		var bindKey = this.@cn.mapway.ace.client.AceCommandDescription::bindKey;
		if (bindKey)
			ret['bindKey'] = bindKey.@cn.mapway.ace.client.AceCommandDescription.KeyBinding::toJavaScript()();
		var readOnly = this.@cn.mapway.ace.client.AceCommandDescription::readOnly;
		if (readOnly)
			ret['readOnly'] = readOnly;
		var passEvent = this.@cn.mapway.ace.client.AceCommandDescription::passEvent;
		if (passEvent) 
			ret['passEvent'] = passEvent;
		var scrollIntoView = this.@cn.mapway.ace.client.AceCommandDescription::scrollIntoView;
		if (scrollIntoView)
			ret['scrollIntoView'] = scrollIntoView.@cn.mapway.ace.client.AceCommandDescription.ScrollIntoView::name()();
		var multiSelectAction = this.@cn.mapway.ace.client.AceCommandDescription::multiSelectAction;
		if (multiSelectAction)
			ret['multiSelectAction'] = multiSelectAction.@cn.mapway.ace.client.AceCommandDescription.MultiSelectAction::name()();
		var aceCommandGroup = this.@cn.mapway.ace.client.AceCommandDescription::aceCommandGroup;
		if (aceCommandGroup)
			ret['aceCommandGroup'] = aceCommandGroup;
		return ret;
	}-*/;
	
	/**
	 * Key binding description.
	 */
	public static class KeyBinding {
		private final String allPlatforms;
		private final String mac;
		private final String exceptMac;
		
		/**
		 * Constructor for key bindings for all platforms.
		 * @param allPlatforms key bindings (e.g. "shift-esc|ctrl-`" or "Command+Alt+C")
		 */
		public KeyBinding(String allPlatforms) {
			this.allPlatforms = allPlatforms;
			this.mac = null;
			this.exceptMac = null;
		}
		
		/**
		 * Constructor for separate key bindings for Mac and other platforms.
		 * @param exceptMac key bindings for all other than Mac (e.g. "shift-esc|ctrl-`")
		 * @param mac key bindings for Mac (e.g. "Command+Alt+C")
		 */
		public KeyBinding(String exceptMac, String mac) {
			this.allPlatforms = null;
			this.mac = mac;
			this.exceptMac = exceptMac;
		}
		
		public String getAllPlatforms() {
			return allPlatforms;
		}
		
		public String getMac() {
			return mac;
		}
		
		public String getExceptMac() {
			return exceptMac;
		}

		@Override
		public String toString() {
			if (allPlatforms != null)
				return "KeyBinding [allPlatforms=" + allPlatforms + "]";
			return "KeyBinding [mac=" + mac + ", exceptMac=" + exceptMac + "]";
		}
		
		private static native KeyBinding fromJavaScript(Object obj) /*-{
			if (!obj)
				return null;
			if (typeof obj === "string")
				return @cn.mapway.ace.client.AceCommandDescription.KeyBinding::new(Ljava/lang/String;)(obj);
			return @cn.mapway.ace.client.AceCommandDescription.KeyBinding::new(Ljava/lang/String;Ljava/lang/String;)(obj.win, obj.mac);
		}-*/;

		private native Object toJavaScript() /*-{
			var ret = this.@cn.mapway.ace.client.AceCommandDescription.KeyBinding::allPlatforms;
			if (ret)
				return ret;
			var win = this.@cn.mapway.ace.client.AceCommandDescription.KeyBinding::exceptMac;
			var mac = this.@cn.mapway.ace.client.AceCommandDescription.KeyBinding::mac;
			return {win: win, mac: mac};
		}-*/;
	}
	
	/**
	 * Ace command execution action.
	 */
	public static interface ExecAction {
		public Object exec(AceEditor editor);
	}
	
	/**
	 * Ace command scrolling options.
	 */
	public static enum ScrollIntoView {
		/**
		 * Scroll to cursor with animation
		 */
		animate, 
		/**
		 * Locate cursor in the center of the screen
		 */
		center, 
		/**
		 * Scroll to cursor without animation
		 */
		cursor, 
		/**
		 * Scroll to selected text 
		 */
		selectionPart;
		
		public static ScrollIntoView fromString(String value) {
			for (ScrollIntoView ret : ScrollIntoView.values()) {
				if (ret.name().equals(value))
					return ret;
			}
			return null;
		}
	}
	
	/**
	 * Action defines a way of running commands based on editor text selection.
	 */
	public static enum MultiSelectAction {
		/**
		 * Execute command for each selection in multi-select mode
		 */
		forEach, 
		/**
		 * Execute command for each selection grouped by lines in multi-select mode
		 */
		forEachLine,
		/**
		 * Execute command once treating multi-selection as a single range
		 */
		single;

		public static MultiSelectAction fromString(String value) {
			for (MultiSelectAction ret : MultiSelectAction.values()) {
				if (ret.name().equals(value))
					return ret;
			}
			return null;
		}
	}
}
