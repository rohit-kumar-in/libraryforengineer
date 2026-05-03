package com.engineering.library.mapper;

import com.engineering.library.dto.MemberRequestDto;
import com.engineering.library.dto.MemberResponseDto;
import com.engineering.library.entity.Member;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-03T23:21:10+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member toEntity(MemberRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.email( dto.getEmail() );
        member.fullName( dto.getFullName() );
        member.role( dto.getRole() );
        member.rollNumber( dto.getRollNumber() );

        return member.build();
    }

    @Override
    public MemberResponseDto toResponseDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberResponseDto.MemberResponseDtoBuilder memberResponseDto = MemberResponseDto.builder();

        memberResponseDto.activeBorrowCount( member.getActiveBorrowCount() );
        memberResponseDto.createdAt( member.getCreatedAt() );
        memberResponseDto.email( member.getEmail() );
        memberResponseDto.fullName( member.getFullName() );
        memberResponseDto.id( member.getId() );
        memberResponseDto.role( member.getRole() );
        memberResponseDto.rollNumber( member.getRollNumber() );
        memberResponseDto.status( member.getStatus() );
        memberResponseDto.updatedAt( member.getUpdatedAt() );

        return memberResponseDto.build();
    }

    @Override
    public void updateEntityFromDto(MemberRequestDto dto, Member member) {
        if ( dto == null ) {
            return;
        }

        member.setEmail( dto.getEmail() );
        member.setFullName( dto.getFullName() );
        member.setRole( dto.getRole() );
        member.setRollNumber( dto.getRollNumber() );
    }
}
