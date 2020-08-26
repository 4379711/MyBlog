package com.my.blog.website.service.impl;

import com.my.blog.website.dao.OptionVoMapper;
import com.my.blog.website.model.Vo.OptionVo;
import com.my.blog.website.model.Vo.OptionVoExample;
import com.my.blog.website.service.IOptionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author liuyalong
 */
@Service
public class OptionServiceImpl implements IOptionService {


    @Resource
    private OptionVoMapper optionDao;

    @Override
    public void insertOption(OptionVo optionVo) {
        optionDao.insertSelective(optionVo);
    }

    @Override
    public void insertOption(String name, String value) {
        OptionVo optionVo = new OptionVo();
        optionVo.setName(name);
        optionVo.setValue(value);
        if (optionDao.selectByExample(new OptionVoExample()).size() == 0) {
            optionDao.insertSelective(optionVo);
        } else {
            optionDao.updateByPrimaryKeySelective(optionVo);
        }
    }

    @Override
    public void saveOptions(Map<String, String> options) {
        if (null != options && !options.isEmpty()) {
            options.forEach(this::insertOption);
        }
    }

    @Override
    public List<OptionVo> getOptions() {
        return optionDao.selectByExample(new OptionVoExample());
    }
}
