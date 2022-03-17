package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString

public class BaseModel {
    protected UUID id;
    protected String fullName;
    protected Date createdDate;
    protected Date updatedDate;

    {
        this.id=UUID.randomUUID();
        this.createdDate= new Date();
    }
}
