package com.foxframe.segment.extention;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.foxframe.interfaces.SegmentStack;
import com.foxframe.segment.core.Segment;

/**
 * Created by wuguonan on 2016/9/2 0002.
 */
public class SegStackSegment extends Segment implements SegmentStack {

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

  protected View getCurrentFocusView() {
    if (childViewSlot != null) {
      return childViewSlot.getChildAt(childViewSlot.getChildCount() - 1);
    }
    return null;
  }

  protected void moveViewToForground(View aView) {
    ViewParent parentView = aView.getParent();
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
          childViewSlot.removeView(priviousView);
        }
      }
    }

  }

  public void setChildViewSlot(FrameLayout childViewSlot) {
    this.childViewSlot = childViewSlot;
  }

  public void setMyView(ViewGroup myView) {
    this.myView = myView;
  }


  @Override
  public boolean canPopSeg() {
    return (getChildrenCount() > 0);
  }

  @Override
  public Segment popSegment() {
    Segment segment = getFocusChild();
    removeSegment(getFocusChild());
    return segment;
  }

  @Override
  public boolean canPopSegRecursive() {
    return canPopSeg();
  }

  @Override
  public Segment popSegmentRecursive() {
    Segment segment = getFocusChild();
    if (segment != null && segment instanceof SegmentStack
        && ((SegmentStack) segment).canPopSegRecursive()) {
      return ((SegmentStack) segment).popSegmentRecursive();
    }
    return popSegment();
  }

  @Override
  public void pushSegment(Segment aChildSeg) {
    final int focus = getChildIndex(getFocusChild());
    this.addSegment(aChildSeg, focus + 1);
    setFocusToChild(aChildSeg);
  }
}
