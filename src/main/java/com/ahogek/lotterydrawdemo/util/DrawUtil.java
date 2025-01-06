package com.ahogek.lotterydrawdemo.util;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.entity.PrizeCheckResult;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author AhogeK
 * @since 2025-01-06 23:04:26
 */
public class DrawUtil {

    private DrawUtil() {
    }

    /**
     * 检查号码的中奖情况
     *
     * @param drawNumbers 本次开奖号码
     * @param selfNumbers 自选号码
     * @return 中奖情况
     */
    public static PrizeCheckResult checkPrizeForNumbers(List<String> drawNumbers, List<String> selfNumbers) {
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
    private static PrizeCheckResult buildPrizeResult(long frontMatch, long backMatch) {
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
    public static int getHighestPrize(PrizeCheckResult result) {
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
    public static boolean isHistoricalFirstWin(LocalDate currentDrawTime, List<String> numbers, int prize, Map<LocalDate,
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
    private static boolean hasSamePrize(PrizeCheckResult result, int targetPrize) {
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
