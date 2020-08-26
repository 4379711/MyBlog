package com.my.blog.website.service.impl;

import com.github.pagehelper.PageHelper;
import com.my.blog.website.dao.LogVoMapper;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.utils.DateKit;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.model.Vo.LogVo;
import com.my.blog.website.model.Vo.LogVoExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liuyalong
 */
@Service
public class LogServiceImpl implements ILogService {

    @Resource
    private LogVoMapper logDao;

    @Override
    public void insertLog(LogVo logVo) {
        logDao.insert(logVo);
    }

    @Override
    public void insertLog(String action, String data, String ip, Integer authorId) {
        LogVo logs = new LogVo();
        logs.setAction(action);
        logs.setData(data);
        logs.setIp(ip);
        logs.setAuthorId(authorId);
        logs.setCreated(DateKit.getCurrentUnixTime());
        logDao.insert(logs);
    }

    @Override
    public List<LogVo> getLogs(int page, int limit) {
        if (page <= 0) {
            page = 1;
        }
        if (limit < 1 || limit > WebConst.MAX_POSTS) {
            limit = 10;
        }
        LogVoExample logVoExample = new LogVoExample();
        logVoExample.setOrderByClause("id desc");
        PageHelper.startPage((page - 1) * limit, limit);
        return logDao.selectByExample(logVoExample);
    }
}
