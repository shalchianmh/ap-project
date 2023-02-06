package edu.sharif.project1.user;

import edu.sharif.project1.ticket.Ticket;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * this class controls user actions, except of register and login methods, other methods check the login process
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;


    /**
     * this method will sends a user when you give his id, it can also be null
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable(value = "id") final Integer id) {
        Optional<User> user = userRepository.findById(id);
        return user;
    }


    /**
     * creates a new user, you have to put the new user in a json and send it by a post request
     * @param user
     * @return a string shows the result
     */
    @PostMapping("/create")
    public String creatUser(@RequestBody final User user) {
        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();
        for (User user1 : users) {
            if (user1.getUserName().equals(user.getUserName())) {
                return "user name is used before";
            }
        }
        user.setRemainingCharge(0);
        user.setLoggedIn(false);
        User savedUser = userRepository.save(user);
        return "successful";
    }

    /**
     * this method controls user login, which avoids login when you have logged in before, and makes the logged in true
     * @param formData
     * @return a string shows the result
     */
    @PostMapping("/login")
    public String login(@RequestBody MultiValueMap<String, String> formData){
        String userName = formData.getFirst("user name");
        String password = formData.getFirst("password");
        User user = null;
        for (User user1 : userRepository.findAll()) {
            if (user1.getUserName().equals(userName)){
                user = user1;
                break;
            }
        }
        if (user == null) return "there is no user with this user name";
        if (!user.getPassword().equals(password)) return "incorrect password";
        if (user.isLoggedIn()) return "you have logged in before";
        user.setLoggedIn(true);
        userRepository.save(user);
        return "logged in successful, use your id for requests: " + String.valueOf(user.getId());
    }

    /**
     * this method makes the user logged out, only logged in admin can log out
     * @param userId
     * @return a string shows the result
     */
    @GetMapping("logout/{user_id}")
    public String logout(@PathVariable(value = "user_id") final Integer userId){
        if (userRepository.findById(userId).isEmpty()) return "incorrect id";
        User user = userRepository.findById(userId).get();
        if (!user.isLoggedIn()) return "you didn't have logg in before";
        user.setLoggedIn(false);
        userRepository.save(user);
        return "logged out successful";
    }

    /**
     * if there is a problem, exception is thrown
     * @param userId
     * @return all tickets that belong to user
     * @throws Exception
     */
    @GetMapping("/get_tickets/{user_id}")
    public Set<Ticket> getUserTickets(@PathVariable(value = "user_id") final Integer userId) throws Exception {
        if (userRepository.findById(userId).isEmpty()) throw new Exception();
        User user = userRepository.findById(userId).get();
        if (!user.isLoggedIn()) throw new Exception();
        return user.getTickets();
    }

    //profile

    /**
     * profile part
     * this method is used to charge account for users
     * @param formData
     * @param userId
     * @return a string shows the result
     */
    @PostMapping("/{user_id}/profile/charge")
    public String chargeAccount(@RequestBody MultiValueMap<String, String> formData,
                                @PathVariable(value = "user_id") final Integer userId){
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) return "invalid user";
        User user = userOptional.get();
        if (!user.isLoggedIn()) return "you have to login!";
        Integer amount = Integer.parseInt(formData.getFirst("amount"));
        user.setRemainingCharge(user.getRemainingCharge() + amount);
        userRepository.save(user);
        return "successful";
    }

    /**
     * profile part
     * this method is used to change username
     * @param formData
     * @param userId
     * @return a string shows the result
     */
    @PostMapping("/{user_id}/profile/changeUserName")
    public String changeUserName(@RequestBody MultiValueMap<String, String> formData,
                                 @PathVariable(value = "user_id") final Integer userId){
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) return "invalid user";
        User user = userOptional.get();
        if (!user.isLoggedIn()) return "you have to login!";
        String newUserName = formData.getFirst("user name");
        for (User user1 : userRepository.findAll()) {
            if (user1.getUserName().equals(newUserName)) return "user name is used before";
        }
        user.setUserName(newUserName);
        userRepository.save(user);
        return "successful";
    }

    /**
     * profile part
     * this method is used to change password
     * @param formData
     * @param userId
     * @return a string shows the result
     */
    @PostMapping("/{user_id}/profile/changePassword")
    public String changePassword(@RequestBody MultiValueMap<String, String> formData,
                                 @PathVariable(value = "user_id") final Integer userId){
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) return "invalid user";
        User user = userOptional.get();
        if (!user.isLoggedIn()) return "you have to login!";
        String newPassword = formData.getFirst("password");
        user.setPassword(newPassword);
        userRepository.save(user);
        return "successful";
    }


}
