package edu.sharif.project1.ticket;

import org.springframework.data.repository.CrudRepository;

public interface TicketRepository  extends CrudRepository<Ticket, Integer > {
}
