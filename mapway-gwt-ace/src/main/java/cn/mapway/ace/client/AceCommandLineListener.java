package cn.mapway.ace.client;

/**
 * Listener for command line enter events.
 */
public interface AceCommandLineListener {
	/**
	 * Notify subscriber (e.g. editor) that command was entered.
	 * @param command ss
	 */
	public void onCommandEntered(String command);
}
