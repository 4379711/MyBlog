package com.my.blog.website.service;

import com.my.blog.website.model.Vo.RelationshipVoKey;

import java.util.List;

/**
 * @author liuyalong
 */
public interface IRelationshipService {
    /**
     * 按主键删除
     */
    void deleteById(Integer cid, Integer mid);

    /**
     * 按主键统计条数
     *
     * @return 条数
     */
    Long countById(Integer cid, Integer mid);


    /**
     * 保存對象
     */
    void insertVo(RelationshipVoKey relationshipVoKey);

    /**
     * 根据id搜索
     */
    List<RelationshipVoKey> getRelationshipById(Integer cid, Integer mid);
}
