package com.example.batchcodecapture.data;

public class BarcodeEntry {
    private String barcodeData;
    private String imagePath;

    public BarcodeEntry(String barcodeData, String imagePath) {
        this.barcodeData = barcodeData;
        this.imagePath = imagePath;
    }

    public String getBarcodeData() { return barcodeData; }
    public String getImagePath() { return imagePath; }
}