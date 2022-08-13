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



    NOITEM_VEHICLE(1001,"NOITEM_Vehicle","载具(NOITEM_Vehicle)"),
    SHIELD(1002,"Shield","护盾(Shield)"),
    POWERPLANT(1003,"PowerPlant","发电机(PowerPlant)"),
    QUANTUMDRIVE(1004,"QuantumDrive","量子引擎(QuantumDrive)"),
    GUN(1005,"WeaponGun","舰炮(WeaponGun)"),
    MININGLASER(1006,"MiningLaser","采矿激光(MiningLaser)"),
    MININGMODIFIER(1007,"MiningModifier","采矿模组(MiningModifier)"),
    TURRET(1008,"Turret","炮塔(Turret)"),
    COOLER(1009,"Cooler","散热器(Cooler)"),
    MISSILE(1010,"Missile","导弹(Missile)"),
    PAINTS(1011,"Paints","涂装(Paints)"),
    WEAPONDEFENSIVE(1012,"WeaponDefensive","防卫装置(WeaponDefensive)"),
    MISSILELAUNCHER(1013,"MissileLauncher","导弹架(MissileLauncher)"),
    BOMB(1014,"Bomb","炸弹(Bomb)"),

    WEAPON_PERSONAL(2001,"WeaponPersonal","FPS武器(WeaponPersonal)"),
    WEAPON_ATTACHMENT(2002,"WeaponAttachment","FPS武器配件(WeaponAttachment)"),
    FPS_CONSUMABLE(2003,"FPS_Consumable","补给(FPS_Consumable)"),

    CHAR_CLOTHING_TORSO_1(2101,"Char_Clothing_Torso_1","夹克(Char_Clothing_Torso_1)"),
    CHAR_CLOTHING_TORSO_0(2102,"Char_Clothing_Torso_0","衬衫(Char_Clothing_Torso_0)"),
    CHAR_CLOTHING_HAT(2103,"Char_Clothing_Hat","帽子(Char_Clothing_Hat)"),
    CHAR_CLOTHING_FEET(2104,"Char_Clothing_Feet","鞋子(Char_Clothing_Feet)"),
    CHAR_CLOTHING_HANDS(2105,"Char_Clothing_Hands","手套(Char_Clothing_Hands)"),
    CHAR_CLOTHING_LEGS(2106,"Char_Clothing_Legs","裤子(Char_Clothing_Legs)"),

    CHAR_ARMOR_UNDERSUIT(2201,"Char_Armor_Undersuit","基底服(Char_Armor_Undersuit)"),
    CHAR_ARMOR_BACKPACK(2202,"Char_Armor_Backpack","背包(Char_Armor_Backpack)"),
    CHAR_ARMOR_ARMS(2203,"Char_Armor_Arms","臂甲(Char_Armor_Arms)"),
    CHAR_ARMOR_LEGS(2204,"Char_Armor_Legs","腿甲(Char_Armor_Legs)"),
    CHAR_ARMOR_TORSO(2205,"Char_Armor_Torso","胸甲(Char_Armor_Torso)"),
    CHAR_ARMOR_HELMET(2206,"Char_Armor_Helmet","头盔(Char_Armor_Helmet)"),




    GADGET(3001,"Gadget","工具(Gadget)"),
    CONTAINER(3002,"Container","货柜(Container)"),
    FOOD(3003,"Food","食物(Food)"),
    DRINK(3004,"Drink","饮料(Food)"),
    MobiGlas(3005,"MobiGlas","智能穿戴设备(MobiGlas)"),
    Misc(3006,"Misc","杂项(Misc)"),

    OTHER(9999,"Other","其它"),
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
