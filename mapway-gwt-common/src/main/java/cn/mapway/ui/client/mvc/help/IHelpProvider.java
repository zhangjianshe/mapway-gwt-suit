package cn.mapway.ui.client.mvc.help;

/**
 * IHelpProvider
 * 帮助信息提供器接口
 *
 * @author zhang
 */
public interface IHelpProvider {
    /**
     * 模块提供的帮助信息
     *
     * @param helpId 暂时没有使用
     * @return
     */
    HelpInfo getHelpInfo(String helpId);
}
