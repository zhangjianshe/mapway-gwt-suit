package cn.mapway.ui.client.mvc;


/**
 * 模块调度器
 *
 * @author zhangjianshe
 */
public interface IModuleDispatcher {
    String MODULE_DISPATCH_EVENT = "MODULE_DISPATCH_EVENT";
    String MODULE_RETURN_EVENT = "MODULE_DISPATCH_RETURN";
    String KEY_MODULE_HASHES = "key_module_hashes";

    /**
     * 切换模块.
     *
     * @param code          the code
     * @param parameter     the parameter
     * @param saveToHistory the save to history
     * @return module dispatcher
     */
    IModuleDispatcher switchModule(String code, ModuleParameter parameter, boolean saveToHistory);
}
