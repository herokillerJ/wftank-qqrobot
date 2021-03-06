package cn.wftank.qqrobot.app.config;

import net.mamoe.mirai.utils.MiraiLoggerPlatformBase;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiraiLogger extends MiraiLoggerPlatformBase {

    private static final Logger logger = LoggerFactory.getLogger("MIRAI-ROBOT");

    @Override
    protected void debug0(@Nullable String s, @Nullable Throwable throwable) {
        logger.debug(s, throwable);
    }

    @Override
    protected void error0(@Nullable String s, @Nullable Throwable throwable) {
        logger.error(s, throwable);
    }

    @Override
    protected void info0(@Nullable String s, @Nullable Throwable throwable) {
        logger.info(s, throwable);
    }

    @Override
    protected void verbose0(@Nullable String s, @Nullable Throwable throwable) {
        logger.info(s, throwable);
    }

    @Override
    protected void warning0(@Nullable String s, @Nullable Throwable throwable) {
        logger.warn(s, throwable);
    }

    @Nullable
    @Override
    public String getIdentity() {
        return "MIRAI-ROBOT";
    }


}
