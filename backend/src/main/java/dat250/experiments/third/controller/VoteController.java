package dat250.experiments.third.controller;

import dat250.experiments.third.PollService;
import dat250.experiments.third.model.Poll;
import dat250.experiments.third.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collection;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/votes")
public class VoteController {
    private final PollService pollService;

    public VoteController(@Autowired PollService pollService) {
        this.pollService = pollService;
    }

    // Create an anonymous vote
    @PostMapping("/{pollId}/{option}")
    public Vote createVote(@PathVariable int pollId,  @PathVariable int option) {
        Poll poll = pollService.getPoll(pollId);
        if(poll == null){ //if poll does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        if(poll.getValidUntil().isBefore(Instant.now())){ // if vote is not on time
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poll has expired");
        }
        if(poll.getOption(option)==null){ // if option does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Option not found");
        }
        return pollService.addVote(null, pollId, option);
    }

    // Create a vote
    @PostMapping("/{pollId}/{option}/{userName}")
    public Vote createUserVote(@PathVariable int pollId, @PathVariable int option ,@PathVariable String userName) {
        if(pollService.getUser(userName) == null){ // if user does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        Poll poll = pollService.getPoll(pollId);
        if(poll==null){ //if poll does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        if(poll.getValidUntil().isBefore(Instant.now())){ // if vote is not on time
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poll has expired");
        }
        if(poll.getOption(option)==null){ // if option does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Option not found");
        }
        if(pollService.getVotes(pollId).stream().anyMatch(v -> v.getUser().getUsername().equals(userName))){
            // if a user has already vote
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already voted");
        }
        return pollService.addVote(userName, pollId, option);
    }

    // Modify a vote by id
    @PutMapping("/{pollId}/{voteId}/{optionId}")
    public Vote updateVote(@PathVariable Integer pollId, @PathVariable Long voteId,@PathVariable Integer optionId) {
        Poll poll = pollService.getPoll(pollId);
        if(poll==null){ //if poll does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        if(poll.getValidUntil().isBefore(Instant.now())){ // if vote is not on time
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poll has expired");
        }
        if(poll.getOption(optionId)==null){ // if option does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Option not found");
        }
        if (this.pollService.getVote(voteId, pollId) == null) { //if the vote does not exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote not found");
        }
        return this.pollService.updateVote(optionId, voteId, pollId);
    }

    // Obtain all votes
    @GetMapping("/{pollId}")
    public Collection<Vote> getVotes(@PathVariable Integer pollId) {
        if (pollService.getPoll(pollId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return pollService.getVotes(pollId);
    }

    // Obtain a vote by id
    @GetMapping("/{pollId}/{voteId}")
    public Vote getPoll(@PathVariable Integer pollId, @PathVariable Long voteId) {
        if (pollService.getPoll(pollId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return pollService.getVote(voteId, pollId);
    }

}
