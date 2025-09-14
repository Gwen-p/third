package dat250.experiments.third.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Vote {

    @JsonIdentityReference(alwaysAsId = true)
    private User user;
    @JsonIdentityReference(alwaysAsId = true)
    private VoteOption option;
    private Long id = 0L;
    private Instant publishedAt;

    public Vote(User user, VoteOption option) {
        this.user = user;
        this.option = option;
        this.publishedAt = Instant.now();
        if(user != null) {  // there could be anonymous votes
            this.user.getMyVotes().add(this);
        }
        this.option.getVotes().add(this);
    }

    public Vote(){}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if(user != null) {
            this.user.getMyVotes().add(this);
        }
    }

    public VoteOption getOption() {
        return option;
    }

    public void setOption(VoteOption option) {
        if(option != null) {
            this.option.getVotes().remove(this);
            this.option = option;
            setPublishedAt(Instant.now());
            this.option.getVotes().add(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
}
