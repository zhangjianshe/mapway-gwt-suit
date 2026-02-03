package cn.mapway.ui.shared;

public class CommonConstant {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String API_TOKEN = "API-TOKEN";
    public static final String KEY_LOGIN_USER="KEY_LOGIN_USER";
    //全局总线　消息Topic
    public static final String BUS_EVENT_MESSAGE = "TOPIC_BUS_EVENT_MESSAGE";

    //文件选择编辑器
    public static final String EDITOR_FILE_DIR = "FILE_DIR_EDITOR";

    public static final String TAG_ADMIN = "admin"; //需要管理员权限
    public static final String TAG_PREFERENCE = "preference"; //这是一个PREFERENCE设置模块
    public static final String TAG_SUBSYSTEM = "subsystem";
    public static final String TAG_FULLSCREEN = "fullscreen"; //全屏模块
    public static final String TAG_NOT_HIDE_TOP_BAR = "hide_top_bar"; //与 FULL_SCREEN 配合使用 可以全屏 但是不隐藏最上面的工具栏
    public static final String TAG_DEV = "dev"; //开发模块 此模块只能在开发环境中使用
    public static final String TAG_PROPOSE = "propose";//推荐标签
    public static final String TAG_HIDDEN = "hidden";//隐藏
}
