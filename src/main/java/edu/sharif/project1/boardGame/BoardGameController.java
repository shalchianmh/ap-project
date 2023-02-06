package edu.sharif.project1.boardGame;


import edu.sharif.project1.admin.AdminRepository;
import edu.sharif.project1.food.Food;
import edu.sharif.project1.food.FoodRepository;
import edu.sharif.project1.ticket.Ticket;
import edu.sharif.project1.ticket.TicketRepository;
import edu.sharif.project1.user.User;
import edu.sharif.project1.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Set;

/**
 * this class controls board game, which has few methods, because most board game methods are dependent to other classes
 */
@RestController
@RequestMapping("/boardGame")
public class BoardGameController {
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
     * this method is for creating a new board game, only admin can use it
     * @param boardGame
     * @param adminId
     * @return a string shows the result
     */
    @PostMapping("/create/{admin_id}")
    public String createBoardGame(@RequestBody BoardGame boardGame, @PathVariable(value = "admin_id") final Integer adminId) {
        if (adminRepository.findById(adminId).isEmpty()) return "invalid admin id";
        if (!adminRepository.findById(adminId).get().isLoggedIn()) return "you have to login as admin";
        boardGame.setRemaining(0);
        for (BoardGame game : boardGameRepository.findAll()) {
            if (game.getName().equals(boardGame.getName())) return "this type of board game is created before!";
        }
        BoardGame savedBoardGame = boardGameRepository.save(boardGame);
        return "successful";
    }

    /**
     * @return all board games
     * everyone can use it
     */
    @GetMapping("/get/all")
    public ArrayList<BoardGame> getAllBoardGames(){
        return (ArrayList<BoardGame>) boardGameRepository.findAll();
    }
}
