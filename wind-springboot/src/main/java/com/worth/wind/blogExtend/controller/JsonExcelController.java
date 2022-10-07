package com.worth.wind.blogExtend.controller;

import com.worth.wind.blogExtend.service.JsonExcelService;
import com.worth.wind.common.entity.BusinessWorkEntity;
import com.worth.wind.common.util.BusinessWorkUtils;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 处理JSON字符串 、http请求   转换成Excel文件
 * @author Qinsanz
 * @date 2022/2/5
 */
@Api(tags = "处理JSON、HTTP请求数据转换成Excel文件")
@RestController
@Log4j2
public class JsonExcelController {

    @Autowired
    private JsonExcelService jsonExcelService;


    @PostMapping("/json/excel")
    @ResponseBody
    public String picGoUpload(String curl,String pageString,String dataIndex){
        return jsonExcelService.jsonExcelService(curl,pageString,dataIndex);
    }


    @PostMapping("/get/businessWork")
    @ResponseBody
    public BusinessWorkEntity businessWorkEntity(String businessId){
        return BusinessWorkUtils.queryBusiness(businessId);
    }



}
