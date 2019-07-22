package com.example.windowmanager;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MessageService extends Service implements View.OnClickListener, View.OnTouchListener {
    private WindowManager mWindowmanager;
    private MyGroupView mViewIcon;
    private MyGroupView mViewSMS;
    private WindowManager.LayoutParams mIconViewParams;
    private WindowManager.LayoutParams mSMSViewParams;
    private int state;
    private static final int state_Icon = 0;
    private static final int state_SMS  = 1;
    private int pre_x;
    private int pre_y;
    private float startX;
    private float startY;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        inItView();
        return START_STICKY;
    }

    private void inItView() {
        mWindowmanager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createIconView();
        createSmsView();
        showIcon();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createSmsView() {
        mViewSMS = new MyGroupView(this);

        View view = View.inflate(this,R.layout.view_sms,mViewSMS);
        Button btn_thoat = view.findViewById(R.id.btn_thoat);
        btn_thoat.setOnClickListener(this);

        mViewSMS.setOnTouchListener(this);
        mSMSViewParams = new WindowManager.LayoutParams();
        mSMSViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mSMSViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mSMSViewParams.gravity = Gravity.CENTER;
        mSMSViewParams.format = PixelFormat.TRANSLUCENT;
        mSMSViewParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mSMSViewParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mSMSViewParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mSMSViewParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
    }

    private void showIcon() {
        try{
            mWindowmanager.removeView(mViewSMS);
        }catch (Exception e){
            e.printStackTrace();
        }
        mWindowmanager.addView(mViewIcon,mIconViewParams);
        state = state_Icon;
    }
    private void showSms() {
        try{
            mWindowmanager.removeView(mViewIcon);
        }catch (Exception e){
            e.printStackTrace();
        }
        mWindowmanager.addView(mViewSMS,mSMSViewParams);
        state = state_SMS;
    }

    private void createIconView() {
        mViewIcon = new MyGroupView(this);
        View view = View.inflate(this,R.layout.layout,mViewIcon);
        TextView tvIcon = view.findViewById(R.id.tv_icon);
        tvIcon.setOnClickListener(this);
        tvIcon.setOnTouchListener(this);
        mIconViewParams = new WindowManager.LayoutParams();
        mIconViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mIconViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mIconViewParams.gravity = Gravity.CENTER;
        mIconViewParams.format = PixelFormat.TRANSLUCENT;
        mIconViewParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mIconViewParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mIconViewParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_icon:
                showSms();
                break;
            case R.id.btn_thoat:
                showIcon();
                default:
                    break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                    if(state == state_Icon){
                        pre_x = mIconViewParams.x;
                        pre_y = mIconViewParams.y;
                    }else{
                        pre_x = mSMSViewParams.x;
                        pre_y = mSMSViewParams.y;
                    }
                    startX = event.getRawX();
                     startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                double delta_x = event.getRawX() - startX;
                double delta_y = event.getRawY() -  startY;
                if(state == state_Icon){
                    mIconViewParams.x = (int) delta_x + pre_x;
                    mIconViewParams.y = (int) delta_y + pre_y;
                    mWindowmanager.updateViewLayout(mViewIcon,mIconViewParams);
                }
                break;
                case MotionEvent.ACTION_OUTSIDE:
                    if(state == state_SMS){
                        showIcon();
                    }
                    break;
        }
        return false;
    }
}
