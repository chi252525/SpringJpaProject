package com.example.controller;

import com.example.dto.CurrencyInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/price")
public class PriceInfoController {
    @GetMapping(value = "/info", produces = "application/json")
    public ResponseEntity getPriceInfo() {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.coindesk.com/v1/bpi/currentprice.json");

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            String json = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(json, Map.class);
            Map<String, Object> time = (Map<String, Object>) data.get("time");
            String updateTime = String.valueOf(time.get("updated"));
            //時間格式範例：1990/01/01 00:00:00
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, uuuu HH:mm:ss z", Locale.ENGLISH);
            LocalDateTime dateTime = LocalDateTime.parse(updateTime, formatter);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
            updateTime = dateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Taipei"))
                    .format(outputFormatter);
            Map<String, Object> bpi = (Map<String, Object>) data.get("bpi");
            Map<String, Object> usd = (Map<String, Object>) bpi.get("USD");
            String currencyCode = String.valueOf(usd.get("code"));
            String currencyName = String.valueOf(usd.get("description"));
            BigDecimal price = new BigDecimal(String.valueOf(usd.get("rate")).replaceAll(",", ""));
            return ResponseEntity.ok(new CurrencyInfo(updateTime, currencyCode, currencyName, price));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
