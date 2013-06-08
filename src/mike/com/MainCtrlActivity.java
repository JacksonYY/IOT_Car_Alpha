package mike.com;

import h264.com.VView;

import java.util.Timer;
import java.util.TimerTask;

import mike.Data.Car_CTRL_Order;
import mike.Data.Car_REC_Data;
import mike.Data.SettingData;
import mike.mapview.OfflineMapView;
import mike.preference.SettingActivity;
import mike.socket.SocketManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainCtrlActivity extends Activity implements OnTouchListener{
	private OfflineMapView mapView;
	private VView vv;
	private Button speedupButton;
	private Button stopButton;
	private Button backButton;
	//private SpeedView speedView;
	//private DirectionView directionView;
	private Button moveButton;
	private ToggleButton Car_CTRL_TB;
	private ToggleButton Car_VIDEO_TB;
	private ToggleButton Car_DATA_TB;
	private TextView testTextView;
	//-------------------------------------------以上是控件
    private boolean isTurnLeft =false;	//小车是否右拐
    private int TurnD = 0;						//拐弯的角度
    private boolean isForward = false;		//小车是否前进
    private int Speed = 0;
    private static String Car_ORDER = "";
    
    private boolean isStop = false;
    
    private String Car_Data_String = "";
    private double Car_Speed = 0;
    private double Car_Height = 0;
    private double Car_Degree = 0;
    private double Car_Longitude = 0;
    private double Car_Latitude = 0;
	//-------------------------------------------此处为全局的数据
    private Car_REC_Data car_REC_Data = new Car_REC_Data();
    private Car_CTRL_Order car_CTRL_Order = new Car_CTRL_Order();
    private SocketManager socketManager = new SocketManager();
    //-------------------------------------------此处为封装的类
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);
        updateSettingData();
        findViews();
        setSensor();
        mapView.setLocation(120.64248919487, 31.30230587142129, 18, 0, 20, 20);
    }

    /**
     * @param 找到所有的控件 
     */
    private void findViews(){
    	mapView = (OfflineMapView)findViewById(R.id.Map_IMAGE);
    	vv = (VView)findViewById(R.id.Video_CAM1_V);
    	speedupButton = (Button)findViewById(R.id.Car_SPEEDUP_B);
    	speedupButton.setOnTouchListener(this);
    	backButton = (Button)findViewById(R.id.Car_BACK_B);
    	backButton.setOnTouchListener(this);
    	stopButton = (Button)findViewById(R.id.Car_STOP_B);
    	stopButton.setOnTouchListener(this);
    	moveButton = (Button)findViewById(R.id.Car_MOVE_B);
    	moveButton.setOnTouchListener(this);
    	
    	testTextView = (TextView)findViewById(R.id.order);
    	Car_CTRL_TB = (ToggleButton)findViewById(R.id.Car_CTRL_TB);
    	Car_DATA_TB = (ToggleButton)findViewById(R.id.Car_DATA_TB);
    	Car_VIDEO_TB = (ToggleButton)findViewById(R.id.Car_VIDEO_TB);
    }
    
    // Menu item Ids
    private static final int PLAY_ID = Menu.FIRST;    
    private static final int STOP_ID = Menu.FIRST + 1;
    private static final int SETSENDSOCKET_ID = Menu.FIRST + 2;
    private static final int SETCARDATA_ID = Menu.FIRST + 3;
    private static final int SETTING_ID = Menu.FIRST + 4;
    private static final int EXIT_ID = Menu.FIRST + 5; 
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplicationContext()).inflate(R.menu.activity_main_control, menu);
        
        MenuItem actionItem = menu.add(0, PLAY_ID, 0, "开始视频");
        actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        actionItem = menu.add(0, STOP_ID, 1, "关闭视频");
        actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        actionItem = menu.add(0, SETSENDSOCKET_ID, 1, "指令连接");
        actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        actionItem =  menu.add(0, SETCARDATA_ID, 1, "数据连接");
        actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        actionItem = menu.add(0, SETTING_ID, 1, "程序设置");
        actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        actionItem = menu.add(0, EXIT_ID, 1, "退出程序");

        return ( super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {        
	        case PLAY_ID:{
	        	new Thread(new Runnable() {
					public void run() {
						try {
			        		if(vv.PlayVideo()){
			        			Message message = new Message();
			        			message.what = 4;
			        			mainControlHandler.sendMessage(message);
			        		}else {
			        			Message message = new Message();
			        			message.what = 5;
			        			mainControlHandler.sendMessage(message);
			        		}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
	            return true;
	        }
	        case STOP_ID:{
	        	vv.stopPlaty();
	        	Car_VIDEO_TB.setChecked(false);
	        	return true;
	        }
	        case EXIT_ID:{
	        	vv.stopPlaty();
	        	finish();
	            return true;
	        }
	        case SETSENDSOCKET_ID:{
	        	new Thread(new Runnable() {
					public void run() {
						try {
							if(!socketManager.setupSendOrderSocket(SettingData.Car_IP, SettingData.PORT_SendOrder)){
				        		errorMessage = null;errorMessage = new Message();
				        		errorMessage.what = 1;
				        		mainControlHandler.sendMessage(errorMessage);
				        	}else {
				        		errorMessage = null;errorMessage = new Message();
				        		errorMessage.what = 6;
				        		mainControlHandler.sendMessage(errorMessage);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
	        	return true;
	        }
	        
	        case SETCARDATA_ID:{
	        	new Thread(new Runnable() {
					public void run() {
						try {
							if (!socketManager.setupReceiveOrderSocket(SettingData.Car_IP, SettingData.PORT_ReceiveData)) {
				        		errorMessage = null;errorMessage = new Message();
				        		errorMessage.what = 2;
				        		mainControlHandler.sendMessage(errorMessage);
							}else {
								try{
									updateViewsTimer.cancel();
									updateViewsTimer = null;
									updateViewsTimer = new Timer();
									updateViewsTimer.schedule(new updateViewsTimerTask(), 0, 1000);
									errorMessage = null;errorMessage = new Message();
					        		errorMessage.what = 7;
					        		mainControlHandler.sendMessage(errorMessage);
								}catch (Exception e) {e.printStackTrace();}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
	        	return true;
	        }
	        
	        case SETTING_ID:{
	        	Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
	        	startActivity(intent);
	        	return true;
	        }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop(){
    	vv.stopPlaty();
    	try{
    		updateViewsTimer.cancel();
    		addspeedTimer.cancel();
    		sendOrderTimer.cancel();
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    	super.onStop();
    }
    
    @Override
    public void	onDestroy(){
    	vv.stopPlaty();
    	try{
    		updateViewsTimer.cancel();
    		addspeedTimer.cancel();
    		sendOrderTimer.cancel();
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    	super.onDestroy();
    }

    //--------------------------------------------------------------------------------
    //设置感应器
    private SensorManager sensorManager;
    private Sensor sensor;
    private double x, y, z;		//从传感器中读取的数据
    private void setSensor(){
    	// 得到当前手机传感器管理对象
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 加速重力感应对象
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 实例化一个监听器
        SensorEventListener lsn = new SensorEventListener() {
            // 实现接口的方法
            public void onSensorChanged(SensorEvent e) {
                // 得到各轴上的重力加速度
                x = e.values[SensorManager.DATA_X];
                y = e.values[SensorManager.DATA_Y];
                z = e.values[SensorManager.DATA_Z];
                
                //-----------------------------------------------------
                if (z > 8 && z < 0) {//判断手机的位置是否正确
					Toast.makeText(getApplicationContext(), "请保持手机的垂直放置", Toast.LENGTH_SHORT).show();
					TurnD = 0;
				}else {
					if (y < 1 && y > -1) {//直线运动
						TurnD = 0;
					}else {
						double degree = x/y;
						if (degree < 0) {
							isTurnLeft = true;
							if (degree >= -1) {//偏转角大于45度(左)
								TurnD = 100;
							}else {
								TurnD = (int) -(100/degree);
								if (TurnD < 16) {//确保指令传输的正确性
									TurnD = 16;
								}
							}
						}else if (degree > 0) {
							isTurnLeft = false;
							if (degree <= 1) {//偏转角大于45度(右)
								TurnD = 100;
							}else {
								TurnD = (int) (100/degree);
								if (TurnD < 16) {//确保指令传输的正确性
									TurnD = 16;
								}
							}
						}
					}
				}
                
                Car_ORDER = car_CTRL_Order.setTurnD(TurnD, isTurnLeft);		//设置控制数据
                //Log.e(String.valueOf(TurnD), String.valueOf(isTurnLeft));
                //----------打印值
                //Toast.makeText(getApplicationContext(), String.valueOf(x) + ":" +String.valueOf(y) + ":" + String.valueOf(z), Toast.LENGTH_SHORT).show();
            }
            
            public void onAccuracyChanged(Sensor s, int accuracy) {}
        };
        // 注册listener，第三个参数是检测的精确度
        sensorManager.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);
    }
    //-----------------------------------------------------------------------------
    
    /**
     * @param 控制小车按钮的事件
     */
    public boolean onTouch(View _v, MotionEvent _event) {//重要的按键使用
		switch (_v.getId()) {
		case R.id.Car_SPEEDUP_B://设置加速按钮按下的操作
			switch (_event.getAction()) {
			case KeyEvent.ACTION_UP://松开
				Log.e("a", "松开");
				try{
					addspeedTimer.cancel();
					sendOrderTimer.cancel();
				}catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case KeyEvent.ACTION_DOWN://按下
				isForward = true;
				Car_ORDER = car_CTRL_Order.resetOrder();
				try{
					sendOrderTimer.cancel();
					addspeedTimer.cancel();
				}catch (Exception e) {
					e.printStackTrace();
				}
				addspeedTimer = null;
				addspeedTimer = new Timer();
				sendOrderTimer = null;
				sendOrderTimer = new Timer();
				addspeedTimer.schedule(new addSpeedTimerTask(), 0, SettingData.TimeToAgain);
				sendOrderTimer.schedule(new sendOrderTimeTask(), 0, SettingData.TimeToAgain);
				break;
			}
			break;

		case R.id.Car_MOVE_B://匀速前进
			switch (_event.getAction()) {
				case KeyEvent.ACTION_DOWN:
					isForward = true;
					Log.e(Car_ORDER, Car_ORDER);
					try{
						sendOrderTimer.cancel();
					}catch (Exception e) {
						e.printStackTrace();
					}
					sendOrderTimer = null;
					sendOrderTimer = new Timer();
					sendOrderTimer.schedule(new sendOrderTimeTask(), 0, SettingData.TimeToAgain);
					break;
	
				case KeyEvent.ACTION_UP:
					try{
						sendOrderTimer.cancel();
					}catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			break;
			
		case R.id.Car_STOP_B://设置停止按钮按下的操作
			switch (_event.getAction()) {
			case KeyEvent.ACTION_UP://松开
				try{
					isStop = false;
					sendOrderTimer.cancel();
				}catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case KeyEvent.ACTION_DOWN://按下
				Log.e("c", "按下");
				isStop = true;
				Car_ORDER = car_CTRL_Order.resetOrder();
				try{
					sendOrderTimer.cancel();
				}catch (Exception e) {
					e.printStackTrace();
				}
				sendOrderTimer = null;
				sendOrderTimer = new Timer();
				sendOrderTimer.schedule(new sendOrderTimeTask(), 0, SettingData.TimeToAgain);
				break;
			}
			break;
			
		case R.id.Car_BACK_B://设置倒车按钮按下的操作
			switch (_event.getAction()) {
			case KeyEvent.ACTION_UP://松开
				Log.e("b", "松开");
				try{
					addspeedTimer.cancel();
					sendOrderTimer.cancel();
				}catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case KeyEvent.ACTION_DOWN://按下
				Log.e("b", "按下");
				isForward = false;
				Car_ORDER = car_CTRL_Order.resetOrder();
				try{
					sendOrderTimer.cancel();
					addspeedTimer.cancel();
				}catch (Exception e) {
					e.printStackTrace();
				}
				addspeedTimer = null;
				addspeedTimer = new Timer();
				sendOrderTimer = null;
				sendOrderTimer = new Timer();
				addspeedTimer.schedule(new addSpeedTimerTask(), 0, SettingData.TimeToAgain);
				sendOrderTimer.schedule(new sendOrderTimeTask(), 0, SettingData.TimeToAgain);
				break;
			}
			break;
		}
		return false;
	}
    
    //发送命令区域
    //--------------------------------------------------------
    private Timer sendOrderTimer = new Timer();
    private class sendOrderTimeTask  extends TimerTask{
    	@Override
		public void run() {
			Log.e("Send", "Send ");
			if (socketManager.isSendOrderSocketAlive()) {
				//directionView.setDirection(Car_ORDER);
				Log.e("Order", Car_ORDER);
				if (isStop) {
					Car_ORDER = car_CTRL_Order.resetOrder();
				}
				if (!socketManager.sendCarOrder(Car_ORDER)) {
					errorMessage = null;errorMessage = new Message();
					errorMessage.what = 1;
					mainControlHandler.sendMessage(errorMessage);
				}else {
					errorMessage = null;errorMessage = new Message();
					errorMessage.what = 999;	//表明连接失败
					mainControlHandler.sendMessage(errorMessage);
				}
			}else {
				errorMessage = null;errorMessage = new Message();
				errorMessage.what = 1;	//表明连接失败
				mainControlHandler.sendMessage(errorMessage);
			}
		}
	};
    //--------------------------------------------------------
    
    //加减速度区域
    //--------------------------------------------------------
    private Timer addspeedTimer = new Timer();
    private class addSpeedTimerTask extends TimerTask {//用来加速度的任务
    	@Override
		public void run() {
			if (Speed < 250) {
				Speed += SettingData.Car_Delta_SPEED; 
				Log.e(String.valueOf(Speed), "Speed");
				Car_ORDER = car_CTRL_Order.setSpeed(Speed, isForward);
				Log.e(Car_ORDER, Car_ORDER);
			}else {
				Car_ORDER = car_CTRL_Order.setSpeed(250, isForward);
				Log.e(Car_ORDER, Car_ORDER);
			}
		}
    	
    	public addSpeedTimerTask(){
    		Speed = 0;
    	}
	};
	//--------------------------------------------------------
	
	//处理小车数据
	//--------------------------------------------------------
	private Timer updateViewsTimer = new Timer();
	private class updateViewsTimerTask extends TimerTask {
		@Override
		public void run() {
			getCarData();
		}
	};
	
	public void getCarData() {
		try{
			Car_Data_String = socketManager.getCarData();
			if(!Car_Data_String.equals("1") && !Car_Data_String.equals("0")&&Car_Data_String != null){
				Log.e(Car_Data_String, Car_Data_String);
				car_REC_Data = new Car_REC_Data(Car_Data_String);
				Car_Height = car_REC_Data.getHeight();
				Car_Speed = car_REC_Data.getSpeed();
				Car_Degree = car_REC_Data.getTurnDegree();
				double[] temp = new double[2];
				temp = car_REC_Data.getLocation();
				Car_Longitude = temp[0];
				Car_Latitude = temp[1];
				errorMessage = null;
				errorMessage = new Message();
				errorMessage.what = 0;//更新控件
				mainControlHandler.sendMessage(errorMessage);
			}else {
				errorMessage = null;
				errorMessage = new Message();
				errorMessage.what = 2;
				mainControlHandler.sendMessage(errorMessage);
			}
		}catch (Exception e) {e.printStackTrace();
		errorMessage = null;
		errorMessage = new Message();
		errorMessage.what = 2;
		mainControlHandler.sendMessage(errorMessage);}
	}
	
	private void updateViews(){
		mapView.setLocation(Car_Longitude, Car_Latitude, 18, Car_Degree, Car_Speed, Car_Height);
		//speedView.setSpeed(Car_Speed);
	}
	//--------------------------------------------------------
	
	//错误处理
	//--------------------------------------------------------
	private Message errorMessage = new Message();
	private Handler mainControlHandler = new Handler(){
		@Override
    	public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:{	//这是发送数据Socket出现问题
				try{
					Log.e("1", "Send Cancel");
					//需要重新建立连接
					//---------------
					//---------------
					Car_CTRL_TB.setChecked(false);
					testTextView.setText("Shit");
				}catch (Exception e) {e.printStackTrace();}
				break;
			}
			case 2:{//这是接收数据socket的问题
				try {
					Log.e("2", "Receive Cancel!");
					updateViewsTimer.cancel();
					Car_DATA_TB.setChecked(false);
				} catch (Exception e) {e.printStackTrace();}
				break;
			}
			case 4:{//视频正常
				Car_VIDEO_TB.setChecked(true);
				break;
			}
			case 5:{//视频掉线
				Car_VIDEO_TB.setChecked(false);
				break;
			}
			case 6:{//发送数据真诚
				Car_CTRL_TB.setChecked(true);
				break;
			}
			case 7:{//数据接收正常
				Car_DATA_TB.setChecked(true);
				break;
			}
			case 999:{//调试用
				testTextView.setText(Car_ORDER);
				break;
			}
			case 0:{//更新控件
				updateViews();
			}
			default:
				break;
			}
    	}
	};
	//--------------------------------------------------------
	 public void updateSettingData(){
			Log.e(SettingData.Car_IP, SettingData.Car_IP);
			SharedPreferences spc = getSharedPreferences("mike.com_preferences", MODE_WORLD_WRITEABLE);
			SettingData.Car_IP = spc.getString("CarIP", "192.168.135.116");
	    	Log.e("读取的IP为:", "IP:" + SettingData.Car_IP);
		}
}
