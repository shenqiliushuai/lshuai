package com.les.ls.service;

import com.les.ls.pojo.vo.BaseWebResultVO;
import com.les.ls.pojo.vo.LoginVO;

public interface UserService {

    BaseWebResultVO login(LoginVO loginVO);
}
