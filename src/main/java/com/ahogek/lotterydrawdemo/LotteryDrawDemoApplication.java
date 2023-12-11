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
                LocalDate date = LocalDate.parse(itemNumbers.getFirst(),
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
                    List.of("2023-11-08", "01", "05", "07", "12", "13", "02", "06"),
                    List.of("2023-11-11", "09", "23", "25", "27", "33", "06", "12"),
                    List.of("2023-11-13", "13", "20", "27", "29", "30", "01", "07"),
                    List.of("2023-11-15", "03", "07", "21", "22", "24", "06", "07"),
                    List.of("2023-11-18", "03", "04", "12", "15", "23", "02", "06"),
                    List.of("2023-11-20", "10", "18", "25", "28", "33", "05", "11"),
                    List.of("2023-11-22", "10", "15", "22", "27", "33", "01", "12"),
                    List.of("2023-11-25", "05", "18", "22", "28", "29", "09", "12"),
                    List.of("2023-11-27", "13", "23", "27", "30", "34", "06", "09"),
                    List.of("2023-11-29", "04", "19", "21", "30", "31", "06", "12"),
                    List.of("2023-12-02", "07", "12", "20", "28", "31", "09", "10"),
                    List.of("2023-12-04", "15", "16", "25", "31", "34", "05", "09"),
                    List.of("2023-12-06", "01", "02", "09", "19", "30", "01", "02"),
                    List.of("2023-12-09", "04", "22", "25", "30", "31", "04", "05"),
                    List.of("2023-12-11", "04", "13", "15", "17", "32", "10", "12")
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
