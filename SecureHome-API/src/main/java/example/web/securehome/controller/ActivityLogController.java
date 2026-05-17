package example.web.securehome.controller;

import example.web.securehome.dto.response.ActivityLogResponseDto;
import example.web.securehome.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping("/homes/{homeId}/activity-logs")
    public ResponseEntity<List<ActivityLogResponseDto>> getHomeActivityLogs(@PathVariable Long homeId) {
        return ResponseEntity.ok(activityLogService.getHomeActivityLogs(homeId));
    }
}
