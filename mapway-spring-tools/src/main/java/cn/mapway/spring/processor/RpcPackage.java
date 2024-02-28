package cn.mapway.spring.processor;

import java.lang.annotation.*;

/**
 *  注解在一个 package-info.java 中的 package 上
 *  注解代码生产器 (ClientRpcStubGenerator) 根据此配置 再输出目录中 生成一个接口
 *  代码生成器 会扫描这个 package下面的 Controller类（包括RestController） ,根据类中方法的注解
 *  生成一个或者多个 接口文件
 *  Controller  --> IXXXClientProxy             -->                   XXXClientProxy
 *        [本生成器]              [mapway-gwt-common:JsonRpcProcessor]    客户端使用
 *  文件的输出路径 可以在编译器命令行中 添加参数
 *  <code>
 *      -ARPC_OUT_PATH=${project.basedir}/src/test/java
 *  </code>
 *  maven POM文件可以如下配置
 *  <code>
 *           <plugin>
 *                 <groupId>org.apache.maven.plugins</groupId>
 *                 <artifactId>maven-compiler-plugin</artifactId>
 *                 <configuration>
 *                     <source>1.8</source>
 *                     <target>1.8</target>
 *                     <compilerArgs>
 *                         <arg>-ARPC_OUT_PATH=${project.basedir}/src/test/java</arg>
 *                     </compilerArgs>
 *                 </configuration>
 *             </plugin>
 *  </code>
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PACKAGE})
public @interface RpcPackage {
    String localPath() default "";

    /**
     * IXXXClientProxy 的包名
     * 缺省会使用 注解下的包名称
     * @return
     */
    String packageName() default "";
    String name() default "";

    /**
     * XXXClientProxy 所在的包
     * @return
     */
    String proxyPackage() default "";

    /**
     * XXXClientProxy 的自定义名称
     * @return
     */
    String proxyName() default "";
    Class[] excludes() default {};
    Class[] includes() default {};
    boolean merge() default true;

    /**
     * 定义一些类转换成另外一些类 这些类将不会生成代码 只是转换 需要项目提供该类的实现
     * 比如 cn.mapway.ui.RpcResult:cn.client.ApiResult
     * 数组可以添加多对数据
     * @return
     */
    String[] maps() default {};
}
