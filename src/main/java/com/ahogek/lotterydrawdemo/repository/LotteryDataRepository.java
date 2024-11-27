package com.ahogek.lotterydrawdemo.repository;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2023-10-26 15:26:43
 */
@Repository
public interface LotteryDataRepository extends JpaRepository<LotteryData, Long> {
    long countByLotteryDrawTime(LocalDate lotteryDrawTime);

    LotteryData findTopByOrderByLotteryDrawTimeDesc();

    @Query("SELECT l.lotteryDrawTime FROM LotteryData l WHERE l.lotteryDrawNumber IN :numbers " +
            "GROUP BY l.lotteryDrawTime HAVING COUNT(DISTINCT l.lotteryDrawNumberType) = 7")
    List<LocalDate> findMatchingDrawDates(@Param("numbers") List<String> numbers);

    // 一等奖:前区5个+后区2个全中
    @Query("""
                SELECT COUNT(DISTINCT l.lotteryDrawTime)
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 5
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 2
            """)
    Integer findFirstPrizeCount(@Param("frontNumbers") List<String> frontNumbers,
                                @Param("backNumbers") List<String> backNumbers);

    // 二等奖:前区5个+后区1个
    @Query("""
                SELECT COUNT(DISTINCT l.lotteryDrawTime)
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 5
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 1
            """)
    Integer findSecondPrizeCount(@Param("frontNumbers") List<String> frontNumbers,
                                 @Param("backNumbers") List<String> backNumbers);

    // 三等奖:前区5个
    @Query("""
                SELECT COUNT(DISTINCT l.lotteryDrawTime)
                FROM LotteryData l
                WHERE l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers
                GROUP BY l.lotteryDrawTime
                HAVING COUNT(*) = 5
            """)
    Integer findThirdPrizeCount(@Param("frontNumbers") List<String> frontNumbers);

    // 四等奖:前区4个+后区2个
    @Query("""
                SELECT COUNT(DISTINCT l.lotteryDrawTime)
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 4
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 2
            """)
    Integer findFourthPrizeCount(@Param("frontNumbers") List<String> frontNumbers,
                                 @Param("backNumbers") List<String> backNumbers);

    // 五等奖:前区4个+后区1个
    @Query("""
                SELECT COUNT(DISTINCT l.lotteryDrawTime)
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 4
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 1
            """)
    Integer findFifthPrizeCount(@Param("frontNumbers") List<String> frontNumbers,
                                @Param("backNumbers") List<String> backNumbers);

    // 六等奖:前区3个+后区2个
    @Query("""
                SELECT COUNT(DISTINCT l.lotteryDrawTime)
                FROM LotteryData l
                GROUP BY l.lotteryDrawTime
                HAVING SUM(CASE
                    WHEN l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers THEN 1 ELSE 0 END) = 3
                AND SUM(CASE
                    WHEN l.lotteryDrawNumberType > 4 AND l.lotteryDrawNumber IN :backNumbers THEN 1 ELSE 0 END) = 2
            """)
    Integer findSixthPrizeCount(@Param("frontNumbers") List<String> frontNumbers,
                                @Param("backNumbers") List<String> backNumbers);

    // 七等奖:前区4个
    @Query("""
                SELECT COUNT(DISTINCT l.lotteryDrawTime)
                FROM LotteryData l
                WHERE l.lotteryDrawNumberType <= 4 AND l.lotteryDrawNumber IN :frontNumbers
                GROUP BY l.lotteryDrawTime
                HAVING COUNT(*) = 4
            """)
    Integer findSeventhPrizeCount(@Param("frontNumbers") List<String> frontNumbers);


    // 八等奖: 前区3个+后区1个 或 前区2个+后区2个
    @Query(value = """
                SELECT COUNT(DISTINCT temp.drawTime)
                FROM (
                    SELECT l.lottery_draw_time AS drawTime
                    FROM lottery_data l
                    GROUP BY l.lottery_draw_time
                    HAVING SUM(IF(l.lottery_draw_number_type <= 4
                                 AND l.lottery_draw_number IN :frontNumbers, 1, 0)) = 3
                       AND SUM(IF(l.lottery_draw_number_type > 4
                                 AND l.lottery_draw_number IN :backNumbers, 1, 0)) = 1
            
                    UNION ALL
            
                    SELECT l.lottery_draw_time AS drawTime
                    FROM lottery_data l
                    GROUP BY l.lottery_draw_time
                    HAVING SUM(IF(l.lottery_draw_number_type <= 4
                                 AND l.lottery_draw_number IN :frontNumbers, 1, 0)) = 2
                       AND SUM(IF(l.lottery_draw_number_type > 4
                                 AND l.lottery_draw_number IN :backNumbers, 1, 0)) = 2
                ) temp
            """, nativeQuery = true)
    Integer findEighthPrizeCount(@Param("frontNumbers") List<String> frontNumbers,
                                 @Param("backNumbers") List<String> backNumbers);

    // 九等奖: 前区3个 或 前区1个+后区2个 或 前区2个+后区1个 或 后区2个
    @Query(value = """
                WITH matched_times AS (
                    SELECT DISTINCT drawTime
                    FROM (
                        -- 前区3个
                        SELECT
                            l.lottery_draw_time AS drawTime
                        FROM lottery_data l
                        WHERE l.lottery_draw_number_type <= 4
                        GROUP BY l.lottery_draw_time
                        HAVING SUM(IF(l.lottery_draw_number IN :frontNumbers, 1, 0)) = 3
            
                        UNION ALL
            
                        -- 前区1个+后区2个
                        SELECT
                            l.lottery_draw_time AS drawTime
                        FROM lottery_data l
                        GROUP BY l.lottery_draw_time
                        HAVING SUM(IF(l.lottery_draw_number_type <= 4
                            AND l.lottery_draw_number IN :frontNumbers, 1, 0)) = 1
                           AND SUM(IF(l.lottery_draw_number_type > 4
                            AND l.lottery_draw_number IN :backNumbers, 1, 0)) = 2
            
                        UNION ALL
            
                        -- 前区2个+后区1个
                        SELECT
                            l.lottery_draw_time AS drawTime
                        FROM lottery_data l
                        GROUP BY l.lottery_draw_time
                        HAVING SUM(IF(l.lottery_draw_number_type <= 4
                            AND l.lottery_draw_number IN :frontNumbers, 1, 0)) = 2
                           AND SUM(IF(l.lottery_draw_number_type > 4
                            AND l.lottery_draw_number IN :backNumbers, 1, 0)) = 1
            
                        UNION ALL
            
                        -- 后区2个
                        SELECT
                            l.lottery_draw_time AS drawTime
                        FROM lottery_data l
                        WHERE l.lottery_draw_number_type > 4
                        GROUP BY l.lottery_draw_time
                        HAVING SUM(IF(l.lottery_draw_number IN :backNumbers, 1, 0)) = 2
                    ) all_matches
                )
                SELECT COUNT(*) FROM matched_times
            """, nativeQuery = true)
    Integer findNinthPrizeCount(@Param("frontNumbers") List<String> frontNumbers,
                                @Param("backNumbers") List<String> backNumbers);
}
