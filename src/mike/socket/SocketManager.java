package mike.socket;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import mike.Data.SettingData;
import android.util.Log;

public class SocketManager {
	private Socket SendOrderSocket = null;
	private Socket ReceiveDataSocket = null;
	private boolean isSendOrderSocketAlive = false;
	private boolean isReceiveDataSocketAlive = false;
	private DataInputStream CarDataInputStream;
	private DataOutputStream CarOrderOutputStream;
	
	/**
	 * @return 判断接收数据是不是已经建立
	 */
	public boolean isReceiveDataSocketAlive() {
		return (isReceiveDataSocketAlive);
	}
	
	/**
	 * @return 判断发送数据是不是已经建立
	 */
	public boolean isSendOrderSocketAlive() {
		return (isSendOrderSocketAlive);
	}
	
	/**
	 * @param _order 需要发送的指令
	 * @return boolean 是否发送成功
	 */
	public boolean sendCarOrder(String _order) {
		if (isSendOrderSocketAlive) {
			try {
				OutputStreamWriter opsw = new OutputStreamWriter(CarOrderOutputStream);
				BufferedWriter bw = new BufferedWriter(opsw);
				bw.write(_order);
				Log.e("命令", _order);
				bw.flush();
				Log.e(_order, _order);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				isSendOrderSocketAlive = false;
				try{
					CarOrderOutputStream.close();
					SendOrderSocket.close();}catch (Exception e1) {e1.printStackTrace();}
				return false;
			}
		}else {
			try{
				CarOrderOutputStream.close();
				SendOrderSocket.close();}catch (Exception e1) {e1.printStackTrace();}
			return false;
		}
	}
	
	/**
	 * @return 小车收到的数据，"1" 为没有新数据，"0"为连接失败
	 */
	public String getCarData() {
		if (isReceiveDataSocketAlive) {
			try {
				if (CarDataInputStream != null) {
						if(ReceiveDataSocket.isConnected() && !ReceiveDataSocket.isClosed()){
						String tmpString = CarDataInputStream.readLine();
						Log.e(tmpString, tmpString);
						if (tmpString.contains("@")) {
							return tmpString;
						}else {
							return "1";
						}
					}else {
						return "1";
					}
				}else {
					return "1";
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (ReceiveDataSocket.isConnected()) 
					return "1";
				else{
					isReceiveDataSocketAlive = false;
					try{
						CarDataInputStream.close();
						ReceiveDataSocket.close();}catch (Exception e1) {e1.printStackTrace();}
					return "0";
				}
			}
		}else {
			try{
				CarDataInputStream.close();
				ReceiveDataSocket.close();
			}catch (Exception e1) {e1.printStackTrace();}
			return "0";
		}
	}
	
	/**
	 * @return 表明是否建立接受数据Socket成功
	 */
	public boolean setupReceiveOrderSocket(String _ip, int _port) {
		try {
			ReceiveDataSocket = new Socket(_ip, _port);
			CarDataInputStream = new DataInputStream(ReceiveDataSocket.getInputStream());
			isReceiveDataSocketAlive = true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	/**
	 * @return 表示是否建立发送命令Socket成功
	 */
	public boolean setupSendOrderSocket(String _ip, int _port) {
		try {
			SendOrderSocket = new Socket(_ip, _port);
			CarOrderOutputStream = new DataOutputStream(SendOrderSocket.getOutputStream());
			isSendOrderSocketAlive = true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	/**
	 * @param 只是格没用的函数
	 */
	public SocketManager() {
		Log.e("Socket Establish!","Ready");
	}
}
