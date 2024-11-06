package com.example.batchcodecapture;

import java.util.List;

public class Session {
    private String sessionId;
    private List<String> barcodes;

    public Session(String sessionId, List<String> barcodes) {
        this.sessionId = sessionId;
        this.barcodes = barcodes;
    }

    public String getSessionId() {
        return sessionId;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }
}