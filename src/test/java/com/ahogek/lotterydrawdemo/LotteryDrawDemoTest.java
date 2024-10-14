package com.ahogek.lotterydrawdemo;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.entity.SelfChosen;
import com.ahogek.lotterydrawdemo.repository.SelfChosenRepository;
import com.ahogek.lotterydrawdemo.service.LotteryDataService;
import com.ahogek.lotterydrawdemo.util.ProgressBarWithTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    SelfChosenRepository selfChosenRepository;

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
        LotteryDrawDemoApplication.groupSelfChosenData(allDataGroup, selfChosenRepository.findAll());
    }

    @Test
    void testDrawFirstPrize() {
        List<String> firstPrize = List.of("01", "08", "17", "29", "33", "03", "04");

        // 先检查是否有历史一等奖
        int check = service.checkFirstPrize(firstPrize);
        if (check > 0) {
            System.out.println("该号码历史上已经中过一等奖" + check + "次，祝你好运！");
        } else {
            System.out.println("该号码历史上还没有中过一等奖，祝你好运！");
        }


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
        long totalCount = 7579991314L;
        long updateInterval = totalCount / 10000;
        long nextUpdate = updateInterval;

        ProgressBarWithTime progressBar = new ProgressBarWithTime(totalCount, 50);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
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
            for (int i = 0; i < 7; i++) {
                SelfChosen selfChosen = new SelfChosen(result.get(i), i);
                insertList.add(selfChosen);
            }
            selfChosenRepository.saveAll(insertList);

            System.out.println("随机摇奖号码为：" + result + "，祝你好运！");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
