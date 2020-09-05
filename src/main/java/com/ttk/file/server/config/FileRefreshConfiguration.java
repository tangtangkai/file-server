package com.ttk.file.server.config;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class FileRefreshConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private RefreshScope scope;

    @ApolloConfigChangeListener(value = "application", interestedKeyPrefixes = {
            "application.file."
    })
    private void onChange(ConfigChangeEvent event) {
        Set<String> keys = event.changedKeys();
        for (String key : keys) {
            ConfigChange change = event.getChange(key);
            log.info("配置[{}]已更新，[oldValue]={};[newValue]={}", key, change.getOldValue(), change.getNewValue());
        }
        applicationContext.publishEvent(
                new EnvironmentChangeEvent(keys)
        );
        scope.refreshAll();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
