package com.worth.wind.basic.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.worth.wind.basic.vo.BasicVo;
import com.worth.wind.common.constant.CommonConst;
import com.worth.wind.common.util.UserUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 基础类
 * @author Qinsanz
 * @date 2022/2/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 删除标记  0否 1是
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 更新人
     */
    private Integer updateBy;

    /**
     * 备注
     */
    private String remark;

    public void preInsert(BasicVo vo){
        if(vo.getId()==null){
            this.createBy= UserUtils.getLoginUser().getUserInfoId();
        }else{
            this.updateBy= UserUtils.getLoginUser().getUserInfoId();
        }
        this.delFlag= CommonConst.FALSE;
    }

}
