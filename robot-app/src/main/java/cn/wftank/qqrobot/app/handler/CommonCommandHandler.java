package cn.wftank.qqrobot.app.handler;

import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.enums.event.command.CommandEventType;
import cn.wftank.qqrobot.common.event.NotifyEventPublisher;
import cn.wftank.qqrobot.common.event.command.CommandNotifyEvent;
import cn.wftank.qqrobot.common.util.FileUtil;
import cn.wftank.qqrobot.common.util.WatchDir;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.file.AbsoluteFile;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.OfflineAudio;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 通用指令处理器,检测指令文件夹中文件的增删改来动态控制机器人支持的指令,以消息通知的形式下发到
 */
@Component
@Slf4j
public class CommonCommandHandler {

    private NotifyEventPublisher notifyEventPublisher;

    public CommonCommandHandler(NotifyEventPublisher notifyEventPublisher) {
        this.notifyEventPublisher = notifyEventPublisher;
        initCommand();
    }

    private static final ThreadPoolExecutor DIR_MONITOR = new ScheduledThreadPoolExecutor(10,  new BasicThreadFactory.Builder().daemon(true)
            .namingPattern("command-dir-checker").build());

    private static final Map<String, Path> commandMap = new ConcurrentHashMap<>();


    public void initCommand(){
        String dir = GlobalConfig.getConfig(ConfigKeyEnum.COMMAND_DIR);
        File fileDir = new File(dir);
        if (!fileDir.exists()){
            try {
                Files.createDirectories(fileDir.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        File[] files = fileDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (checkCommandFile(files[i].toPath())){
                String command = FilenameUtils.removeExtension(files[i].getName());
                addCommand(command, files[i].toPath());
            }
        }
        try {
            WatchDir watchDir = new WatchDir(Paths.get(dir).toAbsolutePath().normalize(), false, (event, filePath) -> {

                try {
                    if (checkCommandFile(filePath)){
                        WatchEvent.Kind<Path> kind = event.kind();
                        String fileName = filePath.getFileName().toString();
                        String command = FilenameUtils.removeExtension(fileName);
                        if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind)){
                            notifyEventPublisher.publish(new CommandNotifyEvent(command,filePath, CommandEventType.CREATE));
                        }else if(StandardWatchEventKinds.ENTRY_DELETE.equals(kind)){
                            notifyEventPublisher.publish(new CommandNotifyEvent(command,filePath, CommandEventType.DELETE));
                        }else if(StandardWatchEventKinds.ENTRY_MODIFY.equals(kind)){
                            notifyEventPublisher.publish(new CommandNotifyEvent(command,filePath, CommandEventType.MODIFY));
                        }
                    }
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            DIR_MONITOR.submit(() ->
                    watchDir.processEvents());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCommand(String command,Path path){
        commandMap.put(command, path.toAbsolutePath().normalize());
        log.info("添加指令:{} 路径:{}",command,path);
    }

    public void removeCommand(String command){
        Path remove = commandMap.remove(command);
        log.info("删除指令:{} 路径:{}",command,remove);
    }

    public boolean checkCommandFile(Path path){
        try {
            return Files.exists(path) && !Files.isDirectory(path) && !Files.isHidden(path) && !path.getFileName().startsWith("~$");
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    public MessageChain handleCommand(String command, GroupMessageEvent event){
        MessageChainBuilder messageBuilder = new MessageChainBuilder();
        if ("指令列表".equals(command)){
            messageBuilder.add("小助手支持以下指令，直接发送中文即可，不用带任何符号：\n");
            messageBuilder.add(String.join("\n",commandMap.keySet()));
            messageBuilder.add("\n*在哪买(查询游戏内商品在哪买,如:\"短剑在哪买\")");
            messageBuilder.add("\ngj(高级查询,可根据尺寸名称类型搜索商品名称)");
            ;
        }else{
            Path path = commandMap.get(command);
            try {
                MimeType mimeType = FileUtil.detectFileMimeType(path);
                if (null == mimeType){
                }else{
                    if ("image".equals(mimeType.getType())){
                        messageBuilder.add(event.getGroup().uploadImage(ExternalResource.create(path.toFile())));
                    }else if ("text".equals(mimeType.getType())){
                        messageBuilder.add(Files.readString(path, StandardCharsets.UTF_8));
                    }else{
                        AbsoluteFile absoluteFile = event.getGroup().getFiles().uploadNewFile(path.toAbsolutePath().toString(), ExternalResource.create(path.toFile()));
                        messageBuilder.add(absoluteFile.toMessage());
                    }
                }
            } catch (IOException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return messageBuilder.build();
    }

    public boolean checkCommand(String command){
        return commandMap.containsKey(command) || command.equals("指令列表");
    }

    public void modifyCommand(String command, Path commandPath) {

    }
}
