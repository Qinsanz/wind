package com.worth.wind.basic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 导入类型
 * @author Qinsanz
 * @date 2022/2/6
 */
@Getter
@AllArgsConstructor
public enum ImportTypeEnum {
    /**
     * 公开
     */
    BLOG("blog", "博客"),
    /**
     * 私密
     */
    BASIC("basic", "基础通用");

    /**
     * 状态
     */
    private final String type;

    /**
     * 描述
     */
    private final String desc;

}
