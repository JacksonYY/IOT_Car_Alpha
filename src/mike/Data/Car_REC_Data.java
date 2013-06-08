package mike.Data;

import android.util.Log;

public class Car_REC_Data {
	private double Car_Height = 0;		//小车海拔
	private double Car_Speed = 0;		//小车速度
	private double Car_Degree = 0;		//小车方向角	
	private double Car_Latitude = 0;	//小车纬度
	private double Car_Longitude = 0;	//小车经度
	private final static int Data_NUM = 5;
	private String ReceiveData = "";
	
	/**
	 * @return 一个double[2]的数组，表示地点，先经度度后纬度
	 */
	public double[] getLocation() {
		double tmpdouble[] = new double[2];
		tmpdouble[0] = Car_Longitude;
		tmpdouble[1] = Car_Latitude;
		return tmpdouble;
	}
	
	/**
	 * @return 返回小车的海拔
	 */
	public double getHeight() {
		double tmpheight = Car_Height;
		return tmpheight;
	}
	
	/**
	 * @return 返回小车的速度
	 */
	public double getSpeed() {
		double tmpspeed = Car_Speed;
		return tmpspeed;
	}
	
	/**
	 * @return 返回小车的偏转角
	 */
	public double getTurnDegree() {
		double tmpdegree = Car_Degree;
		return tmpdegree;
	}
	
	/**
	 * @param _data从Socket中读出的数据
	 */
	public void setData(String _data) {
		ReceiveData = _data;
		setSocketStr();
	}
	
	/**
	 * @param 一个没用的构造函数
	 */
	public Car_REC_Data(){}
	
	/**
	 * @param 构造函数，传入参数为收到的数据
	 */
	public Car_REC_Data(String _recdata) {
		ReceiveData = _recdata;
		setSocketStr();
	}
    //------------------------------------------------------------------------------
    //这是处理socket数据的函数
  	private void setSocketStr(){//设置
		double tempNUM[] = new double[Data_NUM];
			try{
	  		if(ReceiveData != null || ReceiveData != "0"){
	  			String[] tempStrings = ReceiveData.split(",");
	  			int i = 0;
	  			for(String dataString : tempStrings){
	  				dataString = dataString.replace("@", "");
	  				dataString = dataString.replace("N", "");
	  				dataString = dataString.replace("E", "");
	  				dataString = dataString.replace("A", "");
	  				dataString = dataString.replace("H", "");
	  				dataString = dataString.replace("#", "");
	  				dataString = dataString.replace("S", "");
	  				String temo = dataString;
	  				Log.e(temo, temo);
	  				if (i == 0) {
						tempNUM[0] = Double.valueOf(dataString);
					}else if (i == 1) {
						tempNUM[1] = Double.valueOf(dataString);
					}else if(i == 2) {
						tempNUM[2] = Double.valueOf(dataString);
					}else if (i == 3) {
						tempNUM[3] = Double.valueOf(dataString);
					}else if (i == 4) {
						tempNUM[4] = Double.valueOf(dataString);
					}
	  				i++;
	  			}
	  			i = 0;
	  		}
		}catch (Exception e) {
			return;
		}
		Car_Latitude = tempNUM[0];
		Car_Longitude = tempNUM[1];
		Car_Height = tempNUM[2];
		Car_Degree = tempNUM[3];
		Car_Speed = tempNUM[4];
	}
}
