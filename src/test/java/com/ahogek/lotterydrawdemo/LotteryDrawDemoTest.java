package com.ahogek.lotterydrawdemo;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.service.LotteryDataService;
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
        List<String> firstPrize = List.of("05", "15", "24", "31", "34", "03", "10");

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
        long totalCount = 17575219991L;
        long updateInterval = totalCount / 10000;
        long nextUpdate = updateInterval;

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
                    double progress = (double) count / totalCount;
                    updateProgressBar(writer, progress);
                    nextUpdate += updateInterval;
                }
            } while (count != totalCount);

            writer.write("\n");
            writer.flush();

            Assertions.assertNotNull(result);

            System.out.println("随机摇奖号码为：" + result + "，祝你好运！");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateProgressBar(BufferedWriter writer, double progress) throws IOException {
        int width = 50; // 进度条的总宽度
        int completedWidth = (int) (width * progress);
        StringBuilder sb = new StringBuilder("\r[");
        for (int i = 0; i < width; i++) {
            if (i < completedWidth) {
                sb.append("#");
            } else {
                sb.append(" ");
            }
        }
        sb.append("] ").append(String.format("%.2f%%", progress * 100));
        writer.write(sb.toString());
        writer.flush();
    }
}
