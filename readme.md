本项目适用于以下用户：
1. 拥有外网IP，但外网IP不固定。
2. 拥有支持JAVA的群晖
3. 拥有支持DMZ主机或端口转的路由器

# 阿里云
在正式开始前，你需要来到阿里云的：RAM访问控制 -> 人员管理 -> 用户。
1. 创建1个新用户
2. 为该用户创建 AccessKey。会生成1个AccessKey ID，以及1个AccessKey Secret（该secret只出现1次，要保存好）
3. 为该用户赋予AliyunDNSFullAccess权限。
4. 你需要有个阿里的域名
5. 在域名管理中增加一个nas的专用域名。比如：`nas.test.com`

# 本程序
使用方法：

0. 在套件中心安装java8
1. `maven install`打包应用
2. 打开群晖控制面板 -> 终端机和SNMP -> 选择启用SSH功能
3. 将打包后的jar文件上传到File Station中的某个文件夹，推荐上传到第4步使用到的用户的home文件夹中。
4. 使用administrators用户组中的用户ssh登录系统
5. 找到当前用户的home文件夹，比如我的为：/volume1/homes/panjie
6. 执行`nohup java -jar 下载nas-ddns-x.x.x.jar --id=阿里云AccessKeyID --secret=阿里云AccessKeySecret --domain-name=你的主域名 --sub-domain=你的二级域名 > nas.log &`，比如：`nohup java -jar nas-ddns-1.0.0.jar --id=2zNxSAeSByVcxHSaDEDCdweID --secret=2zNxSAeSByVcxSecret --domain-name=test.com --sub-domain=nas > nas.log &`
7. 执行无误退出终端，并关闭SSH功能

> 使用`ps -a`或`ps -ef`查看进程信息

# 注意事项
1. 应用启动时会自动的打印日志，如果有错误会在系统启动时报错，请注意看报错信息。
2. 系统以 https://jsonip.com/ 返回的IP为依据，如果你使用软路由等自动分流的软件，请确认 jsonip.com 位于直连列表中。
