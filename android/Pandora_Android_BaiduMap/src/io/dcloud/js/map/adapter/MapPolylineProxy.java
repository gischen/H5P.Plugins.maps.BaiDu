package io.dcloud.js.map.adapter;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.mapapi.map.Symbol.Stroke;
import com.baidu.platform.comapi.basestruct.GeoPoint;


/**
 * 
 * <p>Description:在地图上显示的折线</p>
 *
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-11-6 下午6:00:35 created.
 * 
 * <pre><p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-6 下午6:00:35</pre>
 */
public class MapPolylineProxy{
	
	/**
	 * JS对应的ID
	 */
	private String mJsId;
	/**
	 * GeoPoint点的集合
	 */
	private ArrayList<MapPoint> mMapPoints;
	/**
	 * 像素点的集合
	 */
	private ArrayList<Point> mPoints;
	/**
	 * 线的颜色
	 */
	private int mStrokeColor = 0xFF000000;
	/**
	 * 线的透明度
	 */
	private float mStrokeOpacity = 1;
	/**
	 * 线的宽度
	 */
	private int mLineWidth = 5;
	
//	Symbol lineSymbol = null;
//	Geometry lineGeometry = null;
	/**
	 * 
	 * Description: 构造函数 
	 * @param mapview 父类GraphicsOverlay需要MapView
	 * @param pMapPoints 
	 *</pre> Create By: yanglei Email:yanglei@dcloud.io at 2014-5-30 下午04:36:16
	 */
	public MapPolylineProxy(ArrayList<MapPoint> pMapPoints){
		mMapPoints = pMapPoints;
	}
	MapPolyline mMapPolylineImpl = null;
	public void initMapPolyline(MapView mapView){
		mMapPolylineImpl = new MapPolyline(mapView);
	}
	
	public MapPolyline getMapPolyline(){
		return mMapPolylineImpl;
	}
	/**
	 * 
	 * Description:设置折线的顶点坐标
	 * @param pAryPoint
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-6 下午6:03:55</pre>
	 */
	public void setPath(ArrayList<MapPoint> pMapPoints){
		mMapPoints = pMapPoints;
		updateData();
	}
	
	public int getStrokeColor() {
		return mStrokeColor;
	}

	public void setStrokeColor(int pStrokeColor) {
		this.mStrokeColor = 0x88000000 | pStrokeColor;
		updateData();
	}
	/**
	 * @return the strokeOpacity
	 */
	public float getStrokeOpacity() {
		return mStrokeOpacity;
	}
	/**
	 * @param pStrokeOpacity the strokeOpacity to set
	 */
	public void setStrokeOpacity(float pStrokeOpacity) {
		this.mStrokeOpacity = pStrokeOpacity;
		updateData();
	}
	/**
	 * @return the lineWidth
	 */
	public float getLineWidth() {
		return mLineWidth;
	}
	/**
	 * @param pLineWidth the lineWidth to set
	 */
	public void setLineWidth(int pLineWidth) {
		this.mLineWidth = pLineWidth;
		updateData();
	}
	
	private void updateData(){
		if(mMapPolylineImpl != null){
			mMapPolylineImpl.updateData();
		}
	}
	private Graphic getNewGraphic(){
		//生成Graphic对象
  		Geometry lineGeometry = new Geometry();
  		MapPoint[] mps = new MapPoint[mMapPoints.size()];
  		mMapPoints.toArray(mps);
  		lineGeometry.setPolyLine(mps);
  		//设定样式
  		Symbol lineSymbol = new Symbol();
		Symbol.Color lineColor = lineSymbol.new Color(mStrokeColor);
//  		lineColor.red = (0x00ff0000 | mStrokeColor) >> 8;
//  		lineColor.green = (0x0000ff00 | mStrokeColor) >> 16;
//  		lineColor.blue = (0x000000ff | mStrokeColor) >> 24;
  		lineColor.alpha = (int)(mStrokeOpacity * 255);
  		lineSymbol.setLineSymbol(lineColor, mLineWidth);
		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
		return lineGraphic;
	}
	class MapPolyline extends MyGraphicsOverlay{
		public MapPolyline(MapView arg0) {
			super(arg0);
			
	  		updateData();
		}
		@Override
		protected void updateData() {
			updateData(getNewGraphic());
		}
		
	}
}
