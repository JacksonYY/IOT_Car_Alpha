package mike.preference;

import mike.Data.SettingData;
import mike.com.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingActivity extends PreferenceActivity{
	  public void onCreate(Bundle savedInstanceState) {
	    	//requestWindowFeature(Window.FEATURE_NO_TITLE);
	    	//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    	//以上代码使用来全屏显示
	    	
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.setting_preference);
	  }
	  
	  @Override
	  public void onStop(){
		  updateData();
		  super.onStop();
	  }
	  
	  @Override
	  public void onDestroy(){
		  updateData();
		  super.onDestroy();
	  }
	
	  public void updateData(){
			Log.e(SettingData.Car_IP, SettingData.Car_IP);
			SharedPreferences spc = getSharedPreferences("mike.com_preferences", MODE_WORLD_WRITEABLE);
			SettingData.Car_IP = spc.getString("CarIP", "192.168.135.116");
	    	Log.e("读取的IP为:", "IP:" + SettingData.Car_IP);
		}
}
