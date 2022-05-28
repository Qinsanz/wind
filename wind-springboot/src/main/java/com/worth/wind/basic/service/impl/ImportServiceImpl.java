package com.worth.wind.basic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worth.wind.basic.dao.ImportDao;
import com.worth.wind.basic.dto.ImportDto;
import com.worth.wind.basic.entity.ImportEntity;
import com.worth.wind.basic.service.ImportService;
import com.worth.wind.basic.vo.ImportCondVo;
import com.worth.wind.basic.vo.ImportSaveVo;
import com.worth.wind.blog.service.ArticleService;
import com.worth.wind.blog.service.CategoryService;
import com.worth.wind.blog.service.TagService;
import com.worth.wind.blog.vo.*;
import com.worth.wind.common.util.BeanCopyUtils;
import com.worth.wind.common.util.FileUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.worth.wind.common.constant.CommonConst.FALSE;


/**
 *
 * @author Qinsanz
 * @date 2022/2/6
 */
@Log4j2
@Service
public class ImportServiceImpl extends ServiceImpl<ImportDao, ImportEntity> implements ImportService {

    @Autowired
    private ImportDao importDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ArticleService articleService;


    @Override
    public Result<String> importBasic(ImportSaveVo vo) {
        //创建分类
        String importFileUrl=vo.getImportFileUrl();
        if(StringUtils.isNotBlank(importFileUrl)){
            List<String> directoryList=FileUtils.getFiles(importFileUrl,false);
            for (String directoryUrl : directoryList) {
                String split=directoryUrl.contains("/")?"/":"\\\\";
                String[] files=directoryUrl.split(split);
                String directoryName=files[files.length-1];
                if(directoryName.contains(".")){continue;}
                Integer categoryId=null;
                try {
                    categoryId=categoryService.saveOrUpdateCategory(
                            CategoryVO.builder().id(null).categoryName(directoryName).build()
                    );
                } catch (Exception e) {
                }
                Integer tagId=null;
                List<String> fileUrlList=FileUtils.getFiles(directoryUrl,true);
                for (String fileUrl : fileUrlList) {
                    split=fileUrl.contains("/")?"/":"\\\\";
                    String[] fileNames=fileUrl.split(split);
                    String fileName=fileNames[fileNames.length-1].split("\\.")[0];
                    try {
                        tagId=tagService.saveOrUpdateTag(
                                TagVO.builder().categoryId(categoryId).tagName(fileName).build()
                        );
                    } catch (Exception e) {
                    }

                    //读取文章内容
                    String content = null;
                    try (Stream<String> lines = Files.lines(Paths.get(fileUrl))) {
                        content = lines.collect(Collectors.joining(System.lineSeparator()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //发布文章
                    String img="https://www.static.qinsanz.cn/articles/f399c1f1cba57f4e1dc1c24b2b79d81d.jpg";
                    Integer id=null;
                    List<String> tagNameList=new ArrayList<>();
                    tagNameList.add(fileName);
                    //只存后半段
                    fileUrl=fileUrl.replace(importFileUrl,"");
                    articleService.saveOrUpdateArticle(
                            ArticleVO.builder().articleContent(content).articleTitle(fileName)
                                    .articleCover(img).categoryName(directoryName)
                                    .importUrl(fileUrl).isTop(FALSE).id(id)
                                    .status(1).tagNameList(tagNameList).type(1).build()
                    );
                }

            }
        }


        //导入记录
//        ImportEntity importEntity = BeanCopyUtils.copyObject(vo, ImportEntity.class);
//        importEntity.preInsert(vo);
//        saveOrUpdate(importEntity);
        return Result.ok();
    }

    @Override
    public Result<?> importBasicDel(String key) {
        return null;
    }

    @Override
    public Result<?> listArticlesByCondition(ImportCondVo condition) {
        Page<ImportEntity> page = new Page<>(condition.getCurrent(), condition.getSize());
        // 获取分页数据
        Page<ImportEntity> entityPage = importDao.selectPage(page, new LambdaQueryWrapper<ImportEntity>()
                .eq(condition.getId()!=null,ImportEntity::getId,condition.getId())
                .like(StringUtils.isNotBlank(condition.getImportFileUrl()),ImportEntity::getImportFileUrl,condition.getImportFileUrl())
                .like(condition.getImportFileId()!=null,ImportEntity::getImportFileId,condition.getImportFileId())
                .eq(StringUtils.isNotBlank(condition.getImportType()),ImportEntity::getImportType,condition.getImportType())
                .like(StringUtils.isNotBlank(condition.getImportVersion()),ImportEntity::getImportVersion,condition.getImportVersion())
                .orderByDesc(ImportEntity::getCreateTime)
                //基础信息查询
                .eq(condition.getCreateBy()!=null,ImportEntity::getCreateBy, condition.getCreateBy())
                .eq(condition.getUpdateBy()!=null,ImportEntity::getUpdateBy, condition.getUpdateBy())
                .ge(condition.getCreateStartTime()!=null,ImportEntity::getCreateTime, condition.getCreateStartTime())
                .le(condition.getCreateEndTime()!=null,ImportEntity::getCreateTime, condition.getCreateEndTime())
                .ge(condition.getUpdateStartTime()!=null,ImportEntity::getUpdateTime, condition.getUpdateStartTime())
                .le(condition.getUpdateEndTime()!=null,ImportEntity::getUpdateTime, condition.getUpdateEndTime())
                .like(StringUtils.isNotBlank(condition.getRemark()),ImportEntity::getRemark,condition.getRemark())
                .eq(condition.getDelFlag()==null,ImportEntity::getDelFlag, FALSE)
                .eq(condition.getDelFlag()!=null,ImportEntity::getDelFlag, condition.getDelFlag()));
        List<ImportDto> list = BeanCopyUtils.copyList(entityPage.getRecords(), ImportDto.class);
        return Result.ok(new PageResult<>(list, (int) entityPage.getTotal()));
    }
}
