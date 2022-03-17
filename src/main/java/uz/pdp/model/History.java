package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uz.pdp.model.user.User;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString

public class History extends BaseModel{
    private User customer;
    private Basket product;
    private Date date;
    private BigDecimal overall;
    {
       this.date= new Date();
    }


}
