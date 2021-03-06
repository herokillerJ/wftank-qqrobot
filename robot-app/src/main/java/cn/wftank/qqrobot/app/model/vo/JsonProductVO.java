package cn.wftank.qqrobot.app.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: wftank
 * @Date: 2020/10/5
 * @Description: 组件通用实体
 */
@Getter
@Setter
@ToString(callSuper = true)
public class JsonProductVO {

    private String id;
    private String name;
    private String nameCn;
    private String type;
    private String typeCn;

    private String showTypeKey;
    private String showType;
    private String showTypeCn;

    //物品的源属性
    private String productType;
    private String productSubType;

    private String classDes;
    private String classDesCn;
    private Integer size;
    private Integer grade;
    private Boolean canBuy = false;
    private Boolean canSell = false;
    private Boolean canRent = false;
    private Boolean commodity = false;
    private String description;
    private String descriptionCn;

    private List<ProductShopVO> shopBuy = new LinkedList<>();
    private List<ProductShopVO> shopSell = new LinkedList<>();
    private List<ProductRentShopVO> shopRent = new LinkedList<>();

    private String fileName;
    private String jsonPath;





}
