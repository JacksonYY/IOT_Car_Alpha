package mike.mapview;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

import mike.com.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

public class OfflineMapView extends ImageView {
	float x_down = 0;
	float y_down = 0;
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	float oldRotation = 0;
	Matrix matrix = new Matrix();
	Matrix matrix1 = new Matrix();
	Matrix savedMatrix = new Matrix();
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int zoom_Map = 18;
	private Location location = new Location();
	private Bitmap mapBitmaps[][] = new Bitmap[3][3];
	private Drawable mapDrawable;
	BitmapDrawable mapBitmapDrawable;
	Bitmap mapBitmap;
	int mode = NONE;
	boolean matrixCheck = false;
	int widthScreen;
	int heightScreen;
	Bitmap gintama;
	
	private double Speed = 0.0;
	private double Degree = 0.0;
	private double Height = 0.0;
	
	// 读取手机SD卡上的离线地图包
	private void findMapPics() {
		// 判断sdcard是否存在于手机上而且没有写保护
		// Android2.2版本以后sdcard的路径在mnt/sdcard，2.2之前在/sdcard
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.e("SDCard", "OK");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					String path = Environment	.getExternalStorageDirectory().toString()+ "/OfflineMap/" + String.valueOf(zoom_Map) + "/" + location.PicFileNames[i][j];
					Log.e("PicName", location.PicFileNames[i][j]);
					RandomAccessFile mMiniThumbFile = null;
					File imgfile = new File(path);
					try {
							mMiniThumbFile = new RandomAccessFile(imgfile, "r");
							byte[] data = new byte[25002];
							mMiniThumbFile.seek(0);
							mMiniThumbFile.read(data, 0, 25001);
							// 通过data获得bitmap
							mapBitmaps[i][j] = BitmapFactory.decodeByteArray(data, 0, data.length);
					} catch (Exception ex2) {
						Toast.makeText(getContext(), "地图文件异常，是否没有地图包？", Toast.LENGTH_SHORT).show();
						ex2.printStackTrace();
						Resources res=getResources();
						Bitmap bmp=BitmapFactory.decodeResource(res, R.drawable.mi);
						mapDrawable = new BitmapDrawable(bmp);
						return;
					}
				}
			}
			Bitmap mapBitmap_new = Bitmap.createBitmap(256 * 3, 256 * 3, Config.ARGB_8888);
			Canvas mapCanvas = new Canvas(mapBitmap_new);
			location.getDrawLocation_XY();
			Paint mapPaint = new Paint();
			mapPaint.setColor(Color.RED);
				for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					try{
						mapCanvas.drawBitmap(mapBitmaps[i][j], 256 * i, 256 * j, mapPaint);
					}catch (Exception e) {
						e.printStackTrace();
						Resources res=getResources();
						Bitmap bmp=BitmapFactory.decodeResource(res, R.drawable.mi);
						mapDrawable = new BitmapDrawable(bmp);
						return;
					}
				}
			}
			
			mapPaint.setColor(Color.RED);
			mapPaint.setStrokeWidth(3);
			mapCanvas.drawLine(location.draw_X, location.draw_Y - 30, location.draw_X, location.draw_Y + 30, mapPaint);
			mapCanvas.drawLine(location.draw_X - 30, location.draw_Y, location.draw_X + 30, location.draw_Y, mapPaint);
			mapCanvas.drawCircle(location.draw_X, location.draw_Y, 4,	mapPaint);
			mapPaint.setTextSize(30);
			Typeface typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD);
			mapPaint.setTypeface(typeface);
			if (location.draw_X < 384 && location.draw_Y < 384) {
				mapCanvas.drawLine(location.draw_X + 10, location.draw_Y + 10, location.draw_X + 50, location.draw_Y + 50, mapPaint);
				mapPaint.setColor(Color.BLACK);
				mapCanvas.drawText("目标车辆", location.draw_X + 55, location.draw_Y + 55, mapPaint);
				mapCanvas.drawText("高度:" + String.valueOf((int)Height) + "M", location.draw_X + 55, location.draw_Y + 85, mapPaint);
				mapCanvas.drawText("速度:" + String.valueOf((int)Speed) + "KM/H", location.draw_X + 55, location.draw_Y + 115, mapPaint);
			}else if (location.draw_X > 384 && location.draw_Y < 384) {
				mapCanvas.drawLine(location.draw_X - 10, location.draw_Y + 10, location.draw_X - 50, location.draw_Y + 50, mapPaint);
				mapPaint.setColor(Color.BLACK);
				mapCanvas.drawText("目标车辆", location.draw_X - 85, location.draw_Y + 80, mapPaint);
				mapCanvas.drawText("高度:" + String.valueOf((int)Height) + "M", location.draw_X - 85, location.draw_Y + 110, mapPaint);
				mapCanvas.drawText("速度:" + String.valueOf((int)Speed) + "KM/H", location.draw_X - 85, location.draw_Y + 140, mapPaint);
			}else if (location.draw_X < 384 && location.draw_Y > 384) {
				mapCanvas.drawLine(location.draw_X + 10, location.draw_Y - 10, location.draw_X + 50, location.draw_Y - 50, mapPaint);
				mapPaint.setColor(Color.BLACK);
				mapCanvas.drawText("目标车辆", location.draw_X + 55, location.draw_Y - 55, mapPaint);
				mapCanvas.drawText("高度:" + String.valueOf((int)Height) + "M", location.draw_X + 55, location.draw_Y - 85, mapPaint);
				mapCanvas.drawText("速度:" + String.valueOf((int)Speed) + "KM/H", location.draw_X + 55, location.draw_Y - 115, mapPaint);
			}else {
				mapCanvas.drawLine(location.draw_X - 10, location.draw_Y - 10, location.draw_X - 50, location.draw_Y - 50, mapPaint);
				mapPaint.setColor(Color.BLACK);
				mapCanvas.drawText("目标车辆", location.draw_X - 85, location.draw_Y - 55, mapPaint);
				mapCanvas.drawText("高度:" + String.valueOf((int)Height) + "M", location.draw_X - 85, location.draw_Y - 85, mapPaint);
				mapCanvas.drawText("速度:" + String.valueOf((int)Speed) + "KM/H", location.draw_X - 85, location.draw_Y - 115, mapPaint);
			}
			
			//-------------------------------------------
			//这边进行地图的绘制
			mapDrawable = new BitmapDrawable(mapBitmap_new);
		}
	} 

	private void updateView() {
		mapBitmapDrawable = (BitmapDrawable) mapDrawable;
		mapBitmap = mapBitmapDrawable.getBitmap();
		matrix.reset();
		matrix.postTranslate(-150, -150);
		matrix.postScale((float)0.6, (float)0.6);
		gintama = null;
		gintama = mapBitmap;
		invalidate();
	}

	public OfflineMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		gintama = BitmapFactory.decodeResource(getResources(), R.drawable.mi);
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		widthScreen = dm.widthPixels;
		heightScreen = dm.heightPixels;
		matrix = new Matrix();
	}

	public OfflineMapView(Context context) {
		super(context);
		gintama = BitmapFactory.decodeResource(getResources(), R.drawable.mi);
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		widthScreen = dm.widthPixels;
		heightScreen = dm.heightPixels;
		matrix = new Matrix();
	}

	/**
	 * @param _x 经度
	 * @param _y 纬度
	 * @param _z 缩放
	 * @param _direction 小车的角度
	 * @param _speed 小车速度
	 * @param _height 小车海拔
	 */
	public void setLocation(double _x, double _y, int _z, double _direction, double _speed, double _height) {
		location.X_Degree = _x;
		location.Y_Degree = _y;
		location.z = _z;
		zoom_Map = _z;
		location.DegreeToPicNUM(_x, _y, _z);
		location.getFileName();
		Speed = _speed;
		Degree = _direction;
		Height = _height;
		findMapPics();
		updateView();
	}

	
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.drawBitmap(gintama, matrix, null);
		canvas.restore();
	}

	/**
	 * @param 设置图片空间上的触控事件
	 */
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			x_down = event.getX();
			y_down = event.getY();
			savedMatrix.set(matrix);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			oldDist = spacing(event);
			oldRotation = rotation(event);
			savedMatrix.set(matrix);
			midPoint(mid, event);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == ZOOM) {
				matrix1.set(savedMatrix);
				float rotation = rotation(event) - oldRotation;
				float newDist = spacing(event);
				float scale = newDist / oldDist;
				matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
				matrix1.postRotate(rotation, mid.x, mid.y);// 旋轉
				matrixCheck = matrixCheck();
				if (matrixCheck == false) {
					matrix.set(matrix1);
					invalidate();
				}
			} else if (mode == DRAG) {
				matrix1.set(savedMatrix);
				matrix1.postTranslate(event.getX() - x_down, event.getY()
						- y_down);// 平移
				matrixCheck = matrixCheck();
				matrixCheck = matrixCheck();
				if (matrixCheck == false) {
					matrix.set(matrix1);
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		}
		return true;
	}

	private boolean matrixCheck() {
		float[] f = new float[9];
		matrix1.getValues(f);
		// 图片4个顶点的坐标
		float x1 = f[0] * 0 + f[1] * 0 + f[2];
		float y1 = f[3] * 0 + f[4] * 0 + f[5];
		float x2 = f[0] * gintama.getWidth() + f[1] * 0 + f[2];
		float y2 = f[3] * gintama.getWidth() + f[4] * 0 + f[5];
		float x3 = f[0] * 0 + f[1] * gintama.getHeight() + f[2];
		float y3 = f[3] * 0 + f[4] * gintama.getHeight() + f[5];
		float x4 = f[0] * gintama.getWidth() + f[1] * gintama.getHeight()
				+ f[2];
		float y4 = f[3] * gintama.getWidth() + f[4] * gintama.getHeight()
				+ f[5];
		// 图片现宽度
		double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		// 缩放比率判断
		if (width < widthScreen / 3 || width > widthScreen * 3) {
			return true;
		}
		// 出界判断
		if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
				&& x3 < widthScreen / 3 && x4 < widthScreen / 3)
				|| (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
						&& x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
				|| (y1 < heightScreen / 3 && y2 < heightScreen / 3
						&& y3 < heightScreen / 3 && y4 < heightScreen / 3)
				|| (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3
						&& y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
			return true;
		}
		return false;
	}

	// 触碰两点间距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 取手势中心点
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// 取旋转角度
	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	public static class MapController {
		/**
		 * @param 算出地图编号 
		 * double_Long_x:为经度 double_la_y:为纬度 z:为缩放大小
		 * @return int[] 为地图的xy编号
		 */
		public static int[] getGMapSpotLocation(double double_Long_x,
				double double_la_y, int z) {
			double tw, mapsize, longdeg, longppd, longtiles, e;
			double pixelx, pixely;
			int tilex, tiley;
			tw = 256; // 每个小图的尺寸256×256(px)
			mapsize = tw * Math.pow((double) 2, (double) z);

			longdeg = Math.abs(180 + double_Long_x);
			longppd = mapsize / 360;
			pixelx = longdeg * longppd;
			longtiles = pixelx / tw;
			tilex = (int) longtiles;
			// ---------------------用来算x
			e = Math.sin((double_la_y) / 180 * Math.PI);
			if (e > 0.9999) {
				e = 0.9999;
			}
			if (e < -0.9999) {
				e = -0.9999;
			}
			pixely = tw * Math.pow((double) 2, (double) z) / 2 + 0.5
					* Math.log((1 + e) / (1 - e))
					* (-tw * Math.pow((double) 2, (double) z) / (2 * Math.PI));
			tiley = (int) (pixely / tw);
			// ---------------------用来算y
			// 保存在这里
			int[] PNGNum = new int[2];
			PNGNum[0] = tilex;
			PNGNum[1] = tiley;
			return PNGNum;
		}

		/**
		 * @param map_num_x
		 *            ,map_num_y,z 算出地图左上角的经纬度 map_num_x:为地图的x map_num_y:为地图的y
		 *            z:为缩放大小
		 * @return 一个double[],用来存放地图右上角的经纬
		 */
		public static double[] getGMapPicLocation(long map_num_x,
				long map_num_y, int z) {
			double PicLoation[] = new double[2];
			double tw, e;
			double pixely;
			double head = -85;
			double foot = 85;
			double delta = foot - head;
			double mid = 0;
			double flag = 0;

			tw = 256; // 每个小图的尺寸256×256(px)

			PicLoation[0] = (double) map_num_x / Math.pow(2, z) * 360 - 180;

			while (delta >= 0.000001) {
				mid = (foot + head) / 2;
				e = Math.sin((mid) / 180 * Math.PI);
				if (e > 0.9999) {
					e = 0.9999;
				}
				if (e < -0.9999) {
					e = -0.9999;
				}
				pixely = tw
						* Math.pow((double) 2, (double) z)
						/ 2
						+ 0.5
						* Math.log((1 + e) / (1 - e))
						* (-tw * Math.pow((double) 2, (double) z) / (2 * Math.PI));
				flag = map_num_y - (pixely / (double) tw);
				if (flag > 0) {
					foot = mid;
				} else if (flag < 0) {
					head = mid;
				} else {
					break;
				}
				delta = foot - head;
			}
			PicLoation[1] = mid;
			// System.out.print("\n" + (mid - 0.001) + "\n");
			return PicLoation;
		}

		/**
		 * @param z
		 *            算出地图X轴一个像素点的经纬度差 z:为地图的缩放
		 * @return double 为像素经度的值
		 */
		public static double getDeltaDegree_X(int z) {
			double Delta_Degree_X = (getGMapPicLocation((long) 2, (long) 1, z)[0] - getGMapPicLocation(
					(long) 1, (long) 1, z)[0]) / (double) 256;
			return Delta_Degree_X;
		}

		/**
		 * @param map_num_y
		 *            ,z 算出地图Y轴一个像素点的经纬度差 map_num_y:为地图编号 z:为地图缩放
		 * @return double 为像素纬度的值
		 */
		public static double getDeltaDegree_Y(long map_num_y, int z) {
			double Delta_Degree_Y = (getGMapPicLocation(218919,
					Long.valueOf(map_num_y), z)[1] - getGMapPicLocation(218919,
					Long.valueOf(map_num_y) + 1, z)[1]);
			return Delta_Degree_Y;
		}
	}

	public class Location {
		public double X_Degree;
		public double Y_Degree;

		public long X_PicNUM;
		public long Y_PicNUM;
		public int z;

		public String[][] PicFileNames = new String[3][3];

		public double Delta_Degree_X;
		public double Delta_Degree_Y;

		public int draw_X;
		public int draw_Y;

		public void DegreeToPicNUM(double _x, double _y, int z) {
			X_PicNUM = MapController.getGMapSpotLocation(_x, _y, z)[0];
			Y_PicNUM = MapController.getGMapSpotLocation(_x, _y, z)[1];
		}

		public void getFileName() {
			DegreeToPicNUM(X_Degree, Y_Degree, z);
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					Log.e(new String("gm_" + String.valueOf(X_PicNUM - 1 + i)
							+ "_" + String.valueOf(Y_PicNUM - 1 + j) + "_"
							+ String.valueOf(z) + ".png"), "");
					PicFileNames[i][j] = (new String("gm_"
							+ String.valueOf(X_PicNUM - 1 + i) + "_"
							+ String.valueOf(Y_PicNUM - 1 + j) + "_"
							+ String.valueOf(z) + ".png"));
				}
			}
		}

		public void getDrawLocation_XY() {
			DegreeToPicNUM(X_Degree, Y_Degree, z);
			Delta_Degree_X = MapController.getDeltaDegree_X(18);
			Delta_Degree_Y = MapController.getDeltaDegree_Y(Y_PicNUM, z);
			Log.e("差值",
					String.valueOf(Delta_Degree_X)
							+ ":"
							+ String.valueOf(Delta_Degree_Y)
							+ ":Y:"
							+ String.valueOf((Y_Degree - MapController
									.getGMapPicLocation(X_PicNUM, Y_PicNUM, 18)[1])
									/ Delta_Degree_X));
			draw_X = 256 + (int) ((X_Degree - MapController.getGMapPicLocation(
					X_PicNUM, Y_PicNUM, 18)[0]) / Delta_Degree_X);
			draw_Y = 256 + (int) Math
					.abs(((Y_Degree - MapController.getGMapPicLocation(
							X_PicNUM, Y_PicNUM, 18)[1]) / Delta_Degree_X));
			Log.e("坐标", String.valueOf(draw_X) + ":" + String.valueOf(draw_Y));
		}
	}
}
