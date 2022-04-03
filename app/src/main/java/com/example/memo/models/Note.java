package com.example.memo.models;

import java.time.LocalDateTime;

public class Note {
    private String id;
    private String title;
    private String type;
    private String content;
    private LocalDateTime createdAt;
    private boolean isPinned;

    public Note(String id, String title, String type, String content, LocalDateTime createdAt, boolean isPinned) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.content = content;
        this.createdAt = createdAt;
        this.isPinned = isPinned;
    }

    public String getId() {
        return id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}
