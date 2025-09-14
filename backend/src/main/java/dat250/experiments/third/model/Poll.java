package dat250.experiments.third.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Poll {

    private int id;
    private String question;
    @JsonIdentityReference(alwaysAsId = true)
    private User creator;
    private Instant publishedAt;
    private Instant validUntil;

    private List<VoteOption> options = new ArrayList<>();

    public Poll(int id, String question, User creator,  Instant validUntil, List<VoteOption> options) {
        this.question = question;
        this.id = id;
        if(creator != null) {
            this.creator = creator;
            creator.getMyPolls().add(this);
        }
        this.publishedAt = Instant.now();
        this.validUntil = validUntil;
        int i = 1;
        for(VoteOption option : options) {
            option.setPresentationOrder(i);
            this.options.add(option);
            i++;
        }
    }

    public Poll(){
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<VoteOption> getOptions() {
        return options;
    }

    public VoteOption getOption(int index){
        return options.get(index-1);
    }

    public void addVoteOption(VoteOption voteOption) {
        if (voteOption != null) {
            voteOption.setPresentationOrder(options.size());
            this.options.add(voteOption);
        }
    }

    public void setOptions(List<VoteOption> options) {
        this.options = options;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
        this.creator.getMyPolls().add(this);
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }
}
