package mike.Data;

public class Car_CTRL_Order {
		private String orderString;
		private String speedOrder;
		private String turnOrder;
		/**
		 * @param 构造函数
		 */
		public Car_CTRL_Order(){
			orderString = "@-00+00#";
			speedOrder = "+00#";
			turnOrder = "@-00";
		}
		
		/**
		 * @param _speed小车的速度
		 * @param _isforward是否前进
		 * @return 一条指令
		 */
		public String setSpeed(int _speed, boolean _isforward) {
			System.out.println(_speed);
			if(_isforward == true){
				if (_speed == 0) {
					speedOrder = "+00#";
				}else if(_speed < 16 )
					speedOrder = "+0F#";
				else if (_speed < 100) {
					speedOrder = "+"+ Integer.toHexString(_speed) + "#";
				}else {
					speedOrder = "+64#";
				}
			}else{
				if (_speed == 0) {
					speedOrder = "+00#";
				}else if(_speed < 16 )
					speedOrder = "-0F#";
				else if (_speed < 100) {
					speedOrder = "-"+ Integer.toHexString(_speed) + "#";
				}else {
					speedOrder = "-64#";
				}
			}
			orderString = turnOrder + speedOrder;
			return new String(orderString);
		}
		
		/**
		 * @param _turnD 转弯角度
		 * @param _isturnleft 是否左转
		 * @return 一条指令
		 */
		public String setTurnD(int _turnD, boolean _isturnleft	) {
			if(_isturnleft == false){
				if (_turnD == 0) {
					turnOrder = "@+00";
				}else if(_turnD < 16)
					turnOrder = "@-00";
				else if (_turnD < 100) {
					turnOrder = "@+" + Integer.toHexString(_turnD) ;
				} else {
					turnOrder = "@+64";
				}
			}else{
				if (_turnD == 0) {
					turnOrder = "@-00";
				}else if(_turnD < 16)
					turnOrder = "@-00";
				else if (_turnD < 100) {
					turnOrder = "@-" + Integer.toHexString(_turnD);
				} else {
					turnOrder = "@-64";
				}
			}
			orderString = turnOrder + speedOrder;
			return new String(orderString);
		}
		
		/**
		 * @return 一个完整的命令，如果没有设置，则为@+00+00#
		 */
		public String getOrder() {
			return new String(orderString);
		}
		
		/**
		 * @return 将命令致0
		 */
		public String resetOrder(){
			orderString = "@-00+00#";
			speedOrder = "+00#";
			turnOrder = "@-00";
			return new String(orderString);
		}
	

}
