package cn.wftank.qqrobot.app.finder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString(callSuper = true)
public class MatchIndexEntity extends IndexEntity{

 private Double matchScore;
 private Map<String,String> scoreMap = new HashMap<>();

}
