package com.example.securesearch.util;

import java.util.HashSet;
import java.util.Set;

public class NGramUtil {
    public static Set<String> generate(String text,int n){
        Set<String> grams = new HashSet<>();
        text = text.toLowerCase();
        for(int i=0;i<=text.length()-n;i++){
            grams.add(text.substring(i,i+n));
        }
        return grams;
    }
}