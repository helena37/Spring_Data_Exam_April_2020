package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.TicketSeedRootDto;
import softuni.exam.models.entities.Passenger;
import softuni.exam.models.entities.Plane;
import softuni.exam.models.entities.Ticket;
import softuni.exam.models.entities.Town;
import softuni.exam.repository.TicketRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.service.PlaneService;
import softuni.exam.service.TicketService;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static softuni.exam.constants.GlobalConstants.*;

@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final TownService townService;
    private final PassengerService passengerService;
    private final PlaneService planeService;

    public TicketServiceImpl(TicketRepository ticketRepository, ValidationUtil validationUtil, XmlParser xmlParser, ModelMapper modelMapper, TownService townService, PassengerService passengerService, PlaneService planeService) {
        this.ticketRepository = ticketRepository;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.townService = townService;
        this.passengerService = passengerService;
        this.planeService = planeService;
    }

    @Override
    public boolean areImported() {
        return this.ticketRepository.count() > 0;
    }

    @Override
    public String readTicketsFileContent() throws IOException {
        return Files.readString(Path.of(TICKETS_FILE_PATH));
    }

    @Override
    public String importTickets() throws JAXBException, FileNotFoundException {
        StringBuilder importOutput = new StringBuilder();
        TicketSeedRootDto ticketSeedRootDto =
                this.xmlParser.unmarshalFromFile(TICKETS_FILE_PATH, TicketSeedRootDto.class);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ticketSeedRootDto.getTickets()
                .forEach(ticketSeedDto -> {
                    if (this.validationUtil.isValid(ticketSeedDto)) {
                        if (this.ticketRepository.findBySerialNumber(ticketSeedDto.getSerialNumber()) == null) {
                            LocalDateTime takeOff = LocalDateTime.parse(ticketSeedDto.getTakeOff(), dtf);
                            Ticket ticket = this.modelMapper.map(ticketSeedDto, Ticket.class);
                            Town fromTown = this.townService.getTownByName(ticketSeedDto.getFromTown().getName());
                            Town toTown = this.townService.getTownByName(ticketSeedDto.getToTown().getName());
                            Passenger passenger = this.passengerService
                                    .getPassengerByEmail(ticketSeedDto.getPassenger().getEmail());
                            Plane plane = this.planeService
                                    .getPlaneByRegisterNumber(ticketSeedDto.getPlane().getRegisterNumber());
                            ticket.setTakeOff(takeOff);
                            ticket.setFromTown(fromTown);
                            ticket.setToTown(toTown);
                            ticket.setPassenger(passenger);
                            ticket.setPlane(plane);
                            this.ticketRepository.saveAndFlush(ticket);
                            importOutput.append(String.format(
                                    "Successfully imported Ticket %s - %s",
                                    ticketSeedDto.getFromTown().getName(),
                                    ticketSeedDto.getToTown().getName()
                            ));
                        } else {
                            importOutput.append(ALREADY_IN_DB_MESSAGE);
                        }
                    } else {
                        importOutput.append(INCORRECT_TICKET_MESSAGE);
                    }
                    importOutput.append(System.lineSeparator());
                });
        return importOutput.toString();
    }
}
