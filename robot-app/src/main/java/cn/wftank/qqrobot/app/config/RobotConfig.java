package cn.wftank.qqrobot.app.config;

import cn.wftank.qqrobot.app.handler.QQEventHandlers;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RobotConfig {

    private static final Logger log = LoggerFactory.getLogger(RobotConfig.class);

//    @Bean
//    public Bot bot(QQEventHandlers qqEventHandlers){
//        String miraiProtocol = GlobalConfig.getConfig(ConfigKeyEnum.MIRAI_PROTOCOL);
//        //默认手机登录
//        if (StringUtils.isBlank(miraiProtocol)){
//            miraiProtocol = "ANDROID_PHONE";
//        }
//        BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.valueOf(miraiProtocol);
//        protocol = protocol == null ? BotConfiguration.MiraiProtocol.ANDROID_PHONE : protocol;
//
//        BotConfiguration.MiraiProtocol finalProtocol = protocol;
//        Bot bot = BotFactory.INSTANCE.newBot(Long.valueOf(GlobalConfig.getConfig(ConfigKeyEnum.QQ)),
//                GlobalConfig.getConfig(ConfigKeyEnum.PASSWORD),new BotConfiguration() {{
//            fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
//            setProtocol(finalProtocol); // 切换协议
//            File workDir = new File("./qqbot");
//            if (!workDir.exists()){
//                try {
//                    Files.createDirectories(workDir.toPath());
//                } catch (IOException e) {
//                    log.error(ExceptionUtils.getStackTrace(e));
//                }
//            }
//            setWorkingDir(workDir);
//            setNetworkLoggerSupplier(bot -> LoggerAdapters.asMiraiLogger(LoggerFactory.getLogger(this.getClass())));
//            setBotLoggerSupplier(bot -> LoggerAdapters.asMiraiLogger(LoggerFactory.getLogger(this.getClass())));
//        }});
//        bot.getEventChannel()
//                .filter(ev -> ev instanceof GroupEvent)
//                .registerListenerHost(qqEventHandlers);
//        bot.login();
//        return bot;
//    }

    @Bean
    public Bot bot(QQEventHandlers qqEventHandlers){
        return new Bot() {
            @NotNull
            @Override
            public BotConfiguration getConfiguration() {
                return null;
            }

            @Override
            public long getId() {
                return 0;
            }

            @NotNull
            @Override
            public String getNick() {
                return null;
            }

            @NotNull
            @Override
            public MiraiLogger getLogger() {
                return null;
            }

            @Override
            public boolean isOnline() {
                return false;
            }

            @NotNull
            @Override
            public EventChannel<BotEvent> getEventChannel() {
                return null;
            }

            @NotNull
            @Override
            public ContactList<OtherClient> getOtherClients() {
                return null;
            }

            @NotNull
            @Override
            public Friend getAsFriend() {
                return null;
            }

            @NotNull
            @Override
            public Stranger getAsStranger() {
                return null;
            }

            @NotNull
            @Override
            public ContactList<Stranger> getStrangers() {
                return null;
            }

            @NotNull
            @Override
            public ContactList<Friend> getFriends() {
                return null;
            }

            @NotNull
            @Override
            public ContactList<Group> getGroups() {
                return null;
            }

            @Nullable
            @Override
            public Object login(@NotNull Continuation<? super Unit> continuation) {
                return null;
            }

            @Override
            public void close(@Nullable Throwable throwable) {

            }

            @NotNull
            @Override
            public CoroutineContext getCoroutineContext() {
                return null;
            }
        };
    }

}
