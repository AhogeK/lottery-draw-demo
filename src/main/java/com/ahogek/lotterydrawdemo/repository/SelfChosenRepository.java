package com.ahogek.lotterydrawdemo.repository;

import com.ahogek.lotterydrawdemo.entity.SelfChosen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2024-10-06 15:11:22
 */
@Repository
public interface SelfChosenRepository extends JpaRepository<SelfChosen, Long> {
    Optional<SelfChosen> findTopByOrderByDrawTimeDesc();

    List<SelfChosen> findAllByPrizeIsNull();

    @Query("SELECT s.number FROM SelfChosen s WHERE s.drawTime = :drawTime ORDER BY s.numberType")
    List<Integer> findNumbersByDrawTimeOrderByNumberType(LocalDate drawTime);

    List<SelfChosen> findAllByPrizeNot(int prize);
}
