# 星际公民 QQ群助手
基于开源项目 https://github.com/mamoe/mirai
，以及星际公民汉化组https://starcitizenzw.com的汉化内容，感谢以上项目的所有贡献者!
## 功能
### 商品查询（你在游戏里能够买卖租的商品都支持查询，贸易品价格为基准价格，非实时价格）
1. 使用特殊关键字触发查询，支持中文英文  
   正则如下，会提取第一部分内容
    ```regexp
    ^(.+)(在|去|到)*.*(哪|那)+.*(买|卖|租)+.*$
    ```
   例：
   ```
   cf117在哪买?
   ```
   效果：
   ![cf117在哪买.png](https://vip1.loli.io/2021/03/14/CDcaY4AP5j1MeZg.png)
2. 使用@小助手 -s [搜索内容]查询，将搜索内容用“{}”包裹，则使用精确查询
   例：
   ```
   #模糊查询
   @小助手 -s cf117
   #精确查询
   @小助手 -s {CF-117 "恶犬"能量速射炮}
   ```
   例：
   ![模糊查询和精确查询.png](https://vip2.loli.io/2021/03/14/Ao2eJUHE6CdzZhn.png)
### 提醒
1. 官方公告提醒（可接百度翻译，翻译标题）
   最近官方没啥公告，所以没例子，格式跟bug反馈一样
2. bug提交提醒（可接百度翻译，翻译标题）
   ![玩家bug提交监控.png](https://vip2.loli.io/2021/03/14/u8YVm9t71CQUse4.png)
3. G-LAO新帖提醒
   ![G佬帖子监控.png](https://vip1.loli.io/2021/03/14/uXCKBQxkDGA4sOm.png)
## 使用

- 私有部署
  1. 运行环境jdk11+（不会安装请百度）
  2. 将最新版的zip文件下载到本地，[下载地址](https://1drv.ms/u/s!AprDolSye6dIlJFOb4-j12LgLFf5iw?e=tifffe)
  3. 解压到任意目录下，修改config.txt配置文件
    ```properties
    # #wftank的星际公民数据库版本,默认最新版(latest),如需指定版本,请从连接中查看所有版本https://cdn.jsdelivr.net/gh/herokillerJ/starcitizen-data/
    sc.database.version=latest
    #qq号
    qq=机器人QQ号
    #QQ密码
    password=QQ密码
    #监控的群号,多个用英文逗号分隔
    groups=123,1234
    #登录方式 ANDROID_PHONE ANDROID_PAD ANDROID_WATCH
    # 注意: ANDROID_PAD不支持戳一戳事件解析
    mirai.protocol=ANDROID_PAD
    #百度翻译api设置,删掉就没有翻译
    translate.baidu.appid=
    translate.baidu.secret=
    ```
  4. 启动服务，在wftank-qqrobot.jar文件所在目录通过cmd、powershell等执行
    ```shell
    java -jar wftank-qqrobot.jar
    ```
  5. 所在目录下会生成一系列标记文件和日志，无视即可
- 直接使用我提供的服务  
   通过各种方式联系到我WFtank（贴吧、QQ群），我提供服务，只需要把我的小助手加到你的群里即可使用。
## 注意
1. 