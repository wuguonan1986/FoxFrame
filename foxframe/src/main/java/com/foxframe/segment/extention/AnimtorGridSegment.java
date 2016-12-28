package com.foxframe.segment.extention;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.foxframe.interfaces.MultiTabHolder;
import com.foxframe.segment.core.Segment;
import com.foxframe.ui.FoxAnimatorGrid;

/**
 * Created by wuguonan on 2016/9/5 0005.
 */
public class AnimtorGridSegment extends Segment implements MultiTabHolder {
  /**
   * 窗口视图插槽
   */
  private FoxAnimatorGrid childViewSlot;

  private ViewGroup myView;


  @Override
  protected void onChildSegmentViewCreated(View aView, Segment aChildSegment) {
    int index = getChildIndex(aChildSegment);
    if (childViewSlot != null) {
      childViewSlot.addView(aView, index);
    }
  }

  @Override
  protected void onRemoveChildSegmentView(View aView, Segment aChildSegment) {
    if (childViewSlot != null) {
      childViewSlot.removeView(aView);
    }
  }

  @Override
  protected void onChildResumed(Segment aChildSegment) {
    if (childViewSlot != null) {
      childViewSlot.setFocusChild(aChildSegment.getView());
    }
  }


  @Override
  protected View onCreateView(Activity activity) {
    if (myView == null) {
      myView = new FrameLayout(getAppContext());
      childViewSlot = new FoxAnimatorGrid(getAppContext());
    }
    return myView;
  }

  @Override
  protected void onDestroyView() {
    myView = null;
    childViewSlot = null;
  }

  public void setChildViewSlot(FoxAnimatorGrid childViewSlot) {
    this.childViewSlot = childViewSlot;
  }

  public void setMyView(ViewGroup myView) {
    this.myView = myView;
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
