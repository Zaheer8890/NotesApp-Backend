package com.notesapi.NoteApp.dto;

public class AuthResponseDto {
    private String accessToken;
    private String tokenType;
    private UserDto user;

    public AuthResponseDto(String accessToken, String tokenType, UserDto user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.user = user;
    }

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }
}
