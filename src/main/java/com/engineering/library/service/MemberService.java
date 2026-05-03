package com.engineering.library.service;

import com.engineering.library.dto.MemberRequestDto;
import com.engineering.library.dto.MemberResponseDto;
import com.engineering.library.entity.Member;
import com.engineering.library.exception.DuplicateResourceException;
import com.engineering.library.exception.MemberNotFoundException;
import com.engineering.library.mapper.MemberMapper;
import com.engineering.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper     memberMapper;

    @Transactional
    public MemberResponseDto registerMember(MemberRequestDto request) {
        if (memberRepository.existsByRollNumber(request.getRollNumber())) {
            throw new DuplicateResourceException(
                "A member with roll number " + request.getRollNumber() + " already exists."
            );
        }
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                "A member with email " + request.getEmail() + " already exists."
            );
        }

        Member member = memberMapper.toEntity(request);
        if (request.getRole() != null) member.setRole(request.getRole());

        Member saved = memberRepository.save(member);
        log.info("Registered member [id={}] roll={}", saved.getId(), saved.getRollNumber());
        return memberMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto getMemberById(Long id) {
        return memberMapper.toResponseDto(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<MemberResponseDto> listAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(memberMapper::toResponseDto);
    }

    @Transactional
    public MemberResponseDto updateMember(Long id, MemberRequestDto request) {
        Member existing = findOrThrow(id);
        memberMapper.updateEntityFromDto(request, existing);
        Member saved = memberRepository.save(existing);
        return memberMapper.toResponseDto(saved);
    }

    @Transactional
    public void suspendMember(Long id) {
        Member member = findOrThrow(id);
        member.setStatus(Member.MemberStatus.SUSPENDED);
        memberRepository.save(member);
        log.info("Suspended member [id={}]", id);
    }

    private Member findOrThrow(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new MemberNotFoundException(id));
    }
}
