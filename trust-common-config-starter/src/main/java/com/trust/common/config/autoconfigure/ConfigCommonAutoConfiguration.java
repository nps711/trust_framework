package com.trust.common.config.autoconfigure;

import com.trust.common.config.properties.TrustQuantProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(TrustQuantProperties.class)
public class ConfigCommonAutoConfiguration {
}
