package cn.wftank.qqrobot.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    private static final String TMP_DIR = "./temp";
    private static final Tika tika = new Tika();


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

    public static MimeType detectFileMimeType(File file) {
        return detectFileMimeType(file.toPath());
    }

    public static MimeType detectFileMimeType(Path path) {
        if (Objects.isNull(path)){
            return null;
        }

        try {
            String mimeType = tika.detect(path);
            return MimeType.valueOf(mimeType);
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));

        }
        return null;
    }

    public static String generateFileName(String fileName){
        return fileName+"_"+System.currentTimeMillis();
    }

}
