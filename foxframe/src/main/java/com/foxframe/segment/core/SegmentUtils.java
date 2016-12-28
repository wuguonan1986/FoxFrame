package com.foxframe.segment.core;

import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;

/**
 * Created by wuguonan on 2016/8/31 0031.
 */
public final class SegmentUtils {


  static void checkUiThread() {
    if (!(Looper.getMainLooper().equals(Looper.myLooper()))) {
      throw new RuntimeException("This method Access Should be in Main Thread !");
    }
  }

  /**
   * 用于记录还在队列中没执行的action操作
   */
  private static ArrayList<Message> mPendingActionMsgs = new ArrayList<Message>();

  /*****************************************************/
  /**
   * 消息入队
   *
   * @param aMsg 消息
   */
  static synchronized void enqueueMsg(Message aMsg) {
    mPendingActionMsgs.add(aMsg);
  }

  /**
   * 消息出队
   *
   * @param aMsg 消息
   */
  static synchronized void removeMsg(Message aMsg) {
    mPendingActionMsgs.remove(aMsg);
  }

  /**
   * 同步执行之前pending的任务
   */
  public static void excutePendingActions() {
    checkUiThread();
    for (int i = 0; i < mPendingActionMsgs.size(); i++) {
      Message msg = mPendingActionMsgs.get(i);
      if (msg.obj != null && msg.obj instanceof AbsSegment) {
        AbsSegment.dealWithMsg(msg);
      }
    }
    mPendingActionMsgs.clear();
  }

  public static int getRandomColor() {
    int red = (int) (Math.random() * 255);
    int green = (int) (Math.random() * 255);
    int blue = (int) (Math.random() * 255);
    return 0xcf000000 + (red << 16) + (green << 8) + (blue);
  }

}
