package ru.stepup.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stepup.dto.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {

    Optional<Limit> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    <S extends Limit> S save(S entity);

    void deleteByUserId(Long userId);

    @Modifying
    @Query("UPDATE Limit l SET l.value = :initialUserLimit")
    void updateAllLimits(BigDecimal initialUserLimit);
}