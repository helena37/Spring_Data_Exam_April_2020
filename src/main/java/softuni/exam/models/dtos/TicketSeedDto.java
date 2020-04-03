package softuni.exam.models.dtos;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "ticket")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketSeedDto {
    @XmlElement(name = "serial-number")
    private String serialNumber;
    @XmlElement
    private BigDecimal price;
    @XmlElement(name = "take-off")
    private String takeOff;
    @XmlElement(name = "from-town")
    private TownSeedFromTownDto fromTown;
    @XmlElement(name = "to-town")
    private TownSeedToTownDto toTown;
    @XmlElement(name = "passenger")
    private PassengerSeedEmailDto passenger;
    @XmlElement(name = "plane")
    private PlaneSeedRegisterNumberDto plane;

    public TicketSeedDto() {
    }

    @NotNull
    @Length(min = 2)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @DecimalMin("0")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTakeOff() {
        return takeOff;
    }

    public void setTakeOff(String takeOff) {
        this.takeOff = takeOff;
    }

    public TownSeedFromTownDto getFromTown() {
        return fromTown;
    }

    public void setFromTown(TownSeedFromTownDto fromTown) {
        this.fromTown = fromTown;
    }

    public TownSeedToTownDto getToTown() {
        return toTown;
    }

    public void setToTown(TownSeedToTownDto toTown) {
        this.toTown = toTown;
    }

    public PassengerSeedEmailDto getPassenger() {
        return passenger;
    }

    public void setPassenger(PassengerSeedEmailDto passenger) {
        this.passenger = passenger;
    }

    public PlaneSeedRegisterNumberDto getPlane() {
        return plane;
    }

    public void setPlane(PlaneSeedRegisterNumberDto plane) {
        this.plane = plane;
    }
}
