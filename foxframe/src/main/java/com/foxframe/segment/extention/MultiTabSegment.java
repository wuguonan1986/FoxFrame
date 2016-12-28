package com.foxframe.segment.extention;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.foxframe.interfaces.MultiTabHolder;
import com.foxframe.segment.core.Segment;

/**
 * Created by wuguonan on 2016/9/1 0001. 容纳多窗口的Segment
 */
public class MultiTabSegment extends Segment implements MultiTabHolder {

  /**
   * 窗口视图插槽
   */
  private FrameLayout childViewSlot;

  private ViewGroup myView;


  @Override
  protected void onChildSegmentViewCreated(View aView, Segment aChildSegment) {

  }

  @Override
  protected void onRemoveChildSegmentView(View aView, Segment aChildSegment) {
    if (childViewSlot != null) {
      childViewSlot.removeView(aView);
    }
  }

  @Override
  protected void onChildResumed(Segment aChildSegment) {
    View view = aChildSegment.getView();
    View topView = getCurrentFocusView();
    if (topView == null || !topView.equals(view)) {
      moveViewToForground(view);
    }
  }


  @Override
  protected View onCreateView(Activity activity) {
    if (myView == null) {
      myView = new FrameLayout(getAppContext());
    }
    return myView;
  }

  @Override
  protected void onDestroyView() {
    myView = null;
    childViewSlot = null;
  }

  public void setChildViewSlot(FrameLayout childViewSlot) {
    this.childViewSlot = childViewSlot;
  }

  public void setMyView(ViewGroup myView) {
    this.myView = myView;
  }

  /**
   * 移动窗口视图至前台
   * @param aView 视图
   */
  protected void moveViewToForground(View aView) {
    ViewParent parentView = aView.getParent();
    aView.setVisibility(View.VISIBLE);
    if (parentView != null) {
      ((ViewGroup) parentView).removeView(aView);
    }

    if (childViewSlot != null) {
      FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT);
      childViewSlot.addView(aView, lp);
    }

    if (childViewSlot != null) {
      int cnt = childViewSlot.getChildCount();
      for (int i = cnt - 2; i >= 0; i--) {
        View priviousView = childViewSlot.getChildAt(i);
        if (priviousView != null) {
          priviousView.setVisibility(View.GONE);
        }
      }
    }

  }

  protected View getCurrentFocusView() {
    if (childViewSlot != null) {
      return childViewSlot.getChildAt(childViewSlot.getChildCount() - 1);
    }
    return null;
  }

  /**
   * 添加新窗口
   * @param aTabseg 窗口
   * @param requairFocus 是否前台
   */
  @Override
  public final void addNewTab(Segment aTabseg, boolean requairFocus) {
    int index = getChildIndex(getFocusChild());
    this.addSegment(aTabseg, index + 1);
    if (requairFocus) {
      aTabseg.runMySelf();
    } else {
      aTabseg.prepareMyself();
    }
  }

  /**
   * 添加新窗口
   * @param aTabseg 窗口
   * @param requairFocus 是否前台
   */
  @Override
  public final void addNewTab(Segment aTabseg, boolean requairFocus, int index) {
    int cnt = getChildrenCount();
    if (index < 0 || index > cnt) {
      throw new ArrayIndexOutOfBoundsException();
    }
    this.addSegment(aTabseg, index);
    if (requairFocus) {
      aTabseg.runMySelf();
    } else {
      aTabseg.prepareMyself();
    }
  }

  @Override
  public final void removeTab(String aTabseg) {
    this.removeSegment(this.find(aTabseg));
  }

  @Override
  public final void swithFocusToTab(String aTabseg) {
    setFocusToChild(this.find(aTabseg));
  }


}
