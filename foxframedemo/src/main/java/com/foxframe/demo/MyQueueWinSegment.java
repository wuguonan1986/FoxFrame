package com.foxframe.demo;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foxframe.segment.core.SegmentUtils;
import com.foxframe.segment.extention.SegQueueSegment;

/**
 * Created by wuguonan on 2016/9/1 0001.
 */
public class MyQueueWinSegment extends SegQueueSegment implements MyWin {

  LinearLayout myView;
  MyViewFlipper slot;
  TextView running;
  TextView tab;
  Button back;
  Button forward;
  Button newModule;

  int count = 0;
  boolean run = false;
  int childCnt = 0;

  int animType = 1; //1--f, 2--b

  View.OnClickListener clickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      if (view.equals(back)) {
        animType = 2;
        goBackward();
      } else if (view.equals(forward)) {
        animType = 1;
        goForward();
      } else if (view.equals(newModule)) {
        animType = 1;
        addNewSegment(new MyContentSegment(childCnt ++));
      }
    }
  };


  @Override
  protected View onCreateView(Activity activity) {
    myView = (LinearLayout) View.inflate(getAppContext(), R.layout.fox_queue_win_seg, null);
    running = (TextView) myView.findViewById(R.id.horse_running);
    slot = (MyViewFlipper) myView.findViewById(R.id.content_slot);
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
    forward = (Button) myView.findViewById(R.id.forward);
    forward.setOnClickListener(clickListener);
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
    if (animType == 2) {
      slot.switchBackToVIew(aView);
    } else {
      slot.switchForwardToView(aView);
    }
  }
}
