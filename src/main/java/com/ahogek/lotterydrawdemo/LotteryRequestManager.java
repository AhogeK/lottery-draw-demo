package com.ahogek.lotterydrawdemo;

import com.ahogek.lotterydrawdemo.entity.LotteryData;
import com.ahogek.lotterydrawdemo.service.LotteryDataService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2023-10-26 17:47:40
 */
@Component
public class LotteryRequestManager {
    private static final Logger LOG = LoggerFactory.getLogger(LotteryRequestManager.class);
    private final OkHttpClient client;
    private final String baseUrl;
    private int pageSize;
    private final int gameNo;
    private final int provinceId;
    private final int isVerify;
    private final LotteryDataService lotteryDataService;
    private int pageNo;
    private int pages = 1;

    public LotteryRequestManager(LotteryDataService lotteryDataService) {
        this.client = new OkHttpClient();
        this.baseUrl = "https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry";
        this.pageNo = 1; // 初始页码
        this.pageSize = 100; // 根据需求调整
        this.gameNo = 85; // 根据需求调整
        this.provinceId = 0; // 根据需求调整
        this.isVerify = 1; // 根据需求调整
        this.lotteryDataService = lotteryDataService;
    }

    @Transactional(rollbackFor = Exception.class)
    public void setData() throws IOException {
        List<LotteryData> all = lotteryDataService.findAll();
        if (all.isEmpty()) {
            List<LotteryData> insertData = new ArrayList<>();
            // 初始化
            do {
                JSONObject response = getNextPage(null);

                JSONArray list = response.getJSONObject("value").getJSONArray("list");
                for (int i = 0; i < list.size(); i++) {
                    JSONObject data = list.getJSONObject(i);
                    String[] drawNumbers = data.getString("lotteryDrawResult").split(" ");
                    for (int j = 0; j < 7; j++) {
                        LotteryData lotteryData = new LotteryData();
                        lotteryData.setLotteryDrawTime(LocalDate.ofInstant(data.getDate("lotteryDrawTime").toInstant(), ZoneId.systemDefault()));
                        lotteryData.setLotteryDrawNumber(drawNumbers[j]);
                        lotteryData.setLotteryDrawNumberType(j);
                        insertData.add(lotteryData);
                    }
                }
            } while (hasNextPage());
            lotteryDataService.batchInsert(insertData);
        }
    }

    public JSONObject getNextPage(Integer count) throws IOException {
        if (count != null)
            this.pageSize = count;
        HttpUrl parse = HttpUrl.parse(baseUrl);
        if (parse != null) {
            HttpUrl.Builder urlBuilder = parse.newBuilder()
                    .addQueryParameter("gameNo", String.valueOf(gameNo))
                    .addQueryParameter("provinceId", String.valueOf(provinceId))
                    .addQueryParameter("pageSize", String.valueOf(pageSize))
                    .addQueryParameter("isVerify", String.valueOf(isVerify))
                    .addQueryParameter("pageNo", String.valueOf(pageNo));

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseStr = response.body().string();
                JSONObject responseObj = JSON.parseObject(responseStr);

                if (LOG.isInfoEnabled()) {
                    LOG.info("{}", responseObj.toString(JSONWriter.Feature.PrettyFormat));
                }

                if (!response.isSuccessful() ||
                        !Boolean.TRUE.equals(responseObj.getBoolean("success")))
                    throw new IOException("Unexpected code " + response);
                pages = responseObj.getJSONObject("value").getInteger("pages");
                pageNo++; // 增加页码
                return responseObj; // 或根据需要进行其他处理
            }
        }
        throw new IOException("Unexpected code");
    }

    private boolean hasNextPage() {
        return pageNo <= pages;
    }
}
