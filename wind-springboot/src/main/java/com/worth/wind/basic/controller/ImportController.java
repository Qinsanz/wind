package com.worth.wind.basic.controller;

import com.worth.wind.basic.service.ImportService;
import com.worth.wind.basic.vo.ImportCondVo;
import com.worth.wind.basic.vo.ImportSaveVo;
import com.worth.wind.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 导入模块
 * @author Qinsanz
 * @date 2022/2/5
 */
@Api(tags = "导入模块")
@RestController
public class ImportController {

    @Autowired
    private ImportService importService;

    /**
     * 导入对应文件内容 生成版本号
     *
     * @return {@link Result < String >} 每批导入生成的版本号 方便统一操作
     */
    @ApiOperation(value = "导入对应文件内容 生成版本号")
    @PostMapping("/admin/import")
    public Result<String> importBasic(@Valid @RequestBody ImportSaveVo vo) {
        return importService.importBasic(vo);
    }

    /**
     * 删除 版本号对应内容
     *
     * @param key 每批导入生成的编号 可以到操作记录中查找
     *  @return {@link Result<>}
     */
    @ApiOperation(value = "删除 版本号对应内容")
    @GetMapping("/admin/import/del")
    public Result<?> importBasicDel(String key) {
        return Result.ok();
    }

    /**
     * 导入查询
     *
     * @return {@link Result < String >} 导入查询
     */
    @ApiOperation(value = "导入查询")
    @PostMapping("/admin/import/list")
    public Result<?> listArticlesByCondition(@Valid @RequestBody ImportCondVo condition) {
        return importService.listArticlesByCondition(condition);
    }
}
