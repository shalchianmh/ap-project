package edu.sharif.project1.food;

import edu.sharif.project1.ticket.Ticket;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Set;

/**
 * here is the entity class for food, that has four important field
 * id, name, price and remaining which shows how much of a specified one is remaining in store
 */
@Entity
public class Food {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;


    private String name;

    private Integer price;

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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }
}
