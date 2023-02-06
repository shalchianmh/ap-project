package edu.sharif.project1.food;


import edu.sharif.project1.admin.AdminRepository;
import edu.sharif.project1.boardGame.BoardGameRepository;
import edu.sharif.project1.ticket.TicketRepository;
import edu.sharif.project1.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Set;

/**
 * this class controls food, which has few methods, because most food methods are dependent to other classes
 */
@RestController
@RequestMapping("/food")
public class FoodController {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private BoardGameRepository boardGameRepository;

    /**
     * this method is for creating a new food, only admin can use it
     * @param food
     * @param adminId
     * @return a string shows the result
     */
    @PostMapping("/create/{admin_id}")
    public String createFood(@RequestBody Food food, @PathVariable(value = "admin_id") final Integer adminId) {
        if (adminRepository.findById(adminId).isEmpty()) return "invalid admin id";
        if (!adminRepository.findById(adminId).get().isLoggedIn()) return "you have to login as admin";
        food.setRemaining(0);
        for (Food food1 : foodRepository.findAll()) {
            if (food.getName().equals(food1.getName())) return "this kind of food is created before";
        }
        Food savedFood = foodRepository.save(food);
        return "successful";
    }

    /**
     * @return a list of all foods
     * everyone can use it
     */
    @GetMapping("/get/all")
    public ArrayList<Food> getAllFoods(){
        return (ArrayList<Food>) foodRepository.findAll();
    }
}
