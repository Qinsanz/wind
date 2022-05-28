package com.worth.wind.basic.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author Qinsanz
 * @date 2022/2/6
 */
@Data
@ApiModel(description = "导入保存VO")
public class ImportSaveVo extends BasicVo {

    /**
     * 导入文件路径
     */
    @ApiModelProperty(name = "importFileUrl", value = "导入文件路径", dataType = "String")
    private String importFileUrl;

    /**
     * 导入文件id
     */
    @ApiModelProperty(name = "importFileId", value = "导入文件id", dataType = "String")
    private Integer importFileId;

}
