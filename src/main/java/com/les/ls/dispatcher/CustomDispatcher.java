package com.les.ls.dispatcher;

import com.les.ls.handler.CustomHandler;
import com.les.ls.handler.ParamRule;
import com.les.ls.utils.annotation.CustomAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现一个自定义扫描分发器，自定义注解可携带分发参数
 */
@Slf4j
@Component
public class CustomDispatcher<P extends ParamRule, R> {

    @Resource
    private ApplicationContext applicationContext;

    private final Map<String, CustomHandler<P, R>> handlers = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("Init CustomHandler start...");
        initCustomHandler();
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroy CustomHandler start...");
        handlers.clear();
    }

    public R dispatch(P object) {
        CustomHandler<P, R> handler = handlers.get(getKey(object.getKey(), object.getValue()));
        if (handler == null) {
            log.error("No such Handler !");
            return null;
        }
        return handler.test(object);
    }

    private void initCustomHandler() {
        try {
            handlers.clear();
            log.info("--------------------   Init CustomHandler.handler   --------------------");
            //从容器中获得加了该注解的bean
            for (String beanName : applicationContext.getBeanNamesForAnnotation(CustomAnnotation.class)) {
                @SuppressWarnings("unchecked")
                CustomHandler<P, R> customHandler = (CustomHandler<P, R>) applicationContext.getBean(beanName);
                CustomAnnotation customAnnotation = customHandler.getClass().getAnnotation(CustomAnnotation.class);
                String key = getKey(customAnnotation.name(), customAnnotation.test().name());
                //注册handler
                handlers.put(key, customHandler);
            }
            log.info("--------------------   Init  CustomHandler.handler complete !   --------------------");
        } catch (Exception e) {
            log.error("Init CustomHandler.handler error", e);
        }
    }

    private String getKey(String name, String test) {
        return String.format("%s_%s", name, test);
    }
}
