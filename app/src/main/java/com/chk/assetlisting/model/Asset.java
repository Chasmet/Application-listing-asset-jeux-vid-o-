package com.chk.assetlisting.model;

import java.util.ArrayList;
import java.util.List;

public class Asset {
    public static final int STATUS_TODO = 0;
    public static final int STATUS_CREATED = 1;
    public static final int STATUS_INTEGRATED = 2;
    private long id;
    private long gameId;
    private long regionId;
    private String regionName;
    private String name;
    private String category;
    private String description;
    private String fileName;
    private String filePath;
    private int status;
    private long updatedAt;
    private List<String> imageUris = new ArrayList<>();
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getGameId() { return gameId; }
    public void setGameId(long gameId) { this.gameId = gameId; }
    public long getRegionId() { return regionId; }
    public void setRegionId(long regionId) { this.regionId = regionId; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public List<String> getImageUris() { return imageUris; }
    public void setImageUris(List<String> imageUris) { this.imageUris = imageUris == null ? new ArrayList<>() : imageUris; }
    public String getMainImageUri() { return imageUris.isEmpty() ? null : imageUris.get(0); }
}
