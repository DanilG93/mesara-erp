package com.mesara.app.dto;

public record AuthenticationRequest(
        String username,
        String password) {
}
