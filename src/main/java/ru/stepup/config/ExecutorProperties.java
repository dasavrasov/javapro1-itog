package ru.stepup.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "executor")
public class ExecutorProperties {
    private BigDecimal initialUserLimit;
    private long resetInterval;

    public BigDecimal getInitialUserLimit() {
        return initialUserLimit;
    }

    public void setInitialUserLimit(BigDecimal initialUserLimit) {
        this.initialUserLimit = initialUserLimit;
    }

    public long getResetInterval() {
        return resetInterval;
    }

    public void setResetInterval(long resetInterval) {
        this.resetInterval = resetInterval;
    }
}