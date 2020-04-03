package softuni.exam.models.dtos;

import org.hibernate.validator.constraints.Length;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "from_town")
@XmlAccessorType(XmlAccessType.FIELD)
public class TownSeedFromTownDto {
    @XmlElement
    private String name;

    public TownSeedFromTownDto() {
    }

    @Length(min = 2)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
