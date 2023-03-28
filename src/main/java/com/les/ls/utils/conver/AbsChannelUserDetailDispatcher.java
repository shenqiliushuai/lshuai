package com.les.ls.utils.conver;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * @author ruoxiao.li
 * @date 2022/10/17
 * @describe 渠道用户详情分发路由
 */
@Component
public class AbsChannelUserDetailDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(AbsChannelUserDetailDispatcher.class);

    private final Map<Integer, AbsChannelUserDetailService> handlerMap = Maps.newHashMap();
    private final Map<Integer, Class> classMap = Maps.newHashMap();

    @Autowired
    private ApplicationContext applicationContext;

    public AbsChannelUserDetailService getHandler(Integer channelId) {
        return handlerMap.get(channelId);
    }

    public Class getUserDetailClass(Integer channelId) {
        return classMap.get(channelId);
    }

    @PostConstruct
    public void initChannelLoginHandler() {
        try {
            logger.info("================    Init Channel UserDetail handlers    ==================");
            handlerMap.clear();

            String[] beanNames = applicationContext.getBeanNamesForAnnotation(AbsChannelUserDetail.class);
            if (beanNames.length == 0) {
                logger.warn("================Init Channel UserDetail handler finished, no handler found==================");
                return;
            }
            for (String beanName : beanNames) {
                Object tmpHandler = applicationContext.getBean(beanName);
                if (!AbsChannelUserDetailService.class.isAssignableFrom(tmpHandler.getClass())) {
                    continue;
                }

                AbsChannelUserDetailService handler = (AbsChannelUserDetailService) tmpHandler;
                AbsChannelUserDetail annotation = handler.getClass().getAnnotation(AbsChannelUserDetail.class);
                int channelId = annotation.channelId();
                Class<?> aClass = annotation.userClass();
                logger.info("Mapping Channel UserDetail handler [ModuleId={},  UserClass={}] ==> {}", channelId, aClass.getName(), handler.getClass());
                this.handlerMap.put(channelId, handler);
                this.classMap.put(channelId, aClass);
            }
            logger.info("================Init Channel UserDetail handler finished==================");
        } catch (Exception ex) {
            logger.info("Init Channel UserDetail handlers error:" + ex.getMessage(), ex);
        }
    }

    @PreDestroy
    public void cleanup() {
        handlerMap.clear();
        classMap.clear();
        logger.info("Channel UserDetail handler dispatcher is destroyed");
    }
}
