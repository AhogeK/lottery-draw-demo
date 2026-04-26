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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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

        // 将self_chosen_winning的数据加入allDataGroup中
        List<Long> ids = selfChosenWinningRepository.findAll().stream().map(SelfChosenWinning::getWinningId)
                .toList();
        LotteryDrawDemoApplication.groupSelfChosenData(allDataGroup, selfChosenRepository.findAllById(ids));
    }

    @Test
    void testDrawProbability() {
        List<String> firstPrize = List.of("04", "11", "24", "34", "35", "01", "02");

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
        // 将self_chosen_winning的数据加入allDataGroup中
        List<Long> ids = selfChosenWinningRepository.findAll().stream().map(SelfChosenWinning::getWinningId)
                .toList();
        LotteryDrawDemoApplication.groupSelfChosenData(allDataGroup, selfChosenRepository.findAllById(ids));
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
    void getDrawNumber() {
        List<String> firstPrize = List.of("04", "11", "24", "34", "35", "01", "02");

        long count = 0;
        for (int i = 0; i < 100; i++) {
            LOG.info("第{}次摇奖", i + 1);
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
        Assertions.assertEquals(firstPrize, result);

        LOG.info("平均次数：{}", count / 100);
    }

    @Test
    void testDrawFirstPrize() {
        List<String> firstPrize = List.of("04", "11", "24", "34", "35", "01", "02");

        firstPrizeInfo(firstPrize);
    }

    private void firstPrizeInfo(List<String> firstPrize) {
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
        if (!selfChosenRepository.findAllByPrizeIsNull().isEmpty()) {
            System.out.println("有未开奖的数据，请先执行syncSelfChosenPrizeData");
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

        // 获取最新一期数据作为目标
        Map<LocalDate, List<LotteryData>> historyMap = service.findAll().stream()
                .collect(Collectors.groupingBy(LotteryData::getLotteryDrawTime));
        LocalDate latestDate = historyMap.keySet().stream().max(LocalDate::compareTo).orElseThrow();
        List<LotteryData> latestPeriodData = historyMap.get(latestDate);

        LOG.info("最新一期日期: {}, 将作为摇奖目标", latestDate);

        // 构建不含最新一期的数据池
        List<LotteryData> dataWithoutLatest = historyMap.entrySet().stream()
                .filter(e -> !e.getKey().equals(latestDate))
                .flatMap(e -> e.getValue().stream())
                .toList();

        List<List<String>> dataGroupWithoutLatest = new ArrayList<>();
        LotteryDrawDemoApplication.groupAllData(dataGroupWithoutLatest, dataWithoutLatest);

        // 将self_chosen_winning的自选数据也加入不含最新一期的数据池（与beforeAll逻辑一致）
        List<Long> ids = selfChosenWinningRepository.findAll().stream().map(SelfChosenWinning::getWinningId)
                .toList();
        LotteryDrawDemoApplication.groupSelfChosenData(dataGroupWithoutLatest, selfChosenRepository.findAllById(ids));

        // 提取最新一期的目标号码
        Set<String> targetFront = latestPeriodData.stream()
                .filter(d -> d.getLotteryDrawNumberType() <= 4)
                .map(LotteryData::getLotteryDrawNumber)
                .collect(Collectors.toSet());
        Set<String> targetBack = latestPeriodData.stream()
                .filter(d -> d.getLotteryDrawNumberType() > 4)
                .map(LotteryData::getLotteryDrawNumber)
                .collect(Collectors.toSet());

        // 用不含最新一期的数据池，并行摇最新一期的目标号码，1000次取平均
        LOG.info("开始计算摇出最新一期所需的平均次数...");
        LOG.info("CPU核心数: {}", Runtime.getRuntime().availableProcessors());
        LOG.info("正在启动并行运算引擎...");

        int rounds = 1000;
        AtomicInteger finishedCounter = new AtomicInteger(0);
        long[] roundResults = new long[rounds];

        try (ForkJoinPool customThreadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors())) {
            customThreadPool.submit(() -> {
                java.util.stream.IntStream.range(0, rounds).parallel().forEach(r -> {
                    long count = 0;
                    Set<String> localFront = new HashSet<>(8);
                    Set<String> localBack = new HashSet<>(4);
                    ThreadLocalRandom random = ThreadLocalRandom.current();

                    while (true) {
                        count++;
                        localFront.clear();
                        localBack.clear();

                        for (int i = 0; i < 5; i++) {
                            List<String> pool = dataGroupWithoutLatest.get(i);
                            int size = pool.size();
                            while (localFront.size() <= i) {
                                localFront.add(pool.get(random.nextInt(size)));
                            }
                        }

                        if (!localFront.equals(targetFront)) {
                            continue;
                        }

                        for (int i = 5; i < 7; i++) {
                            List<String> pool = dataGroupWithoutLatest.get(i);
                            int size = pool.size();
                            while (localBack.size() <= (i - 5)) {
                                localBack.add(pool.get(random.nextInt(size)));
                            }
                        }

                        if (localBack.equals(targetBack)) {
                            break;
                        }
                    }

                    roundResults[r] = count;
                    int current = finishedCounter.incrementAndGet();
                    int remaining = rounds - current;
                    LOG.info("进度[{}/{}] 剩余:{} | 耗次:{}",
                            String.format("%4d", current),
                            rounds,
                            String.format("%4d", remaining),
                            String.format("%9d", count));
                });
            }).get();
        } catch (Exception e) {
            LOG.error("计算出错", e);
        }

        long avgCount = Arrays.stream(roundResults).sum() / rounds;
        LOG.info("摇出最新一期平均需要 {} 次（{}轮并行平均）", avgCount, rounds);

        // 使用全部数据（包括最新一期）进行正式摇奖
        System.out.println("本次随机次数为：" + avgCount);
        long updateInterval = avgCount / 10000;
        if (updateInterval == 0) updateInterval = 1;
        long nextUpdate = updateInterval;

        long count = 0;
        ProgressBarWithTime progressBar = new ProgressBarWithTime(avgCount, 50);

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
            } while (count < avgCount);

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

            firstPrizeInfo(result);
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

        List<SelfChosen> carryOverRecords = new ArrayList<>();
        LocalDate latestDate = groupedByDate.keySet().stream().max(LocalDate::compareTo).orElse(null);

        int prize = 0;
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
            prize = getHighestPrize(prizeResult);

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
                LOG.info("Result for {}: No prize.", drawTime);

                if (drawTime.equals(latestDate)) {
                    LocalDate nextDrawDate = getNextDrawDay(drawTime);

                    List<SelfChosen> nextRound = dayRecords.stream()
                            .map(old -> {
                                SelfChosen next = new SelfChosen(old.getNumber(), old.getNumberType(), old.getSort());
                                next.setDrawTime(nextDrawDate);
                                next.setPrize(null);
                                return next;
                            })
                            .toList();

                    carryOverRecords.addAll(nextRound);
                    LOG.info(">> Trigger carry-over: Moving numbers from {} to next draw date {}", drawTime, nextDrawDate);
                }
            }
        }

        selfChosenRepository.saveAll(updateRecords);

        if (!carryOverRecords.isEmpty()) {
            List<SelfChosen> uniqueCarryOver = carryOverRecords.stream()
                    .filter(recordSelfChosen -> !selfChosenRepository.existsByDrawTimeAndNumberAndNumberType(
                            recordSelfChosen.getDrawTime(), recordSelfChosen.getNumber(), recordSelfChosen.getNumberType())).toList();
            if (!uniqueCarryOver.isEmpty()) {
                selfChosenRepository.saveAll(uniqueCarryOver);
                LOG.info("Successfully carried over {} number to the next round.", uniqueCarryOver.size());
            } else {
                LOG.info("Carry-over records already exist. Skipping");
            }
        }

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
            if (prize == 0) {
                LOG.info("可惜没有中奖");
                // 核心延续下期逻辑已在上方主循环中通过 carryOverRecords 实现
            } else {
                LOG.info("恭喜你中了{}等奖", prize);
            }
        }

        if (!insertList.isEmpty())
            selfChosenWinningRepository.saveAll(insertList);
    }

    private LocalDate getNextDrawDay(LocalDate date) {
        LocalDate nextDate = date.plusDays(1);
        while (true) {
            DayOfWeek day = nextDate.getDayOfWeek();
            if (day == DayOfWeek.MONDAY || day == DayOfWeek.WEDNESDAY || day == DayOfWeek.SATURDAY) return nextDate;
            nextDate = nextDate.plusDays(1);
        }
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
