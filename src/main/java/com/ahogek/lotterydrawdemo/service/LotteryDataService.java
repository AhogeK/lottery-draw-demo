package com.ahogek.lotterydrawdemo.service;

import com.ahogek.lotterydrawdemo.entity.LotteryData;

import java.util.List;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2023-10-26 17:59:47
 */
public interface LotteryDataService {

    void batchInsert(List<LotteryData> data);

    List<LotteryData> findAll();
}
