package com.ahogek.lotterydrawdemo;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.service.LotteryDataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * 用于测试是否可以抽到一等奖
 *
 * @author AhogeK ahogek@gmail.com
 * @since 2024-04-19 14:53:12
 */
@SpringBootTest
class LotteryDrawDemoTest {

    @Autowired
    LotteryDataService service;

    @Autowired
    LotteryDrawDemoApplication application;

    List<LotteryData> all = new ArrayList<>();
    List<String> result = new ArrayList<>((int) (7 / 0.75f + 1));
    Set<String> front = new HashSet<>();
    Set<String> back = new HashSet<>();
    List<List<String>> allDataGroup = new ArrayList<>();

    @BeforeEach
    void beforeAll() {
        all = service.findAll();
        LotteryDrawDemoApplication.groupAllData(allDataGroup, all);
    }

    @Test
    void testDrawFirstPrize() {
        List<String> firstPrize = List.of("05", "11", "19", "22", "29", "02", "12");

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
        long count = 0;
        long maxCount = 7579991314L;
        long randomCount = new Random().nextLong(maxCount);
        long finalCount;

        do {
            count++;
            finalCount = new Random().nextLong(maxCount);
        } while (count != randomCount);

        count = 0;

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
        } while (count != finalCount);

        Assertions.assertNotNull(result);

        System.out.println("随机摇奖号码为：" + result + "，祝你好运！");
    }
}
