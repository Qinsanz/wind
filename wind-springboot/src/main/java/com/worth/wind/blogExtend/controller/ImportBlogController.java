//package com.worth.wind.blogExtend.controller;
//
//import com.worth.wind.blog.vo.Result;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.web.bind.annotation.*;
//
//
///**
// * 博客导入模块
// * @author Qinsanz
// * @date 2022/2/5
// */
//@Api(tags = "博客导入模块")
//@RestController
//public class ImportBlogController {
//
//
//    /**
//     * 导入对应文件夹下md文档 生成博客
//     *
//     * @return {@link Result < String >} 每批导入生成的编号 方便统一操作
//     */
//    @ApiOperation(value = "导入对应文件夹下md文档")
//    @GetMapping("/admin/import/md")
//    public Result<String> importMd() {
//        return Result.ok();
//    }
//
//    /**
//     * 删除 导入对应文件夹下md文档 生成的博客
//     *
//     * @param key 每批导入生成的编号 可以到操作记录中查找
//     * @return {@link Result<>}
//     */
//    @ApiOperation(value = "查询角色列表")
//    @GetMapping("/admin/import/md/del")
//    public Result<?> importMdDel(String key) {
//        return Result.ok();
//    }
//
//}
