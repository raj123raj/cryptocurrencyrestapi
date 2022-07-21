package com.cryptocurrency.cryptocurrency_Rest_API.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class CryptocurrencyControllerTest {
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new CryptocurrencyController()).build();
    }


    @Test
    public void testCryptocurrencyDetails() throws Exception {

        
        //expected
        String expectedData = null;
        StringBuilder responseData = new StringBuilder();
        JsonObject expectedJsonObject = null;
        String expectedRank = null,expectedSymbol = null,expectedPriceInUSD = null;
        URL url = new URL("https://api.coincap.io/v2/assets/bitcoin");
        //URL url = new URL("https://api.exchangerate-api.com/v4/latest/USD");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {

            String line;

            while ((line = in.readLine()) != null) {
                responseData.append(line);
            }

            expectedJsonObject = new Gson().fromJson(responseData.toString(), JsonObject.class);
            expectedData = expectedJsonObject.get("data").toString();
            expectedData = expectedData.replaceAll("^\"|\"$", "");
            StringTokenizer jsonTokenizer = new StringTokenizer(expectedData,",");
            String internalData[];
            String expectedCryptocurrencyOutput = null;
            
            while (jsonTokenizer.hasMoreTokens()) {  
            	expectedCryptocurrencyOutput = jsonTokenizer.nextToken();
            	internalData = StringUtils.split(expectedCryptocurrencyOutput,":");
            	//System.out.println(internalData[0]+internalData[1]);
            	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("rank")) {
            		expectedRank = internalData[1].substring(1,internalData[1].length()-1);
            		
            	}
            	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("symbol")) {
            		expectedSymbol = internalData[1].substring(1,internalData[1].length()-1);
            	}
            	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("priceUsd")) {
            		expectedPriceInUSD = internalData[1].substring(1,internalData[1].length()-1);
            	}
            }
            
            System.out.println(expectedRank + expectedSymbol + expectedPriceInUSD);
        }

        //actual
        MvcResult result = mockMvc.perform(get("/getCryptocurrencyDetailsByName?cryptocurrency=bitcoin"))
                .andReturn();
        String recievedResponse = result.getResponse().getContentAsString();
        JsonObject actualJsonObject = new Gson().fromJson(recievedResponse, JsonObject.class);
        String actualRank = actualJsonObject.get("rank").toString();
        actualRank = actualRank.replaceAll("^\"|\"$", "");
        String actualSymbol = actualJsonObject.get("symbol").toString();
        actualSymbol = actualSymbol.replaceAll("^\"|\"$", "");
        String actualPriceInUSD = actualJsonObject.get("priceUsd").toString();
        actualPriceInUSD = actualPriceInUSD.replaceAll("^\"|\"$", "");
        assertEquals(expectedRank, actualRank);
        assertEquals(expectedSymbol, actualSymbol);
        assertEquals(expectedPriceInUSD, actualPriceInUSD);
    }

}
