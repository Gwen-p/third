package dat250.experiments.third.controller;

import dat250.experiments.third.PollService;
import dat250.experiments.third.model.Poll;
import dat250.experiments.third.model.VoteOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/polls")
public class PollController {
    private final PollService pollService;

    public PollController(@Autowired PollService pollService) {
        this.pollService = pollService;
    }

    // Create a poll
    @PostMapping("/{userId}")
    public Poll createPoll(@RequestBody Poll poll, @PathVariable String userId) {
        if (pollService.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return pollService.addPoll(poll, userId);
    }

    // Add an option to a poll
    @PostMapping("/{pollId}/option")
    public VoteOption addVoteOptions(@RequestBody VoteOption option, @PathVariable Integer pollId) {
        if (pollService.getPoll(pollId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return pollService.addVoteOption(pollId, option);
    }

    // Get all the polls
    @GetMapping
    public List<Poll> getPolls() {
        return pollService.getPolls();
    }

    // Obtain a poll by id
    @GetMapping("/{pollId}")
    public Poll getPoll(@PathVariable Integer pollId) {
        if (pollService.getPoll(pollId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return pollService.getPoll(pollId);
    }

    // Delete a poll by id
    @DeleteMapping("/{pollId}")
    public void deletePoll(@PathVariable Integer pollId) {
        if (pollService.getPoll(pollId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        pollService.deletePoll(pollId);
    }

}
