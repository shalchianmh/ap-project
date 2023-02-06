package edu.sharif.project1.admin;


import edu.sharif.project1.boardGame.BoardGame;
import edu.sharif.project1.boardGame.BoardGameRepository;
import edu.sharif.project1.food.Food;
import edu.sharif.project1.food.FoodRepository;
import edu.sharif.project1.ticket.Ticket;
import edu.sharif.project1.ticket.TicketRepository;
import edu.sharif.project1.ticket.TicketState;
import edu.sharif.project1.user.User;
import edu.sharif.project1.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * here is the admin controller class, all the actions that admin can do are here,
 * all the methods check that sent id has already logged in
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private BoardGameRepository boardGameRepository;

    /**
     * this method is for creating a new admin, we can only have one admin in system
     * @param admin
     * @return a string shows the result
     */
    @PostMapping("/create")
    public String createAdmin(@RequestBody final Admin admin) {
        ArrayList<Admin> admins = (ArrayList<Admin>) adminRepository.findAll();
        if (!admins.isEmpty()) return "admin has been registered before";
        admin.setLoggedIn(false);
        adminRepository.save(admin);
        return "successful";
    }

    /**
     * this method controls admin login, which avoids login when you have logged in before, and makes the logged in true
     * @param formData
     * @return the result and sends admin's id for other actions
     */
    @PostMapping("/login")
    public String login(@RequestBody MultiValueMap<String, String> formData){
        String adminName = formData.getFirst("admin name");
        String password = formData.getFirst("password");
        Admin admin = null;
        for (Admin admin1 : adminRepository.findAll()) {
            if (admin1.getAdminName().equals(adminName)){
                admin = admin1;
                break;
            }
        }
        if (admin == null) return "there is no admin with this name";
        if (!admin.getPassword().equals(password)) return "incorrect password";
        if (admin.isLoggedIn()) return "you have logged in before";
        admin.setLoggedIn(true);
        adminRepository.save(admin);
        return "logged in successful, use your id for requests: " + admin.getId();
    }

    /**
     * this method makes the admin logged out, only logged in admin can log out
     * @param adminId
     * @return a string shows the result
     */
    @GetMapping("logout/{admin_id}")
    public String logout(@PathVariable(value = "admin_id") final Integer adminId){
        if (adminRepository.findById(adminId).isEmpty()) return "incorrect id";
        Admin admin = adminRepository.findById(adminId).get();
        if (!admin.isLoggedIn()) return "you didn't have logg in before";
        admin.setLoggedIn(false);
        adminRepository.save(admin);
        return "logged out successful";
    }


    /**
     * this method returns a list of all users as a json for admin
     * if something has a problem, it throws exeption
     * @param adminId
     * @return list of all users
     * @throws Exception
     */
    @GetMapping("/all_users/{admin_id}")
    public List<User> getAllUsers(@PathVariable(value = "admin_id") final Integer adminId) throws Exception {
        ArrayList<Admin> admins = (ArrayList<Admin>) adminRepository.findAll();
        if (admins.isEmpty() || !admins.get(0).getId().equals(adminId)) throw new Exception();
        if (!admins.get(0).isLoggedIn()) throw new Exception();
        List<User> users = (List<User>) userRepository.findAll();
        return users;
    }

    /**
     * this method returns a list of all tickets that have been sent by users, admin can accept or reject them by id,
     * if something like login or admin id has a problem it throws exception
     * @param adminId
     * @return list of submitted tickets
     * @throws Exception
     */
    @GetMapping("/submitted_tickets/{admin_id}")
    public ArrayList<Ticket> getSubmittedTickets(@PathVariable(value = "admin_id") final Integer adminId) throws Exception{
        ArrayList<Admin> admins = (ArrayList<Admin>) adminRepository.findAll();
        if (admins.isEmpty() || !admins.get(0).getId().equals(adminId)) throw new Exception();
        if (!admins.get(0).isLoggedIn()) throw new Exception();
        ArrayList<Ticket> tickets = (ArrayList<Ticket>) ticketRepository.findAll();
        ArrayList<Ticket> submittedTickets = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getState().equals(TicketState.SUBMITTED)){
                submittedTickets.add(ticket);
            }
        }
        return submittedTickets;
    }

    /**
     * this method returns a list of all tickets in each state of all users for admin,
     * if something like login or admin id has a problem it throws exception
     * @param adminId
     * @return list of all tickets
     * @throws Exception
     */
    @GetMapping("/get_tickets/{admin_id}")
    public ArrayList<Ticket> getAllTickets(@PathVariable(value = "admin_id") final Integer adminId) throws Exception {
        if (adminRepository.findById(adminId).isEmpty()) throw new Exception();
        Admin admin = adminRepository.findById(adminId).get();
        if (!admin.isLoggedIn()) throw new Exception();
        return (ArrayList<Ticket>) ticketRepository.findAll();
    }

    /**
     * this method is used to accept a ticket, and returns a string as result
     * @param ticketId
     * @param adminId
     * @return a string shows the result
     */
    @GetMapping("/accept_ticket/{admin_id}/{ticket_id}")
    public String acceptTicket(@PathVariable(value = "ticket_id") final Integer ticketId,
                               @PathVariable(value = "admin_id") final Integer adminId){
        if (adminRepository.findById(adminId).isEmpty()) return "invalid admin id";
        if (!adminRepository.findById(adminId).get().isLoggedIn()) return "you have to login as admin";
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        if (ticketOptional.isEmpty()) return "invalid ticket";
        Ticket ticket = ticketOptional.get();
        if (!ticket.getState().equals(TicketState.SUBMITTED)) return "this ticket is not submitted!";
        ticket.setState(TicketState.ACCEPTED);
        ticketRepository.save(ticket);
        return "accepted successfully";
    }

    /**
     * this method is used to reject a ticket, and returns a string as a result, when admin cancel a ticket,
     * all the foods and board games come back to store and what user paid will be returned to him
     * @param ticketId
     * @param adminId
     * @return a string shows the result
     */
    @GetMapping("/reject_ticket/{admin_id}/{ticket_id}")
    public String rejectTicket(@PathVariable(value = "ticket_id") final Integer ticketId,
                               @PathVariable(value = "admin_id") final Integer adminId){
        if (adminRepository.findById(adminId).isEmpty()) return "invalid admin id";
        if (!adminRepository.findById(adminId).get().isLoggedIn()) return "you have to login as admin";
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        if (ticketOptional.isEmpty()) return "invalid ticket";
        Ticket ticket = ticketOptional.get();
        if (!ticket.getState().equals(TicketState.SUBMITTED)) return "this ticket is not submitted!";
        User customer = ticket.getCustomer();
        customer.setRemainingCharge(customer.getRemainingCharge() + ticket.getAmount());
        ticket.setState(TicketState.REJECTED);
        for (Food food : ticket.getFoods()) {
            food.setRemaining(food.getRemaining() + 1);
            foodRepository.save(food);
        }
        for (BoardGame boardGame : ticket.getBoardGames()) {
            boardGame.setRemaining(boardGame.getRemaining() + 1);
            boardGameRepository.save(boardGame);
        }
        userRepository.save(customer);
        ticketRepository.save(ticket);
        return "accepted successfully";
    }

    /**
     * this method is used when admin wants to charge food in store, for example adding three pizzas
     * @param food_id
     * @param amount
     * @param adminId
     * @return a string shows the result
     */
    @GetMapping("/add_food/{admin_id}/{food_id}/{amount}")
    public String addFood(@PathVariable(value = "food_id") Integer food_id,
                          @PathVariable(value = "amount") Integer amount,
                          @PathVariable(value = "admin_id") final Integer adminId){
        if (adminRepository.findById(adminId).isEmpty()) return "invalid admin id";
        if (!adminRepository.findById(adminId).get().isLoggedIn()) return "you have to login as admin";
        if (foodRepository.findById(food_id).isEmpty()) return "invalid food";
        Food food = foodRepository.findById(food_id).get();
        food.setRemaining(food.getRemaining() + amount);
        foodRepository.save(food);
        return "successful";
    }

    /**
     * this method is used when admin wants to charge board game in store, for example adding three chess,
     * also when users return their loaned games, admin will use this methid
     * @param board_game_id
     * @param amount
     * @param adminId
     * @return a string shows the result
     */
    @GetMapping("/add_board_game/{admin_id}/{board_game_id}/{amount}")
    public String addBoardGame(@PathVariable(value = "board_game_id") Integer board_game_id,
                               @PathVariable(value = "amount") Integer amount,
                               @PathVariable(value = "admin_id") final Integer adminId){
        if (adminRepository.findById(adminId).isEmpty()) return "invalid admin id";
        if (!adminRepository.findById(adminId).get().isLoggedIn()) return "you have to login as admin";
        if (boardGameRepository.findById(board_game_id).isEmpty()) return "invalid board game";
        BoardGame boardGame = boardGameRepository.findById(board_game_id).get();
        boardGame.setRemaining(boardGame.getRemaining() + amount);
        boardGameRepository.save(boardGame);
        return "successful";
    }
}
