package com.foxframe.interfaces;

import com.foxframe.segment.core.Segment;

/**
 * Created by wuguonan on 2016/9/2 0002.
 */
public interface SegmentStack {

  boolean canPopSeg();

  Segment popSegment();

  boolean canPopSegRecursive();

  Segment popSegmentRecursive();

  void pushSegment(Segment aChildSeg);
}
