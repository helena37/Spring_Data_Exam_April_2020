package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.constants.GlobalConstants;
import softuni.exam.models.dtos.PlaneSeedRootDto;
import softuni.exam.models.entities.Plane;
import softuni.exam.repository.PlaneRepository;
import softuni.exam.service.PlaneService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static softuni.exam.constants.GlobalConstants.*;

@Service
public class PlaneServiceImpl implements PlaneService {
    private final PlaneRepository planeRepository;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;

    public PlaneServiceImpl(PlaneRepository planeRepository, ValidationUtil validationUtil, XmlParser xmlParser, ModelMapper modelMapper) {
        this.planeRepository = planeRepository;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.planeRepository.count() > 0;
    }

    @Override
    public String readPlanesFileContent() throws IOException {
        return Files.readString(Path.of(PLANES_FILE_PATH));
    }

    @Override
    public String importPlanes() throws JAXBException, FileNotFoundException {
        StringBuilder importOutput = new StringBuilder();
        PlaneSeedRootDto planeSeedRootDto =
                this.xmlParser.unmarshalFromFile(PLANES_FILE_PATH, PlaneSeedRootDto.class);
        planeSeedRootDto.getPlanes()
                .forEach(planeSeedDto -> {
                    if (this.validationUtil.isValid(planeSeedDto)) {
                        if (this.planeRepository.findByRegisterNumber(planeSeedDto.getRegisterNumber()) == null) {
                            Plane plane = this.modelMapper.map(planeSeedDto, Plane.class);
                            this.planeRepository.saveAndFlush(plane);
                            importOutput.append(String.format(
                                    "Successfully imported Plane %s",
                                    planeSeedDto.getRegisterNumber()
                            ));
                        } else {
                            importOutput.append(ALREADY_IN_DB_MESSAGE);
                        }
                    } else {
                        importOutput.append(INCORRECT_PLANE_MESSAGE);
                    }
                    importOutput.append(System.lineSeparator());
                });
        return importOutput.toString();
    }

    @Override
    public Plane getPlaneByRegisterNumber(String registerNumber) {
        return this.planeRepository.findByRegisterNumber(registerNumber);
    }
}
