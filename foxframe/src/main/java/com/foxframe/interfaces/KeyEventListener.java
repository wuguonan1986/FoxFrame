package com.foxframe.interfaces;

import android.view.KeyEvent;

/**
 * Created by wuguonan on 2015/2/4.按键响应接口
 */
public interface KeyEventListener {
    /**
     * 处理key down事件的回调
     *
     * @param aKeyCode key code
     * @param aEvent   key event
     * @return true表示被消耗，false表示未消耗
     */
    boolean onKeyDown(int aKeyCode, KeyEvent aEvent);


    /**
     * 处理key up事件的回调
     * @param aKeyCode key code
     * @param aEvent key event
     * @return true表示被消耗，false表示未消耗
     */
    boolean onKeyUp(int aKeyCode, KeyEvent aEvent);
}
