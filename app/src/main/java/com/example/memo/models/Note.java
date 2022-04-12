package com.example.memo.models;

public class Note {
    private final String id;
    private String uid;
    private String title;
    private String type;
    private String content;
    private String createdAt;
    private boolean isPinned;
    private boolean isRemoved;

    public Note(String id, String uid, String title, String type, String content,
//                String createdAt,
                boolean isPinned, boolean isRemoved) {
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.type = type;
        this.content = content;
        this.createdAt = createdAt;
        this.isPinned = isPinned;
        this.isRemoved = isRemoved;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }
}
