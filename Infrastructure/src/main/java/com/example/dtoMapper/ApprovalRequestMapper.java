package com.example.dtoMapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",uses{ApprovalMapper.class})
public interface ApprovalRequestMapper {

    ApprovalRequestMapper INSTANCE = Mappers.getMapper(ApprovalRequestMapper.class);
}
