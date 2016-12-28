package com.foxframe.interfaces;

import com.foxframe.segment.core.Segment;

/**
 * Created by wuguonan on 2016/9/1 0001.
 */
public interface SegmentQueue {

  boolean canGoForward();

  boolean canGoBackward();

  void goForward();

  void goBackward();

  void addNewSegment(Segment aChildSeg);

  void clearAllChildren();
}
