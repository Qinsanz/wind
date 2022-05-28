package com.worth.wind.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.worth.wind.blog.dao.ArticleTagDao;
import com.worth.wind.blog.dto.TagBackDTO;
import com.worth.wind.common.util.PageUtils;
import com.worth.wind.blog.vo.ConditionVO;
import com.worth.wind.blog.vo.PageResult;
import com.worth.wind.blog.dto.TagDTO;
import com.worth.wind.blog.entity.ArticleTag;
import com.worth.wind.blog.entity.Tag;
import com.worth.wind.blog.dao.TagDao;
import com.worth.wind.common.exception.BizException;
import com.worth.wind.blog.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worth.wind.common.util.BeanCopyUtils;
import com.worth.wind.blog.vo.TagVO;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 标签服务
 *
 * @author yezhiqiu
 * @date 2021/07/28
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagDao, Tag> implements TagService {
    @Autowired
    private TagDao tagDao;
    @Autowired
    private ArticleTagDao articleTagDao;

    @Override
    public PageResult<TagDTO> listTags() {
        // 查询标签列表
        List<Tag> tagList = tagDao.selectList(null);
        // 转换DTO
        List<TagDTO> tagDTOList = BeanCopyUtils.copyList(tagList, TagDTO.class);
        // 查询标签数量
        Integer count = tagDao.selectCount(null);
        return new PageResult<>(tagDTOList, count);
    }

    @Override
    public PageResult<TagBackDTO> listTagBackDTO(ConditionVO condition) {
        // 查询标签数量
        Integer count = tagDao.selectCount(new LambdaQueryWrapper<Tag>()
                .like(StringUtils.isNotBlank(condition.getKeywords()), Tag::getTagName, condition.getKeywords()));
        if (count == 0) {
            return new PageResult<>();
        }
        // 分页查询标签列表
        List<TagBackDTO> tagList = tagDao.listTagBackDTO(PageUtils.getLimitCurrent(), PageUtils.getSize(), condition);
        return new PageResult<>(tagList, count);
    }

    @Override
    public List<TagDTO> listTagsBySearch(ConditionVO condition) {
        // 搜索标签
        List<Tag> tagList = tagDao.selectList(new LambdaQueryWrapper<Tag>()
                .like(StringUtils.isNotBlank(condition.getKeywords()), Tag::getTagName, condition.getKeywords())
                .orderByDesc(Tag::getId));
        return BeanCopyUtils.copyList(tagList, TagDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteTag(List<Integer> tagIdList) {
        // 查询标签下是否有文章
        Integer count = articleTagDao.selectCount(new LambdaQueryWrapper<ArticleTag>()
                .in(ArticleTag::getTagId, tagIdList));
        if (count > 0) {
            throw new BizException("删除失败，该标签下存在文章");
        }
        tagDao.deleteBatchIds(tagIdList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer saveOrUpdateTag(TagVO tagVO) {
        // 查询标签名是否存在
        Tag existTag = tagDao.selectOne(new LambdaQueryWrapper<Tag>()
                .select(Tag::getId)
                .eq(Tag::getTagName, tagVO.getTagName())
                .eq(tagVO.getCategoryId()!=null,Tag::getCategoryId,tagVO.getCategoryId()));
        if (Objects.nonNull(existTag) && !existTag.getId().equals(tagVO.getId())) {
            log.warn("标签已存在！");
            return existTag.getId();
        }
        Tag tag = BeanCopyUtils.copyObject(tagVO, Tag.class);
        this.saveOrUpdate(tag);
        return tag.getId();
    }

}
