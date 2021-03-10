package cn.wftank.qqrobot.app.finder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@ToString(callSuper = true)
public class MatchIndexEntity extends IndexEntity{

 private AtomicInteger matchCount = new AtomicInteger(0);
 private AtomicInteger matchKeyLength = new AtomicInteger(0);

 public void plusMatchCount(){
  matchCount.incrementAndGet();
 }

 public void plusMatchCount(int delta){
  if (delta != 0) matchCount.addAndGet(delta);
 }

 public void plusMatchKeyLength(int delta){
  if (delta != 0) matchKeyLength.addAndGet(delta);
 }
}
