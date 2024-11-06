package ru.stepup.repository;

import ru.stepup.dto.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimitDao extends JpaRepository<Limit, Long> {
    Limit findTopByUserIdOrderByCreatedAtDesc(Long userId);

    @Override
    <S extends Limit> S save(S entity);

    void deleteByUserId(Long userId);
}