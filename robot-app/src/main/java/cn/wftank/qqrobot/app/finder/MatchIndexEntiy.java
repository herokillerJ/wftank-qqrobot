package cn.wftank.qqrobot.app.finder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class MatchIndexEntiy extends IndexEntity{

 private int matchCount = 0;
 private int matchKeyLength = 0;
}
