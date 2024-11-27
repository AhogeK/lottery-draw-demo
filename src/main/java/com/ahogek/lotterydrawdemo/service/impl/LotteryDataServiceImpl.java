package com.ahogek.lotterydrawdemo.service.impl;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.entity.PrizeCheckResult;
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

    @Override
    public PrizeCheckResult checkAllPrizes(List<String> numbers) {
        if (numbers == null || numbers.size() != 7) {
            throw new IllegalArgumentException("必须输入7个号码");
        }

        // 分割蓝球和黄球
        List<String> blueNumbers = numbers.subList(0, 5);
        List<String> yellowNumbers = numbers.subList(5, 7);

        // 验证号码格式
        validateNumbers(blueNumbers, yellowNumbers);

        return PrizeCheckResult.builder()
                .firstPrize(lotteryDataRepository.findFirstPrizeCount(blueNumbers, yellowNumbers))
                .secondPrize(lotteryDataRepository.findSecondPrizeCount(blueNumbers, yellowNumbers))
                .thirdPrize(lotteryDataRepository.findThirdPrizeCount(blueNumbers))
                .fourthPrize(lotteryDataRepository.findFourthPrizeCount(blueNumbers, yellowNumbers))
                .fifthPrize(lotteryDataRepository.findFifthPrizeCount(blueNumbers, yellowNumbers))
                .sixthPrize(lotteryDataRepository.findSixthPrizeCount(blueNumbers, yellowNumbers))
                .seventhPrize(lotteryDataRepository.findSeventhPrizeCount(blueNumbers))
                .eighthPrize(lotteryDataRepository.findEighthPrizeCount(blueNumbers, yellowNumbers))
                .ninthPrize(lotteryDataRepository.findNinthPrizeCount(blueNumbers, yellowNumbers))
                .build();
    }

    private void validateNumbers(List<String> blueNumbers, List<String> yellowNumbers) {
        // 验证蓝球号码
        for (String blue : blueNumbers) {
            if (!blue.matches("\\d{2}")) {
                throw new IllegalArgumentException("蓝球号码格式错误: " + blue);
            }
            int num = Integer.parseInt(blue);
            if (num < 1 || num > 35) {
                throw new IllegalArgumentException("蓝球号码范围必须在1-35之间: " + blue);
            }
        }

        // 验证黄球号码
        for (String yellow : yellowNumbers) {
            if (!yellow.matches("\\d{2}")) {
                throw new IllegalArgumentException("黄球号码格式错误: " + yellow);
            }
            int num = Integer.parseInt(yellow);
            if (num < 1 || num > 12) {
                throw new IllegalArgumentException("黄球号码范围必须在1-12之间: " + yellow);
            }
        }
    }
}
