# 星际公民 QQ群助手
基于开源项目 https://github.com/mamoe/mirai
，感谢该项目的所有贡献者
## 功能
### 商品查询
1. 使用特殊关键字触发查询，支持中文英文  
   正则如下，会提取第一部分内容
    ```regexp
    ^(.+)(在|去|到)*.*(哪|那)+.*(买|卖|租)+.*$
    ```
   例：
   ```
   cf117在哪买?
   ```
2. 使用@小助手 -s [搜索内容]查询，将搜索内容用“{}”包裹，则使用精确查询
   例：
   ```
   #模糊查询
   @小助手 -s cf117
   #精确查询
   @小助手 -s {CF-117 "恶犬"能量速射炮}
   ```
### 提醒
1. 官方公告提醒（可接百度翻译，翻译标题）
2. bug提交提醒（可接百度翻译，翻译标题）
3. G-LAO新帖提醒
## 使用
- 私有部署
  1. 将jar文件下载到本地
  2. 在jar文件相同目录下创建config.txt文件，配置文件内容如下
    ```properties
    #qq号
    qq=机器人QQ号
    #QQ密码
    password=QQ密码
    #监控的群号,多个用英文逗号分隔
    groups=123,1234
    #登录方式 ANDROID_PHONE ANDROID_PAD ANDROID_WATCH
    # 注意: ANDROID_PAD不支持戳一戳事件解析
    mirai.protocol=ANDROID_PAD
    #百度翻译设置,删掉就没有翻译
    translate.baidu.appid=
    translate.baidu.secret=
    ```
  3. 启动服务，在jar文件所在目录通过命令行执行
    ```shell
    java -jar 你的jar文件名.jar
    ```
  4. 所在目录下会生成一系列标记文件和日志，无视即可
- 直接使用我自己的服务  
   通过各种方式联系到我WFtank（贴吧、QQ群），我提供服务，只需要把我的小助手加到你的群里即可使用。
