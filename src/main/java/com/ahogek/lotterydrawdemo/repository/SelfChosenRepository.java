package com.ahogek.lotterydrawdemo.repository;

import com.ahogek.lotterydrawdemo.entity.SelfChosen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2024-10-06 15:11:22
 */
@Repository
public interface SelfChosenRepository extends JpaRepository<SelfChosen, Long> {
    Optional<SelfChosen> findTopByOrderByDrawTimeDesc();
}
