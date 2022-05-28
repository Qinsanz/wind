package com.worth.wind.basic.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 导入
 * @author Qinsanz
 * @date 2022/2/6
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("wd_import")
public class ImportEntity extends BasicEntity{

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
