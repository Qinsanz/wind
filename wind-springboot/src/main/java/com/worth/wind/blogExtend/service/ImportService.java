//package com.worth.wind.blogExtend.service;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.worth.wind.basic.entity.ImportEntity;
//import com.worth.wind.blog.vo.Result;
//
///**
// * 博客导入服务
// * @author Qinsanz
// * @date 2022/2/6
// */
//public interface ImportService extends IService<ImportEntity> {
//
//    /**
//     * 导入对应文件夹下md文档
//     *
//     * @return {@link Result < String >} 每批导入生成的编号 方便统一操作
//     */
//    Result<String> importMd();
//
//    /**
//     * 删除 导入对应文件夹下md文档 生成的博客
//     *
//     * @param key 每批导入生成的编号 可以到操作记录中查找
//     * @return {@link Result<>}
//     */
//    Result<?> importMdDel(String key);
//}
