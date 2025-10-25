# mapway-gwt-suit

based gwt,provider a bunch of tools to build a mvc application.

## Quick Start

```xml
<dependency>
    <groupId>cn.mapway</groupId>
    <artifactId>mapway-gwt-common</artifactId>
    <version>1.0.11</version>
</dependency>
```

## Build & Install & Deploy

```shell


#如果只需安装到本地 请执行下面命令
git clone https://gitlab.cangling.io/develop/mapway-gwt-suit.git
cd mapway-gwt-suit
mvn clean package install

#发布到 cangling repository
# 如果需要发布到其他仓库 请修改 变量
deploy.sh

```

## Reference
- [GWT Book](https://livebook.manning.com/book/gwt-in-action-second-edition/about-this-book/)

- IntelliJ IDEA Run Panel Maven彩色输出配置 [参考](https://youtrack.jetbrains.com/issue/IDEA-181337/Make-Maven-plugin-use-colors-in-Run-window)

```code
  -Djansi.passthrough=true      
```

- git push multiple remote 
```console
git config --global alias.pushall '!git remote | xargs -L1 git push --all'
```