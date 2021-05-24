# flyrafter-mvn-plugin

## FlyRafter-Maven-Plugin 是什么

`FlyRafter-Maven-Plugin` 是基于 `FlyRafter` 实现的 maven 插件。

## 特性
- 解析 JPA 相关注解标记的实体类，生成相应的 DDL 语句
- 自动读取配置文件 `application.properties` 或 `application.yaml` 中的相关数据库、FlyWay 及 FlyRafter 的配置，连接数据库，比对数据表，生成 SQL 文件

## 快速开始 

1. 方法一
- maven `pom.xml` 添加插件：

```xml
<plugin>
  <groupId>fun.mortnon</groupId>
  <artifactId>flyrafter-maven-plugin</artifactId>
  <version>0.0.1</version>
</plugin>
```

- 执行

输入命令
```bash
mvn flyrafter:generate
```

2. 方法二
- maven `pom.xml` 添加插件并指定 `compile` 阶段后执行命令 `generate`：

```xml
<plugin>
  <groupId>fun.mortnon</groupId>
  <artifactId>flyrafter-maven-plugin</artifactId>
  <version>0.0.1</version>
  <executions>
    <execution>
      <phase>compile</phase>
      <goals>
        <goal>generate</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

## 更多

参阅 [Wiki](https://gitee.com/mortise-and-tenon/flyrafter-mvn-plugin/wikis)
