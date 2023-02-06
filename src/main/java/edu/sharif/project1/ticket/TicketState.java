package edu.sharif.project1.ticket;

/**
 * this enum is for ticket state
 * in progress means ticket is not still sent to admin, so user can add foods and board games
 * submitted means ticket is sent
 * rejected means ticket is sent and can't be prepared so it is canceled by admin
 * accepted means that ticket is accepted and the order is delivered to the customer instantly
 */
public enum TicketState {
    IN_PROGRESS,
    SUBMITTED,
    REJECTED,
    ACCEPTED
}
