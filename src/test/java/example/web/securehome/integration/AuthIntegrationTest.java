package example.web.securehome.integration;

import example.web.securehome.entity.User;
import example.web.securehome.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Test case 1: Register and Login
 *
 * Execution Steps:
 *   1. Send POST /api/v1/auth/register with valid firstName, lastName, email, and password
 *   2. Verify the user record exists in the DB and the password is stored hashed
 *   3. Send POST /api/v1/auth/login with the same email and password
 *   4. Inspect the response body for a valid JWT token (non-empty "token" field)
 */
class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void register_returns201_andStoresHashedPassword() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "firstName": "John",
                            "lastName":  "Doe",
                            "email":     "john@test.com",
                            "password":  "Test@1234"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.firstName").value("John"));

        User saved = userRepository.findByEmail("john@test.com").orElseThrow();
        assertThat(saved.getEmail()).isEqualTo("john@test.com");
        assertThat(saved.getPassword()).isNotEqualTo("Test@1234");
        assertThat(passwordEncoder.matches("Test@1234", saved.getPassword())).isTrue();
    }

    @Test
    void login_returnsJwtToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "firstName": "Jane",
                            "lastName":  "Doe",
                            "email":     "jane@test.com",
                            "password":  "Test@1234"
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email":    "jane@test.com",
                            "password": "Test@1234"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("jane@test.com"));
    }
}
