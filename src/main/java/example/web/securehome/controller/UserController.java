package example.web.securehome.controller;

import example.web.securehome.dto.response.MemberResponseDto;
import example.web.securehome.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final MemberService memberService;

    @GetMapping("/{userId}/memberships")
    public ResponseEntity<List<MemberResponseDto>> getUserMemberships(
            @PathVariable Long userId) {
        return ResponseEntity.ok(memberService.findAllMembersByUserId(userId));
    }
}
