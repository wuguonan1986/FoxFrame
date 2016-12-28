package com.foxframe.segment.core;

import android.app.Activity;

/**
 * Created by wuguonan on 2016/8/31 0031. root segment
 */
public abstract class RootSegment extends Segment {

  protected RootSegment(Activity activity) {
    super(true);
    setActivity(activity);
  }


  public void startMyself() {
    super.attach();
    super.start();
  }


  /**
   * 暂停
   */
  public void pauseMyself() {
    super.pause();
  }


  /**
   * 停止，被外部主动调用
   */
  public void stopMyself() {
    super.pause();
    super.stop();
    super.detach();
  }

  /**
   * 移除
   */
  public void destory() {
    super.detach();
  }

}
