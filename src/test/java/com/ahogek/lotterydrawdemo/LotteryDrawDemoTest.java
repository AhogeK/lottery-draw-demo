package com.ahogek.lotterydrawdemo;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.entity.PrizeCheckResult;
import com.ahogek.lotterydrawdemo.entity.SelfChosen;
import com.ahogek.lotterydrawdemo.entity.SelfChosenWinning;
import com.ahogek.lotterydrawdemo.repository.LotteryDataRepository;
import com.ahogek.lotterydrawdemo.repository.SelfChosenRepository;
import com.ahogek.lotterydrawdemo.repository.SelfChosenWinningRepository;
import com.ahogek.lotterydrawdemo.service.LotteryDataService;
import com.ahogek.lotterydrawdemo.util.ProgressBarWithTime;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.ahogek.lotterydrawdemo.util.DrawUtil.*;

/**
 * 用于测试是否可以抽到一等奖
 *
 * @author AhogeK ahogek@gmail.com
 * @since 2024-04-19 14:53:12
 */
@SpringBootTest
class LotteryDrawDemoTest {

    private static final Logger LOG = LoggerFactory.getLogger(LotteryDrawDemoTest.class);

    @Autowired
    LotteryDataService service;

    @Autowired
    SelfChosenRepository selfChosenRepository;

    @Autowired
    LotteryDataRepository lotteryDataRepository;

    @Autowired
    SelfChosenWinningRepository selfChosenWinningRepository;

    @Autowired
    LotteryDrawDemoApplication application;

    @Autowired
    LotteryRequestManager manager;

    List<LotteryData> all = new ArrayList<>();
    List<String> result = new ArrayList<>();
    List<String> sortBeforeResult = new ArrayList<>();
    Map<String, Integer> sortBeforeFrontResultMap = new HashMap<>();
    Map<String, Integer> sortBeforeBackResultMap = new HashMap<>();
    Set<String> front = new HashSet<>();
    Set<String> back = new HashSet<>();
    List<List<String>> allDataGroup = new ArrayList<>();

    @BeforeEach
    void beforeAll() {
        all = service.findAll();
        LotteryDrawDemoApplication.groupAllData(allDataGroup, all);
        // 不再将曾经的所有自己的摇奖数据作为随机摇奖的数据,仅对所有大奖数据进行随机摇奖
        // LotteryDrawDemoApplication.groupSelfChosenData(allDataGroup, selfChosenRepository.findAllByPrizeNot(0));
    }

    @Test
    void testDrawProbability() {
        List<String> firstPrize = List.of("09", "10", "11", "12", "29", "01", "10");

        int count = 0;
        for (int i = 0; i < 100; i++) {
            do {
                count++;
                result.clear();
                front.clear();
                back.clear();
                for (int j = 0; j < 7; j++) {
                    application.drawNumbers(j, allDataGroup, front, back);
                }
                front.stream().sorted().forEach(result::add);
                back.stream().sorted().forEach(result::add);
            } while (!firstPrize.equals(result));
        }

        Assertions.assertTrue(count > 0);
        int countA = count;
        System.out.println("不加自选中奖抽到一等奖100次需要的次数：" + countA);
        count = 0;
        LotteryDrawDemoApplication.groupSelfChosenData(allDataGroup, selfChosenRepository.findAllByPrizeNot(0));
        for (int i = 0; i < 100; i++) {
            do {
                count++;
                result.clear();
                front.clear();
                back.clear();
                for (int j = 0; j < 7; j++) {
                    application.drawNumbers(j, allDataGroup, front, back);
                }
                front.stream().sorted().forEach(result::add);
                back.stream().sorted().forEach(result::add);
            } while (!firstPrize.equals(result));
        }
        System.out.println("加自选中奖抽到一等奖100次需要的次数：" + count);
        if (countA < count) {
            System.out.println("不加自选更容易抽到一等奖");
        } else {
            System.out.println("加自选更容易抽到一等奖");
        }
    }

    @Test
    void testDrawFirstPrize() {
        List<String> firstPrize = List.of("03", "05", "12", "24", "28", "05", "11");

        PrizeCheckResult prizeCheckResult = service.checkAllPrizes(firstPrize);
        LOG.info("号码{}的{}", firstPrize, prizeCheckResult);

        long count = 0;
        do {
            count++;
            result.clear();
            front.clear();
            back.clear();
            for (int i = 0; i < 7; i++) {
                application.drawNumbers(i, allDataGroup, front, back);
            }
            front.stream().sorted().forEach(result::add);
            back.stream().sorted().forEach(result::add);
        } while (!firstPrize.equals(result));

        Assertions.assertEquals(firstPrize, result);
        System.out.println("抽到一等奖了！抽了" + count + "次");
    }

    @Test
    void testDraw() {
        // 判断如果不是周一周三周六则不进行抽奖
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        boolean isDrawDay = dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.WEDNESDAY || dayOfWeek == DayOfWeek.SATURDAY;
        if (!isDrawDay) {
            System.out.println("今天不是抽奖日！");
            return;
        }
        // 先判断今天有没有抽过，抽过的不进行操作直接结束
        AtomicBoolean alreadyDraw = new AtomicBoolean(false);
        selfChosenRepository.findTopByOrderByDrawTimeDesc().ifPresent(selfChosen -> {
            if (selfChosen.getDrawTime().isEqual(LocalDate.now())) {
                System.out.println("今天已经抽过了!");
                // 输出抽过的号码
                List<Integer> drawnNumbers = selfChosenRepository.findNumbersByDrawTimeOrderByNumberType(LocalDate.now());
                System.out.println(drawnNumbers);
                alreadyDraw.set(true);
            }
        });
        if (alreadyDraw.get()) {
            return;
        }
        long count = 0;
        long totalCount = ThreadLocalRandom.current().nextInt(520_999, 757_520_999 + 1);
        System.out.println("本次随机次数为：" + totalCount);
        long updateInterval = totalCount / 10000;
        long nextUpdate = updateInterval;

        ProgressBarWithTime progressBar = new ProgressBarWithTime(totalCount, 50);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
            do {
                count++;
                result.clear();
                sortBeforeResult.clear();
                front.clear();
                back.clear();
                for (int i = 0; i < 7; i++) {
                    application.drawNumbers(i, allDataGroup, front, back);
                }

                sortBeforeResult.addAll(front);
                sortBeforeResult.addAll(back);
                front.stream().sorted().forEach(result::add);
                back.stream().sorted().forEach(result::add);

                if (count >= nextUpdate) {
                    progressBar.updateProgressBar(writer, count);
                    nextUpdate += updateInterval;
                }
            } while (count != totalCount);

            writer.write("\n");
            writer.flush();

            Assertions.assertNotNull(result);
            // 对结果进行存储
            List<SelfChosen> insertList = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                if (i <= 5) {
                    sortBeforeFrontResultMap.put(sortBeforeResult.get(i - 1), i);
                } else {
                    sortBeforeBackResultMap.put(sortBeforeResult.get(i - 1), i);
                }
            }
            for (int i = 0; i < 7; i++) {
                SelfChosen selfChosen;
                if (i < 5) {
                    selfChosen = new SelfChosen(result.get(i), i, sortBeforeFrontResultMap.get(result.get(i)));
                } else {
                    selfChosen = new SelfChosen(result.get(i), i, sortBeforeBackResultMap.get(result.get(i)));
                }
                insertList.add(selfChosen);
            }
            selfChosenRepository.saveAll(insertList);

            System.out.println("随机摇奖号码为：" + result + "，祝你好运！");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void checkResponse() {
        Assertions.assertDoesNotThrow(() -> {
            JSONObject nextPage = manager.getNextPage(1);
            Assertions.assertNotNull(nextPage);
            LOG.info("{}", nextPage);
        });
    }

    @Test
    void syncSelfChosenPrizeData() {
        Map<LocalDate, List<LotteryData>> allLotteryDataByDate = service.findAll().stream()
                .collect(Collectors.groupingBy(LotteryData::getLotteryDrawTime));
        List<SelfChosen> allSelfChosen = selfChosenRepository.findAllByPrizeIsNull();
        if (allSelfChosen.isEmpty()) {
            LOG.warn("暂且没有自选号码数据");
            return;
        }
        Map<LocalDate, List<SelfChosen>> groupedByDate = allSelfChosen.stream()
                .collect(Collectors.groupingBy(SelfChosen::getDrawTime));

        List<SelfChosen> updateRecords = new ArrayList<>();

        // 处理每次的记录
        for (Map.Entry<LocalDate, List<SelfChosen>> entry : groupedByDate.entrySet()) {
            LocalDate drawTime = entry.getKey();
            List<SelfChosen> dayRecords = entry.getValue();

            List<String> numbers = dayRecords.stream().sorted(Comparator.comparingInt(SelfChosen::getNumberType))
                    .map(SelfChosen::getNumber).toList();
            List<LotteryData> drawDayNumbers = allLotteryDataByDate.get(drawTime);
            if (drawDayNumbers == null || drawDayNumbers.isEmpty()) {
                LOG.warn("{}没有开奖记录", drawTime);
                continue;
            }

            List<String> drawNumbers = drawDayNumbers.stream().sorted(Comparator.comparingInt(LotteryData::getSort))
                    .map(LotteryData::getLotteryDrawNumber).toList();
            // 检查中奖情况
            PrizeCheckResult prizeResult = checkPrizeForNumbers(drawNumbers, numbers);
            int prize = getHighestPrize(prizeResult);

            // 如果有中奖检查是否是历史首次中奖
            if (prize > 0) {
                // 检查是否是历史第一次
                boolean isHistoricalFirst = isHistoricalFirstWin(drawTime, numbers, prize, allLotteryDataByDate);

                for (SelfChosen self : dayRecords) {
                    self.setPrize(prize);
                    self.setHistoricalFirst(isHistoricalFirst);
                    updateRecords.add(self);
                }
            } else {
                for (SelfChosen self : dayRecords) {
                    self.setPrize(0);
                    self.setHistoricalFirst(false);
                    updateRecords.add(self);
                }
            }
        }

        selfChosenRepository.saveAll(updateRecords);

        // 同时同步 SelfChosenWinning
        List<SelfChosenWinning> insertList = new ArrayList<>();
        for (Map.Entry<LocalDate, List<SelfChosen>> entry : groupedByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<SelfChosen> records = entry.getValue();
            List<LotteryData> lotteryDataList = allLotteryDataByDate.get(date);
            if (lotteryDataList == null || lotteryDataList.isEmpty()) {
                LOG.warn("{}没有中奖，没有需要记录 self_chosen_winning 的数据", date);
                continue;
            }
            List<LotteryData> frontLotteryDataList = lotteryDataList.stream()
                    .filter(lotteryData -> lotteryData.getLotteryDrawNumberType() <= 4)
                    .toList();
            List<LotteryData> backLotteryDataList = lotteryDataList.stream()
                    .filter(lotteryData -> lotteryData.getLotteryDrawNumberType() > 4)
                    .toList();
            List<SelfChosen> winningSelfChosen = records.stream()
                    .filter(selfChosen -> {
                        if (selfChosen.getNumberType() <= 4) {
                            return frontLotteryDataList.stream()
                                    .anyMatch(lotteryData -> lotteryData.getLotteryDrawNumber().contains(selfChosen.getNumber()));
                        } else {
                            return backLotteryDataList.stream()
                                    .anyMatch(lotteryData -> lotteryData.getLotteryDrawNumber().contains(selfChosen.getNumber()));
                        }
                    })
                    .toList();

            insertList.addAll(winningSelfChosen.stream().map(selfChosen ->
                            new SelfChosenWinning(selfChosen.getId(), selfChosen.getDrawTime(), selfChosen.getNumber(),
                                    selfChosen.getNumberType(), selfChosen.getSort(), selfChosen.getPrize()))
                    .toList());
            LOG.info("{}中了{}个号", date, winningSelfChosen.size());
        }

        if (!insertList.isEmpty())
            selfChosenWinningRepository.saveAll(insertList);
    }

    @Test
    void syncSelfChosenWinningData() {
        Assertions.assertDoesNotThrow(() -> {
            if (!selfChosenWinningRepository.findAll().isEmpty()) {
                LOG.warn("已存在数据，请勿重复同步");
                return;
            }
            // 获取所有自选号码并分组
            Map<LocalDate, List<SelfChosen>> allSelfChosen = selfChosenRepository.findAll().stream()
                    .collect(Collectors.groupingBy(SelfChosen::getDrawTime));
            // 根据日期获取所有彩票数据
            Map<LocalDate, List<LotteryData>> allLotteryDataByDate = lotteryDataRepository
                    .findByLotteryDrawTimeIn(allSelfChosen.keySet()).stream()
                    .collect(Collectors.groupingBy(LotteryData::getLotteryDrawTime));
            if (allLotteryDataByDate.isEmpty()) {
                LOG.warn("没有彩票数据, 同步结束");
                return;
            }
            List<SelfChosenWinning> insertList = new ArrayList<>();
            allSelfChosen.forEach((date, selfChosenList) -> {
                List<LotteryData> lotteryDataList = allLotteryDataByDate.get(date);
                // 将lotteryDataList分为前后区匹配，numberType 0-4 匹配，5-9 匹配,只要number包含就为winningSelfChosen
                List<LotteryData> frontLotteryDataList = lotteryDataList.stream()
                        .filter(lotteryData -> lotteryData.getLotteryDrawNumberType() <= 4)
                        .toList();
                List<LotteryData> backLotteryDataList = lotteryDataList.stream()
                        .filter(lotteryData -> lotteryData.getLotteryDrawNumberType() > 4)
                        .toList();
                List<SelfChosen> winningSelfChosen = selfChosenList.stream()
                        .filter(selfChosen -> {
                            if (selfChosen.getNumberType() <= 4) {
                                return frontLotteryDataList.stream()
                                        .anyMatch(lotteryData -> lotteryData.getLotteryDrawNumber().contains(selfChosen.getNumber()));
                            } else {
                                return backLotteryDataList.stream()
                                        .anyMatch(lotteryData -> lotteryData.getLotteryDrawNumber().contains(selfChosen.getNumber()));
                            }
                        })
                        .toList();
                // 将匹配的SelfChosen转换为SelfChosenWinning
                insertList.addAll(winningSelfChosen.stream()
                        .map(selfChosen -> new SelfChosenWinning(selfChosen.getId(), selfChosen.getDrawTime(),
                                selfChosen.getNumber(), selfChosen.getNumberType(), selfChosen.getSort(), selfChosen.getPrize()))
                        .toList());
            });
            // 存储到数据库
            selfChosenWinningRepository.saveAll(insertList);
        });
    }
}
