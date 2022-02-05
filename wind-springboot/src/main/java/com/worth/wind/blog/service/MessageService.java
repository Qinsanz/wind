package com.worth.wind.blog.service;

import com.worth.wind.blog.dto.MessageBackDTO;
import com.worth.wind.blog.vo.PageResult;
import com.worth.wind.blog.vo.ConditionVO;
import com.worth.wind.blog.vo.MessageVO;
import com.worth.wind.blog.dto.MessageDTO;
import com.worth.wind.blog.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import com.worth.wind.blog.vo.ReviewVO;

import java.util.List;

/**
 * 留言服务
 *
 * @author yezhiqiu
 * @date 2021/07/29
 */
public interface MessageService extends IService<Message> {

    /**
     * 添加留言弹幕
     *
     * @param messageVO 留言对象
     */
    void saveMessage(MessageVO messageVO);

    /**
     * 查看留言弹幕
     *
     * @return 留言列表
     */
    List<MessageDTO> listMessages();

    /**
     * 审核留言
     *
     * @param reviewVO 审查签证官
     */
    void updateMessagesReview(ReviewVO reviewVO);

    /**
     * 查看后台留言
     *
     * @param condition 条件
     * @return 留言列表
     */
    PageResult<MessageBackDTO> listMessageBackDTO(ConditionVO condition);

}
