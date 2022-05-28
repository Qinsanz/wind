package com.worth.wind.blogExtend.controller;

import com.worth.wind.blog.enums.FilePathEnum;
import com.worth.wind.common.strategy.context.UploadStrategyContext;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * PicGo 图片上传
 * @author Qinsanz
 * @date 2022/2/5
 */
@Api(tags = "PicGo 图片上传")
@RestController
public class PicGoController {

    @Autowired
    private UploadStrategyContext uploadStrategyContext;


    @PostMapping("/picgo")
    @ResponseBody
    public String picGoUpload(MultipartFile img, String key){
        //判断key是否合法
        //转存图片
        //返回图片的网络路径
        return uploadStrategyContext.executeUploadStrategy(img, FilePathEnum.IMG.getPath());
    }



}
