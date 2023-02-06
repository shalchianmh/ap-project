package edu.sharif.project1.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.sharif.project1.boardGame.BoardGame;
import edu.sharif.project1.food.Food;
import edu.sharif.project1.user.User;
import jakarta.persistence.*;
import org.springframework.data.util.Pair;


import java.util.Optional;
import java.util.Set;

/**
 * here is the entity class for ticket, that has six important field
 * id, amount(should be paid), state and customer, a list of foods and a list of board games
 */
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private Integer amount;

    private TicketState state;


    @JsonIgnore
    @ManyToOne()
    private User customer;

    @ManyToMany()
    private Set<Food> foods;



    @ManyToMany()
    private Set<BoardGame> boardGames;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Set<Food> getFoods() {
        return foods;
    }

    public void setFoods(Set<Food> foods) {
        this.foods = foods;
    }

    public Set<BoardGame> getBoardGames() {
        return boardGames;
    }

    public void setBoardGames(Set<BoardGame> boardGames) {
        this.boardGames = boardGames;
    }
}
