package com.my.blog.website.service;

import com.my.blog.website.dto.MetaDto;
import com.my.blog.website.model.Bo.ArchiveBo;
import com.my.blog.website.model.Bo.StatisticsBo;
import com.my.blog.website.model.Vo.CommentVo;
import com.my.blog.website.model.Vo.ContentVo;

import java.util.List;

/**
 * 站点服务
 */
public interface ISiteService {


    /**
     * 最新收到的评论
     */
    List<CommentVo> recentComments(int limit);

    /**
     * 最新发表的文章
     */
    List<ContentVo> recentContents(int limit);

    /**
     * 查询一条评论
     */
    CommentVo getComment(Integer coid);


    /**
     * 获取后台统计数据
     */
    StatisticsBo getStatistics();

    /**
     * 查询文章归档
     */
    List<ArchiveBo> getArchives();

    /**
     * 获取分类/标签列表
     */
    List<MetaDto> metas(String type, String orderBy, int limit);

}
