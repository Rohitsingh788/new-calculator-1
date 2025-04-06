package com.example.newcalculator; // Replace with your package name

import com.google.gson.annotations.SerializedName;

public class GlobalQuoteResponse {
    @SerializedName("Global Quote")
    private GlobalQuote globalQuote;

    public GlobalQuote getGlobalQuote() {
        return globalQuote;
    }

    public static class GlobalQuote {
        @SerializedName("05. price")
        private String price;

        public String getPrice() {
            return price;
        }
    }
}