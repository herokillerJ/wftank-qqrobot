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
    }

    private static final ThreadPoolExecutor DIR_MONITOR = new ScheduledThreadPoolExecutor(10,  new BasicThreadFactory.Builder().daemon(true)
            .namingPattern("command-dir-checker").build());

    private static final Map<String, Path> commandMap = new ConcurrentHashMap<>();


    @PostConstruct
    private void initCommand(){
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
            if (files[i].isFile()){
                String command = FilenameUtils.removeExtension(files[i].getName());
                addCommand(command, files[i].toPath());
            }
        }
        try {
            WatchDir watchDir = new WatchDir(Paths.get(dir).toAbsolutePath().normalize(), false, (event, filePath) -> {
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

    public MessageChain handleCommand(String command, GroupMessageEvent event){
        Path path = commandMap.get(command);
        MessageChainBuilder messageBuilder = new MessageChainBuilder();
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
        return messageBuilder.build();
    }

    public boolean checkCommand(String command){
        return commandMap.containsKey(command);
    }

    public void modifyCommand(String command, Path commandPath) {

    }
}
