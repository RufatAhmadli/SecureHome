package example.web.securehome.service;

import example.web.securehome.dto.response.ActivityLogResponseDto;
import example.web.securehome.entity.ActivityLog;
import example.web.securehome.entity.HomeMember;
import example.web.securehome.entity.User;
import example.web.securehome.enums.HomeMemberRole;
import example.web.securehome.exception.custom.HomeAccessDeniedException;
import example.web.securehome.repository.ActivityLogRepository;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<ActivityLogResponseDto> getHomeActivityLogs(Long homeId) {
        User currentUser = securityUtils.getCurrentUser();
        HomeMember member = memberRepository.findByHomeIdAndUserId(homeId, currentUser.getId())
                .orElseThrow(HomeAccessDeniedException::new);
        if (member.getRole() != HomeMemberRole.OWNER && member.getRole() != HomeMemberRole.ADMIN) {
            throw new HomeAccessDeniedException("Only Owners and Admins can view activity logs.");
        }
        return activityLogRepository.findAllByHomeIdOrderByOccurredAtDesc(homeId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ActivityLogResponseDto toDto(ActivityLog log) {
        return ActivityLogResponseDto.builder()
                .id(log.getId())
                .actorEmail(log.getActorEmail())
                .category(log.getCategory())
                .action(log.getAction())
                .description(log.getDescription())
                .homeId(log.getHomeId())
                .occurredAt(log.getOccurredAt())
                .build();
    }
}
