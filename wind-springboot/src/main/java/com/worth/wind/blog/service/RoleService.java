package com.worth.wind.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worth.wind.blog.vo.PageResult;
import com.worth.wind.blog.dto.RoleDTO;
import com.worth.wind.blog.dto.UserRoleDTO;
import com.worth.wind.blog.entity.Role;
import com.worth.wind.blog.vo.ConditionVO;
import com.worth.wind.blog.vo.RoleVO;

import java.util.List;

/**
 * 角色服务
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取用户角色选项
     *
     * @return 角色
     */
    List<UserRoleDTO> listUserRoles();

    /**
     * 查询角色列表
     *
     * @param conditionVO 条件
     * @return 角色列表
     */
    PageResult<RoleDTO> listRoles(ConditionVO conditionVO);

    /**
     * 保存或更新角色
     *
     * @param roleVO 角色
     */
    void saveOrUpdateRole(RoleVO roleVO);

    /**
     * 删除角色
     * @param roleIdList 角色id列表
     */
    void deleteRoles(List<Integer> roleIdList);

}
