package cn.mapway.ui.client.mvc;


import java.util.List;

/**
 * 表明某个类是可以动态生成的.
 *
 * @author zhangjianshe
 */
public interface ModuleFactory {

    /**
     * 根据代码实例化模块.
     *
     * @param moduleCode the module code
     * @param single     the single
     * @return the i frame module
     */
     IModule createModule(String moduleCode, boolean single);

    /**
     * 获取系统中的所有可用模块.
     *
     * @return the modules
     */
    List<ModuleInfo> getModules();

    /**
     * 返回根模块列表
     * @return
     */
    List<ModuleInfo> getRootModules();

    /**
     * 根据模块代码找到模块信息
     *
     * @param moduleCode the module code
     * @return module info
     */
     ModuleInfo findModuleInfo(String moduleCode);

    /**
     * 根据模块哈希找到模块信息
     *
     * @param hash the hash
     * @return module info
     */
     ModuleInfo findModuleInfoByHash(String hash);

    /**
     * 注册一个Java组件
     * @param moduleInfo
     * @return
     */
     public ModuleInfo registerComponent(ModuleInfo moduleInfo);

}
