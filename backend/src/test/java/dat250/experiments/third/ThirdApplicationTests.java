package dat250.experiments.third;

import dat250.experiments.third.model.Poll;
import dat250.experiments.third.model.User;
import dat250.experiments.third.model.Vote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ThirdApplicationIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void completePollWorkflowTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ### 1 Create user Alice
        String aliceJson = """
        {
            "username": "alice",
            "email": "alice@example.com"
        }
        """;
        ResponseEntity<User> createAliceResponse = restTemplate.exchange(
                "/users", HttpMethod.POST, new HttpEntity<>(aliceJson, headers), User.class);

        assertEquals(HttpStatus.OK, createAliceResponse.getStatusCode());
        assertNotNull(createAliceResponse.getBody());

        // ### 2 Create user Marco
        String marcoJson = """
        {
            "username": "marco",
            "email": "marco@example.com"
        }
        """;
        ResponseEntity<User> createMarcoResponse = restTemplate.exchange(
                "/users", HttpMethod.POST, new HttpEntity<>(marcoJson, headers), User.class);

        assertEquals(HttpStatus.OK, createMarcoResponse.getStatusCode());

        // ### 3 Alice creates a Poll
        String pollJson = """
        {
            "question": "What is your favourite animal?",
            "validUntil": "%s",
            "options": [
                {"caption": "Giraffe", "presentationOrder": 1},
                {"caption": "Snake", "presentationOrder": 2}
            ]
        }
        """.formatted(Instant.now().plus(365, ChronoUnit.DAYS).toString());

        ResponseEntity<Poll> createPollResponse = restTemplate.exchange(
                "/polls/alice", HttpMethod.POST, new HttpEntity<>(pollJson, headers), Poll.class);

        System.out.println("Poll created: " + createPollResponse.getBody());

        assertEquals(HttpStatus.OK, createPollResponse.getStatusCode());
        assertNotNull(createPollResponse.getBody());

        int pollId = createPollResponse.getBody().getId();
        System.out.println("Poll ID received: " + pollId);
        assertTrue(pollId > 0, "Poll ID should be positive");

        // ### 4 List polls
        ResponseEntity<List> listPollsResponse = restTemplate.getForEntity("/polls", List.class);
        System.out.println("Poll list: " + listPollsResponse.getBody());

        assertEquals(HttpStatus.OK, listPollsResponse.getStatusCode());
        assertNotNull(listPollsResponse.getBody());
        assertEquals(1, listPollsResponse.getBody().size(), "There should be exactly 1 poll");

        // ### 5 Marco votes for option 1
        ResponseEntity<Vote> userVoteResponse = restTemplate.postForEntity(
                "/votes/" + pollId + "/1/marco", null, Vote.class);

        assertEquals(HttpStatus.OK, userVoteResponse.getStatusCode());
        assertNotNull(userVoteResponse.getBody());

        // ### 6 Anonymous user votes for option 1
        ResponseEntity<String> anonymousVoteResponse = restTemplate.exchange(
                "/votes/" + pollId + "/1",
                HttpMethod.POST,
                HttpEntity.EMPTY,
                String.class);

        assertEquals(HttpStatus.OK, anonymousVoteResponse.getStatusCode());
        System.out.println("Anonymous vote raw: " + anonymousVoteResponse.getBody());


        // ### 7 Marco changes his vote to option 2
        ResponseEntity<Vote[]> votesResponse = restTemplate.getForEntity(
                "/votes/" + pollId, Vote[].class);

        assertEquals(HttpStatus.OK, votesResponse.getStatusCode());
        assertNotNull(votesResponse.getBody());

        ResponseEntity<Vote> updateVoteResponse = restTemplate.exchange(
                "/votes/" + pollId + "/" + 1 + "/2",
                HttpMethod.PUT, new HttpEntity<>(headers), Vote.class);

        assertEquals(HttpStatus.OK, updateVoteResponse.getStatusCode());
        assertNotNull(updateVoteResponse.getBody());

        // ### 8 List final votes
        ResponseEntity<Vote[]> finalVotesResponse = restTemplate.getForEntity(
                "/votes/" + pollId, Vote[].class);

        assertEquals(HttpStatus.OK, finalVotesResponse.getStatusCode());
        assertNotNull(finalVotesResponse.getBody());
        System.out.println("Final votes: " + Arrays.toString(finalVotesResponse.getBody()));

        // ### 9 Delete the poll
        restTemplate.delete("/polls/" + pollId);

        // ### 10 Listing votes from deleted poll should return 4xx
        ResponseEntity<String> deletedVotesResponse = restTemplate.getForEntity(
                "/votes/" + pollId, String.class);

        assertTrue(deletedVotesResponse.getStatusCode().is4xxClientError());
    }


    @Test
    void testUserCreation() {
        // Test básico de creación de usuario
        String userJson = """
        {
            "username": "testuser",
            "email": "test@example.com"
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(userJson, headers);

        ResponseEntity<User> response = restTemplate.exchange(
                "/users", HttpMethod.POST, request, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    void testPollCreation() {
        // Primero crear usuario
        String userJson = """
        {
            "username": "pollcreator",
            "email": "creator@example.com"
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> userRequest = new HttpEntity<>(userJson, headers);

        restTemplate.exchange("/users", HttpMethod.POST, userRequest, User.class);

        // Crear poll simple
        String pollJson = """
        {
            "question": "Simple test?",
            "validUntil": "%s"
        }
        """.formatted(Instant.now().plus(1, ChronoUnit.DAYS).toString());

        HttpEntity<String> pollRequest = new HttpEntity<>(pollJson, headers);

        ResponseEntity<Poll> response = restTemplate.exchange(
                "/polls/pollcreator", HttpMethod.POST, pollRequest, Poll.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getId() > 0);
    }
}