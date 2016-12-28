package com.foxframe.segment.core;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.view.KeyEvent;
import android.view.View;

import com.foxframe.interfaces.KeyEventListener;

/**
 * Created by wuguonan on 2016/8/31 0031. common class
 */
public abstract class Segment extends AbsSegment implements KeyEventListener {

  protected Segment() {
    super(false);
  }

  protected Segment(boolean isRoot) {
    super(isRoot);
  }


  /**
   * 将子Segment的view添加到view tree中
   *
   * @param aView
   *            待显示的view
   */
  @Override
  final void onChildSegmentViewCreated(final View aView, AbsSegment aChildSegment) {
    this.onChildSegmentViewCreated(aView, (Segment) aChildSegment);
  }

  /**
   * 将子Segment的view从view tree中移除
   *
   * @param aView
   *            待移除的view
   */
  @Override
  final void onRemoveChildSegmentView(final View aView, AbsSegment aChildSegment) {
    this.onRemoveChildSegmentView(aView, (Segment) aChildSegment);
  }

  @Override
  final void onChildResumed(AbsSegment aChildSegment) {
    this.onChildResumed((Segment) aChildSegment);
  }

  @Override
  final void onChildPaused(AbsSegment aChildSegment) {
    this.onChildPaused((Segment) aChildSegment);
  }


  @Override
  final void onChildAttached(AbsSegment aChildSegment) {
    this.onChildAttached((Segment) aChildSegment);
  }

  @Override
  final void onChildDetached(AbsSegment aChildSegment) {
    this.onChildDetached((Segment) aChildSegment);
  }

  /**
   * 将子Segment的view添加到view tree中
   *
   * @param aView
   *            待显示的view
   */
  protected abstract void onChildSegmentViewCreated(final View aView, Segment aChildSegment);

  /**
   * 将子Segment的view从view tree中移除
   *
   * @param aView
   *            待移除的view
   */
  protected abstract void onRemoveChildSegmentView(final View aView, Segment aChildSegment);


  protected void onChildResumed(Segment aChildSegment) {

  }

  protected void onChildPaused(Segment aChildSegment) {

  }

  protected void onChildAttached(Segment aChildSegment) {

  }

  protected void onChildDetached(Segment aChildSegment) {

  }

  @MainThread
  @CallSuper
  protected void onActivityResumed() {

  }

  @MainThread
  @CallSuper
  protected void onActivityPaused() {

  }


  @Override
  public boolean onKeyDown(int aKeyCode, KeyEvent aEvent) {
    return false;
  }

  /**
   * 根据segment tree分发key down事件
   *
   * @param aKeyCode
   *            key code
   * @param aEvent
   *            key event
   * @return true表示被消耗，false表示未消耗
   */
  public final boolean dispatchKeyDown(int aKeyCode, KeyEvent aEvent) {
    // 先将事件传递给top child，再自身处理
    Segment forgroundChild = getFocusChild();
    if (forgroundChild != null) {
      if (forgroundChild.dispatchKeyDown(aKeyCode, aEvent)) {
        return true;
      }
    }
    return onKeyDown(aKeyCode, aEvent);
  }

  @Override
  public boolean onKeyUp(int aKeyCode, KeyEvent aEvent) {
    return false;
  }

  /**
   * 根据segment tree分发key up事件
   *
   * @param aKeyCode
   *            key code
   * @param aEvent
   *            key event
   * @return true表示被消耗，false表示未消耗
   */
  public final boolean dispatchKeyUp(int aKeyCode, KeyEvent aEvent) {
    // 先将事件传递给top child，再自身处理
    Segment forgroundChild = getFocusChild();
    if (forgroundChild != null) {
      if (forgroundChild.dispatchKeyUp(aKeyCode, aEvent)) {
        return true;
      }
    }
    return onKeyUp(aKeyCode, aEvent);
  }



  /*****************************************************
   * public 接口
   ****************************************************/

  /**
   * add a child segment, 参数可设置同步或者异步, 参数可指定加入的index
   *
   * @param aChildSegment
   *            child segment
   * @param aIndex
   *            栈中的index
   */
  public final void addSegment(Segment aChildSegment, int aIndex) {
    super.addSegment(aChildSegment, aIndex);
  }

  /**
   * 删除传入的segment及其所有的子segment
   *
   * @param aSegment
   *            传入segment
   */
  public final void removeSegment(Segment aSegment) {
    if (mChildren == null) {
      return;
    }
    super.removeSegment(aSegment);
  }

  /**
   * 将子Segment移植栈顶
   *
   * @param aChildSeg 子Seg
   */
  protected void setFocusToChild(Segment aChildSeg) {
    super.setFocusToChild(aChildSeg);
  }


  /**
   *
   * @param aChild
   *            child segment
   * @return 传入子segment的index，如果无子或者找不到返回-1
   */
  public final int getChildIndex(Segment aChild) {
    return super.getChildIndex(aChild);
  }

  /**
   * @return segment产生的view，因为原生view的限制，所以重新定义一个
   */
  public final View getView() {
    return mView;
  }

  /**
   * @return activity
   */
  public final Activity getActivity() {
    return activity;
  }

  public final Context getAppContext() {
    if (activity != null) {
      return activity.getApplication();
    }
    return null;
  }

  /**
   * @return tag
   */
  public final String getTag() {
    return mTag;
  }

  /**
   * 在addSegment中调用，外部不应该使用
   *
   * @param aTag tag
   */
  public final void setTag(final String aTag) {
    mTag = aTag;
  }


  /**
   * @return true表示有children, false表示木有
   */
  public final boolean hasChildren() {
    return (mChildren != null) && (mChildren.size() > 0);
  }


  public final int getCurState() {
    return mCurState;
  }

  /**
   *
   * @return 返回segment树中的对应的parent segment
   */
  public final Segment getParent() {
    return (Segment) super.getParent();
  }

  /**
   * @param aTag
   *            tag
   * @return 根据tag查找到的节点
   */
  public final Segment find(final String aTag) {
    return (Segment) super.find(aTag);
  }

  /**
   * 根据index获得子segment
   *
   * @param aIndex
   *            index
   * @return 子segment，如果没有则为null
   */
  public final Segment getChildAt(int aIndex) {
    return (Segment) super.getChildAt(aIndex);
  }

  /**
   * 获取前台子Seg
   * @return 前台子Seg
   */
  public final Segment getFocusChild() {
    return (Segment) super.getFocusChild();
  }

  public void dispatchActivityResumed() {
    onActivityResumed();
    if (getFocusChild() != null) {
      getFocusChild().onActivityResumed();
    }
  }

  public final void dispatchActivityPaused() {
    onActivityPaused();
    if (getFocusChild() != null) {
      getFocusChild().onActivityPaused();
    }
  }


}
