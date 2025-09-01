package com.notesapi.NoteApp.dto;

public class ShareResponseDto {
    private String shareUrl;
    private String shareToken;

    public ShareResponseDto(String shareUrl, String shareToken) {
        this.shareUrl = shareUrl;
        this.shareToken = shareToken;
    }

    // Getters and Setters
    public String getShareUrl() { return shareUrl; }
    public void setShareUrl(String shareUrl) { this.shareUrl = shareUrl; }

    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }
}
