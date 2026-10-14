package com.max.auth.services;

import com.max.auth.dto.LoginRequest;
import com.max.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}
