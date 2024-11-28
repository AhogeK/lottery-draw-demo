package com.ahogek.lotterydrawdemo;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.entity.PrizeCheckResult;
import com.ahogek.lotterydrawdemo.entity.SelfChosen;
import com.ahogek.lotterydrawdemo.repository.SelfChosenRepository;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 用于测试是否可以抽到一等奖
 *
 * @author AhogeK ahogek@gmail.com
 * @since 2024-04-19 14:53:12
 */
@SpringBootTest
class LotteryDrawDemoTest {

    private static final Logger log = LoggerFactory.getLogger(LotteryDrawDemoTest.class);
    @Autowired
    LotteryDataService service;

    @Autowired
    SelfChosenRepository selfChosenRepository;

    @Autowired
    LotteryDrawDemoApplication application;

    @Autowired
    LotteryRequestManager manager;

    List<LotteryData> all = new ArrayList<>();
    List<String> result = new ArrayList<>((int) (7 / 0.75f + 1));
    List<String> sortBeforeResult = new ArrayList<>((int) (7 / 0.75f + 1));
    Map<String, Integer> sortBeforeResultMap = new HashMap<>();
    Set<String> front = new HashSet<>();
    Set<String> back = new HashSet<>();
    List<List<String>> allDataGroup = new ArrayList<>();

    @BeforeEach
    void beforeAll() {
        all = service.findAll();
        LotteryDrawDemoApplication.groupAllData(allDataGroup, all);
        LotteryDrawDemoApplication.groupSelfChosenData(allDataGroup, selfChosenRepository.findAll());
    }

    @Test
    void testDrawFirstPrize() {
        List<String> firstPrize = List.of("05", "17", "20", "28", "34", "04", "09");

        PrizeCheckResult prizeCheckResult = service.checkAllPrizes(firstPrize);
        log.info("号码{}的{}", firstPrize, prizeCheckResult);

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
                alreadyDraw.set(true);
            }
        });
        if (alreadyDraw.get()) {
            return;
        }
        long count = 0;
        long totalCount = 5302646993L;
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
                sortBeforeResultMap.put(sortBeforeResult.get(i - 1), i);
            }
            for (int i = 0; i < 7; i++) {
                SelfChosen selfChosen = new SelfChosen(result.get(i), i, sortBeforeResultMap.get(result.get(i)));
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
            log.info("{}", nextPage);
        });
    }

    @Test
    void syncSelfChosenPrizeData() {
        Map<LocalDate, List<LotteryData>> allLotteryDataByDate = service.findAll().stream()
                .collect(Collectors.groupingBy(LotteryData::getLotteryDrawTime));
        List<SelfChosen> allSelfChosen = selfChosenRepository.findAllByPrizeIsNull();
        if (allSelfChosen.isEmpty()) {
            log.warn("暂且没有自选号码数据");
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
                log.warn("{}没有开奖记录", drawTime);
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
    }

    /**
     * 检查号码的中奖情况
     *
     * @param drawNumbers 本次开奖号码
     * @param selfNumbers 自选号码
     * @return 中奖情况
     */
    private PrizeCheckResult checkPrizeForNumbers(List<String> drawNumbers, List<String> selfNumbers) {
        // 分割
        List<String> drawFront = drawNumbers.subList(0, 5);
        List<String> drawBack = drawNumbers.subList(5, 7);
        List<String> selfFront = selfNumbers.subList(0, 5);
        List<String> selfBack = selfNumbers.subList(5, 7);

        // 计算匹配
        long frontMatch = selfFront.stream().filter(drawFront::contains).count();
        long backMatch = selfBack.stream().filter(drawBack::contains).count();

        // 根据匹配情况返回中奖结果
        return buildPrizeResult(frontMatch, backMatch);
    }

    /**
     * 根据匹配数构建中奖结果
     *
     * @param frontMatch 前区匹配数
     * @param backMatch  后区匹配数
     * @return 中奖结果
     */
    private PrizeCheckResult buildPrizeResult(long frontMatch, long backMatch) {
        return PrizeCheckResult.builder()
                .firstPrize(frontMatch == 5 && backMatch == 2 ? 1 : 0)
                .secondPrize(frontMatch == 5 && backMatch == 1 ? 1 : 0)
                .thirdPrize(frontMatch == 5 && backMatch == 0 ? 1 : 0)
                .fourthPrize(frontMatch == 4 && backMatch == 2 ? 1 : 0)
                .fifthPrize(frontMatch == 4 && backMatch == 1 ? 1 : 0)
                .sixthPrize(frontMatch == 3 && backMatch == 2 ? 1 : 0)
                .seventhPrize(frontMatch == 4 && backMatch == 0 ? 1 : 0)
                .eighthPrize((frontMatch == 3 && backMatch == 1) || (frontMatch == 2 && backMatch == 2) ? 1 : 0)
                .ninthPrize((frontMatch == 3 && backMatch == 0) ||
                        (frontMatch == 1 && backMatch == 2) ||
                        (frontMatch == 2 && backMatch == 1) ||
                        (frontMatch == 0 && backMatch == 2) ? 1 : 0)
                .build();
    }

    /**
     * 确定中奖奖项
     *
     * @param result 中奖情况
     * @return 中奖奖项
     */
    private int getHighestPrize(PrizeCheckResult result) {
        if (result.getFirstPrize() > 0) return 1;
        if (result.getSecondPrize() > 0) return 2;
        if (result.getThirdPrize() > 0) return 3;
        if (result.getFourthPrize() > 0) return 4;
        if (result.getFifthPrize() > 0) return 5;
        if (result.getSixthPrize() > 0) return 6;
        if (result.getSeventhPrize() > 0) return 7;
        if (result.getEighthPrize() > 0) return 8;
        if (result.getNinthPrize() > 0) return 9;
        return 0;
    }

    /**
     * 判断是否是历史首次中奖
     *
     * @param currentDrawTime      当前开奖时间
     * @param numbers              自选号码
     * @param prize                中奖奖项
     * @param allLotteryDataByDate 所有开奖数据
     * @return 是否是历史首次中奖
     */
    private boolean isHistoricalFirstWin(LocalDate currentDrawTime, List<String> numbers, int prize, Map<LocalDate,
            List<LotteryData>> allLotteryDataByDate) {
        for (Map.Entry<LocalDate, List<LotteryData>> entry : allLotteryDataByDate.entrySet()) {
            // 只检查当前日期之前的数据
            if (entry.getKey().isAfter(currentDrawTime)) {
                continue;
            }

            // 转换为号码列表
            List<String> dailyNumbers = entry.getValue().stream().sorted(Comparator.comparingInt(LotteryData::getSort))
                    .map(LotteryData::getLotteryDrawNumber).toList();
            PrizeCheckResult prizeCheckResult = checkPrizeForNumbers(dailyNumbers, numbers);

            // 如果历史上有相同等级的中奖记录，则不是历史首次中奖
            if (hasSamePrize(prizeCheckResult, prize)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否有相同等级的中奖
     *
     * @param result      中奖结果
     * @param targetPrize 目标中奖等级
     * @return 是否有相同等级的中奖
     */
    private boolean hasSamePrize(PrizeCheckResult result, int targetPrize) {
        return switch (targetPrize) {
            case 1 -> result.getFirstPrize() > 0;
            case 2 -> result.getSecondPrize() > 0;
            case 3 -> result.getThirdPrize() > 0;
            case 4 -> result.getFourthPrize() > 0;
            case 5 -> result.getFifthPrize() > 0;
            case 6 -> result.getSixthPrize() > 0;
            case 7 -> result.getSeventhPrize() > 0;
            case 8 -> result.getEighthPrize() > 0;
            case 9 -> result.getNinthPrize() > 0;
            default -> false;
        };
    }
}
