package com.worth.wind.basic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worth.wind.basic.entity.ImportEntity;
import com.worth.wind.basic.vo.ImportCondVo;
import com.worth.wind.basic.vo.ImportSaveVo;
import com.worth.wind.blog.vo.Result;

/**
 * 导入服务
 * @author Qinsanz
 * @date 2022/2/6
 */
public interface ImportService extends IService<ImportEntity> {

    /**
     * 导入
     * @return
     */
     Result<String> importBasic(ImportSaveVo vo);

     Result<?> importBasicDel(String key);

    /**
     * 查询
     * @param condition
     * @return
     */
     Result<?> listArticlesByCondition(ImportCondVo condition);

}
