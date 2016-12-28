package com.foxframe.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

/**
 * Created by wuguonan on 2016/9/1 0001.
 */
public class MyViewFlipper extends ViewFlipper {

  private Animation mForwardInAnim;

  private Animation mForwardOutAnim;

  private Animation mBackInAnim;

  private Animation mBackOutAnim;

  private ViewFlipperAnimListener mForWardAnimListener;

  private ViewFlipperAnimListener mBackAnimListener;

  private int mForwardFinishCnt = 0;

  private int mBackFinishCnt = 0;


  public MyViewFlipper(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    mForwardInAnim = AnimationUtils.loadAnimation(context, R.anim.view_flipper_right_in);
    mForwardOutAnim = AnimationUtils.loadAnimation(context, R.anim.view_flipper_left_out);
    mBackInAnim = AnimationUtils.loadAnimation(context, R.anim.view_flipper_left_in);
    mBackOutAnim = AnimationUtils.loadAnimation(context, R.anim.view_flipper_right_out);
  }


  public MyViewFlipper(Context context) {
    super(context, null);

  }

  /**
   * 前进
   * @param aView 传入的View为要显示的View
   */
  public void switchForwardToView(View aView) {
    ViewParent parent = aView.getParent();
    if (parent != null && parent instanceof ViewGroup) {
      ((ViewGroup) parent).removeView(aView);
    }
    int cnt = getChildCount();
    if (cnt >= 1) { // 之前存在另一个oldview
      addView(aView); // 先加进来在做动画
      mForwardInAnim.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
          post(new Runnable() {
            @Override
            public void run() {
              onForwardAnimEnd();
            }
          });
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
      });
      mForwardOutAnim.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
          post(new Runnable() {
            @Override
            public void run() {
              onForwardAnimEnd();
            }
          });
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
      });
      setInAnimation(mForwardInAnim);
      setOutAnimation(mForwardOutAnim);
      mForwardFinishCnt = 0;
      showNext(); // 会自动把之前的oldview设置成gone
    } else {
      addView(aView); // 直接添加
    }
  }

  /**
   * 后退,需注意,传入的View不是要被remove的View
   * @param aView 传入的View为要显示的View
   */
  public void switchBackToVIew(View aView) {
    ViewParent parent = aView.getParent();
    if (parent != null && parent instanceof ViewGroup) {
      ((ViewGroup) parent).removeView(aView);
    }
    int cnt = getChildCount();
    if (cnt >= 1) { // 之前存在另一个oldview
      final View oldTop = getChildAt(cnt - 1);
      addView(aView, cnt - 1);
      mBackInAnim.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
          post(new Runnable() {
            @Override
            public void run() {
              onBackAnimEnd();
            }
          });
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
      });
      mBackOutAnim.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
          post(new Runnable() {
            @Override
            public void run() {
              onBackAnimEnd();
              removeView(oldTop);
            }
          });
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
      });
      setInAnimation(mBackInAnim);
      setOutAnimation(mBackOutAnim);
      mBackFinishCnt = 0;
      showPrevious();
    } else {
      addView(aView);
    }
  }

  private synchronized void onForwardAnimEnd() {
    mForwardFinishCnt++;
    if (mForwardFinishCnt >= 2) {
      if (mForWardAnimListener != null) {
        mForWardAnimListener.onAnimEnd();
        mForwardFinishCnt = 0;
      }
    }
  }
  private synchronized void onBackAnimEnd() {
    mBackFinishCnt++;
    if (mBackFinishCnt >= 2) {
      if (mBackAnimListener != null) {
        mBackAnimListener.onAnimEnd();
        mBackFinishCnt = 0;
      }
    }
  }


  public void setForwardInAnim(Animation mForwardInAnim) {
    this.mForwardInAnim = mForwardInAnim;
  }

  public void setForwardOutAnim(Animation mForwardOutAnim) {
    this.mForwardOutAnim = mForwardOutAnim;
  }

  public void setBackInAnim(Animation mBackInAnim) {
    this.mBackInAnim = mBackInAnim;
  }

  public void setBackOutAnim(Animation mBackOutAnim) {
    this.mBackOutAnim = mBackOutAnim;
  }

  public void setForWardAnimListener(ViewFlipperAnimListener mForWardAnimListener) {
    this.mForWardAnimListener = mForWardAnimListener;
  }

  public void setBackAnimListener(ViewFlipperAnimListener mBackAnimListener) {
    this.mBackAnimListener = mBackAnimListener;
  }

  public interface ViewFlipperAnimListener {
    void onAnimEnd();
  }
}
