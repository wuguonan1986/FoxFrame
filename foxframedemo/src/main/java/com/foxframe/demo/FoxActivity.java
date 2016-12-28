package com.foxframe.demo;

import android.content.res.Configuration;
import android.os.Bundle;

import com.foxframe.framework.SegmentActivity;

/**
 * Created by wuguonan on 2016/9/1 0001.
 */
public class FoxActivity extends SegmentActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    MyMultiWinSegment mul = new MyMultiWinSegment();
    addSegment(mul);
  }
}
