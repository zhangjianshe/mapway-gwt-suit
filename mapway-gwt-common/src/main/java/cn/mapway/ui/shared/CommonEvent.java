package cn.mapway.ui.shared;

import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.widget.CommandData;
import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

/**
 * CommonEvent
 *
 * @author zhangjianshe@gmail.com
 */
public class CommonEvent extends GwtEvent<CommonEventHandler> {
    public final static int MESSAGE = 0;
    public final static int CLOSE = 10;
    public final static int ADD = 11;
    public final static int DELETE = 12;
    public final static int SElECT = 14;
    public final static int UPDATE = 15;
    public final static int FIRST = 16;
    public final static int NEXT = 17;
    public final static int PREV = 18;
    public final static int LAST = 19;
    public final static int SAVE = 20;
    public final static int EDIT = 21;
    public final static int CREATE = 22;
    public final static int MOUSEWHEEL = 61;
    public final static int MOUSEMOVE = 62;
    public final static int MOUSEDOWN = 63;
    public final static int MOUSEUP = 64;
    public final static int SCROLLBOTTOM = 65;
    /**
     * 模块切换事件
     */
    public final static int SWITCH = 23;
    public final static int PAGER = 24;
    public final static int RETURN = 25;
    public final static int DATA = 26;
    public final static int COLOR = 27;
    public final static int COLORS = 28;
    public final static int REMOVE = 29;
    public final static int TOOGLE = 30;
    public final static int CONFIG = 31;
    public final static int RESET = 32;
    public final static int OPACITY = 33;
    public final static int REFRESH = 34;
    public final static int LOGIN = 35;
    public final static int UP = 36;
    public final static int DOWN = 37;
    public final static int SEARCH = 38;
    public final static int SORT = 39;
    public final static int CLEAR = 40;
    public final static int FULL_SCREEN = 41;
    public final static int FULL_SCREEN_EXIT = 42;
    public final static int COMMAND = 43;
    public final static int START = 44;
    public final static int PAUSE = 45;
    public final static int STOP = 46;
    public final static int OPEN = 47;
    public final static int DOUBLE_CLICK = 48;
    public static final int PLAY = 49;
    public final static int ACTION = 50;
    public final static int LIST = 51;
    public final static int DETAIL = 52;
    public static final int LOG = 53;
    public static final int PROGRESS = 54;
    public static final int ABORT = 55;
    public static final int ERROR = 56;
    public static final int LOAD = 57;
    public static final int LOADSTART = 58;
    public static final int LOADEND = 59;
    public static final int READYSTATECHANGE = 60;
    public static final int LINK = 61;
    public static final int ATTRIBUTE = 62;
    public static final int PAGINATION_SELECT = 63;
    public static final int LOCATION = 64;
    public static final int CONTEXT = 65;
    public static final int HIDE = 66;
    public static final int SHOW = 67;
    public static final int RESIZE = 68;
    public static final int GRID = 69;
    public static final int BORDER = 70;
    public static final int LAYOUT = 71;
    public static final int SETUP = 72;
    public static final int ORDER = 73;
    public static final int COMMIT = 74;
    public static final int INFO = 75;
    public static final int RENAME = 76;
    public static final int FILTER = 77;
    public static final int MULTI_SELECT = 78;
    public static final int PALLET = 79;
    public static final int MOVE = 80;
    public static final int CANSAVE = 81;//可以保存 data总是 true /false
    public static final int DOWNLOAD = 82;//下载
    public static final int CLICK = 83;//下载
    public static final int CHECKED = 84;//用户check
    public static final int UNCHECKED = 85;//用户uncheck
    public static final int VALUE_CHANGE = 86;//数据改变


    public final static int OK = 200;
    public final static int FAIL = 500;


    /**
     * The type.
     */
    public static Type<CommonEventHandler> TYPE = new Type<CommonEventHandler>();
    private int type;
    private Object value;

    public CommonEvent(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static CommonEvent logEvent(Object data) {
        return new CommonEvent(LOG, data);
    }

    public static CommonEvent checkedEvent(Object data) {
        return new CommonEvent(CHECKED, data);
    }

    public static CommonEvent unCheckedEvent(Object data) {
        return new CommonEvent(UNCHECKED, data);
    }

    public static CommonEvent valueChangedEvent(Object data) {
        return new CommonEvent(VALUE_CHANGE, data);
    }

    public static CommonEvent canSaveEvent(Object data) {
        return new CommonEvent(CANSAVE, data);
    }

    public static CommonEvent moveEvent(Object data) {
        return new CommonEvent(MOVE, data);
    }

    public static CommonEvent infoEvent(Object data) {
        return new CommonEvent(INFO, data);
    }

    public static CommonEvent renameEvent(Object data) {
        return new CommonEvent(RENAME, data);
    }

    public static CommonEvent filterEvent(Object data) {
        return new CommonEvent(FILTER, data);
    }

    public static CommonEvent pagerEvent(Object data) {
        return new CommonEvent(PAGER, data);
    }

    public static CommonEvent multiSelectEvent(Object data) {
        return new CommonEvent(MULTI_SELECT, data);
    }

    public static CommonEvent addEvent(Object data) {
        return new CommonEvent(ADD, data);
    }

    public static CommonEvent contextEvent(Object data) {
        return new CommonEvent(CONTEXT, data);
    }

    public static CommonEvent showEvent(Object data) {
        return new CommonEvent(SHOW, data);
    }

    public static CommonEvent hideEvent(Object data) {
        return new CommonEvent(HIDE, data);
    }

    public static CommonEvent removeEvent(Object data) {
        return new CommonEvent(REMOVE, data);
    }

    public static CommonEvent closeEvent(Object data) {
        return new CommonEvent(CLOSE, data);
    }

    public static CommonEvent switchEvent(Object data) {
        return new CommonEvent(SWITCH, data);
    }

    public static CommonEvent progressEvent(Object data) {
        return new CommonEvent(PROGRESS, data);
    }

    public static CommonEvent clickEvent(Object data) {
        return new CommonEvent(CLICK, data);
    }

    public static CommonEvent loadEvent(Object data) {
        return new CommonEvent(LOAD, data);
    }

    public static CommonEvent locationEvent(Object data) {
        return new CommonEvent(LOCATION, data);
    }

    public static CommonEvent downloadEvent(Object data) {
        return new CommonEvent(DOWNLOAD, data);
    }

    public static CommonEvent attributeEvent(Object data) {
        return new CommonEvent(ATTRIBUTE, data);
    }

    public static CommonEvent resizeEvent(Object data) {
        return new CommonEvent(RESIZE, data);
    }

    public static CommonEvent loadStartEvent(Object data) {
        return new CommonEvent(LOADSTART, data);
    }

    public static CommonEvent loadEndEvent(Object data) {
        return new CommonEvent(LOADEND, data);
    }

    public static CommonEvent AbortEvent(Object data) {
        return new CommonEvent(ABORT, data);
    }

    public static CommonEvent readyChangeEvent(Object data) {
        return new CommonEvent(READYSTATECHANGE, data);
    }

    public static CommonEvent ErrorEvent(Object data) {
        return new CommonEvent(ERROR, data);
    }

    public static CommonEvent selectEvent(Object data) {
        return new CommonEvent(SElECT, data);
    }

    public static CommonEvent opacityEvent(Object data) {
        return new CommonEvent(OPACITY, data);
    }

    public static CommonEvent refreshEvent(Object data) {
        return new CommonEvent(REFRESH, data);
    }

    public static CommonEvent resetEvent(Object data) {
        return new CommonEvent(RESET, data);
    }

    public static CommonEvent playEvent(Object data) {
        return new CommonEvent(PLAY, data);
    }

    public static CommonEvent messageEvent(MessageObject message) {
        return new CommonEvent(MESSAGE, message);
    }

    public static CommonEvent messageEvent(int level, Integer code, String message) {
        return new CommonEvent(MESSAGE, new MessageObject(level, code, message));
    }

    public static CommonEvent editEvent(Object data) {
        return new CommonEvent(EDIT, data);
    }

    public static CommonEvent createEvent(Object data) {
        return new CommonEvent(CREATE, data);
    }

    public static CommonEvent saveEvent(Object data) {
        return new CommonEvent(SAVE, data);
    }

    public static CommonEvent okEvent(Object data) {
        return new CommonEvent(OK, data);
    }

    public static CommonEvent failEvent(Object data) {
        return new CommonEvent(FAIL, data);
    }

    public static CommonEvent updateEvent(Object data) {
        return new CommonEvent(UPDATE, data);
    }

    public static CommonEvent returnEvent(Object data) {
        return new CommonEvent(RETURN, data);
    }

    public static CommonEvent dataEvent(Object data) {
        return new CommonEvent(DATA, data);
    }

    public static CommonEvent colorEvent(Object data) {
        return new CommonEvent(COLOR, data);
    }

    public static CommonEvent toogleEvent(Object data) {
        return new CommonEvent(TOOGLE, data);
    }

    public static CommonEvent configEvent(Object data) {
        return new CommonEvent(CONFIG, data);
    }

    public static CommonEvent colorsEvent(List<String> colors) {
        return new CommonEvent(COLORS, colors);
    }

    public static CommonEvent deleteEvent(Object data) {
        return new CommonEvent(DELETE, data);
    }

    public static CommonEvent loginEvent(Object data) {
        return new CommonEvent(LOGIN, data);
    }

    public static CommonEvent searchEvent(Object data) {
        return new CommonEvent(SEARCH, data);
    }

    public static CommonEvent mouseWheelEvent(Object data) {
        return new CommonEvent(MOUSEWHEEL, data);
    }

    public static CommonEvent mouseMoveEvent(Object data) {
        return new CommonEvent(MOUSEMOVE, data);
    }

    public static CommonEvent mouseDownEvent(Object data) {
        return new CommonEvent(MOUSEDOWN, data);
    }

    public static CommonEvent mouseUpEvent(Object data) {
        return new CommonEvent(MOUSEUP, data);
    }

    public static CommonEvent scrollBottomEvent(Object data) {
        return new CommonEvent(SCROLLBOTTOM, data);
    }

    public static CommonEvent linkEvent(Object data) {
        return new CommonEvent(LINK, data);
    }

    public static CommonEvent paginationSelectEvent(Object data) {
        return new CommonEvent(PAGINATION_SELECT, data);
    }

    public static CommonEvent upEvent(Object d) {
        return new CommonEvent(UP, d);
    }

    public static CommonEvent downEvent(Object d) {
        return new CommonEvent(DOWN, d);
    }

    public static CommonEvent sortEvent(Object data) {
        return new CommonEvent(SORT, data);
    }

    public static CommonEvent nextEvent(Object data) {
        return new CommonEvent(NEXT, data);
    }

    public static CommonEvent prevEvent(Object data) {
        return new CommonEvent(PREV, data);
    }

    public static CommonEvent clearEvent(Object data) {
        return new CommonEvent(CLEAR, data);
    }

    public static CommonEvent fullScreenEvent(Object data) {
        return new CommonEvent(FULL_SCREEN, data);
    }

    public static CommonEvent fullScreenExitEvent(Object data) {
        return new CommonEvent(FULL_SCREEN_EXIT, data);
    }

    public static CommonEvent commandEvent(CommandData data) {
        return new CommonEvent(COMMAND, data);
    }

    public static CommonEvent startEvent(Object data) {
        return new CommonEvent(START, data);
    }

    public static CommonEvent pauseEvent(Object data) {
        return new CommonEvent(PAUSE, data);
    }

    public static CommonEvent stopEvent(Object data) {
        return new CommonEvent(STOP, data);
    }

    public static CommonEvent openEvent(Object data) {
        return new CommonEvent(OPEN, data);
    }

    public static CommonEvent create(Integer type, Object value) {
        return new CommonEvent(type, value);
    }

    public static CommonEvent doubleClickEvent(Object data) {
        return new CommonEvent(DOUBLE_CLICK, data);
    }

    public static CommonEvent actionEvent(Object action) {
        return new CommonEvent(ACTION, action);
    }

    public static CommonEvent listEvent(Object value) {
        return new CommonEvent(LIST, value);
    }

    public static CommonEvent detailEvent(Object value) {
        return new CommonEvent(DETAIL, value);
    }

    public static CommonEvent gridEvent(Object data) {
        return new CommonEvent(GRID, data);
    }

    public static CommonEvent layoutEvent(Object data) {
        return new CommonEvent(LAYOUT, data);
    }

    public static CommonEvent borderEvent(Object data) {
        return new CommonEvent(BORDER, data);
    }

    public static CommonEvent setupEvent(Object data) {
        return new CommonEvent(SETUP, data);
    }

    public static CommonEvent orderEvent(Object data) {
        return new CommonEvent(ORDER, data);
    }

    public static CommonEvent commitEvent(Object data) {
        return new CommonEvent(COMMIT, data);
    }

    public static CommonEvent palletEvent(Object data) {
        return new CommonEvent(PALLET, data);
    }

    public boolean isMultiSelect() {
        return MULTI_SELECT == this.type;
    }

    public boolean isProgress() {
        return PROGRESS == this.type;
    }

    public boolean isPallet() {
        return PALLET == this.type;
    }

    public boolean isOrder() {
        return ORDER == this.type;
    }

    public boolean isLoadEnd() {
        return LOADEND == this.type;
    }

    public boolean isError() {
        return ERROR == this.type;
    }

    public boolean isReadyChange() {
        return READYSTATECHANGE == this.type;
    }

    public boolean isLoad() {
        return LOAD == this.type;
    }

    public boolean isLoadStart() {
        return LOADSTART == this.type;
    }

    public boolean isAbort() {
        return ABORT == this.type;
    }

    public boolean isEdit() {
        return EDIT == this.type;
    }

    public boolean isAttribute() {
        return ATTRIBUTE == this.type;
    }

    public boolean isCanSave() {
        return CANSAVE == this.type;
    }

    public boolean isClose() {
        return CLOSE == this.type;
    }

    public boolean isCreate() {
        return CREATE == this.type;
    }

    public boolean isMessage() {
        return MESSAGE == this.type;
    }

    public boolean isDownload() {
        return DOWNLOAD == this.type;
    }

    public boolean isUpdate() {
        return UPDATE == this.type;
    }

    public boolean isInfo() {
        return INFO == this.type;
    }

    public boolean isRename() {
        return RENAME == this.type;
    }

    public boolean isRefresh() {
        return REFRESH == this.type;
    }

    public boolean isAdd() {
        return ADD == this.type;
    }

    public boolean isClick() {
        return CLICK == this.type;
    }

    public boolean isPlay() {
        return PLAY == this.type;
    }

    public boolean isOpacity() {
        return OPACITY == this.type;
    }

    public boolean isDelete() {
        return DELETE == this.type;
    }

    public boolean isReset() {
        return RESET == this.type;
    }

    public boolean isRemove() {
        return REMOVE == this.type;
    }

    public boolean isSelect() {
        return SElECT == this.type;
    }

    public boolean isSave() {
        return SAVE == this.type;
    }

    public boolean isDoubleClick() {
        return DOUBLE_CLICK == this.type;
    }

    public boolean isOk() {
        return OK == this.type;
    }

    public boolean isFilter() {
        return FILTER == this.type;
    }

    public boolean isOpen() {
        return OPEN == this.type;
    }

    public boolean isLocation() {
        return LOCATION == this.type;
    }

    public boolean isFail() {
        return FAIL == this.type;
    }

    public boolean isConfig() {
        return CONFIG == this.type;
    }

    public boolean isSwitch() {
        return SWITCH == this.type;
    }

    public boolean isPager() {
        return PAGER == this.type;
    }

    public boolean isHide() {
        return HIDE == this.type;
    }

    public boolean isShow() {
        return SHOW == this.type;
    }

    public boolean isReturn() {
        return RETURN == this.type;
    }

    public boolean isData() {
        return DATA == this.type;
    }

    public boolean isColor() {
        return COLOR == this.type;
    }

    public boolean isColors() {
        return COLORS == this.type;
    }

    public boolean isToogle() {
        return TOOGLE == this.type;
    }

    public boolean isAction() {
        return ACTION == this.type;
    }

    public boolean isLogin() {
        return LOGIN == this.type;
    }

    public boolean isUp() {
        return UP == this.type;
    }

    public boolean isSort() {
        return SORT == this.type;
    }

    public boolean isDown() {
        return DOWN == this.type;
    }

    public boolean isDetail() {
        return DETAIL == this.type;
    }

    public boolean isList() {
        return LIST == this.type;
    }

    public boolean isSearch() {
        return SEARCH == this.type;
    }

    public boolean isPrev() {
        return PREV == this.type;
    }

    public boolean isNext() {
        return NEXT == this.type;
    }

    public boolean isClear() {
        return CLEAR == this.type;
    }

    public boolean isLog() {
        return LOG == this.type;
    }

    public boolean isCommand() {
        return COMMAND == this.type;
    }

    public boolean isFullScreenExit() {
        return FULL_SCREEN_EXIT == this.type;
    }

    public boolean isFullScreen() {
        return FULL_SCREEN == this.type;
    }

    public boolean isStart() {
        return START == this.type;
    }

    public boolean isResize() {
        return RESIZE == this.type;
    }

    public boolean isPause() {
        return PAUSE == this.type;
    }

    public boolean isStop() {
        return STOP == this.type;
    }

    public boolean isMouseWheel() {
        return MOUSEWHEEL == this.type;
    }

    public boolean isMouseMove() {
        return MOUSEMOVE == this.type;
    }

    public boolean isMouseDown() {
        return MOUSEDOWN == this.type;
    }

    public boolean isMouseUp() {
        return MOUSEUP == this.type;
    }

    public boolean isScrollBottom() {
        return SCROLLBOTTOM == this.type;
    }

    public boolean isContext() {
        return CONTEXT == this.type;
    }

    public boolean isLink() {
        return LINK == this.type;
    }

    public boolean isPaginationSelect() {
        return PAGINATION_SELECT == this.type;
    }

    public boolean isGrid() {
        return GRID == this.type;
    }

    public boolean isLayout() {
        return LAYOUT == this.type;
    }

    public boolean isBorder() {
        return BORDER == this.type;
    }

    public boolean isSetup() {
        return SETUP == this.type;
    }

    public boolean isCommit() {
        return COMMIT == this.type;
    }

    public boolean isMove() {
        return MOVE == this.type;
    }

    public boolean isChecked(int type) {
        return CHECKED == type;
    }

    public boolean isUnChecked(int type) {
        return UNCHECKED == type;
    }

    public boolean isValueChanged(int type) {
        return VALUE_CHANGE == type;
    }

    @Override
    public Type<CommonEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CommonEventHandler handler) {
        handler.onCommonEvent(this);
    }

    public <T> T getValue() {
        return (T) value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
