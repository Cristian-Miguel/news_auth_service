package com.auth.auth_service.auth.infrastructure.adapter.output.persistance.mapper;

import com.auth.auth_service.auth.domain.model.TokenSession;
import com.auth.auth_service.auth.infrastructure.adapter.output.persistance.entity.TokenSessionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TokenSessionPersistenceMapper {

    TokenSessionEntity toTokenSessionEntity(TokenSession tokenSession);

    TokenSession toTokenSession(TokenSessionEntity tokenSessionEntity);

    List<TokenSession> toListTokenSession(List<TokenSessionEntity> tokenSessionEntities);

}
