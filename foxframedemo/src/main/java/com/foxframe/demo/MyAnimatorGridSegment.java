package com.foxframe.demo;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foxframe.segment.core.SegmentUtils;
import com.foxframe.segment.extention.AnimtorGridSegment;
import com.foxframe.ui.FoxAnimatorGrid;

/**
 * Created by wuguonan on 2016/9/5 0005.
 */
public class MyAnimatorGridSegment extends AnimtorGridSegment implements MyWin  {
  LinearLayout myView;
  FoxAnimatorGrid slot;
  TextView running;
  TextView tab;
  Button close;
  Button toggle;
  Button newModule;

  int count = 0;
  boolean run = false;

  View.OnClickListener clickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      if (view.equals(close)) {
        removeSegment(getFocusChild());
      } else if (view.equals(newModule)) {
        addNewTab(new MyContentSegment(getChildrenCount()), true);
      } else if (view.equals(toggle)) {
        toggle();
      } else {
//        if (slot.contains(view)) {
//          slot.setFocusChild(view);
//          toggle();
//        }
      }
    }
  };

  public void toggle() {
    if (slot != null) {
      if (slot.getViewMode() == FoxAnimatorGrid.ViewMode.FULLVIEW_MODE) {
        slot.toOverViewMode();
      } else {
        slot.toFullViewMode();
      }
    }
  }


  @Override
  protected View onCreateView(Activity activity) {
    myView = (LinearLayout) View.inflate(getAppContext(), R.layout.fox_animator_grid_seg, null);
    running = (TextView) myView.findViewById(R.id.horse_running);
    slot = (FoxAnimatorGrid) myView.findViewById(R.id.content_slot);
    setChildViewSlot(slot);
    int color = SegmentUtils.getRandomColor();
    myView.setBackgroundColor(color);
    tab = new TextView(getAppContext());
    tab.setBackgroundColor(color);
    tab.setTextColor(Color.WHITE);
    tab.setTextSize(30);
    tab.setGravity(Gravity.CENTER);

    close = (Button) myView.findViewById(R.id.close_child);
    close.setOnClickListener(clickListener);

    toggle = (Button) myView.findViewById(R.id.toggle);
    toggle.setOnClickListener(clickListener);


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


}
