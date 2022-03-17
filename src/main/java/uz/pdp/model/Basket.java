package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString

public class Basket {
    private UUID basketId;
    private UUID userId;
    private String foodName;
    private int amount;
    private BigDecimal price;
}
