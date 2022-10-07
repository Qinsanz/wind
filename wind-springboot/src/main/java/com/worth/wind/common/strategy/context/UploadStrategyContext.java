package com.worth.wind.common.strategy.context;

import com.worth.wind.common.strategy.UploadStrategy;
import com.worth.wind.blog.enums.UploadModeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * 上传策略上下文
 *
 * @author yezhiqiu
 * @date 2021/07/28
 */
@Service
public class UploadStrategyContext {
    /**
     * 上传模式
     */
    @Value("${upload.mode}")
    private String uploadMode;

    /**
     * 文件路径  todo  兼容上传模式
     */
    @Value("${upload.local.path}")
    private String uploadPath;

    @Autowired
    private Map<String, UploadStrategy> uploadStrategyMap;

    /**
     * 上传文件
     *
     * @param file 文件
     * @param path 路径
     * @return {@link String} 文件地址
     */
    public String executeUploadStrategy(MultipartFile file, String path) {
        return uploadStrategyMap.get(UploadModeEnum.getStrategy(uploadMode)).uploadFile(file, path);
    }


    /**
     * 文件查询
     * @param fileId 文件id或者路径
     * @return
     */
    public String getFilePath(String fileId) {
        //todo
        return fileId;
    }

    /**
     * 文件完整路径
     * @param path 部分路径
     * @return
     */
    public String getUploadPath(String path) {
        return (uploadPath+"\\"+path).replace("\\","/");
    }

}
