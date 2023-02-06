package edu.sharif.project1.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.sharif.project1.ticket.Ticket;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Set;

/**
 * here is the entity class for user, that has six important field
 * id, userName, password, remaining charge and a list of tickets that belong to this user
 */
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    private String userName;

    private String password;

    private Integer remainingCharge;

    private boolean loggedIn;

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private Set<Ticket> tickets;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRemainingCharge() {
        return remainingCharge;
    }

    public void setRemainingCharge(Integer remainingCharge) {
        this.remainingCharge = remainingCharge;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }
}