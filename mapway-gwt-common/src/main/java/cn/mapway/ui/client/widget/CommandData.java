package cn.mapway.ui.client.widget;

/**
 * CommandData
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class CommandData {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_OTHER = 1;

    private Integer commandId;
    private Object data;
    private int state;


    public CommandData() {

    }

    public static CommandData create(Integer commandId) {
        CommandData data = new CommandData();
        data.setCommandId(commandId);
        data.setData(null);
        return data;
    }

    public static CommandData create(Integer commandId, Object value) {
        CommandData data = new CommandData();
        data.setCommandId(commandId);
        data.setData(value);
        return data;
    }

    public boolean isNormalState() {
        return state == STATE_NORMAL;
    }

    public Integer getCommandId() {
        return commandId;
    }

    public void setCommandId(Integer commandId) {
        this.commandId = commandId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
