package com.worth.wind.basic.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础类
 * @author Qinsanz
 * @date 2022/2/6
 */
@Data
public class BasicDto {
    /**
     * id
     */
    private Integer id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 备注
     */
    private String remark;


}
