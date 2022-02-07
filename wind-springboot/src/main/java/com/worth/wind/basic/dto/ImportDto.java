package com.worth.wind.basic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 归档文章
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportDto extends BasicDto {

    /**
     * 导入文件路径
     */
    private String importFileUrl;

    /**
     * 导入文件id
     */
    private Integer importFileId;

    /**
     * 导入类型
     */
    private String importType;

    /**
     * 导入版本号
     */
    private String importVersion;
}
