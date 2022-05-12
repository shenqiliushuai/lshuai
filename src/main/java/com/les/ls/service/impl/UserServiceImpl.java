package com.les.ls.service.impl;

import com.les.ls.dao.OrganizationMapper;
import com.les.ls.pojo.Organization;
import com.les.ls.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private OrganizationMapper organizationMapper;

    @Override
    public void login() {
        //Transactional失效
        test();
    }


    @Transactional
    public void test() {
        Organization organization = new Organization();
        organization.setOrgCode("demo");
        organization.setOrgName("demo");
        organization.setIsVirtual("1");
        organization.setPid(null);
        organizationMapper.insertSelective(organization);
        organization.setOrgCode("demo");
        organization.setOrgName("demo");
        organization.setIsVirtual("1");
        organization.setPid(null);
        organizationMapper.insertSelective(organization);
    }
}
