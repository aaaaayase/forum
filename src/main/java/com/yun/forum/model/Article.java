package com.yun.forum.model;

import lombok.Data;

import java.util.Date;

@Data
public class Article {
    private Long id;

    private Long boardId;

    private Long userId;

    private String title;

    private Integer visitCount;

    private Integer replyCount;

    private Integer likeCount;

    private Byte state;

    private Byte deleteState;

    private Date createTime;

    private Date updateTime;

    private String content;

    private Boolean Own;

    // 关联对象
    private User user;

    // 关联版块
    private Board board;
}