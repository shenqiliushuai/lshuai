package com.les.ls.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
@ConfigurationProperties(prefix = "custom")
public class ConstantsConfig {

    public String windowsRestartTaskName;

    public String windowsRestartScriptName;

    public String windowsSystemPassword;

    public String windowsCheckCmdForService;

    public String windowsRestartCmdForService;

    public String linuxRestartScriptName;
}
