package cn.mapway.ui.server.db;

public interface IProgressHandler {
    /**
     * progress 回调
     * @param progress [0-100]
     * @param message
     */
    void progress(long progress,String message);
}
