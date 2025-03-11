package cn.mapway.ui.server.mvc;

import cn.mapway.ui.client.mvc.IModule;
import cn.mapway.ui.client.mvc.ModuleFactory;
import cn.mapway.ui.client.mvc.ModuleInfo;
import com.google.gwt.core.client.GWT;

import java.util.*;

/**
 * Temp
 *
 * @author zhang
 */
public class Temp implements ModuleFactory {
    List<ModuleInfo> modules = new ArrayList<>();
    Map<String, Object> moduleInstances = new HashMap<>();
    List<ModuleInfo> modulesFlat = new ArrayList<>();
    public Temp() {
        //构造模块树
        ModuleInfo moduleInfo = new ModuleInfo(null, null, null, false, null, null, true);
        modulesFlat.add(moduleInfo);
        buildModuleTree();
    }

    /**
     * 构造模型树
     * [list  --> tree]
     */
    private void buildModuleTree() {
        for (ModuleInfo info : modulesFlat) {
            ModuleInfo parent = sureParent(info);
            if (parent == null) {
                modules.add(info);
            } else {
                parent.children.add(info);
            }
        }
        sort(modules);
    }

    private void sort(List<ModuleInfo> modules) {
        if (modules == null || modules.size() == 0) {
            return;
        }
        for (ModuleInfo info : modules) {
            sort(info.children);
        }
        Collections.sort(modules, new Comparator<ModuleInfo>() {
            @Override
            public int compare(ModuleInfo o1, ModuleInfo o2) {
                return o1.order > o2.order ? 1 : -1;
            }
        });
    }

    private ModuleInfo sureParent(ModuleInfo info) {
        if (info.parent == null || info.parent.isEmpty()) {
            return null;
        }
        for (ModuleInfo info2 : modulesFlat) {
            if (info2.code.equals(info.parent)) {
                return info2;
            }
        }
        return null;
    }

    private ModuleInfo findByCode(List<ModuleInfo> parents, String moduleCode) {
        if(moduleCode == null || moduleCode.isEmpty() || parents == null || parents.size() == 0){
            return null;
        }
        for(ModuleInfo info : parents){
            if(info.code.equals(moduleCode)){
                return info;
            }
        }
        return null;
    }
    private ModuleInfo findHash(List<ModuleInfo> parents, String hashCode) {
        if (hashCode == null || hashCode.isEmpty() || parents == null || parents.size() == 0) {
            return null;
        }
        for(ModuleInfo info : parents){
            if(info.hash.equals(hashCode)){
                return info;
            }
        }
        return null;
    }

    @Override
    public IModule createModule(String moduleCode, boolean single) {
        if (moduleCode == null || moduleCode.isEmpty()) {
            GWT.log("moduleCode is null");
            return null;
        }
        if (single) {
            IModule module = (IModule) moduleInstances.get(moduleCode);
            if (module == null) {
                module = createOne(moduleCode);
                moduleInstances.put(moduleCode, module);
            }
            return module;
        } else {
            return createOne(moduleCode);
        }
    }

    private IModule createOne(String moduleCode) {
        if (moduleCode == null || moduleCode.isEmpty()) {
            return null;
        }
        //
        return null;
    }

    @Override
    public List<ModuleInfo> getModules() {
        return modulesFlat;
    }

    @Override
    public List<ModuleInfo> getRootModules() {
        return modules;
    }

    @Override
    public ModuleInfo findModuleInfo(String moduleCode) {
        return findByCode(modulesFlat, moduleCode);
    }

    @Override
    public ModuleInfo findModuleInfoByHash(String hash) {
        return findHash(modulesFlat, hash);
    }

    @Override
    public ModuleInfo registerComponent(ModuleInfo moduleInfo) {
        modulesFlat.add(moduleInfo);
        modules.add(moduleInfo);
        return moduleInfo;
    }

}
