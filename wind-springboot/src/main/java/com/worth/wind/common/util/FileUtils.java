package com.worth.wind.common.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.util.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件md5工具类
 *
 * @author yezhiqiu
 * @date 2021/07/28
 */
@Log4j2
public class FileUtils {

    /**
     * 获取文件md5值
     *
     * @param inputStream 文件输入流
     * @return {@link String} 文件md5值
     */
    public static String getMd5(InputStream inputStream) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(md5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 得到文件扩展名
     *
     * @param fileName 文件名称
     * @return {@link String} 文件后缀
     */
    public static String getExtName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 转换file
     *
     * @param multipartFile 多部分文件
     * @return {@link File} file
     */
    public static File multipartFileToFile(MultipartFile multipartFile) {
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String[] filename = Objects.requireNonNull(originalFilename).split("\\.");
            file = File.createTempFile(filename[0], filename[1]);
            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * file转换为MultipartFile
     *
     * @param file 文件
     * @return MultipartFile
     */
    public static MultipartFile getMultipartFile(File file) {

        InputStream input;
        try {
            input = new FileInputStream(file);
            MockMultipartFile file1 = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
            input.close();
            return file1;
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * 自动调节精度(经验数值)
     *
     * @param size 源图片大小
     * @return 图片压缩质量比
     */
    private static double getAccuracy(long size) {
        double accuracy;
        if (size < 900) {
            accuracy = 0.85;
        } else if (size < 2048) {
            accuracy = 0.6;
        } else if (size < 3072) {
            accuracy = 0.44;
        } else {
            accuracy = 0.4;
        }
        return accuracy;
    }

    /**
     * @Author：
     * @Description：获取某个目录下所有文件、文件夹
     * @Date：
     */
    public static List<String> getFiles(String path,boolean isFile) {
        List<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            String fileName=tempList[i].toString();
            if (tempList[i].isFile() && isFile) {
                //文件名
                files.add(fileName);
            }
            if (tempList[i].isDirectory() && !isFile) {
                //文件夹名
                files.add(fileName);
            }

        }
        return files;
    }

}
