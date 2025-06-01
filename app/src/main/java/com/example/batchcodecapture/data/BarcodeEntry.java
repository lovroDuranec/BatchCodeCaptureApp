package com.example.batchcodecapture.data;

public class BarcodeEntry {
    private String barcodeData;
    private String imagePath;
    private String timestamp;

    public BarcodeEntry(String barcodeData, String imagePath, String timestamp) {
        this.barcodeData = barcodeData;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }

    public String getBarcodeData() { return barcodeData; }

    public String getImagePath() { return imagePath; }

    public String getTimestamp() { return timestamp; }

}