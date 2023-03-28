package com.les.ls.utils.conver;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Converter;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class AbsQQGameUserDetailReverseConverter implements Converter<AbsUserDetail> {

    private final String[] columnKeyArr = {"openId", "openKey", "pf", "pfKey", "packageId", "", "", "", "", ""};

    @Override
    @SuppressWarnings(value = "unchecked")
    public AbsUserDetail convert(Object o, AbsUserDetail absUserDetail) throws IllegalArgumentException {
        Map<String, Object> dataMap = (Map<String, Object>) o;
        try {
            absUserDetail = new AbsUserDetail();
            BeanUtil.fillBeanWithMap(dataMap, absUserDetail, false);
            String col0 = (String) dataMap.get(columnKeyArr[0]);
            absUserDetail.setColumn0(Strings.isNullOrEmpty(col0) ? "" : col0);
            String col1 = (String) dataMap.get(columnKeyArr[1]);
            absUserDetail.setColumn1(Strings.isNullOrEmpty(col1) ? "" : col1);
            String col2 = (String) dataMap.get(columnKeyArr[2]);
            absUserDetail.setColumn2(Strings.isNullOrEmpty(col2) ? "" : col2);
            String col3 = (String) dataMap.get(columnKeyArr[3]);
            absUserDetail.setColumn3(Strings.isNullOrEmpty(col3) ? "" : col3);
            String col4 = (String) dataMap.get(columnKeyArr[4]);
            absUserDetail.setColumn4(Strings.isNullOrEmpty(col4) ? "" : col4);

            absUserDetail.setColumn5("");
            absUserDetail.setColumn6("");
            absUserDetail.setColumn7("");
            absUserDetail.setColumn8("");
            absUserDetail.setColumn9("");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return absUserDetail;
    }
}
