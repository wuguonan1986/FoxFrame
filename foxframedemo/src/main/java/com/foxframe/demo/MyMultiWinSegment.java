package com.foxframe.demo;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;

import com.foxframe.segment.core.Segment;
import com.foxframe.segment.extention.MultiTabSegment;

/**
 * Created by wuguonan on 2016/9/1 0001.
 */
public class MyMultiWinSegment extends MultiTabSegment {


  Button add;
  Button close;
  ViewPager slot;
  LinearLayout winList;

  View.OnClickListener clickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      if (view.equals(add)) {

        int a = ((int) (Math.random() * 100)) % 3;
        if (a == 0) {
          addNewTab(new MyQueueWinSegment(), true);
        } else if (a == 1) {
          addNewTab(new MyStackWinSegment(), true);
        } else {
          addNewTab(new MyAnimatorGridSegment(), true);
        }

      } else if (view.equals(close)) {
        removeSegment(getFocusChild());
      }
    }
  };

  @Override
  protected void onChildSegmentViewCreated(View aView, final Segment aChildSegment) {
    super.onChildSegmentViewCreated(aView, aChildSegment);
    if (winList != null && aChildSegment instanceof MyWin) {
      View view = ((MyWin) aChildSegment).getTab();
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
          LinearLayout.LayoutParams.MATCH_PARENT);
      lp.weight = 1;
      lp.gravity = Gravity.CENTER_VERTICAL;
      int index = getChildIndex(aChildSegment);
      winList.addView(view, index, lp);
      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          setFocusToChild(aChildSegment);
        }
      });
    }
    pagerAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onRemoveChildSegmentView(View aView, Segment aChildSegment) {
    super.onRemoveChildSegmentView(aView, aChildSegment);
    if (winList != null && aChildSegment instanceof MyWin) {
      winList.removeView(((MyWin) aChildSegment).getTab());
    }
    pagerAdapter.notifyDataSetChanged();
  }

  @Override
  protected View onCreateView(Activity activity) {
    LinearLayout multiWin = (LinearLayout) View.inflate(getAppContext(),
        R.layout.fox_multi_win_seg, null);
    setMyView(multiWin);
    add = (Button) multiWin.findViewById(R.id.add_new);
    add.setOnClickListener(clickListener);
    close = (Button) multiWin.findViewById(R.id.close);
    close.setOnClickListener(clickListener);
    slot = (ViewPager) multiWin.findViewById(R.id.mutli_win_slot);
    winList = (LinearLayout) multiWin.findViewById(R.id.win_list);

    slot.setAdapter(pagerAdapter);
    slot.addOnPageChangeListener(listener);

    return multiWin;
  }

  @Override
  protected void onChildResumed(Segment aChildSegment) {
    super.onChildResumed(aChildSegment);
    slot.setCurrentItem(getChildIndex(aChildSegment));

  }

  @Override
  protected void onDestroyView() {
    super.onDestroyView();
  }

  @Override
  protected void moveViewToForground(View aView) {

  }


  PagerAdapter pagerAdapter = new PagerAdapter() {
    @Override
    public int getCount() {
      return getChildrenCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
      // TODO Auto-generated method stub
      if (object != null && object instanceof View) {
        Segment segment = getChildAt(position);
        if (segment != null && object.equals(segment.getView())) {
          return;
        }
        container.removeView((View) object);
      }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      View view = getChildAt(position).getView();
      ViewParent vp = view.getParent();
      if (vp != null && vp instanceof ViewGroup) {
        ((ViewGroup) vp).removeView(view);
      }
      container.addView(view, position);
      return view;
    }

    @Override
    public int getItemPosition(Object object) {
      int cnt = getChildrenCount();
      for (int i = 0; i < cnt; i++) {
        if (object.equals(getChildAt(i).getView())) {
          return i;
        }
      }
      return POSITION_NONE;
    }


  };

  ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
      setFocusToChild(getChildAt(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
  };


}
