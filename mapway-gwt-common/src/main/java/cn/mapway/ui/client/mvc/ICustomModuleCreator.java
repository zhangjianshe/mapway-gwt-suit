package cn.mapway.ui.client.mvc;

@FunctionalInterface
public interface ICustomModuleCreator {
    IModule create();
}
