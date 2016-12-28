package com.foxframe.framework;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.foxframe.segment.core.Segment;
import com.foxframe.segment.core.SegmentUtils;

/**
 * Created by wuguonan on 2016/8/31 0031.
 */
public abstract class SegmentActivity extends Activity {

  private boolean isKeyDownConsumed = false;

  ActivityRootSegment rootSegment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FrameLayout rootView = new FrameLayout(getApplicationContext());
    setContentView(rootView);
    rootSegment = new ActivityRootSegment(this, rootView);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (rootSegment != null) {
      rootSegment.dispatchActivityResumed();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (rootSegment != null) {
      rootSegment.dispatchActivityPaused();
    }
  }

  @Override
  protected void onDestroy() {
    if (rootSegment != null) {
      rootSegment.stopMyself();
    }
    SegmentUtils.excutePendingActions();
    Log.d("tracestate", "Activity[" + this.getClass().getSimpleName() + "] destory!");
    super.onDestroy();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (rootSegment != null) {
      isKeyDownConsumed = rootSegment.dispatchKeyDown(keyCode, event);
    }
    return isKeyDownConsumed || super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    boolean ret = false;
    if (isKeyDownConsumed && rootSegment != null) {
      ret = rootSegment.dispatchKeyUp(keyCode, event);
    }
    isKeyDownConsumed = true;
    return ret || super.onKeyUp(keyCode, event);
  }

  public void addSegment(Segment segment) {
    if (rootSegment != null) {
      rootSegment.addSegment(segment, rootSegment.getChildrenCount());
      segment.runMySelf();
    }
  }
}
