package com.chk.assetlisting.model;

public class Region {
    private long id;
    private long gameId;
    private String name;
    private String type;
    private String imageUri;
    private String notes;
    private int assetCount;
    private int integratedCount;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getGameId() { return gameId; }
    public void setGameId(long gameId) { this.gameId = gameId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public int getAssetCount() { return assetCount; }
    public void setAssetCount(int assetCount) { this.assetCount = assetCount; }
    public int getIntegratedCount() { return integratedCount; }
    public void setIntegratedCount(int integratedCount) { this.integratedCount = integratedCount; }
}
