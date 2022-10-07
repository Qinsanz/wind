package com.worth.wind.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件路径枚举
 *
 * @author yezhiqiu
 * @date 2021/08/04
 */
@Getter
@AllArgsConstructor
public enum FilePathEnum {
    /**
     * 头像路径
     */
    AVATAR("avatar/", "头像路径"),
    /**
     * 文章图片路径
     */
    ARTICLE("articles/", "文章图片路径"),
    /**
     * 音频路径
     */
    VOICE("voice/", "音频路径"),
    /**
     * 照片路径
     */
    PHOTO("photos/","相册路径"),
    /**
     * PicGo  图片路径
     */
    IMG("img/","PicGo路径"),
    /**
     * TMP  临时路径
     */
    TMP("tmp/","临时路径"),
    /**
     * Excel  Excel路径
     */
    EXCEL("excel/","Excel路径"),
    /**
     * 配置图片路径
     */
    CONFIG("config/","配置图片路径"),
    /**
     * 说说图片路径
     */
    TALK("talks/","配置图片路径");

    /**
     * 路径
     */
    private final String path;

    /**
     * 描述
     */
    private final String desc;

}
