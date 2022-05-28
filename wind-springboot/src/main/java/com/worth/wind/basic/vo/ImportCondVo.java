package com.worth.wind.basic.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Qinsanz
 * @date 2022/2/6
 */
@Data
@ApiModel(description = "导入查询VO")
public class ImportCondVo extends ImportSaveVo {
    /**
     * 导入类型
     */
    @ApiModelProperty(name = "importFileUrl", value = "导入类型", dataType = "String")
    private String importType;

    /**
     * 导入版本号
     */
    @ApiModelProperty(name = "importFileId", value = "导入版本号", dataType = "String")
    private String importVersion;
}
