package com.auth.auth_service.repository;

import com.auth.auth_service.model.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, String> {
}
