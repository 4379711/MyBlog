package com.my.blog.website.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.dao.AttachVoMapper;
import com.my.blog.website.utils.DateKit;
import com.my.blog.website.model.Vo.AttachVo;
import com.my.blog.website.model.Vo.AttachVoExample;
import com.my.blog.website.service.IAttachService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liuyalong
 */
@Service
public class AttachServiceImpl implements IAttachService {

    @Resource
    private AttachVoMapper attachDao;

    @Override
    public PageInfo<AttachVo> getAttachs(Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        AttachVoExample attachVoExample = new AttachVoExample();
        attachVoExample.setOrderByClause("id desc");
        List<AttachVo> attachVos = attachDao.selectByExample(attachVoExample);
        return new PageInfo<>(attachVos);
    }

    @Override
    public AttachVo selectById(Integer id) {
        if(null != id){
            return attachDao.selectByPrimaryKey(id);
        }
        return null;
    }

    @Override
    public void save(String fname, String fkey, String ftype, Integer author) {
        AttachVo attach = new AttachVo();
        attach.setFname(fname);
        attach.setAuthorId(author);
        attach.setFkey(fkey);
        attach.setFtype(ftype);
        attach.setCreated(DateKit.getCurrentUnixTime());
        attachDao.insertSelective(attach);
    }

    @Override
    public void deleteById(Integer id) {
        if (null != id) {
            attachDao.deleteByPrimaryKey( id);
        }
    }
}
