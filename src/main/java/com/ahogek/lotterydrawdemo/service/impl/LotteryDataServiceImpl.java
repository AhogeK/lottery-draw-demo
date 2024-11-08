package com.ahogek.lotterydrawdemo.service.impl;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.repository.LotteryDataRepository;
import com.ahogek.lotterydrawdemo.service.LotteryDataService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2023-10-26 18:00:05
 */
@Service
public class LotteryDataServiceImpl implements LotteryDataService {

    private final LotteryDataRepository lotteryDataRepository;
    private final EntityManager entityManager;

    public LotteryDataServiceImpl(LotteryDataRepository lotteryDataRepository, EntityManager entityManager) {
        this.lotteryDataRepository = lotteryDataRepository;
        this.entityManager = entityManager;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchInsert(List<LotteryData> data) {
        int batchSize = 1000; // 可配置
        int totalBatches = (data.size() + batchSize - 1) / batchSize;

        for (int batch = 0; batch < totalBatches; batch++) {
            int start = batch * batchSize;
            int end = Math.min(start + batchSize, data.size());

            StringBuilder sql = new StringBuilder("INSERT INTO lottery_data (lottery_draw_time, lottery_draw_number, lottery_draw_number_type, sort) VALUES ");

            sql.append("(?, ?, ?, ?),".repeat(Math.max(0, end - start)));
            if (end > start) {
                sql.setLength(sql.length() - 1); // 移除最后一个逗号
            }

            try {
                Query query = entityManager.createNativeQuery(sql.toString());

                for (int i = start; i < end; i++) {
                    LotteryData item = data.get(i);
                    int paramIndex = (i - start) * 4;
                    // 数据校验可以在这里进行
                    query.setParameter(paramIndex + 1, item.getLotteryDrawTime());
                    query.setParameter(paramIndex + 2, item.getLotteryDrawNumber());
                    query.setParameter(paramIndex + 3, item.getLotteryDrawNumberType());
                    query.setParameter(paramIndex + 4, item.getSort());
                }

                query.executeUpdate();
            } catch (Exception e) {
                // 如果需要，可以向异常中添加上下文信息。
                throw new ServiceException("Failed to batch insert lottery data", e);
            } finally {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }


    @Override
    public List<LotteryData> findAll() {
        return lotteryDataRepository.findAll();
    }

    @Override
    public int checkFirstPrize(List<String> firstPrize) {
        // 这里可以实现检查是否有历史一等奖的逻辑
        var matchingDrawDates = lotteryDataRepository.findMatchingDrawDates(firstPrize);

        return matchingDrawDates.size();
    }
}
