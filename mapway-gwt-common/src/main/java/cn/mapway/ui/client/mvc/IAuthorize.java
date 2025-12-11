package cn.mapway.ui.client.mvc;

/**
 * 检查用户是否拥有模块的接口
 */
public interface IAuthorize {
    String AUTHORITY_KEY = "authority";
    String IGNORE_CHECK_KEY = "ignore_authority";

    /**
     * 检查是否对 code拥有授权模块
     *
     * @param code
     * @return
     */
    boolean isAuthorized( String code,ModuleParameter moduleParameter);
}
