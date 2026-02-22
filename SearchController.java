package com.example.securesearch.controller;

import com.example.securesearch.util.AESUtil;
import com.example.securesearch.util.NGramUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/api")
public class SearchController {

    // Bank account entries
    private List<Map<String,String>> bankData = new ArrayList<>();
    private List<Map<String,String>> encryptedBankData = new ArrayList<>();
    private List<Map<String,Object>> searchHistory = new ArrayList<>();

    // Add bank account entries
    @PostMapping("/addBankData")
    public String addBankData(@RequestBody List<Map<String,String>> entries){
        bankData.clear();
        encryptedBankData.clear();

        for(Map<String,String> entry : entries){
            bankData.add(entry);
            Map<String,String> encryptedEntry = new HashMap<>();
            for(String key : entry.keySet()){
                encryptedEntry.put(key, AESUtil.encrypt(entry.get(key)));
            }
            encryptedBankData.add(encryptedEntry);
        }
        return "Bank data stored and encrypted successfully!";
    }

    // Search keyword in any field
    @GetMapping("/search")
    public Map<String,Object> search(@RequestParam String keyword){
        List<Map<String,String>> resultList = new ArrayList<>();
        int matchedCount=0, notMatchedCount=0;
        Set<String> keywordGrams = NGramUtil.generate(keyword,3);

        for(int i=0;i<bankData.size();i++){
            Map<String,String> decryptedEntry = new HashMap<>();
            boolean found = false;

            for(String key : encryptedBankData.get(i).keySet()){
                String value = AESUtil.decrypt(encryptedBankData.get(i).get(key));
                decryptedEntry.put(key, value);

                Set<String> grams = NGramUtil.generate(value,3);
                if(keywordGrams.stream().anyMatch(grams::contains)){
                    found = true;
                }
            }

            Map<String,String> row = new HashMap<>(decryptedEntry);
            row.put("status", found ? "MATCHED" : "NOT MATCHED");
            resultList.add(row);

            if(found) matchedCount++; else notMatchedCount++;
        }

        Map<String,Object> response = new HashMap<>();
        response.put("results", resultList);
        response.put("matchedCount", matchedCount);
        response.put("notMatchedCount", notMatchedCount);

        // Save search history
        Map<String,Object> historyRecord = new HashMap<>();
        historyRecord.put("keyword", keyword);
        historyRecord.put("matchedCount", matchedCount);
        historyRecord.put("notMatchedCount", notMatchedCount);
        historyRecord.put("time", new Date().toString());
        searchHistory.add(historyRecord);

        return response;
    }

    // Return search history
    @GetMapping("/history")
    public List<Map<String,Object>> getHistory(){
        return searchHistory;
    }
}