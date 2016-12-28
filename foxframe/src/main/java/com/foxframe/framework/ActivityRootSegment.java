package com.foxframe.framework;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.foxframe.segment.core.RootSegment;
import com.foxframe.segment.core.Segment;

/**
 * Created by wuguonan on 2016/8/31 0031. 最根部的Segment
 */
class ActivityRootSegment extends RootSegment {

  /**
   * 根视图
   */
  private FrameLayout mRootView;


  public ActivityRootSegment(Activity activity, FrameLayout parentView) {
    super(activity);
    mRootView = parentView;
  }


  @Override
  protected void onChildSegmentViewCreated(View aView, Segment aChildSegment) {
    ViewParent parent = aView.getParent();
    if (parent != null) {
      ((ViewGroup) parent).removeView(aView);
    }
    if (mRootView != null) {
      FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT);
      mRootView.addView(aView, lp);
    }
  }

  @Override
  protected void onRemoveChildSegmentView(View aView, Segment aChildSegment) {
    if (mRootView != null) {
      mRootView.removeView(aView);
    }
  }



  @Override
  protected View onCreateView(Activity activity) {
    return null;
  }

  @Override
  protected void onDestroyView() {

  }
}
