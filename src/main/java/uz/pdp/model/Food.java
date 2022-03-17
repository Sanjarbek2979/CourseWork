package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Food extends BaseModel {
    private UUID CategoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isActiveFood;

    {
        this.isActiveFood=true;
    }
}
