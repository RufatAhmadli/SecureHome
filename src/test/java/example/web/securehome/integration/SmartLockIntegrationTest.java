package example.web.securehome.integration;

import example.web.securehome.entity.SmartLock;
import example.web.securehome.enums.LockStatus;
import example.web.securehome.repository.SmartLockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test case 3: Register Device and Send Lock Command
 * Execution Steps:
 * 1. Send POST /api/v1/smart-locks with owner JWT and homeId to register a new smart lock
 * 2. Confirm 201 response and record the device ID from the response body
 * 3. Send PATCH /api/v1/smart-locks/{id}/lock with owner JWT to send the lock command
 * 4. Verify 200 response with lockStatus LOCKED and confirm the state in DB
 */
class SmartLockIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SmartLockRepository smartLockRepository;

    @Test
    void registerSmartLock_andLock_updatesStatusToLocked() throws Exception {
        // prerequisite: owner registers, creates a home
        String ownerToken = registerAndLogin("Carol", "White", "carol@test.com", "Test@1234");

        String homeResponse = mockMvc.perform(post("/api/v1/homes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken)
                        .content("""
                                {
                                    "name": "Carol's Home"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long homeId = objectMapper.readTree(homeResponse).get("id").asLong();

        // Step 1 & 2: register a smart lock device
        String lockResponse = mockMvc.perform(post("/api/v1/smart-locks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken)
                        .content("""
                                {
                                    "deviceName":           "front-door-lock",
                                    "displayName":          "Front Door Lock",
                                    "protocol":             "MQTT",
                                    "homeId":               %d,
                                    "autoLock":             true,
                                    "autoLockDelaySeconds": 30,
                                    "tamperAlert":          true
                                }
                                """.formatted(homeId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.deviceName").value("front-door-lock"))
                .andExpect(jsonPath("$.lockStatus").value("LOCKED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long deviceId = objectMapper.readTree(lockResponse).get("id").asLong();

        // Step 3 & 4: send lock command, verify response and DB state
        mockMvc.perform(patch("/api/v1/smart-locks/" + deviceId + "/unlock")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deviceId))
                .andExpect(jsonPath("$.lockStatus").value("UNLOCKED"));

        SmartLock saved = smartLockRepository.findById(deviceId).orElseThrow();
        assertThat(saved.getLockStatus()).isEqualTo(LockStatus.UNLOCKED);

    }
}
