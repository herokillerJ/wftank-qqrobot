package cn.wftank.qqrobot.app.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductShopVO {

    private Double minBuyingPrice;

    private Double maxSellingPrice;

    private Double currentPrice;

    private String layoutName;

    private String layoutNameCn;

    private Double shopInventory;

    private Double shopMaxInventory;

    private Double refreshPerMinute;
}
