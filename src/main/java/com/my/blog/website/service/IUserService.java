package com.my.blog.website.service;

import com.my.blog.website.model.Vo.UserVo;

/**
 * @author liuyalong
 */
public interface IUserService {

    /**
     * 保存用户数据
     *
     * @param userVo 用户数据
     * @return 主键
     */

    Integer insertUser(UserVo userVo);

    /**
     * 通过uid查找对象
     */
    UserVo queryUserById(Integer uid);

    /**
     * 用戶登录
     */
    UserVo login(String username, String password);

    /**
     * 根据主键更新user对象
     */
    void updateByUid(UserVo userVo);
}
