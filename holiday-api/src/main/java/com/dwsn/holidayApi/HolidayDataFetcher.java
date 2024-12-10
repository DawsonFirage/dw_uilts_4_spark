package com.dwsn.holidayApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dwsn.holidayApi.dto.HolidayInfoDTO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HolidayDataFetcher {

    private static final String API_URL = "https://date.nager.at/api/v3/PublicHolidays/";
    private static final String COUNTRY_CODE = "CN";
    private static final int END_YEAR = 2025;
    private static final String OUTPUT_FILE_PATH = "D:\\holidays.json";

    public static void main(String[] args) throws Exception {

        List<List<HolidayInfoDTO>> holidayInfoLists = new ArrayList<>();

        for (int i = 0; i <= 30; i++) {
            // 获取节假日数据
            String jsonData = fetchHolidays(API_URL, COUNTRY_CODE, END_YEAR - i);
            // 解析JSON数据
            holidayInfoLists.add(JSON.parseArray(jsonData, HolidayInfoDTO.class));
        }

        List<HolidayInfoDTO> holidayInfos = holidayInfoLists.stream()
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());

        String holidayInfoStr = JSON.toJSONString(holidayInfos);

        // 保存到本地文件
        saveToJsonFile(holidayInfoStr, OUTPUT_FILE_PATH);

    }

    private static String fetchHolidays(String url, String countryCode, int year) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url + year + "/" + countryCode);
        CloseableHttpResponse response = httpClient.execute(httpGet);

        if (response.getStatusLine().getStatusCode() == 200) {
            return EntityUtils.toString(response.getEntity());
        } else {
            throw new RuntimeException("Failed to fetch holidays: " + response.getStatusLine());
        }
    }

    private static void saveToJsonFile(String jsonStr, String filePath) throws IOException {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonStr);
            System.out.println("JSON data saved to " + filePath);
        }
    }
}