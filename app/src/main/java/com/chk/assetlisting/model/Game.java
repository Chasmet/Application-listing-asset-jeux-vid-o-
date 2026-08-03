package com.chk.assetlisting.model;

public class Game {
    private long id;
    private String name;
    private String description;
    private String coverUri;
    private long updatedAt;
    private int assetCount;
    private int integratedCount;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverUri() { return coverUri; }
    public void setCoverUri(String coverUri) { this.coverUri = coverUri; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public int getAssetCount() { return assetCount; }
    public void setAssetCount(int assetCount) { this.assetCount = assetCount; }
    public int getIntegratedCount() { return integratedCount; }
    public void setIntegratedCount(int integratedCount) { this.integratedCount = integratedCount; }
    public int getProgressPercent() { return assetCount == 0 ? 0 : Math.round((integratedCount * 100f) / assetCount); }
}
