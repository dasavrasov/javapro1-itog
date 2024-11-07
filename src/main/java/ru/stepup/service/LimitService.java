package ru.stepup.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import ru.stepup.config.ExecutorProperties;
import ru.stepup.dto.Limit;
import org.springframework.stereotype.Service;
import ru.stepup.repository.LimitRepository;

import java.math.BigDecimal;

@Service
public class LimitService {

    private static final Logger log = LoggerFactory.getLogger(LimitService.class);

    private final LimitRepository limitRepository;
    private final ExecutorProperties executorProperties;

    public LimitService(LimitRepository limitRepository, ExecutorProperties executorProperties) {

        this.limitRepository = limitRepository;
        this.executorProperties = executorProperties;
    }

    public Limit getLimitByUserId(Long userId) {
        return limitRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseGet(() -> {
                    Limit newLimit = new Limit(userId, executorProperties.getInitialUserLimit());
                    return limitRepository.save(newLimit);
                });
    }

    public Limit reduceUserLimit(Long userId, BigDecimal summa) {
        Limit limit = getLimitByUserId(userId);
        BigDecimal newValue = limit.getValue().subtract(summa);
        Limit newLimit = new Limit(userId, newValue);
        if (newLimit.getValue().intValue()<0)
            throw new IllegalArgumentException("Сумма превышает лимит");
        return limitRepository.save(newLimit);
    }

    public Limit restoreUserLimit(Long userId) {
        Limit limit = getLimitByUserId(userId);
        limitRepository.delete(limit);
        return getLimitByUserId(userId);
    }

    @Scheduled(fixedRateString = "#{executorProperties.resetInterval}")
    @Transactional
    public void resetAllLimits() {
        log.info("Resetting all limits");
        limitRepository.updateAllLimits(executorProperties.getInitialUserLimit());
    }
}