package com.foxframe.segment.core;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuguonan on 2016/8/31 0031. base segment class
 */
public abstract class AbsSegment {
  /**
   * debug
   */
  private static final boolean DEBUG = true;

  /**
   * 初始状态，还没有创建成功
   */
  private static final int STATE_INITED = 0;
  /**
   * 对象创建成功
   */
  private static final int STATE_ATTACHED = 1;
  /**
   * 视图对象创建成功
   */
  private static final int STATE_PREPARED = 2;
  /**
   * 启动状态，但是还放在该状态下
   */
  private static final int STATE_RUNNING = 3;
  /**
   * 当前状态
   */
  int mCurState = STATE_INITED;
  /**
   * MSG: move state
   */
  private static final int MSG_ACTION_ATTACH = 1;
  private static final int MSG_ACTION_START = 2;
  private static final int MSG_ACTION_RESUME = 3;
  private static final int MSG_ACTION_PAUSE = 4;
  private static final int MSG_ACTION_STOP = 5;
  private static final int MSG_ACTION_DETACH = 6;

  private static final String[] STATE = {"INITED", "ATTACHED", "PREPARED", "RUNNING"};


  /**
   * activity
   */
  Activity activity;
  /**
   * segment产生的view，因为原生view的限制，所以重新定义一个
   */
  View mView;
  /**
   * 父segment, 如果没有则为null
   */
  private AbsSegment mParent;

  boolean isRoot = false;

  /**
   * tag
   */
  String mTag = "";


  /**
   * 焦点子Segment
   */
  private AbsSegment mFocusChild;

  /**
   * 子segment list，当没有子segment时，为null
   */
  protected List<AbsSegment> mChildren;



  /**
   * handler with main looper
   */
  private static Handler sHandler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      dealWithMsg(msg);
      SegmentUtils.removeMsg(msg);
      super.handleMessage(msg);
    }
  };



  /**
   * 构造方法
   */
  AbsSegment(boolean isRoot) {
    this.isRoot = isRoot;
  }

  /*****************************************************
   * 供各个子类继承实现的回调
   ****************************************************/

  /**
   * @param activity activity
   */
  @MainThread
  @CallSuper
  protected void onAttach(Activity activity) {
    AbsSegment parent = getParent();
    if (parent != null) {
      parent.onChildAttached(this);
    }
  }

  /**
   * @param activity activity
   * @return 视图
   */
  @MainThread
  protected abstract View onCreateView(Activity activity);

  /**
   * start
   */
  @MainThread
  @CallSuper
  protected void onResume() {
    AbsSegment parent = getParent();
    if (parent != null) {
      parent.onChildResumed(this);
    }
  }



  /**
   * stop
   */
  @MainThread
  @CallSuper
  protected void onPause() {
    AbsSegment parent = getParent();
    if (parent != null) {
      parent.onChildPaused(this);
    }
  }

  /**
   * destroy view
   */
  @MainThread
  protected abstract void onDestroyView();

  /**
   * destroy
   */
  @MainThread
  @CallSuper
  protected void onDetach() {
    AbsSegment parent = getParent();
    if (parent != null) {
      parent.onChildDetached(this);
    }
  }

  @MainThread
  @CallSuper
  protected void onAllChildRemoved() {

  }


  /**
   * 获取前台子Seg
   *
   * @return 前台子Seg
   */
  public AbsSegment getFocusChild() {
    return mFocusChild;
  }

  /**
   * @return children count
   */
  public final int getChildrenCount() {
    if (mChildren == null) {
      return 0;
    }
    return mChildren.size();
  }



  /**
   * 将子Segment移植栈顶
   *
   * @param aChildSeg 子Seg
   */
  void setFocusToChild(AbsSegment aChildSeg) {

    SegmentUtils.checkUiThread();
    int index = getChildIndex(aChildSeg);
    if (index >= 0 && index < getChildrenCount()) {
      if (mFocusChild != null) {
        if (!mFocusChild.equals(aChildSeg)) {
          pauseRunningSeg();
          mFocusChild = aChildSeg;
          startFocusSeg();
          resumeFocusSeg();
        }
      } else {
        mFocusChild = aChildSeg;
        startFocusSeg();
        resumeFocusSeg();
      }
    }
  }


  public final void runMySelf() {
    if (mParent != null) {
      mParent.runMySelf();
      mParent.setFocusToChild(this);
    } else if (isRoot) {
      attach();
      start();
      resume();
    }
  }

  public final void prepareMyself() {
    attach();
    start();
  }

  /**
   * 将自己从父中移除，结束自己
   */
  public final void finish() {
    if (mParent != null) {
      mParent.removeSegment(this);
    }
  }




  /**
   * 递归移除所有子元素
   */
  public void removeAllChildren() {
    SegmentUtils.checkUiThread();
    if (mChildren != null) {
      int size = mChildren.size();
      for (int i = size - 1; i >= 0; i--) {
        AbsSegment child = mChildren.get(i);
        child.removeAllChildren();
        removeSegment(child);
      }
    }
  }


  /*****************************************************/


  /*****************************************************
   * 此部分接口由BdSegment封装后再public出去
   ****************************************************/

  /**
   * @return 返回segment树中的对应的parent segment
   */
  AbsSegment getParent() {
    return mParent;
  }

  /**
   * @param aTag tag
   * @return 根据tag查找到的节点
   */
  AbsSegment find(final String aTag) {
    if (!TextUtils.isEmpty(mTag) && mTag.equals(aTag)) {
      return this;
    }
    if (mChildren != null) {
      for (AbsSegment child : mChildren) {
        final AbsSegment hit = child.find(aTag);
        if (hit != null) {
          return hit;
        }
      }
    }
    return null;
  }

  /**
   * 根据index获得子segment
   *
   * @param aIndex index
   * @return 子segment，如果没有则为null
   */
  AbsSegment getChildAt(int aIndex) {
    if (mChildren == null) {
      return null;
    }
    if (aIndex < 0 || aIndex >= mChildren.size()) {
      return null;
    }
    return mChildren.get(aIndex);
  }

  /**
   * @param aChild child segment
   * @return 传入子segment的index，如果无子或者找不到返回-1
   */
  int getChildIndex(AbsSegment aChild) {
    int result = -1;
    if (mChildren != null) {
      result = mChildren.indexOf(aChild);
    }
    return result;
  }


  /**
   * add a child segment, 参数可设置同步或者异步, 参数可指定加入的index
   *
   * @param aChildSegment child segment
   * @param aIndex 栈中的index
   */
  void addSegment(final AbsSegment aChildSegment, final int aIndex) {

    SegmentUtils.checkUiThread();
    if (aChildSegment == null) {
      throw new IllegalStateException("The child Segment to be added is null !");
    }
    if (aChildSegment.getParent() != null) {
      throw new IllegalStateException("The child Segment ["
          + aChildSegment.mTag + "] to be added already has parent Segment !");
    }
    checkChildrenList();
    int count = mChildren.size();


    /**
     * 再resume新的Segment
     */
    aChildSegment.mParent = AbsSegment.this;
    if (aIndex >= 0 && aIndex < count) {
      mChildren.add(aIndex, aChildSegment);
    } else {
      mChildren.add(aChildSegment);
    }
    aChildSegment.setActivity(activity);
    aChildSegment.attach();
  }


  /**
   * pause焦点Segment
   */
  private void pauseRunningSeg() {
    if (mFocusChild != null) {
      mFocusChild.pause();
    }
  }

  /**
   * 启动焦点Segment
   */
  private void startFocusSeg() {
    if (mFocusChild != null) {
      mFocusChild.start();
    }
  }

  /**
   * resume焦点Segment
   */
  private void resumeFocusSeg() {
    /**
     * 出于时序问题考虑，此处需要将焦点Segment的resume动作post出去
     * 因为要等待父Segment进入正确的RUNNING状态
     */

    if (mFocusChild != null) {
      mFocusChild.resume();
    }
  }

  /**
   * 删除传入的segment及其所有的子segment
   *
   * @param index 传入index
   */
  protected void removeSegment(int index) {
    int count = getChildrenCount();
    if (index >= count || index < 0) {
      return;
    }
    AbsSegment segment = getChildAt(index);
    removeSegment(segment);

  }


  /**
   * 删除传入的segment及其所有的子segment
   *
   * @param aSegment 传入segment
   */
  protected void removeSegment(final AbsSegment aSegment) {
    SegmentUtils.checkUiThread();
    if (aSegment == null) {
      return;
    }
    if (mChildren == null) {
      return;
    }
    int index = getChildIndex(aSegment);

    int preForgroundSeg = -1;

    if (mFocusChild != null && mFocusChild.equals(aSegment)) { // 若移除焦点seg先pause焦点seg
      pauseRunningSeg();
      preForgroundSeg = getChildIndex(mFocusChild);
    }

    aSegment.stop();
    aSegment.detach();

    if (mFocusChild != null && mFocusChild.equals(aSegment)) {
      int next = getNextRunningSeg(preForgroundSeg);
      mFocusChild = getChildAt(next);
      startFocusSeg();
      resumeFocusSeg();
    }
    if (index >= 0) {
      if (mChildren != null) {
        mChildren.remove(aSegment);
        if (mChildren.size() <= 0) {
          onAllChildRemoved();
        }
      }
    }
  }



  /**
   * 子Segment的切换
   * 
   * @param aPreRunningSeg RUNNING_Segment标识
   * @return 下一个runnning的Segment
   */
  protected int getNextRunningSeg(int aPreRunningSeg) {
    if (aPreRunningSeg >= 1) {
      return aPreRunningSeg - 1;
    } else if (mChildren.size() >= 2) {
      return 1;
    } else {
      return -1;
    }
  }


  /*****************************************************/

  /*****************************************************
   * 可供子类调用，不可被外部调用
   ****************************************************/

  /**
   * 停止，被外部主动调用
   */
  void stop() {
    /**
     * 确保子segment先停止
     */
    if (mChildren != null) {
      int top = mChildren.size() - 1;
      for (int i = top; i >= 0; i--) {
        AbsSegment child = mChildren.get(i);
        child.stop(); // 自己在PREPARED状态下，所有子的状态只可能是ATTACHED或者PREPARED
      }
    }
    Log.d("", "[" + mTag + "]---stop");
    Message msg = sHandler.obtainMessage();
    msg.obj = this;
    msg.what = MSG_ACTION_STOP;
    SegmentUtils.enqueueMsg(msg);
    msg.sendToTarget();
  }


  /**
   * 启动，被外部主动调用
   */
  void start() {
    /**
     * 确保父segment先启动
     */
    if (mParent != null) {
      mParent.start();
    }
    Log.d("", "[" + mTag + "]---start");
    Message msg = sHandler.obtainMessage();
    msg.obj = this;
    msg.what = MSG_ACTION_START;
    SegmentUtils.enqueueMsg(msg);
    msg.sendToTarget();
  }


  /**
   * 切前台，供父调用
   */
  void resume() {
    Log.d("", "[" + mTag + "]---resume");
    Message msg = sHandler.obtainMessage();
    msg.obj = this;
    msg.what = MSG_ACTION_RESUME;
    SegmentUtils.enqueueMsg(msg);
    msg.sendToTarget();

    /**
     * 父resume后其顶端（list最后一项）的子resume
     */
    if (mFocusChild != null) {
      mFocusChild.resume();
    }
  }

  /**
   * 切后台，供父调用
   */
  void pause() {
    pauseRunningSeg();
    Log.d("", "[" + mTag + "]---pause");
    Message msg = sHandler.obtainMessage();
    msg.obj = this;
    msg.what = MSG_ACTION_PAUSE;
    SegmentUtils.enqueueMsg(msg);
    msg.sendToTarget();
  }


  /**
   * 将子Segment的view添加到view tree中
   *
   * @param aView 待显示的view
   * @param aChildSegment 子seg
   */
  void onChildSegmentViewCreated(final View aView, AbsSegment aChildSegment) {
    // 由具体子类实现
  }

  /**
   * 将子Segment的view从view tree中移除
   *
   * @param aView 待移除的view
   * @param aChildSegment 子seg
   */
  void onRemoveChildSegmentView(final View aView, AbsSegment aChildSegment) {
    // 由具体子类实现
  }


  void onChildResumed(AbsSegment aChildSegment) {

  }

  void onChildPaused(AbsSegment aChildSegment) {

  }

  void onChildAttached(AbsSegment aChildSegment) {

  }

  void onChildDetached(AbsSegment aChildSegment) {

  }


  /*****************************************************/


  /*****************************************************
   * 私有方法，不外露
   ****************************************************/




  void setActivity(Activity activity) {
    this.activity = activity;
  }

  /**
   * do when add into parent
   * 包内可见，子类rootSegment需要调用
   */
  void attach() {
    Log.d("", "[" + mTag + "]---attach");
    Message msg = sHandler.obtainMessage();
    msg.obj = this;
    msg.what = MSG_ACTION_ATTACH;
    SegmentUtils.enqueueMsg(msg);
    msg.sendToTarget();
  }

  /**
   * do when remove from parent
   * 包内可见，子类rootSegment需要调用
   */
  void detach() {
    /**
     * 确保子segment先移除
     */
    if (mChildren != null) {
      int top = mChildren.size() - 1;
      for (int i = top; i >= 0; i--) {
        AbsSegment child = mChildren.get(i);
        /**
         * 这里不能用removeSegment，因为removeSegment涉及到焦点的切换
         * 会让子触发父本生状态改变，导致循环状态错乱
         */
        mChildren.remove(i);
        child.detach();
      }
    }
    Log.d("", "[" + mTag + "]---detach");
    Message msg = sHandler.obtainMessage();
    msg.obj = this;
    msg.what = MSG_ACTION_DETACH;
    SegmentUtils.enqueueMsg(msg);
    msg.sendToTarget();
  }


  static void dealWithMsg(Message msg) {
    AbsSegment seg = (AbsSegment) msg.obj;
    if (seg != null) {
      switch (msg.what) {
        case MSG_ACTION_ATTACH:
          seg.performAttach();
          break;
        case MSG_ACTION_START:
          seg.performStart();
          break;
        case MSG_ACTION_RESUME:
          seg.performResume();
          break;
        case MSG_ACTION_PAUSE:
          seg.performPause();
          break;
        case MSG_ACTION_STOP:
          seg.performStop();
          break;
        case MSG_ACTION_DETACH:
          seg.performDetach();
          break;
        default:
          break;
      }
      sHandler.removeMessages(msg.what, msg.obj);
    }
  }



  /**
   * do when attach to parent
   */
  private void performAttach() {
    if (mCurState == STATE_INITED) {
      moveToState(STATE_ATTACHED);
    }
  }

  /**
   * 启动segment，主要工作是调用onCreatView
   */
  private void performStart() {
    if (mCurState == STATE_ATTACHED) {
      moveToState(STATE_PREPARED);
    }
  }

  /**
   * 切前台显示，会触发onResume回调
   */
  private void performResume() {
    if (mCurState == STATE_PREPARED) {
      AbsSegment parent = getParent();
      if (isRoot || (parent != null && parent.mCurState == STATE_RUNNING)) {
        moveToState(STATE_RUNNING);
      }
    }
  }

  /**
   * 切后台，会触发onPause
   */
  private void performPause() {
    if (mCurState == STATE_RUNNING) {
      moveToState(STATE_PREPARED);
    }
  }

  /**
   * 停止，触发onDestoryView()
   */
  private void performStop() {
    if (mCurState == STATE_PREPARED) {
      moveToState(STATE_ATTACHED);
    }
  }

  /**
   * 从父中移除时被调用，触发onDestory()
   */
  private void performDetach() {
    if (mCurState == STATE_ATTACHED) {
      moveToState(STATE_INITED);
    }
  }


  /**
   * segment的状态机管理
   *
   * @param aNewState 新状态
   */
  private void moveToState(final int aNewState) {
    if (DEBUG) {
      Log.d("tracestate",
          "moveToState[" + this.getClass().getSimpleName() + "]" + STATE[mCurState] + " ---> " + STATE[aNewState]);
    }
    int oldState = mCurState;
    mCurState = aNewState;
    if (oldState < aNewState) {
      switch (aNewState) {
        case STATE_ATTACHED:
          onAttach(activity);
          break;
        case STATE_PREPARED:
          mView = onCreateView(activity);
          /**
           * View创建好后再加入视图树
           */
          if (mParent != null) {
            mParent.onChildSegmentViewCreated(mView, this);
          }
          break;
        case STATE_RUNNING:
          onResume();
          break;
      }
    } else if (oldState > aNewState) {
      switch (aNewState) {
        case STATE_INITED:
          onDetach();
          mParent = null;
          break;
        case STATE_ATTACHED:
          /**
           * View先从视图树移除再销毁
           */
          if (mParent != null) {
            mParent.onRemoveChildSegmentView(mView, this);
          }
          onDestroyView();
          mView = null;
          break;
        case STATE_PREPARED:
          onPause();
          break;

      }
    }
  }



  /**
   * 检测children list是否创建
   */
  private void checkChildrenList() {
    if (mChildren == null) {
      mChildren = new ArrayList<AbsSegment>();
    }
  }


}
