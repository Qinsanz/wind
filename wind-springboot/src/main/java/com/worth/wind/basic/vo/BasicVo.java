package com.worth.wind.basic.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * @author Qinsanz
 * @date 2022/2/6
 */
@Data
@ApiModel(description = "基础VO")
public class BasicVo {

    /**
     * ==========保存=========
     * id
     */
    @ApiModelProperty(name = "id", value = "id", dataType = "Integer")
    private Integer id;

    /**
     * 备注
     */
    @ApiModelProperty(name = "remark", value = "备注", dataType = "String")
    private String remark;



    /**
     * ==========查询=========
     * 页码
     */
    @ApiModelProperty(name = "current", value = "页码", dataType = "Long")
    private Long current;

    /**
     * 条数
     */
    @ApiModelProperty(name = "size", value = "条数", dataType = "Long")
    private Long size;

    public Long getCurrent() {
        return current==null?1:current;
    }

    public Long getSize() {
        return size==null?10:size;
    }

    /**
     * 删除标记  0否 1是
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    private LocalDateTime createStartTime;
    private LocalDateTime createEndTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
    private LocalDateTime updateStartTime;
    private LocalDateTime updateEndTime;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 更新人
     */
    private Integer updateBy;
}
