package example.web.securehome.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test case 2: Create Home and Add Member
 * Execution Steps:
 * 1. Send POST /api/v1/homes with owner JWT to create a home
 * 2. Confirm 201 response and record the home ID from the response body
 * 3. Send POST /api/v1/homes/{homeId}/members/addMember with second user email and ADMIN role
 * 4. Verify 201 response, membership role is ADMIN, and userEmail matches the invited user
 */
class HomeIntegrationTest extends BaseIntegrationTest {

    @Test
    void createHome_andAddMember_asMember_returnsCorrectRoleAndEmail() throws Exception {
        // Step 1 & 2: register owner, create home, record home ID
        String ownerToken = registerAndLogin("Alice", "Smith", "alice@test.com", "Test@1234");

        String homeResponse = mockMvc.perform(post("/api/v1/homes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken)
                        .content("""
                                {
                                    "name": "Alice's Home"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Alice's Home"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long homeId = objectMapper.readTree(homeResponse).get("id").asLong();

        // Step 3 & 4: register second user, add as ADMIN member
        registerAndLogin("Bob", "Jones", "bob@test.com", "Test@1234");

        mockMvc.perform(post("/api/v1/homes/" + homeId + "/members/addMember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken)
                        .content("""
                                {
                                    "email": "bob@test.com",
                                    "role":  "ADMIN"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.userEmail").value("bob@test.com"))
                .andExpect(jsonPath("$.homeId").value(homeId));
    }
}
