package dat250.experiments.third;

import dat250.experiments.third.model.Poll;
import dat250.experiments.third.model.User;
import dat250.experiments.third.model.Vote;
import dat250.experiments.third.model.VoteOption;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PollService {
    private final Map<String, User> users;
    private final Map<Integer, Poll> polls;
    private int pollsId =1;
    private Long votesId =1L;


    public PollService(){
        users = new HashMap<>();
        polls = new HashMap<>();
    }

  // Users -------------------------------------------------------------
    // Add a User
    public User addUser(User user){
        users.put(user.getUsername(), user);
        return user;
    }

    // List of users
    public List<User> getUsers(){
        return new ArrayList<>(users.values());
    }

    // Obtain id user
    public User getUser(String username){
        return users.get(username);
    }

    // Delete id user
    public void deleteUser(String username){
        users.remove(username);
    }

  // Polls -------------------------------------------------------------
    // Create a poll
    public Poll addPoll(Poll poll, String username){
        poll.setCreator(getUser(username));
        poll.setPublishedAt(Instant.now());
        poll.setId(pollsId);
        pollsId++;
        polls.put(poll.getId(), poll);
        return poll;
    }

    // Add an option to a poll
    public VoteOption addVoteOption(int pollId, VoteOption voteOption){
        getPoll(pollId).addVoteOption(voteOption);
        return voteOption;
    }

    // Get all the polls
    public List<Poll> getPolls(){
        return new ArrayList<>(polls.values());
    }

    // Obtain a poll by id
    public Poll getPoll(int id){
        return polls.get(id);
    }

    // Delete a poll by id
    public void  deletePoll(int id){
        getPoll(id).getCreator().getMyPolls().remove(getPoll(id));
        polls.remove(id);
    }

  // Votes --------------------------------------------------------------
    // Add a vote
    public Vote addVote(String userId, int pollId, int optionId){
        Vote vote;
        if(userId == null){
            vote = new Vote(null, getPoll(pollId).getOption(optionId));
        }else {
            vote = getUser(userId).voteFor(getPoll(pollId).getOption(optionId));
        }
        vote.setId(votesId);
        votesId++;
        return vote;
    }

    // Obtain all votes
    public List<Vote> getVotes(int pollId){
        List<Vote> allVotes = new ArrayList<>();
        for (VoteOption voteOP:getPoll(pollId).getOptions()){
            allVotes.addAll(voteOP.getVotes());
        }
        return allVotes;
    }

    // Obtain a vote by id
    public Vote getVote(Long id, int pollId){
        return getVotes(pollId).stream()
                .filter(vote -> vote.getId().equals(id))
                .findFirst().orElse(null);
    }

    public Vote updateVote(Integer optionId, Long id, Integer pollId) {
        Vote vote = getVote(id, pollId);
        vote.setOption(getPoll(pollId).getOption(optionId));
        return vote;
    }
}
