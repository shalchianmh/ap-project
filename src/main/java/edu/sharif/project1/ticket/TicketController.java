package edu.sharif.project1.ticket;

import edu.sharif.project1.boardGame.BoardGame;
import edu.sharif.project1.boardGame.BoardGameRepository;
import edu.sharif.project1.food.Food;
import edu.sharif.project1.food.FoodRepository;
import edu.sharif.project1.user.User;
import edu.sharif.project1.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * this class controls ticket actions, all methods are for users, so they check login process
 */
@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private BoardGameRepository boardGameRepository;


    /**
     * this method lets the user make a new ticket
     * @param ticket
     * @param userId
     * @return a string shows the result
     */
    @PostMapping("/create/{user_id}")
    public String createTicket(@RequestBody Ticket ticket, @PathVariable(value = "user_id") final Integer userId) {
        if (userRepository.findById(userId).isEmpty()) return "invalid user id";
        User customer = userRepository.findById(userId).get();
        if (!customer.isLoggedIn()) return "you have to login!";
        ticket.setCustomer(customer);
        ticket.setAmount(0);
        Ticket savedTicket = ticketRepository.save(ticket);
        return "successful";
    }

    /**
     * this method lets the user remove a ticket that is not submitted, all foods and games come back to store
     * @param userId
     * @param ticketId
     * @return
     */
    @GetMapping("/remove/{user_id}/{ticket_id}")
    public String removeTicket(@PathVariable(value = "user_id") final Integer userId,
                               @PathVariable(value = "ticket_id") final Integer ticketId){
        if (userRepository.findById(userId).isEmpty()) return "invalid user id";
        User customer = userRepository.findById(userId).get();
        if (!customer.isLoggedIn()) return "you have to login!";
        if (ticketRepository.findById(ticketId).isEmpty()) return "invalid ticket";
        Ticket ticket = ticketRepository.findById(ticketId).get();
        if (!customer.getTickets().contains(ticket)) return "invalid ticket";
        if (!ticket.getState().equals(TicketState.IN_PROGRESS)) return "you can't delete a submitted ticket";
        customer.getTickets().remove(ticket);
        for (Food food : ticket.getFoods()) {
            food.setRemaining(food.getRemaining() + 1);
            foodRepository.save(food);
        }
        for (BoardGame boardGame : ticket.getBoardGames()) {
            boardGame.setRemaining(boardGame.getRemaining() + 1);
            boardGameRepository.save(boardGame);
        }
        ticketRepository.delete(ticket);
        return "removed";
    }

    /**
     * this method is for submitting a ticket, the amount will be paid at the moment, if user does not have enough money,
     * he will get an error message
     * @param ticketId
     * @param userId
     * @return a string shows the result
     */
    @GetMapping("/state/{user_id}/submit/{ticket_id}")
    public String submitTicket(@PathVariable(value = "ticket_id") final Integer ticketId,
                               @PathVariable(value = "user_id") final Integer userId){
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        if (ticketOptional.isEmpty()) return "invalid ticket";
        Ticket ticket = ticketOptional.get();
        User user = ticket.getCustomer();
        if (!user.isLoggedIn()) return "you have to login!";
        if (user.getId() != userId) return "invalid ticket";
        if (!ticket.getState().equals(TicketState.IN_PROGRESS)) return "ticket is submitted before!";
        if (user.getRemainingCharge() < ticket.getAmount()) return "you need to charge your account";
        user.setRemainingCharge(user.getRemainingCharge() - ticket.getAmount());
        ticket.setState(TicketState.SUBMITTED);
        userRepository.save(user);
        ticketRepository.save(ticket);
        return "successful";
    }


    /**
     * this method is used to add food to specified ticket
     * @param foodId
     * @param userId
     * @param ticketId
     * @return a string shows the result
     */
    @PostMapping("/add/food/{ticket_id}/{user_id}/{food_id}")
    public String addFoodToTicket(@PathVariable(value = "food_id") final Integer foodId,
                                  @PathVariable(value = "user_id") final Integer userId,
                                  @PathVariable(value = "ticket_id") final Integer ticketId){
        if (ticketRepository.findById(ticketId).isEmpty()) return "invalid ticket";
        if (foodRepository.findById(foodId).isEmpty()) return "there is no food with this id";
        Ticket ticket = ticketRepository.findById(ticketId).get();
        Food food = foodRepository.findById(foodId).get();

        if (ticket.getCustomer().getId() != userId) return "invalid user id";
        if (!ticket.getCustomer().isLoggedIn()) return "you have to login!";
        if (ticket.getState() != TicketState.IN_PROGRESS) return "ticket is not available";
        if(ticket.getFoods().contains(food)) return "this food is added before";
        if (food.getRemaining() < 1) return "this food is finished";
        food.setRemaining(food.getRemaining() - 1);
        ticket.getFoods().add(food);
        ticket.setAmount(ticket.getAmount() + food.getPrice());
        foodRepository.save(food);
        ticketRepository.save(ticket);
        return "successful";
    }

    /**
     * this method is used to add board game to specified ticket
     * @param boardGameId
     * @param userId
     * @param ticketId
     * @return a string shows the result
     */
    @PostMapping("/add/board_game/{ticket_id}/{user_id}/{board_game_id}")
    public String addBoardGameToTicket(@PathVariable(value = "board_game_id") final Integer boardGameId,
                                  @PathVariable(value = "user_id") final Integer userId,
                                  @PathVariable(value = "ticket_id") final Integer ticketId){
        if (ticketRepository.findById(ticketId).isEmpty()) return "invalid ticket";
        if (boardGameRepository.findById(boardGameId).isEmpty()) return "there is no board game with this id";
        Ticket ticket = ticketRepository.findById(ticketId).get();
        BoardGame boardGame = boardGameRepository.findById(boardGameId).get();

        if (ticket.getCustomer().getId() != userId) return "invalid user id";
        if (!ticket.getCustomer().isLoggedIn()) return "you have to login!";
        if (ticket.getState() != TicketState.IN_PROGRESS) return "ticket is not available";
        if(ticket.getBoardGames().contains(boardGame)) return "this board game is added before";
        if (boardGame.getRemaining() < 1) return "this board game is finished";
        boardGame.setRemaining(boardGame.getRemaining() - 1);
        ticket.getBoardGames().add(boardGame);
        ticket.setAmount(ticket.getAmount() + boardGame.getRent());
        boardGameRepository.save(boardGame);
        ticketRepository.save(ticket);
        return "successful";
    }






}
