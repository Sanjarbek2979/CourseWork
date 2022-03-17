package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Category extends BaseModel{
     private String categoryName;
     private boolean isActiveFoodType;

    {
        this.isActiveFoodType=true;
        this.id = UUID.randomUUID();
    }

}
