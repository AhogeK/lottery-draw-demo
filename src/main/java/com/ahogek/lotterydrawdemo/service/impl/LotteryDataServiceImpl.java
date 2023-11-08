package com.ahogek.lotterydrawdemo.service.impl;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.repository.LotteryDataRepository;
import com.ahogek.lotterydrawdemo.service.LotteryDataService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<LotteryData> data) {
        int batchSize = 1000; // 每批处理的数据量，可以根据需要调整
        int totalBatches = (data.size() + batchSize - 1) / batchSize; // 计算需要的批次数

        for (int batch = 0; batch < totalBatches; batch++) {
            int start = batch * batchSize;
            int end = Math.min(start + batchSize, data.size());

            StringBuilder sql = new StringBuilder("INSERT INTO lottery_data (lottery_draw_time, lottery_draw_number, lottery_draw_number_type) VALUES ");
            sql.append("(?, ?, ?),".repeat(end - start));
            sql.setLength(sql.length() - 1); // 移除最后一个逗号

            Query query = entityManager.createNativeQuery(sql.toString());

            for (int i = start; i < end; i++) {
                LotteryData item = data.get(i);
                int paramIndex = (i - start) * 3;
                query.setParameter(paramIndex + 1, item.getLotteryDrawTime());
                query.setParameter(paramIndex + 2, item.getLotteryDrawNumber());
                query.setParameter(paramIndex + 3, item.getLotteryDrawNumberType());
            }

            query.executeUpdate();
            entityManager.flush();
            entityManager.clear(); // 清除持久化上下文，释放内存
        }
    }


    @Override
    public List<LotteryData> findAll() {
        return lotteryDataRepository.findAll();
    }
}
