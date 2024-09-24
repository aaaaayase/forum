package com.yun.forum.dao;

import com.yun.forum.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message row);

    int insertSelective(Message row);

    Message selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Message row);

    int updateByPrimaryKey(Message row);

    /**
     * 查询未读站内信数量
     *
     * @param receiveUserId
     * @return
     */
    Integer selectUnreadCount(@Param("receiveUserId") Long receiveUserId);

    /**
     * 查找站内信列表
     *
     * @param receiveUserId
     * @return
     */
    List<Message> selectByReceiveUserId(@Param("receiveUserId") Long receiveUserId);
}