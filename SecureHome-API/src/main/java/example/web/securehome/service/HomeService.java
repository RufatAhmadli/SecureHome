package example.web.securehome.service;

import example.web.securehome.dto.request.HomeRequestDto;
import example.web.securehome.dto.response.HomeResponseDto;
import example.web.securehome.entity.Home;
import example.web.securehome.entity.HomeMember;
import example.web.securehome.entity.User;
import example.web.securehome.enums.HomeMemberRole;
import example.web.securehome.exception.custom.HomeAccessDeniedException;
import example.web.securehome.exception.custom.HomeNameAlreadyExistsException;
import example.web.securehome.exception.custom.UnauthorizedException;
import example.web.securehome.event.HomeEvent;
import example.web.securehome.mapper.HomeMapper;
import example.web.securehome.repository.HomeRepository;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final HomeRepository homeRepository;
    private final HomeMapper homeMapper;
    private final SecurityUtils securityUtils;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public HomeResponseDto findHome(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        Home home = homeRepository.findByIdAndMembersUserId(id, currentUser.getId())
                .orElseThrow(HomeAccessDeniedException::new);
        return homeMapper.toHomeResponseDto(home);
    }

    @Transactional(readOnly = true)
    public List<HomeResponseDto> findUserAllHomes() {
        User currentUser = securityUtils.getCurrentUser();
        return homeRepository.findAllByMembersUserId(currentUser.getId())
                .stream()
                .map(homeMapper::toHomeResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HomeResponseDto> findAllHomes() {
        return homeRepository.findAll()
                .stream()
                .map(homeMapper::toHomeResponseDto)
                .toList();
    }

    @Transactional
    public HomeResponseDto createHome(HomeRequestDto homeRequestDto) {
        User currentUser = securityUtils.getCurrentUser();

        if(homeRepository.existsByNameAndMembersUserId(homeRequestDto.getName(), currentUser.getId())){
            throw new HomeNameAlreadyExistsException(homeRequestDto.getName());
        };

        Home saved = homeRepository.save(homeMapper.toHomeEntity(homeRequestDto));

        HomeMember homeMember = HomeMember.builder()
                .user(currentUser)
                .role(HomeMemberRole.OWNER)
                .home(saved)
                .build();
        memberRepository.save(homeMember);
        eventPublisher.publishEvent(new HomeEvent(
                currentUser.getEmail(), saved.getId(), saved.getName(), HomeEvent.Action.CREATED));
        return homeMapper.toHomeResponseDto(saved);
    }

    @Transactional
    public HomeResponseDto updateHome(Long id, HomeRequestDto homeRequestDto) {
        User currentUser = securityUtils.getCurrentUser();

        HomeMember member = memberRepository.findByHomeIdAndUserId(id, currentUser.getId())
                .orElseThrow(HomeAccessDeniedException::new);

        if (member.getRole() != HomeMemberRole.OWNER) {
            throw new UnauthorizedException("Only Owners can update home settings.");
        }

        Home found = member.getHome();
        homeMapper.updateHomeEntity(found, homeRequestDto);
        Home saved = homeRepository.save(found);
        eventPublisher.publishEvent(new HomeEvent(
                currentUser.getEmail(), saved.getId(), saved.getName(), HomeEvent.Action.UPDATED));
        return homeMapper.toHomeResponseDto(saved);
    }

    @Transactional
    public void deleteHome(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        HomeMember member = memberRepository.findByHomeIdAndUserId(id, currentUser.getId())
                .orElseThrow(HomeAccessDeniedException::new);

        if (member.getRole() != HomeMemberRole.OWNER) {
            throw new UnauthorizedException("Only Owners can delete home.");
        }

        eventPublisher.publishEvent(new HomeEvent(
                currentUser.getEmail(), id, member.getHome().getName(), HomeEvent.Action.DELETED));
        homeRepository.deleteById(id);
    }
}
