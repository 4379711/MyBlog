package com.my.blog.website.service;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.modal.Vo.CommentVo;
import com.my.blog.website.modal.Vo.CommentVoExample;
import com.my.blog.website.modal.Bo.CommentBo;
/**
 * @author liuyalong
 */
public interface ICommentService {

    /**
     * 保存对象
     */
    void insertComment(CommentVo commentVo);

    /**
     * 获取文章下的评论
     */
    PageInfo<CommentBo> getComments(Integer cid, int page, int limit);

    /**
     * 获取文章下的评论
     */
    PageInfo<CommentVo> getCommentsWithPage(CommentVoExample commentVoExample, int page, int limit);


    /**
     * 根据主键查询评论
     */
    CommentVo getCommentById(Integer coid);


    /**
     * 删除评论，暂时没用
     */
    void delete(Integer coid, Integer cid);

    /**
     * 更新评论状态
     */
    void update(CommentVo comments);

}
