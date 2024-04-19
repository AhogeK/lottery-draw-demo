package com.ahogek.lotterydrawdemo;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.service.LotteryDataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Test
    void testDrawFirstPrize() {

        List<String> firstPrize = List.of("03", "04", "08", "14", "27", "05", "07");

        List<LotteryData> all = service.findAll();
        List<String> result = new ArrayList<>((int) (7 / 0.75f + 1));
        Set<String> front = new HashSet<>();
        Set<String> back = new HashSet<>();
        List<List<String>> allDataGroup = new ArrayList<>();
        LotteryDrawDemoApplication.groupAllData(allDataGroup, all);

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
}
