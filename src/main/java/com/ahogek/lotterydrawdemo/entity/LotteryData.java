package com.ahogek.lotterydrawdemo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2023-10-26 14:19:55
 */
@Entity
public class LotteryData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate lotteryDrawTime;

    private String lotteryDrawNumber;

    private Integer lotteryDrawNumberType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getLotteryDrawTime() {
        return lotteryDrawTime;
    }

    public void setLotteryDrawTime(LocalDate lotteryDrawTime) {
        this.lotteryDrawTime = lotteryDrawTime;
    }

    public String getLotteryDrawNumber() {
        return lotteryDrawNumber;
    }

    public void setLotteryDrawNumber(String lotteryDrawNumber) {
        this.lotteryDrawNumber = lotteryDrawNumber;
    }

    public Integer getLotteryDrawNumberType() {
        return lotteryDrawNumberType;
    }

    public void setLotteryDrawNumberType(Integer lotteryDrawNumberType) {
        this.lotteryDrawNumberType = lotteryDrawNumberType;
    }
}
