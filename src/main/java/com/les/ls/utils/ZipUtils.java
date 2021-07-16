package com.les.ls.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * zip工具类
 *
 * @author lshuai
 */
@Slf4j
public class ZipUtils {

    /**
     * zip解压
     *
     * @param srcFile     源zip文件
     * @param destDirPath 解压目录
     * @return 解压结果
     */
    public static boolean unzip(File srcFile, String destDirPath) {
        final int BUFFER_SIZE = 1024;
        try (ZipFile zipFile = new ZipFile(srcFile)) {
            Enumeration<?> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
                File file = new File(destDirPath + File.separator + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!file.mkdirs()) {
                        log.warn("解压时创建文件夹失败！");
                    }
                } else {
                    if (!file.getParentFile().exists()) {
                        if (!file.getParentFile().mkdirs()) {
                            log.warn("解压时创建文件夹失败！");
                            continue;
                        }
                    }
                    if (!file.createNewFile()) {
                        log.warn("解压时创建文件失败！file->{}", file.getName());
                        continue;
                    }
                    try (InputStream is = zipFile.getInputStream(zipEntry);
                         FileOutputStream fos = new FileOutputStream(file)) {
                        int len;
                        byte[] buf = new byte[BUFFER_SIZE];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                    } catch (IOException e) {
                        log.warn("解压文件时发生了异常！", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("文件解压失败！path->{}", srcFile.getPath(), e);
            return false;
        }
        return true;
    }
}
