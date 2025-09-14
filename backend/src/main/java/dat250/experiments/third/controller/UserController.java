package dat250.experiments.third.controller;

import dat250.experiments.third.PollService;
import dat250.experiments.third.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/users")
public class UserController {
    private final PollService pollService;

    public UserController(@Autowired PollService pollService){
        this.pollService = pollService;
    }

    // Create a User
    @PostMapping
    public User createUser(@RequestBody User user){
        if (pollService.getUser(user.getUsername()) != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User already exists");
        }
        return pollService.addUser(user);
    }

    // List of users
    @GetMapping
    public List<User> getUsers() {
        return pollService.getUsers();
    }

    // Obtain id user
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id){
        if (pollService.getUser(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return pollService.getUser(id);
    }

    // Delete id user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id){
        if (pollService.getUser(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        pollService.deleteUser(id);
    }
}
