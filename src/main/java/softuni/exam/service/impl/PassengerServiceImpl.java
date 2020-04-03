package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.PassengerSeedDto;
import softuni.exam.models.dtos.TownSeedDto;
import softuni.exam.models.entities.Passenger;
import softuni.exam.models.entities.Town;
import softuni.exam.repository.PassengerRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static softuni.exam.constants.GlobalConstants.*;
import static softuni.exam.constants.GlobalConstants.INCORRECT_TOWN_MESSAGE;

@Service
public class PassengerServiceImpl implements PassengerService {
    private final PassengerRepository passengerRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final TownService townService;

    public PassengerServiceImpl(PassengerRepository passengerRepository, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil, TownService townService) {
        this.passengerRepository = passengerRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.townService = townService;
    }

    @Override
    public boolean areImported() {
        return this.passengerRepository.count() > 0;
    }

    @Override
    public String readPassengersFileContent() throws IOException {
        return Files.readString(Path.of(PASSENGERS_FILE_PATH));
    }

    @Override
    public String importPassengers() throws FileNotFoundException {
        StringBuilder importOutput = new StringBuilder();
        PassengerSeedDto[] passengerSeedDtos =
                this.gson.fromJson(new FileReader(PASSENGERS_FILE_PATH), PassengerSeedDto[].class);

        Arrays.stream(passengerSeedDtos)
                .forEach(passengerSeedDto -> {
                    if (this.validationUtil.isValid(passengerSeedDto)) {
                        if (this.passengerRepository.findByEmail(passengerSeedDto.getEmail()) == null) {
                            Passenger passenger = this.modelMapper.map(passengerSeedDto, Passenger.class);
                            Town town = this.townService.getTownByName(passengerSeedDto.getTown());
                            passenger.setTown(town);
                            this.passengerRepository.saveAndFlush(passenger);
                            importOutput.append(String.format(
                                    "Successfully imported Passenger %s - %s",
                                    passengerSeedDto.getLastName(),
                                    passengerSeedDto.getEmail()
                            ));
                        } else {
                            importOutput.append(ALREADY_IN_DB_MESSAGE);
                        }
                    } else {
                        importOutput.append(INCORRECT_PASSENGER_MESSAGE);
                    }
                    importOutput.append(System.lineSeparator());
                });
        return importOutput.toString();
    }

    @Override
    public String getPassengersOrderByTicketsCountDescendingThenByEmail() {
        StringBuilder exportResults = new StringBuilder();
        List<Passenger> passengers = this.passengerRepository
                .findPassengersOrderByTicketsCountDescendingThenByEmail();
        passengers.forEach(passenger -> exportResults.append(String.format(
                "Passenger %s  %s\n" +
                        "\tEmail - %s\n" +
                        "\tPhone - %s\n" +
                        "\tNumber of tickets - %d\n",
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getEmail(),
                passenger.getPhoneNumber(),
                passenger.getTickets().size()
        )));

        return exportResults.toString();
    }

    @Override
    public Passenger getPassengerByEmail(String email) {
        return this.passengerRepository.findByEmail(email);
    }
}
