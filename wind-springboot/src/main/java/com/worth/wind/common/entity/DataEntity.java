package com.worth.wind.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.worth.wind.blog.dto.UserDetailDTO;
import com.worth.wind.common.util.UserUtils;
import com.worth.wind.common.util.IdUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author qiuwen
 */
@Data
public abstract class DataEntity<T> implements Serializable {

    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";

    protected String id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createDate;

    protected String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime updateDate;

    protected String updateBy;

    protected String delFlag;

    @ApiModelProperty(value="版本号",name="version" )
    private  Integer version;

    private Integer pageSize;

    private Integer pageNo;

    public void preInsert(String uuid){
    /** 对应数据目的是都创建新的id */
       /* this.id =(this.id!=null)?this.id:IdGen.uuid();*/
        this.id =uuid == null ? IdUtils.uuid() : uuid;
        UserDetailDTO currentUser = UserUtils.getLoginUser();
        this.updateBy = currentUser == null ? "" : String.valueOf(currentUser.getId());
        this.createBy = updateBy;

        this.updateDate = LocalDateTime.now();
        this.createDate = updateDate;
        this.delFlag = DEL_FLAG_NORMAL;

        this.version = 1;
    }

    public void preInsert(){
        preInsert(IdUtils.uuid());
    }

    public void preUpdate(){
        this.createBy = null;
        this.createDate = null;

        UserDetailDTO currentUser = UserUtils.getLoginUser();
        this.updateBy = currentUser == null ? "" : String.valueOf(currentUser.getId());
        this.updateDate = LocalDateTime.now();
    }

    public void preInsertForSetUser(String userId) {
        this.setCreateBy(userId);
        this.setUpdateBy(userId);
    }

    public void preUpdateForSetUser(String userId) {
        this.setUpdateBy(userId);
    }

    public void preDelete(){
        this.createBy = null;
        this.createDate = null;

        this.delFlag = DEL_FLAG_DELETE;
        UserDetailDTO currentUser = UserUtils.getLoginUser();
        this.updateBy = currentUser == null ? "" : String.valueOf(currentUser.getId());
        this.updateDate = LocalDateTime.now();
    }

    public void preQueryPage(){
        if (pageNo != null && pageSize != null) {
            pageNo = (pageNo - 1) * pageSize;
        }
    }
}
