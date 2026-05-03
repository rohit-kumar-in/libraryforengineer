package com.engineering.library.mapper;

import com.engineering.library.dto.MemberRequestDto;
import com.engineering.library.dto.MemberResponseDto;
import com.engineering.library.entity.Member;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "status",           ignore = true)
    @Mapping(target = "activeBorrowCount",ignore = true)
    @Mapping(target = "borrowRecords",    ignore = true)
    @Mapping(target = "createdAt",        ignore = true)
    @Mapping(target = "updatedAt",        ignore = true)
    Member toEntity(MemberRequestDto dto);

    MemberResponseDto toResponseDto(Member member);

    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "status",           ignore = true)
    @Mapping(target = "activeBorrowCount",ignore = true)
    @Mapping(target = "borrowRecords",    ignore = true)
    @Mapping(target = "createdAt",        ignore = true)
    @Mapping(target = "updatedAt",        ignore = true)
    void updateEntityFromDto(MemberRequestDto dto, @MappingTarget Member member);
}
