package com.les.ls.service.impl;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.UUID;
import com.les.ls.dao.UserMapper;
import com.les.ls.pojo.User;
import com.les.ls.pojo.dto.WebResultEnum;
import com.les.ls.pojo.vo.BaseWebResultVO;
import com.les.ls.pojo.vo.LoginVO;
import com.les.ls.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public BaseWebResultVO login(LoginVO loginVO) {
        User queryUser = Convert.convert(User.class, loginVO);
        User user = userMapper.queryUser(queryUser);
        if (user != null) {
            return new BaseWebResultVO(WebResultEnum.SUCCESS, UUID.randomUUID().toString());
        }
        return new BaseWebResultVO(WebResultEnum.FAILED, "user not exist !");
    }
}
