package com.ahogek.lotterydrawdemo.repository;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2023-10-26 15:26:43
 */
@Repository
public interface LotteryDataRepository extends JpaRepository<LotteryData, Long> {
    long countByLotteryDrawTime(LocalDate lotteryDrawTime);

    LotteryData findTopByOrderByLotteryDrawTimeDesc();

    List<LotteryData> findByLotteryDrawTimeIn(Collection<LocalDate> lotteryDrawTime);

    @Query("SELECT l.lotteryDrawTime FROM LotteryData l WHERE l.lotteryDrawNumber IN :numbers " +
            "GROUP BY l.lotteryDrawTime HAVING COUNT(DISTINCT l.lotteryDrawNumberType) = 7")
    List<LocalDate> findMatchingDrawDates(@Param("numbers") List<String> numbers);

    // 一等奖:前区5个+后区2个全中
    @Query("""
            SELECT new map(
                COUNT(*) as count,
                MAX(dates.drawTime) as lastDate
            )
            FROM (
                SELECT l.lotteryDrawTime as drawTime
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 5
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 2
            ) dates
            """)
    Map<String, Object> findFirstPrizeCountAndLastDate(@Param("frontNumbers") List<String> frontNumbers,
                                                       @Param("backNumbers") List<String> backNumbers);

    // 二等奖:前区5个+后区1个
    @Query("""
            SELECT new map(
                COUNT(*) as count,
                MAX(dates.drawTime) as lastDate
            )
            FROM (
                SELECT l.lotteryDrawTime as drawTime
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 5
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 1
            ) dates
            """)
    Map<String, Object> findSecondPrizeCountAndLastDate(@Param("frontNumbers") List<String> frontNumbers,
                                                        @Param("backNumbers") List<String> backNumbers);

    // 三等奖：前区5个，但排除一等奖和二等奖
    @Query("""
            SELECT new map(
                COUNT(*) as count,
                MAX(dates.drawTime) as lastDate
            )
            FROM (
                SELECT l.lotteryDrawTime as drawTime
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 5
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 0
            ) dates
            """)
    Map<String, Object> findThirdPrizeCountAndLastDate(@Param("frontNumbers") List<String> frontNumbers,
                                                       @Param("backNumbers") List<String> backNumbers);


    // 四等奖:前区4个+后区2个
    @Query("""
            SELECT new map(
                COUNT(*) as count,
                MAX(dates.drawTime) as lastDate
            )
            FROM (
                SELECT l.lotteryDrawTime as drawTime
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 4
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 2
            ) dates
            """)
    Map<String, Object> findFourthPrizeCountAndLastDate(@Param("frontNumbers") List<String> frontNumbers,
                                                        @Param("backNumbers") List<String> backNumbers);


    // 五等奖:前区4个+后区1个
    @Query("""
            SELECT new map(
                COUNT(*) as count,
                MAX(dates.drawTime) as lastDate
            )
            FROM (
                SELECT l.lotteryDrawTime as drawTime
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 4
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 1
            ) dates
            """)
    Map<String, Object> findFifthPrizeCountAndLastDate(@Param("frontNumbers") List<String> frontNumbers,
                                                       @Param("backNumbers") List<String> backNumbers);

    // 六等奖:前区3个+后区2个
    @Query("""
            SELECT new map(
                COUNT(*) as count,
                MAX(dates.drawTime) as lastDate
            )
            FROM (
                SELECT l.lotteryDrawTime as drawTime
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 3
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 2
            ) dates
            """)
    Map<String, Object> findSixthPrizeCountAndLastDate(@Param("frontNumbers") List<String> frontNumbers,
                                                       @Param("backNumbers") List<String> backNumbers);


    // 七等奖：前区4个，但排除四等奖和五等奖
    @Query("""
            SELECT new map(
                COUNT(*) as count,
                MAX(dates.drawTime) as lastDate
            )
            FROM (
                SELECT l.lotteryDrawTime as drawTime
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 4
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 0
            ) dates
            """)
    Map<String, Object> findSeventhPrizeCountAndLastDate(@Param("frontNumbers") List<String> frontNumbers,
                                                         @Param("backNumbers") List<String> backNumbers);

    // 八等奖：转换为JPQL，排除更高奖项
    @Query("""
            SELECT new map(
                COUNT(*) as count,
                MAX(dates.drawTime) as lastDate
            )
            FROM (
                SELECT l.lotteryDrawTime as drawTime
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING (
                    (SUM(CASE
                        WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 3
                    AND SUM(CASE
                        WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 1)
                    OR
                    (SUM(CASE
                        WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 2
                    AND SUM(CASE
                        WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 2)
                )
            ) dates
            """)
    Map<String, Object> findEighthPrizeCountAndLastDate(@Param("frontNumbers") List<String> frontNumbers,
                                                        @Param("backNumbers") List<String> backNumbers);

    // 九等奖：转换为JPQL，排除更高奖项
    @Query("""
            SELECT new map(
                COUNT(*) as count,
                MAX(dates.drawTime) as lastDate
            )
            FROM (
                SELECT l.lotteryDrawTime as drawTime
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING (
                    (SUM(CASE
                        WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 3
                    AND SUM(CASE
                        WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 0)
                    OR
                    (SUM(CASE
                        WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 1
                    AND SUM(CASE
                        WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 2)
                    OR
                    (SUM(CASE
                        WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 2
                    AND SUM(CASE
                        WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 1)
                    OR
                    (SUM(CASE
                        WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 0
                    AND SUM(CASE
                        WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 2)
                )
            ) dates
            """)
    Map<String, Object> findNinthPrizeCountAndLastDate(@Param("frontNumbers") List<String> frontNumbers,
                                                       @Param("backNumbers") List<String> backNumbers);
}
