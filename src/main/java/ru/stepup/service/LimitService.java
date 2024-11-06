package ru.stepup.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import ru.stepup.config.ExecutorProperties;
import ru.stepup.dto.Limit;
import org.springframework.stereotype.Service;
import ru.stepup.repository.LimitDao;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LimitService {

    private static final Logger log = LoggerFactory.getLogger(LimitService.class);

    private LimitDao limitDao;
    private ExecutorProperties executorProperties;

    public LimitService(LimitDao limitDao, ExecutorProperties executorProperties) {

        this.limitDao = limitDao;
        this.executorProperties = executorProperties;
    }

    public Limit getLimitByUserId(Long userId) {
        Limit lim=limitDao.findTopByUserIdOrderByCreatedAtDesc(userId);
        if (lim == null) {
            lim = new Limit(userId, executorProperties.getInitialUserLimit());
            limitDao.save(lim);
        }
        return lim;
    }

    public Limit reduceUserLimit(Long userId, BigDecimal summa) {
        Limit limit = getLimitByUserId(userId);
        BigDecimal newValue = (limit == null) ? executorProperties.getInitialUserLimit().subtract(summa) : limit.getValue().subtract(summa);
        Limit newLimit = new Limit(userId, newValue);
        if (newLimit.getValue().intValue()<0)
            throw new IllegalArgumentException("Сумма превышает лимит");
        return limitDao.save(newLimit);
    }

    public Limit restoreUserLimit(Long userId) {
        Limit limit = getLimitByUserId(userId);
        if (limit != null) {
            limitDao.delete(limit);
            return getLimitByUserId(userId);
        } else {
            limit = new Limit(userId, executorProperties.getInitialUserLimit());
            return limitDao.save(limit);
        }
    }

    @Scheduled(fixedRateString = "#{executorProperties.resetInterval}")
    @Transactional
    public void resetAllLimits() {
        log.info("Resetting all limits");
        List<Limit> allLimits = limitDao.findAll();
        Set<Long> userIds = new HashSet<>();
        for (Limit limit : allLimits) {
            userIds.add(limit.getUserId());
            limitDao.deleteByUserId(limit.getUserId());
        }
        for (Long userId : userIds) {
            Limit newLimit = new Limit();
            newLimit.setUserId(userId);
            newLimit.setValue(executorProperties.getInitialUserLimit());
            limitDao.save(newLimit);
        }
    }
}