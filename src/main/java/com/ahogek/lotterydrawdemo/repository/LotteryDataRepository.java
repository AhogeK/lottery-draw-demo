package com.ahogek.lotterydrawdemo.repository;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2023-10-26 15:26:43
 */
@Repository
public interface LotteryDataRepository extends JpaRepository<LotteryData, Long> {
    long countByLotteryDrawTime(LocalDate lotteryDrawTime);

    LotteryData findTopByOrderByLotteryDrawTimeDesc();

    @Query("SELECT l.lotteryDrawTime FROM LotteryData l WHERE l.lotteryDrawNumber IN :numbers " +
            "GROUP BY l.lotteryDrawTime HAVING COUNT(DISTINCT l.lotteryDrawNumberType) = 7")
    List<LocalDate> findMatchingDrawDates(@Param("numbers") List<String> numbers);
}
