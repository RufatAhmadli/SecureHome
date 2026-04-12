package example.web.securehome.service;

import example.web.securehome.dto.request.MemberRequestDto;
import example.web.securehome.dto.request.UpdateMemberRoleRequestDto;
import example.web.securehome.dto.response.MemberResponseDto;
import example.web.securehome.entity.Home;
import example.web.securehome.entity.HomeMember;
import example.web.securehome.entity.User;
import example.web.securehome.enums.HomeMemberRole;
import example.web.securehome.exception.custom.*;
import example.web.securehome.mapper.MemberMapper;
import example.web.securehome.repository.HomeRepository;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.repository.UserRepository;
import example.web.securehome.event.MemberEvent;
import example.web.securehome.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final UserRepository userRepository;
    private final HomeRepository homeRepository;
    private final SecurityUtils securityUtils;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public MemberResponseDto findMemberById(Long homeId, Long memberId) {
        HomeMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        if (!member.getHome().getId().equals(homeId))
            throw new MemberHomeMismatchException(homeId, memberId);
        return memberMapper.toMemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findAllMembersByHomeId(Long homeId) {
        if (!homeRepository.existsById(homeId)) {
            throw new HomeNotFoundException(homeId);
        }
        return memberRepository
                .findAllByHomeId(homeId)
                .stream()
                .map(memberMapper::toMemberResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findAllMembersByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return memberRepository.findAllByUserId(userId)
                .stream()
                .map(memberMapper::toMemberResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findMyMemberships() {
        User currentUser = securityUtils.getCurrentUser();
        return memberRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(memberMapper::toMemberResponseDto)
                .toList();
    }

    @Transactional
    public MemberResponseDto updateMemberRole(Long homeId, Long memberId, UpdateMemberRoleRequestDto dto) {
        User currentUser = securityUtils.getCurrentUser();
        HomeMember requester = requireOwner(homeId, currentUser.getId());
        HomeMember target = resolveTarget(homeId, memberId);

        HomeMemberRole newRole = dto.getRole();

        if (newRole == HomeMemberRole.OWNER) {
            if (requester.getRole() != HomeMemberRole.OWNER) {
                throw new HomeAccessDeniedException("Only the owner can transfer ownership.");
            }
            requester.setRole(HomeMemberRole.ADMIN);
            memberRepository.save(requester);
        } else if (target.getRole() == HomeMemberRole.OWNER) {
            throw new HomeAccessDeniedException("Cannot demote the owner. Transfer ownership first.");
        }

        target.setRole(newRole);
        MemberResponseDto result = memberMapper.toMemberResponseDto(memberRepository.save(target));
        eventPublisher.publishEvent(new MemberEvent(
                currentUser.getEmail(), homeId, target.getUser().getEmail(), MemberEvent.Action.ROLE_CHANGED));
        return result;
    }

    @Transactional
    public MemberResponseDto addMember(Long homeId, MemberRequestDto memberRequestDto) {
        User currentUser = securityUtils.getCurrentUser();
        requireOwnerOrAdmin(homeId, currentUser.getId());

        if (memberRequestDto.getRole() == HomeMemberRole.OWNER) {
            throw new HomeAccessDeniedException("Cannot assign the owner role when adding a member.");
        }

        String email = memberRequestDto.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new HomeNotFoundException(homeId));

        if (memberRepository.existsHomeMemberByUserIdAndHomeId(user.getId(), homeId)) {
            throw new MemberAlreadyExistsException(user.getId(), homeId);
        }

        HomeMember member = HomeMember.builder()
                .user(user)
                .home(home)
                .role(memberRequestDto.getRole())
                .build();
        MemberResponseDto result = memberMapper.toMemberResponseDto(memberRepository.save(member));
        eventPublisher.publishEvent(new MemberEvent(
                currentUser.getEmail(), homeId, email, MemberEvent.Action.ADDED));
        return result;
    }

    @Transactional
    public void deleteMember(Long homeId, Long memberId) {
        User currentUser = securityUtils.getCurrentUser();
        requireOwner(homeId, currentUser.getId());
        HomeMember target = resolveTarget(homeId, memberId);

        if (target.getRole() == HomeMemberRole.OWNER) {
            throw new HomeAccessDeniedException("Cannot remove the owner. Transfer ownership first.");
        }

        String targetEmail = target.getUser().getEmail();
        memberRepository.delete(target);
        eventPublisher.publishEvent(new MemberEvent(
                currentUser.getEmail(), homeId, targetEmail, MemberEvent.Action.REMOVED));
    }

    private HomeMember requireOwner(Long homeId, Long userId) {
        HomeMember requester = memberRepository.findByHomeIdAndUserId(homeId, userId)
                .orElseThrow(HomeAccessDeniedException::new);
        if (requester.getRole() != HomeMemberRole.OWNER) {
            throw new HomeAccessDeniedException("Only the owner can perform this action.");
        }
        return requester;
    }

    private void requireOwnerOrAdmin(Long homeId, Long userId) {
        HomeMember requester = memberRepository.findByHomeIdAndUserId(homeId, userId)
                .orElseThrow(HomeAccessDeniedException::new);
        if (requester.getRole() != HomeMemberRole.OWNER && requester.getRole() != HomeMemberRole.ADMIN) {
            throw new HomeAccessDeniedException();
        }
    }

    private HomeMember resolveTarget(Long homeId, Long memberId) {
        HomeMember target = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        if (!target.getHome().getId().equals(homeId)) {
            throw new MemberHomeMismatchException(homeId, memberId);
        }
        return target;
    }

}
