package com.foxframe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.foxframe.R;

/**
 * Created by wuguonan on 2016/9/2 0002.
 */
public class FoxViewStack extends FrameLayout {

  private Animation pushViewInAnim;

  private Animation pushViewOutAnim;

  private Animation popViewInAnim;

  private Animation popViewOutAnim;

  private boolean isUnderLayerDoAnim = false;

  public FoxViewStack(Context context) {
    super(context);
  }

  public FoxViewStack(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
  }

  public void pushView(View aView) {
    final View priView = getTopView();
    super.addView(aView);
    if (getPushViewInAnim() != null) {
      aView.startAnimation(getPushViewInAnim());
    }
    if (isUnderLayerDoAnim && getPushViewOutAnim() != null && priView != null) {
      getPushViewOutAnim().setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
          priView.setVisibility(GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
      });
      priView.startAnimation(getPushViewOutAnim());
    } else {
      if (priView != null) {
        priView.setVisibility(GONE);
      }

    }
  }

  public void popView() {
    final View topView = getTopView();
    View nextView = getNextView();
    if (topView != null) {
      if (getPopViewOutAnim() != null) {
        getPopViewOutAnim().setAnimationListener(new Animation.AnimationListener() {
          @Override
          public void onAnimationStart(Animation animation) {

          }

          @Override
          public void onAnimationEnd(Animation animation) {
            FoxViewStack.this.post(new Runnable() {
              @Override
              public void run() {
                FoxViewStack.super.removeView(topView);
              }
            });
          }

          @Override
          public void onAnimationRepeat(Animation animation) {

          }
        });
        topView.startAnimation(getPopViewOutAnim());
      } else {
        FoxViewStack.super.removeView(topView);
      }
    }
    if (nextView != null) {
      nextView.setVisibility(VISIBLE);
      if (isUnderLayerDoAnim && getPopViewInAnim() != null) {
        nextView.startAnimation(getPopViewInAnim());
      }
    }
  }

  @Override
  public void removeView(View aView) {
    if (isTopView(aView)) { // 删除顶层View
      popView();
    } else{ // 删除非顶层View
      super.removeView(aView);
    }
  }

  private View getTopView() {
    int cnt = getChildCount();
    return getChildAt(cnt - 1);
  }

  private View getNextView() {
    int cnt = getChildCount();
    return getChildAt(cnt - 2);
  }

  private boolean isTopView(View aView) {
    if (aView == null) {
      return false;
    }
    int cnt = getChildCount();
    return aView.equals(getChildAt(cnt - 1));
  }

  public void setPushViewInAnim(Animation pushViewInAnim) {
    this.pushViewInAnim = pushViewInAnim;
  }

  public void setPushViewOutAnim(Animation pushViewOutAnim) {
    this.pushViewOutAnim = pushViewOutAnim;
  }

  public void setPopViewInAnim(Animation popViewInAnim) {
    this.popViewInAnim = popViewInAnim;
  }

  public void setPopViewOutAnim(Animation popViewOutAnim) {
    this.popViewOutAnim = popViewOutAnim;
  }


  public void setUnderLayerDoAnim(boolean underLayerDoAnim) {
    isUnderLayerDoAnim = underLayerDoAnim;
  }


  public Animation getPushViewInAnim() {
    if (pushViewInAnim == null) {
      pushViewInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.view_flipper_right_in);
    }
    return pushViewInAnim;
  }

  public Animation getPushViewOutAnim() {
    if (pushViewOutAnim == null) {
      pushViewOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.view_flipper_left_out);
    }
    return pushViewOutAnim;
  }

  public Animation getPopViewInAnim() {
    if (popViewInAnim == null) {
      popViewInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.view_flipper_left_in);
    }
    return popViewInAnim;
  }

  public Animation getPopViewOutAnim() {
    if (popViewOutAnim == null) {
      popViewOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.view_flipper_right_out);
    }
    return popViewOutAnim;
  }
}
