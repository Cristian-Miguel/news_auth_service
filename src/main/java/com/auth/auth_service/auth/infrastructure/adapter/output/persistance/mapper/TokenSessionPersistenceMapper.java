package com.auth.auth_service.auth.infrastructure.adapter.output.persistance.mapper;

import com.auth.auth_service.auth.domain.model.TokenSession;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.entity.TokenSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TokenSessionPersistenceMapper {

    @Mapping(source = "revoked", target = "isRevoked")
    TokenSessionEntity toTokenSessionEntity(TokenSession tokenSession);

    @Mapping(source = "revoked", target = "isRevoked")
    TokenSession toTokenSession(TokenSessionEntity tokenSessionEntity);

    List<TokenSession> toListTokenSession(List<TokenSessionEntity> tokenSessionEntities);

}
