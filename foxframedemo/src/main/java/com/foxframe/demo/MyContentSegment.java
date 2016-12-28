package com.foxframe.demo;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.foxframe.segment.core.Segment;
import com.foxframe.segment.core.SegmentUtils;
import com.foxframe.ui.FoxAnimatorGrid;

/**
 * Created by wuguonan on 2016/9/1 0001.
 */
public class MyContentSegment extends Segment {

  public MyContentSegment(int tag) {
    setTag(String.valueOf(tag));
  }

  @Override
  protected void onChildSegmentViewCreated(View aView, Segment aChildSegment) {

  }

  @Override
  protected void onRemoveChildSegmentView(View aView, Segment aChildSegment) {

  }

  @Override
  protected View onCreateView(Activity activity) {
    TextView view = new TextView(getAppContext());
    view.setBackgroundColor(SegmentUtils.getRandomColor());
    view.setGravity(Gravity.CENTER);
    view.setTextSize(30);
    view.setTextColor(Color.WHITE);
    view.setText(getTag());
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MyContentSegment.this.runMySelf();
        Segment parent = getParent();
        if (parent != null && parent instanceof MyAnimatorGridSegment) {
          ((MyAnimatorGridSegment) parent).toggle();
        }
      }
    });
    return view;
  }

  @Override
  protected void onDestroyView() {

  }

}
