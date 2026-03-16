package example.web.securehome.service;

import example.web.securehome.dto.request.MemberRequestDto;
import example.web.securehome.dto.response.MemberResponseDto;
import example.web.securehome.entity.Home;
import example.web.securehome.entity.HomeMember;
import example.web.securehome.entity.User;
import example.web.securehome.exception.custom.*;
import example.web.securehome.mapper.MemberMapper;
import example.web.securehome.repository.HomeRepository;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public MemberResponseDto addMember(Long homeId, Long userId, MemberRequestDto memberRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new HomeNotFoundException(homeId));

        if (memberRepository.existsHomeMemberByUserIdAndHomeId(userId, homeId)) {
            throw new MemberAlreadyExistsException(userId, homeId);
        }

        HomeMember member = HomeMember.builder()
                .user(user)
                .home(home)
                .role(memberRequestDto.getRole())
                .build();
        return memberMapper.toMemberResponseDto(memberRepository.save(member));
    }

    @Transactional
    public void deleteMember(Long homeId, Long memberId) {
        HomeMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        if (!member.getHome().getId().equals(homeId)) {
            throw new MemberHomeMismatchException(homeId, memberId);
        }
        memberRepository.delete(member);
    }

}
