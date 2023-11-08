package com.ahogek.lotterydrawdemo.repository;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2023-10-26 15:26:43
 */
@Repository
public interface LotteryDataRepository extends JpaRepository<LotteryData, Long> {
    long countByLotteryDrawTime(LocalDate lotteryDrawTime);
}
