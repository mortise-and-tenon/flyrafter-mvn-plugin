## 配置
- 调试时，demo 在IDEA的RUN中，配置执行mvn 
- Command line: flyrafter:generate
- Runner中，不勾选 `Use project settings`, `VM Options` 中配置 `-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000`

## 调试

运行demo，mvn项目中运行远程debug，端口配置为 `8000`