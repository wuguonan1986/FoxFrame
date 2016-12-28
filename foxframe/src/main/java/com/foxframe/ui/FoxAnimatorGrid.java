package com.foxframe.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by wuguonan on 2016/9/3 0003.
 */
public class FoxAnimatorGrid extends ViewGroup {

  public static final int DEFAULT_COL = 3;

  public static final int DEFAULT_ROW = 3;

  public static final int DEFAULT_DURATION = 300;

  private View focusChild;

  private ViewMode viewMode = ViewMode.FULLVIEW_MODE;

  private int colunms = DEFAULT_COL;

  private int raws = DEFAULT_ROW;

  private float scaleFactor = 0f;

  private ArrayList<View> viewList = new ArrayList<>();


  ValueAnimator animator = new ValueAnimator();
  ValueAnimator animator2 = new ValueAnimator();

  public enum ViewMode {
    OVERVIEW_MODE,
    FULLVIEW_MODE,
  }


  public FoxAnimatorGrid(Context context) {
    super(context);
  }

  public FoxAnimatorGrid(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    measureOnOverViewMde(widthMeasureSpec, heightMeasureSpec);

  }


  private void measureOnOverViewMde(int widthMeasureSpec, int heightMeasureSpec) {
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);

    int itemWidth = width / colunms;
    int itemHeight = height / raws;

    int cnt = getChildCount();
    for (int i = 0; i < cnt; i++) {
      View child = getChildAt(i);
      if (!child.equals(getFocusChild())) {
        child.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
        if (viewMode.equals(ViewMode.FULLVIEW_MODE) && scaleFactor == 1f) {
          child.setVisibility(INVISIBLE);
        } else {
          child.setVisibility(VISIBLE);
        }
      } else {
        int fW = (int) (((colunms - 1) * scaleFactor + 1) * itemWidth);
        int fH = (int) (((raws - 1) * scaleFactor + 1) * itemHeight);
        child.measure(MeasureSpec.makeMeasureSpec(fW, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(fH, MeasureSpec.EXACTLY));
      }

    }

    setMeasuredDimension(width, height);

  }



  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    layoutOnOverViewMde(l, t, r, b);
  }

  private void layoutOnOverViewMde(int l, int t, int r, int b) {
    int width = r - l;
    int height = b - t;

    int itemWidth = width / colunms;
    int itemHeight = height / raws;

    int y = 0;
    int x = 0;
    int index = 0;
    int cnt = viewList.size();

    int focusX = 0;
    int focusY = 0;


    for (int i = 0; i < raws; i++) {
      y = i * itemHeight;
      boolean shouldBreak = false;
      for (int j = 0; j < colunms; j++) {
        x = j * itemWidth;
        if (index < cnt) {
          View child = viewList.get(index);
          if (!child.equals(getFocusChild())) {
            child.layout(x, y, x + child.getMeasuredWidth(), y + child.getMeasuredHeight());
          } else {
            focusX = x;
            focusY = y;
          }
          index++;
        } else {
          shouldBreak = true;
          break;
        }
      }
      if (shouldBreak) {
        break;
      }
    }

    int desx = (int) ((0 - focusX) * scaleFactor + focusX);
    int desy = (int) ((0 - focusY) * scaleFactor + focusY);
    if (getFocusChild() != null) {
      getFocusChild().layout(desx, desy, desx + getFocusChild().getMeasuredWidth(), desy + getFocusChild().getMeasuredHeight());
    }

  }


  @Override
  public void addView(View child, int index, LayoutParams params) {
    super.addView(child, index, params);
    focusChild = child;
    if (index < 0) {
      viewList.add(focusChild);
    } else {
      viewList.add(index, focusChild);
    }

  }

  @Override
  public void removeView(View view) {
    super.removeView(view);
    viewList.remove(view);
  }

  View getFocusChild() {
    if (viewList.contains(focusChild)) {
      return focusChild;
    } else if (viewList.size() > 0) {
      return viewList.get(viewList.size() - 1);
    } else {
      return null;
    }
  }

  public void setViewMode(ViewMode viewMode) {
    this.viewMode = viewMode;
  }

  public ViewMode getViewMode() {
    return viewMode;
  }

  public void setFocusChild(View focusChild) {
    this.focusChild = focusChild;
    super.removeView(focusChild);
    super.addView(focusChild, -1, focusChild.getLayoutParams());
  }

  public void setColunms(int colunms) {
    this.colunms = colunms;
  }

  public void setRaws(int raws) {
    this.raws = raws;
  }


  public void toFullViewMode() {
    setViewMode(ViewMode.FULLVIEW_MODE);
    animator.setDuration(DEFAULT_DURATION);
    animator.setFloatValues(scaleFactor, 1f);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        scaleFactor = (float) animation.getAnimatedValue();
        FoxAnimatorGrid.this.requestLayout();
      }
    });
    animator.start();
  }

  public void toOverViewMode() {
    setViewMode(ViewMode.OVERVIEW_MODE);
    animator2.setDuration(DEFAULT_DURATION);
    animator2.setFloatValues(scaleFactor, 0f);
    animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        scaleFactor = (float) animation.getAnimatedValue();
        FoxAnimatorGrid.this.requestLayout();
      }
    });
    animator2.start();
  }

  public void focusToAndGoFullView(View childView) {
    setFocusChild(childView);
    toFullViewMode();
  }

  public boolean contains(View child) {
    boolean ret = false;
    for (int i = 0; i < this.getChildCount(); i++) {
      if (getChildAt(i).equals(child)) {
        return true;
      }
    }
    return ret;
  }


}
