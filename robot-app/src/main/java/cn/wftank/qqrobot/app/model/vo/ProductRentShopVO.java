package cn.wftank.qqrobot.app.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ProductRentShopVO extends ProductShopVO {

    private Double rentPrice1;
    private Double rentPrice3;
    private Double rentPrice3PerDay;
    private Double rentPrice7;
    private Double rentPrice7PerDay;
    private Double rentPrice30;
    private Double rentPrice30PerDay;
}
