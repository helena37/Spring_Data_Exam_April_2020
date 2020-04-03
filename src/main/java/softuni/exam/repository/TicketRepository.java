package softuni.exam.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import softuni.exam.models.entities.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Ticket findBySerialNumber(String serialNumber);
}
