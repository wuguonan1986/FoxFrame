package com.foxframe.segment.extention;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.foxframe.interfaces.SegmentQueue;
import com.foxframe.segment.core.Segment;

/**
 * Created by wuguonan on 2016/9/1 0001.
 */
public class SegQueueSegment extends Segment implements SegmentQueue {


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

  @Override
  public boolean canGoForward() {
    Segment focus = getFocusChild();
    if (focus != null) {
      if (focus instanceof SegmentQueue && (((SegmentQueue) focus).canGoForward())) {
          return true;
      }
      int index = getChildIndex(focus);
      if (index + 1 < getChildrenCount()) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean canGoBackward() {
    Segment focus = getFocusChild();
    if (focus != null) {
      if (focus instanceof SegmentQueue && (((SegmentQueue) focus).canGoBackward())) {
        return true;
      }
      int index = getChildIndex(focus);
      if (index > 0) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void goForward() {
    Segment focus = getFocusChild();
    if (focus != null) {
      if (focus instanceof SegmentQueue && (((SegmentQueue) focus).canGoForward())) {
        ((SegmentQueue) focus).goForward();
      }
      int index = getChildIndex(focus);
      if (index + 1 < getChildrenCount()) {
        setFocusToChild(getChildAt(index + 1));
      }
    }
  }

  @Override
  public void goBackward() {
    Segment focus = getFocusChild();
    if (focus != null) {
      if (focus instanceof SegmentQueue && (((SegmentQueue) focus).canGoBackward())) {
        ((SegmentQueue) focus).goBackward();
      }
      int index = getChildIndex(focus);
      if (index > 0) {
        setFocusToChild(getChildAt(index - 1));
      }
    }
  }

  @Override
  public void addNewSegment(Segment aChildSeg) {
    final int focus = getChildIndex(getFocusChild());
    this.addSegment(aChildSeg, focus + 1);
    setFocusToChild(aChildSeg);
    clearListFrom(focus + 2);
  }

  @Override
  public void clearAllChildren() {
    clearListFrom(0);
  }

  private void clearListFrom(int clearStart) {
    int cnt = getChildrenCount();
    for (int i = cnt - 1; i >= clearStart; i--) {
      Segment segment = getChildAt(i);
      this.removeSegment(segment);
    }
  }

}
