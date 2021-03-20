package cn.wftank.qqrobot.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    private static final String TMP_DIR = "./temp";


    public static File downloadAsTmpFromUrl(String url){
        File tmpDir = new File(TMP_DIR);
        try {
            if (!tmpDir.exists()){
                Files.createDirectories(tmpDir.toPath());
            }
            Path tmpPath = Files.createTempFile(generateFileName("tmp"), null);
            File tmpFile = tmpPath.toFile();
            FileUtils.copyURLToFile(new URL(url),tmpFile);
            return tmpFile;
        } catch (IOException e) {
            e.printStackTrace();
            log.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    public static String generateFileName(String fileName){
        return fileName+"_"+System.currentTimeMillis();
    }


}
