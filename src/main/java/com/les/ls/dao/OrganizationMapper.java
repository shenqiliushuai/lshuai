package com.les.ls.dao;

import com.les.ls.pojo.Organization;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrganizationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Organization row);

    int insertSelective(Organization row);

    Organization selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Organization row);

    int updateByPrimaryKey(Organization row);
}