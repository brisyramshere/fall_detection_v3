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
	//�㲥
	MyBroadCaseReceiver receiver;
    //���λ���
    private SurfaceView waveform=null;
    private SurfaceHolder holder=null;
	final int HEIGHT=300;   //���û�ͼ��Χ�߶�
    final int WIDTH=350;    //��ͼ��Χ���
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
	        holder.setFixedSize(WIDTH+50, HEIGHT+100);  //���û�����С��Ҫ��ʵ�ʵĻ�ͼλ�ô�һ��
	        
	        holder.addCallback(new Callback() {  //��������ע�ͣ���ӻص�����
	            public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){ 
	                drawBack(holder); 
	                //���û����仰����ʹ���ڿ�ʼ���г���������Ļû�а�ɫ�Ļ�������
	                //ֱ�����°�������Ϊ�ڰ������ж�drawBack(SurfaceHolder holder)�ĵ���
	            } 
	 
	            public void surfaceCreated(SurfaceHolder holder) { 
	                // TODO Auto-generated method stub 

	            } 
	 
	            public void surfaceDestroyed(SurfaceHolder holder) { 
	                // TODO Auto-generated method stub 

	            }
	        });  
	        
	      //ע��㲥������service���������̷߳��͹�������Ϣ��ͬʱ����UI  
	        receiver=new MyBroadCaseReceiver();
	        IntentFilter filter = new IntentFilter("drawwave");  
	        this.registerReceiver(receiver, filter);  
	}
	
    //�����̵߳���Ϣ����
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
		//���س�����ֿ�ʼ���Ĺ㲥
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
		//����Ӧ�ó����ͬʱ���ٹ㲥
        //unregisterReceiver(receiver);  
	}
    //��ͼ����
    public void DrawWave(){	         
    	// ��ʵ�֣���Canvas�ϻ�ͼ        
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

        	Log.e("wavedraw", "��һ����,���꣺"+x+","+(centerY-y));
        	Log.e("wavedraw","���Ϳ�"+waveform.getWidth()+","+waveform.getHeight());
        	
        	oldx=x;
        	x=x+2;
    	    // ����Canvas������Ⱦ��ǰͼ�� 
        	//sdcard.sdcardwrite(String.valueOf(y*10),String.valueOf(z*10));
        	holder.unlockCanvasAndPost(canvas);
    		}

            	
    }
    
    //���û�������ɫ������XY���λ��
    private void drawBack(SurfaceHolder holder){ 
        Canvas canvas = holder.lockCanvas(); //��������
       // Paint p = new Paint(); 
        if(canvas!=null)
        {
        	canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
      //  p.setColor(Color.BLACK); 
      //  p.setStrokeWidth(1); 
        //canvas.drawLine(30, centerY, 30, centerY-340, p);  //Y������
     //   canvas.drawLine(20, (float)(centerY-20*5), 382, (float)(centerY-20*5), p);
     //   canvas.drawLine(20, (float)(centerY-8*5), 382, (float)(centerY-8*5), p);
     //   canvas.drawLine(20, (float)(centerY-1*5), 382, (float)(centerY-1*5), p);
        holder.unlockCanvasAndPost(canvas);  //�������� ��ʾ����Ļ��
        //holder.lockCanvas(new Rect(0,0,0,0)); //�����ֲ���������ط������ı�
        //holder.unlockCanvasAndPost(canvas); 
        }
    }	
    
    
    //�㲥����������������service�����ݺ���Ϣ
    class MyBroadCaseReceiver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context arg0, Intent intent) {  
            //��service�ķ����Ĺ㲥�л�ȡ����
        	y = intent.getDoubleExtra("data", 0);
            y=(int)(y*5);
    		Log.d("wave","y="+y+" x="+x+" oldy"+oldy+" oldx"+oldx);
    		//��ͼ
    		//Log.i("wavedraw", "!!!!!!!!!!!!!!!!!!!!!");
    		DrawWave();
            
        }  
    }  
}
