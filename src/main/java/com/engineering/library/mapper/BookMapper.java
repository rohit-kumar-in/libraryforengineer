package com.engineering.library.mapper;

import com.engineering.library.dto.BookRequestDto;
import com.engineering.library.dto.BookResponseDto;
import com.engineering.library.entity.Book;
import org.mapstruct.*;

/**
 * MapStruct mapper between {@link Book} entity and its DTOs.
 *
 * <p>Spring component model is set globally via the compiler arg
 * {@code -Amapstruct.defaultComponentModel=spring}, so no explicit
 * {@code componentModel} is needed here.</p>
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    /**
     * Maps a request DTO to a new {@link Book} entity.
     * {@code id}, {@code createdAt}, {@code updatedAt}, and availability
     * fields are intentionally ignored — they are managed by the service layer.
     */
    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "availableCopies",  ignore = true)
    @Mapping(target = "isAvailable",      ignore = true)
    @Mapping(target = "borrowRecords",    ignore = true)
    @Mapping(target = "createdAt",        ignore = true)
    @Mapping(target = "updatedAt",        ignore = true)
    Book toEntity(BookRequestDto dto);

    /** Maps a persisted {@link Book} entity to its public response DTO. */
    BookResponseDto toResponseDto(Book book);

    /**
     * Updates an existing entity from a request DTO.
     * Fields excluded here should not be overwritten by client input.
     */
    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "availableCopies",  ignore = true)
    @Mapping(target = "isAvailable",      ignore = true)
    @Mapping(target = "borrowRecords",    ignore = true)
    @Mapping(target = "createdAt",        ignore = true)
    @Mapping(target = "updatedAt",        ignore = true)
    void updateEntityFromDto(BookRequestDto dto, @MappingTarget Book book);
}
