package com.foxframe.interfaces;

import com.foxframe.segment.core.Segment;

/**
 * Created by wuguonan on 2016/9/2 0002.
 */
public interface MultiTabHolder {

  /**
   * 添加新窗口
   * @param aTabseg 窗口
   * @param requairFocus 是否前台
   */
  void addNewTab(Segment aTabseg, boolean requairFocus);

  /**
   * 添加新窗口
   * @param aTabseg 窗口
   * @param requairFocus 是否前台
   */
  void addNewTab(Segment aTabseg, boolean requairFocus, int index);

  void removeTab(String aTabseg);

  void swithFocusToTab(String aTabseg);
}
