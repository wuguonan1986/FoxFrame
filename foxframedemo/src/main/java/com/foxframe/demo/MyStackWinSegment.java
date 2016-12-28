package com.foxframe.demo;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foxframe.segment.core.SegmentUtils;
import com.foxframe.segment.extention.SegStackSegment;
import com.foxframe.ui.FoxViewStack;

/**
 * Created by wuguonan on 2016/9/2 0002.
 */
public class MyStackWinSegment extends SegStackSegment implements MyWin {

  LinearLayout myView;
  FoxViewStack slot;
  TextView running;
  TextView tab;
  Button back;
  Button newModule;

  int count = 0;
  boolean run = false;


  View.OnClickListener clickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      if (view.equals(back)) {
        popSegment();
      } else if (view.equals(newModule)) {
        pushSegment(new MyContentSegment(getChildrenCount()));
      }
    }
  };


  @Override
  protected View onCreateView(Activity activity) {
    myView = (LinearLayout) View.inflate(getAppContext(), R.layout.fox_stack_win_seg, null);
    running = (TextView) myView.findViewById(R.id.horse_running);
    slot = (FoxViewStack) myView.findViewById(R.id.content_slot);
    slot.setUnderLayerDoAnim(true);
    setChildViewSlot(slot);
    int color = SegmentUtils.getRandomColor();
    myView.setBackgroundColor(color);
    tab = new TextView(getAppContext());
    tab.setBackgroundColor(color);
    tab.setTextColor(Color.WHITE);
    tab.setTextSize(30);
    tab.setGravity(Gravity.CENTER);

    back = (Button) myView.findViewById(R.id.backward);
    back.setOnClickListener(clickListener);
    newModule = (Button) myView.findViewById(R.id.new_module);
    newModule.setOnClickListener(clickListener);


    return myView;
  }

  @Override
  protected void onResume() {
    super.onResume();
    startRunning();
    tab.setText("*");
  }

  @Override
  protected void onPause() {
    super.onPause();
    stopRunning();
    tab.setText("");
  }

  @Override
  public View getTab() {
    return tab;
  }

  private void running() {
    if (run) {
      count++;
      if (count == 10) {
        count = 0;
      }
      running.setText(String.valueOf(count));
      myView.postDelayed(new Runnable() {
        @Override
        public void run() {
          running();
        }
      }, 200);
    }
  }

  private void stopRunning() {
    run = false;
  }

  private void startRunning() {
    run = true;
    running();
  }

  @Override
  protected void moveViewToForground(View aView) {
    ViewParent vp = aView.getParent();
    if (vp != null) {
      if (vp.equals(slot)) {
        return;
      }
      if (vp instanceof ViewGroup) {
        ((ViewGroup) vp).removeView(aView);
      }
    }
    slot.pushView(aView);
  }
}
