package example.web.securehome.controller;

import example.web.securehome.dto.request.MemberRequestDto;
import example.web.securehome.dto.response.MemberResponseDto;
import example.web.securehome.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/homes")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/memberships")
    public ResponseEntity<List<MemberResponseDto>> getMyMemberships() {
        return ResponseEntity.ok(memberService.findMyMemberships());
    }

    @GetMapping("/{homeId}/members/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable Long homeId, @PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.findMemberById(homeId, memberId));
    }

    @GetMapping("/{homeId}/members")
    public ResponseEntity<List<MemberResponseDto>> getMembersByHomeId(@PathVariable Long homeId) {
        return ResponseEntity.ok(memberService.findAllMembersByHomeId(homeId));
    }

    @PostMapping("/{homeId}/members/{userId}")
    public ResponseEntity<MemberResponseDto> createMember(@PathVariable Long homeId, @PathVariable Long userId, @Valid @RequestBody MemberRequestDto memberRequestDto) {
        return new ResponseEntity<>(memberService.addMember(homeId, userId, memberRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{homeId}/members/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMemberRole(@PathVariable Long homeId, @PathVariable Long memberId, @Valid @RequestBody MemberRequestDto memberRequestDto) {
        return ResponseEntity.ok(memberService.updateMemberRole(homeId, memberId, memberRequestDto));
    }

    @DeleteMapping("/{homeId}/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long homeId, @PathVariable Long memberId) {
        memberService.deleteMember(homeId, memberId);
        return ResponseEntity.noContent().build();
    }
}
