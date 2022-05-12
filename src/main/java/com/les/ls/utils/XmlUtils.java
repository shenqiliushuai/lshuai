package com.les.ls.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class XmlUtils {

    public static void main(String[] args) {
        String xml = "<Result>"
                + "<StatusCode>SUCCEED</StatusCode>"
                + "<StatusText>ExecutionSucceed</StatusText>"
                + "<Responses>"
                + "<Responsesaa>sssssss</Responsesaa>"
                + "  <response>"
                + "    <RECORD_COUNT>3</RECORD_COUNT>"
                + "    <PB_FLAG>2536||1015</PB_FLAG>"
                + "    <SCHEDULE_DATE>2018-01-18</SCHEDULE_DATE>"
                + "    <WEEK_DAY>4</WEEK_DAY>"
                + "    <SHIFT_ID>04</SHIFT_ID>"
                + "    <SHIFT_NAME>全天</SHIFT_NAME>"
                + "    <START_TIME>00:01</START_TIME>"
                + "    <END_TIME>23:59</END_TIME>"
                + "    <DEPT_CODE>3</DEPT_CODE>"
                + "    <DEPT_NAME>呼吸三科(北区)</DEPT_NAME>"
                + "     <DOC_CODE>2213</DOC_CODE>"
                + "     <DOC_NAME>呼吸三科方便号</DOC_NAME>"
                + "    <DOC_TITILE_CODE/>"
                + "     <DOC_TITLE_NAME>方便门诊</DOC_TITLE_NAME>"
                + "    <VISIT_LEVEL_CODE>55</VISIT_LEVEL_CODE>"
                + "    <VISIT_LEVEL_NAME>方便门诊</VISIT_LEVEL_NAME>"
                + "    <SUM_FEE>1</SUM_FEE>"
                + "    <REG_FEE>0</REG_FEE>"
                + "    <CHECKUP_FEE>1</CHECKUP_FEE>"
                + "    <OTHER_FEE>0</OTHER_FEE>"
                + "     <VISIT_STATUS>1</VISIT_STATUS>"
                + "     <COUNT>999</COUNT>"
                + "     <REMAIN_COUNT>993</REMAIN_COUNT>"
                + "    <TIME_FLAG>0</TIME_FLAG>"
                + "  </response>"
                + "  <response>"
                + "    <RECORD_COUNT>3</RECORD_COUNT>"
                + "    <PB_FLAG>2537||1014</PB_FLAG>"
                + "    <SCHEDULE_DATE>2018-01-18</SCHEDULE_DATE>"
                + "    <WEEK_DAY>4</WEEK_DAY>"
                + "    <SHIFT_ID>04</SHIFT_ID>"
                + "    <SHIFT_NAME>全天</SHIFT_NAME>"
                + "     <START_TIME>00:01</START_TIME>"
                + "    <END_TIME>23:59</END_TIME>"
                + "    <DEPT_CODE>3</DEPT_CODE>"
                + "   <DEPT_NAME>呼吸三科(北区)</DEPT_NAME>"
                + "   <DOC_CODE>2214</DOC_CODE>"
                + "   <DOC_NAME>呼吸三科老年号</DOC_NAME>"
                + "   <DOC_TITILE_CODE/>"
                + "    <DOC_TITLE_NAME>老年门诊</DOC_TITLE_NAME>"
                + "    <VISIT_LEVEL_CODE>56</VISIT_LEVEL_CODE>"
                + "    <VISIT_LEVEL_NAME>老年门诊</VISIT_LEVEL_NAME>"
                + "    <SUM_FEE>5</SUM_FEE>"
                + "    <REG_FEE>0</REG_FEE>"
                + "   <CHECKUP_FEE>5</CHECKUP_FEE>"
                + "    <OTHER_FEE>0</OTHER_FEE>"
                + "    <VISIT_STATUS>1</VISIT_STATUS>"
                + "    <COUNT>999</COUNT>"
                + "    <REMAIN_COUNT>999</REMAIN_COUNT>"
                + "    <TIME_FLAG>0</TIME_FLAG>"
                + "  </response>"
                + "  <response>"
                + "    <RECORD_COUNT>3</RECORD_COUNT>"
                + "    <PB_FLAG>2538||1014</PB_FLAG>"
                + "    <SCHEDULE_DATE>2018-01-18</SCHEDULE_DATE>"
                + "    <WEEK_DAY>4</WEEK_DAY>"
                + "    <SHIFT_ID>04</SHIFT_ID>"
                + "     <SHIFT_NAME>全天</SHIFT_NAME>"
                + "    <START_TIME>00:01</START_TIME>"
                + "    <END_TIME>23:59</END_TIME>"
                + "    <DEPT_CODE>3</DEPT_CODE>"
                + "    <DEPT_NAME>呼吸三科(北区)</DEPT_NAME>"
                + "     <DOC_CODE>2215</DOC_CODE>"
                + "     <DOC_NAME>呼吸三科急诊号</DOC_NAME>"
                + "     <DOC_TITILE_CODE/>"
                + "    <DOC_TITLE_NAME>急诊</DOC_TITLE_NAME>"
                + "    <VISIT_LEVEL_CODE>57</VISIT_LEVEL_CODE>"
                + "    <VISIT_LEVEL_NAME>急诊</VISIT_LEVEL_NAME>"
                + "    <SUM_FEE>10</SUM_FEE>"
                + "    <REG_FEE>0</REG_FEE>"
                + "    <CHECKUP_FEE>10</CHECKUP_FEE>"
                + "    <OTHER_FEE>0</OTHER_FEE>"
                + "    <VISIT_STATUS>1</VISIT_STATUS>"
                + "    <COUNT>999</COUNT>"
                + "    <REMAIN_COUNT>997</REMAIN_COUNT>"
                + "    <TIME_FLAG>0</TIME_FLAG>"
                + "  </response>"
                + "</Responses>"
                + "</Result>";


        String xml2 = "<a><response>"
                + "<SUM_FEE>1.51</SUM_FEE>"
                + "<PRES_NO/>"
                + "<PRES_TYPE>1</PRES_TYPE>"
                + "<PRES_NAME>西药</PRES_NAME>"
                + "<PRES_FEE>1.51</PRES_FEE>"
                + "<ITEM_NAME>(GJ)蒙托石散</ITEM_NAME>"
                + "<ITEM_SIZES>3g/包</ITEM_SIZES>"
                + "<ITEM_NUM>2</ITEM_NUM>"
                + "<ITEM_UNIT>包</ITEM_UNIT>"
                + "<ITEM_PRICE>0.76</ITEM_PRICE>"
                + "<ITEM_FEE>1.51</ITEM_FEE>"
                + "<PAY_DTIME>2018-01-18 09:40:49</PAY_DTIME>"
                + "</response>"
                + "<response>"
                + "<SUM_FEE>1.51</SUM_FEE>"
                + "<PRES_NO>1801001631</PRES_NO>"
                + "<PRES_TYPE>1</PRES_TYPE>"
                + "<PRES_NAME>西药</PRES_NAME>"
                + "<PRES_FEE>1.51</PRES_FEE>"
                + "<ITEM_NAME>(GJ)蒙托石散</ITEM_NAME>"
                + "<ITEM_SIZES>3g222222222/包</ITEM_SIZES>"
                + "<ITEM_NUM>2</ITEM_NUM>"
                + "<ITEM_UNIT>包</ITEM_UNIT>"
                + "<ITEM_PRICE>0.76</ITEM_PRICE>"
                + "<ITEM_FEE>1.51</ITEM_FEE>"
                + "<PAY_DTIME>2018-01-18 09:40:50</PAY_DTIME>"
                + "</response>"
                + "</a>";


        String xml3 = "<Response>"
                + "<ResultCode>0</ResultCode>"
                + " <RecordCount>1</RecordCount>"
                + " <Departments>"
                + "   <Department>"
                + "     <DepartmentCode>433</DepartmentCode>"
                + "      <DepartmentName>呼吸内一科门诊(南区)</DepartmentName>"
                + "      <ParentId>-1</ParentId>"
                + "      <DepartmentAddress>4楼</DepartmentAddress>"
                + "    </Department>"
                + "    <Department>"
                + "      <DepartmentCode>433ssssssssss</DepartmentCode>"
                + "      <DepartmentName>呼吸内一科门诊(南区)</DepartmentName>"
                + "      <ParentId>-1</ParentId>"
                + "      <DepartmentAddress>4楼</DepartmentAddress>"
                + "    </Department>"
                + "  </Departments>"
                + "</Response>";

        String xml4 =
                "   <Department>"
                        + "     <DepartmentCode>433</DepartmentCode>"
                        + "      <DepartmentName>呼吸内一科门诊(南区)</DepartmentName>"
                        + "      <ParentId>-1</ParentId>"
                        + "      <DepartmentAddress>4楼</DepartmentAddress>"
                        + "    </Department>"
                        + "    <Department>"
                        + "      <DepartmentCode>433ssssssssss</DepartmentCode>"
                        + "      <DepartmentName>呼吸内一科门诊(南区)</DepartmentName>"
                        + "      <ParentId>-1</ParentId>"
                        + "      <DepartmentAddress>4楼</DepartmentAddress>"
                        + "    </Department>";

        Map<String, Object> map1 = xml2map(xml4);
    }

    public static Map<String, Object> xml2map(String xml) {
        Map<String, Object> map = new HashMap<>();
        Document doc = null;
        if (StringUtils.isEmpty(xml))
            return map;
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            if (e.getMessage().contains("文档中根元素后面的标记必须格式正确。")) {
                xml = "<root>" + xml + "</root>";
                try {
                    doc = DocumentHelper.parseText(xml);
                } catch (DocumentException documentException) {
                    log.error("xml转换异常！msg->{}", e.getMessage());
                    return map;
                }
            }
        }
        if (doc == null) return map;
        Element rootElement = doc.getRootElement();
        element2map(rootElement, map);
        return map;
    }

    /**
     * @param elmt 当前元素
     * @param map  主键为当前元素的节点名,值为当前元素的所有直接子元素
     */
    private static void element2map(Element elmt, Map<String, Object> map) {
        if (null == elmt) {
            return;
        }
        String name = elmt.getName();
        if (elmt.isTextOnly()) {
            map.put(name, elmt.getText());
        } else {
            Map<String, Object> mapSub = new HashMap<>();
            List<Element> elements = elmt.elements();
            for (Element elmtSub : elements) {
                element2map(elmtSub, mapSub);
            }
            Object first = map.get(name);
            if (null == first) {
                map.put(name, mapSub);
            } else {
                if (first instanceof List<?>) {
                    ((List<Map<String, Object>>) first).add(mapSub);
                } else {
                    List<Object> listSub = new ArrayList<>();
                    listSub.add(first);
                    listSub.add(mapSub);
                    map.put(name, listSub);
                }
            }
        }
    }
}
