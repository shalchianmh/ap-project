package edu.sharif.project1.boardGame;


import edu.sharif.project1.ticket.Ticket;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Set;

@Entity
/**
 * /**
 *  here is the entity class for board game, that has four important field
 *  id, name, rent and remaining which shows how much of a specified one is remaining in store
 *
 */
public class BoardGame {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;


    private String name;

    private Integer rent;

    private Integer remaining;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRent() {
        return rent;
    }

    public void setRent(Integer rent) {
        this.rent = rent;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }
}
