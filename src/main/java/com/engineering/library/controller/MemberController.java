package com.engineering.library.controller;

import com.engineering.library.dto.MemberRequestDto;
import com.engineering.library.dto.MemberResponseDto;
import com.engineering.library.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Manage library members (students, faculty, staff)")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "Register a new library member")
    public ResponseEntity<MemberResponseDto> register(
            @Valid @RequestBody MemberRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(memberService.registerMember(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member details by ID")
    public ResponseEntity<MemberResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @GetMapping
    @Operation(summary = "List all members (paginated)")
    public ResponseEntity<Page<MemberResponseDto>> listAll(
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(memberService.listAllMembers(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a member's profile")
    public ResponseEntity<MemberResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequestDto request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    @PatchMapping("/{id}/suspend")
    @Operation(summary = "Suspend a member's library privileges")
    public ResponseEntity<Void> suspend(@PathVariable Long id) {
        memberService.suspendMember(id);
        return ResponseEntity.noContent().build();
    }
}
