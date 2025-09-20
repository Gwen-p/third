package dat250.experiments.third.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "username")
public class User {

    private String username;
    private String email;
    private List<Poll> myPolls = new ArrayList<>();
    private Collection<Vote> myVotes  = new HashSet<>();
    private Long id;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Poll> getMyPolls() {
        return myPolls;
    }

    public Collection<Vote> getMyVotes() {
        return myVotes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Creates a new Poll object for this user
     * with the given poll question
     * and returns it.
     */
    public Poll createPoll(String question) {
        Poll poll = new Poll();
        poll.setQuestion(question);
        poll.setCreator(this);
        poll.setPublishedAt(Instant.now());
        poll.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));
        getMyPolls().add(poll);
        return poll;
    }

    /**
     * Creates a new Vote for a given VoteOption in a Poll
     * and returns the Vote as an object.
     */
    public Vote voteFor(VoteOption option) {
        return new Vote(this, option);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
