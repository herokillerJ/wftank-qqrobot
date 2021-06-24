package cn.wftank.qqrobot.app.finder.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: jiawei
 * @create: 2021-06-24 16:36
 * @description: 商品类型枚举
 **/
@Getter
@AllArgsConstructor
public enum ProductTypeEnum {
    WEAPON_PERSONAL(1,"WeaponPersonal","FPS武器"),
    WEAPON_ATTACHMENT(2,"WeaponAttachment","FPS武器配件"),
    VEHICLE(3,"Vehicle","载具"),
    SHIELD(4,"Shield","护盾"),
    POWERPLANT(5,"PowerPlant","发电机"),
    QUANTUMDRIVE(6,"QuantumDrive","量子引擎"),
    GUN(7,"Gun","机炮"),
    MININGLASER(8,"MiningLaser","采矿激光"),
    TURRET(9,"Turret","炮塔"),
    COOLER(10,"Cooler","散热器"),
    MISSILE(11,"Missile","导弹"),
    PAINTS(12,"Paints","涂装"),
    OTHER(13,"Other","其它"),
        ;
    private Integer index;
    private String name;
    private String nameCn;

    private static final Map<Integer, ProductTypeEnum> LOOKUP = new LinkedHashMap<>();
    private static final Map<String, ProductTypeEnum> NAME_LOOKUP = new LinkedHashMap<>();

    static {
        for (ProductTypeEnum e : ProductTypeEnum.values()) {
            LOOKUP.put(e.index, e);
            NAME_LOOKUP.put(e.name,e);
        }
    }

    public static ProductTypeEnum getByCode(Integer code) {
        return LOOKUP.get(code);
    }

    public static ProductTypeEnum getByName(String name) {
        return NAME_LOOKUP.get(name);
    }

}
