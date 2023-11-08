package com.ahogek.lotterydrawdemo;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.repository.LotteryDataRepository;
import com.ahogek.lotterydrawdemo.service.LotteryDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@EnableTransactionManagement
@SpringBootApplication
public class LotteryDrawDemoApplication {

    private static final Logger LOG = LoggerFactory.getLogger(LotteryDrawDemoApplication.class);

    private final Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(LotteryDrawDemoApplication.class, args);
    }

    private static void checkNewInputDrawNumber(LotteryDataRepository lotteryDateRepository, List<List<String>> inputNewDrawNumber) {
        if (inputNewDrawNumber != null && !inputNewDrawNumber.isEmpty()) {
            // 遍历 inputNewDrawNumber 集合
            inputNewDrawNumber.forEach(itemNumbers -> {
                // 遍历每一项，其中第一项为日期，先判断数据库有无该日期的数据，如果没有才执行操作
                LocalDate date = LocalDate.parse(itemNumbers.get(0),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (lotteryDateRepository.countByLotteryDrawTime(date) == 0) {
                    List<LotteryData> insertList = new ArrayList<>();
                    for (int i = 1; i < itemNumbers.size(); i++) {
                        LotteryData lotteryDrawNumber = new LotteryData();
                        lotteryDrawNumber.setLotteryDrawNumber(itemNumbers.get(i));
                        lotteryDrawNumber.setLotteryDrawTime(date);
                        lotteryDrawNumber.setLotteryDrawNumberType(i - 1);
                        insertList.add(lotteryDrawNumber);
                    }
                    if (!insertList.isEmpty())
                        lotteryDateRepository.saveAll(insertList);
                }
            });
        }
    }

    private static void groupAllData(List<List<String>> allData, List<LotteryData> all) {
        for (int i = 0; i < 7; i++) {
            int type = i;
            allData.add(all.stream().filter(item -> type == item.getLotteryDrawNumberType())
                    .map(LotteryData::getLotteryDrawNumber).toList());
        }
    }

    @Bean
    public CommandLineRunner getRandomLotteryNumber(LotteryDataService service, LotteryRequestManager request, LotteryDataRepository lotteryDateRepository) {
        return args -> {
            List<LotteryData> all = service.findAll();
            if (all.isEmpty())
                request.setData();


            List<List<String>> inputNewDrawNumber = List.of(
                    List.of("2023-10-28", "02", "14", "16", "28", "30", "02", "07"),
                    List.of("2023-10-30", "06", "18", "23", "27", "32", "06", "10"),
                    List.of("2023-11-01", "07", "25", "26", "29", "32", "02", "08"),
                    List.of("2023-11-04", "07", "12", "17", "26", "34", "02", "06"),
                    List.of("2023-11-06", "04", "07", "08", "18", "19", "02", "06")
            );
            checkNewInputDrawNumber(lotteryDateRepository, inputNewDrawNumber);

            List<String> result = new ArrayList<>((int) (7 / 0.75f + 1));

            // 前区五个球
            Set<String> front = new HashSet<>();
            // 后区两个球
            Set<String> back = new HashSet<>();

            List<List<String>> allDataGroup = new ArrayList<>();

            groupAllData(allDataGroup, all);

            for (int i = 0; i < 7; i++) {
                // 随机一个列表里的String
                drawNumbers(i, allDataGroup, front, back);
            }

            // 分别排序前后组
            front.stream().sorted().forEach(result::add);
            back.stream().sorted().forEach(result::add);

            LOG.info("随机摇奖号码为：{}，祝你好运！", result);
        };
    }

    private void drawNumbers(int i, List<List<String>> allDataGroup, Set<String> front, Set<String> back) {
        if (i < 5) {
            do {
                int index = this.random.nextInt(allDataGroup.get(i).size());
                front.add(allDataGroup.get(i).get(index));
            } while (front.size() != i + 1);
        } else {
            do {
                int index = this.random.nextInt(allDataGroup.get(i).size());
                back.add(allDataGroup.get(i).get(index));
            } while (back.size() != i - 5 + 1);
        }
    }
}
