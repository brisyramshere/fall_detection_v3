package com.fallDetect.cos;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class Advance extends Activity{
	//广播
	MyBroadCaseReceiver receiver;
    //波形绘制
    private SurfaceView waveform=null;
    private SurfaceHolder holder=null;
	final int HEIGHT=300;   //设置画图范围高度
    final int WIDTH=350;    //画图范围宽度
    final int centerY=330;
    final int X_OFFSET=5; 
    private int x,oldx;
    private double y,oldy;
    public static final int DRAW_DATA=4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advance);
		 waveform=(SurfaceView)findViewById(R.id.wave);
		 	x=30;
		 	oldx=28;
		 	oldy=96;
	        holder=waveform.getHolder();
	        waveform.setZOrderOnTop(true);
	        holder.setFormat(PixelFormat.TRANSLUCENT);
	        holder.setFixedSize(WIDTH+50, HEIGHT+100);  //设置画布大小，要比实际的绘图位置大一点
	        
	        holder.addCallback(new Callback() {  //按照上面注释，添加回调函数
	            public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){ 
	                drawBack(holder); 
	                //如果没有这句话，会使得在开始运行程序，整个屏幕没有白色的画布出现
	                //直到按下按键，因为在按键中有对drawBack(SurfaceHolder holder)的调用
	            } 
	 
	            public void surfaceCreated(SurfaceHolder holder) { 
	                // TODO Auto-generated method stub 

	            } 
	 
	            public void surfaceDestroyed(SurfaceHolder holder) { 
	                // TODO Auto-generated method stub 

	            }
	        });  
	        
	      //注册广播，接收service中启动的线程发送过来的信息，同时更新UI  
	        receiver=new MyBroadCaseReceiver();
	        IntentFilter filter = new IntentFilter("drawwave");  
	        this.registerReceiver(receiver, filter);  
	}
	
    //各个线程的消息处理
	/*
    public final Handler mHandler = new Handler() {
        @Override
        	public void handleMessage(Message msg) {
            	switch (msg.what) {
            	case DRAW_DATA:
            		y=Double.parseDouble(msg.obj.toString());
            		y=(int)(y*10);
            		Log.d("wave","y="+y);
            		DrawWave();
            		break;
            }
        }
    };
    */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//返回程序后又开始订阅广播
		IntentFilter filter = new IntentFilter("drawwave");
		this.registerReceiver(receiver, filter); 
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);  
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//销毁应用程序的同时销毁广播
        //unregisterReceiver(receiver);  
	}
    //画图程序：
    public void DrawWave(){	         
    	// 待实现：在Canvas上绘图        
    		//SurfaceHolder surfaceHolder = holder;
        if(x==380)
    	{
		oldx=28;
		x=30;
		//y=centerY;
		//oldy=y;
	    }	
        if(x==30)
        	drawBack(holder);
        
    		Canvas canvas = holder.lockCanvas(new Rect((int)x-2,0,(int)x+2,waveform.getHeight()));  
    		if(canvas!=null)
    		{
    		Paint p=new Paint();
        	p.setColor(Color.BLUE);
        	p.setStrokeWidth(2);
        	Log.i("wavedraw", "!!!!!!!!!!!!!!!!!!!!!");
        	Log.i("wave","y="+y+" x="+x+"centerY="+centerY);
        	//canvas.drawPoint(x, (float)(centerY-y),p);
        	Log.i("wavedraw", "**********************");
        	p.setStrokeWidth(2);
        	
        	canvas.drawLine(oldx, (float)(centerY-oldy), x, (float)(centerY-y), p);
        	oldy=y;

        	Log.e("wavedraw", "画一个点,座标："+x+","+(centerY-y));
        	Log.e("wavedraw","长和宽："+waveform.getWidth()+","+waveform.getHeight());
        	
        	oldx=x;
        	x=x+2;
    	    // 解锁Canvas，并渲染当前图像 
        	//sdcard.sdcardwrite(String.valueOf(y*10),String.valueOf(z*10));
        	holder.unlockCanvasAndPost(canvas);
    		}

            	
    }
    
    //设置画布背景色，设置XY轴的位置
    private void drawBack(SurfaceHolder holder){ 
        Canvas canvas = holder.lockCanvas(); //锁定画布
       // Paint p = new Paint(); 
        if(canvas!=null)
        {
        	canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
      //  p.setColor(Color.BLACK); 
      //  p.setStrokeWidth(1); 
        //canvas.drawLine(30, centerY, 30, centerY-340, p);  //Y坐标轴
     //   canvas.drawLine(20, (float)(centerY-20*5), 382, (float)(centerY-20*5), p);
     //   canvas.drawLine(20, (float)(centerY-8*5), 382, (float)(centerY-8*5), p);
     //   canvas.drawLine(20, (float)(centerY-1*5), 382, (float)(centerY-1*5), p);
        holder.unlockCanvasAndPost(canvas);  //结束锁定 显示在屏幕上
        //holder.lockCanvas(new Rect(0,0,0,0)); //锁定局部区域，其余地方不做改变
        //holder.unlockCanvasAndPost(canvas); 
        }
    }	
    
    
    //广播接收器，接收来自service的数据和信息
    class MyBroadCaseReceiver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context arg0, Intent intent) {  
            //从service的发来的广播中获取数据
        	y = intent.getDoubleExtra("data", 0);
            y=(int)(y*5);
    		Log.d("wave","y="+y+" x="+x+" oldy"+oldy+" oldx"+oldx);
    		//画图
    		//Log.i("wavedraw", "!!!!!!!!!!!!!!!!!!!!!");
    		DrawWave();
            
        }  
    }  
}
