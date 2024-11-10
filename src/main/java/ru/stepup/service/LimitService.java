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
        //Нужно восстановить именно предыдущий лимит
        //Получить последний(тнекущий) лимит для userId
        Limit limit = getLimitByUserId(userId);
        //если лимит уже 10000, то восстанавливать не надо, вернем его
        if (limit.getValue().compareTo(executorProperties.getInitialUserLimit())==0)
            return new Limit(userId,executorProperties.getInitialUserLimit());
        //удалить эту последню ззапись по лимиту, если она не равна 10,000
        limitRepository.delete(limit);
        //а теперь надо вытащить из базы текущий лимит, где же еще его взять?
        return getLimitByUserId(userId);
    }

    @Scheduled(fixedRateString = "#{executorProperties.resetInterval}")
    @Transactional
    public void resetAllLimits() {
        log.debug("Resetting all limits");
        limitRepository.updateAllLimits(executorProperties.getInitialUserLimit());
    }
}