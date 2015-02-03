package io.dcloud.js.map.adapter;

import java.util.ArrayList;

import android.graphics.Path;
import android.graphics.Point;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.mapapi.map.Symbol.Stroke;

/**
 * <p>Description:地图上画一个多边形</p>
 *
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-10-29 上午11:07:13 created.
 * 
 * <pre><p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-10-29 上午11:07:13</pre>
 */
public class MapPolygonProxy {
	
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
	 * 边框的颜色
	 */
	private int mStrokeColor = 0xFF000000;
	/**
	 * 边框的透明度
	 */
	private float mStrokeOpacity = 1;
	/**
	 * 边框的宽度
	 */
	private float mLineWidth = 5;
	/**
	 * 多边形的填充颜色
	 */
	private int mFillColor = 0x00000000;
	/**
	 * 多边形的填充颜色透明度
	 */
	private float mFillOpacity = 0;
	
//	Geometry polygonGeometry = null;
//	Symbol polygonSymbol = null;
	/**
	 * 
	 * Description: 构造函数 
	 * @param mapview 父类GraphicsOverlay需要MapView
	 * @param pMapPoints 
	 *</pre> Create By: yanglei Email:yanglei@dcloud.io at 2014-5-30 下午04:37:02
	 */
	public MapPolygonProxy(ArrayList<MapPoint> pMapPoints){
		mMapPoints = pMapPoints;
	}
	MapPolygon mMapPolygonImpl = null;
	public void initMapPolygon(MapView mapview){
		mMapPolygonImpl = new MapPolygon(mapview);
	}
	
	public MapPolygon getMapPolygon(){
		return mMapPolygonImpl;
	}
	/**
	 * 
	 * Description:设置点的集合
	 * @param pAryPoint
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-6 下午6:11:27</pre>
	 */
	public void setPath(ArrayList<MapPoint> pAryPoint){
		mMapPoints = pAryPoint;
		updateData();
	}
	
	public int getStrokeColor() {
		return mStrokeColor;
	}

	public void setStrokeColor(int pStrokeColor) {
		this.mStrokeColor = 0xFF000000 + pStrokeColor;
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
	public void setLineWidth(float pLineWidth) {
		this.mLineWidth = pLineWidth;
		updateData();
	}
	/**
	 * @return the fillStyle
	 */
	public int getFillColor() {
		return mFillColor;
	}
	/**
	 * @param pFillStyle the fillStyle to set
	 */
	public void setFillColor(int pFillColor) {
		this.mFillColor =  0xFF000000 | pFillColor;
		updateData();
	}
	/**
	 * @return the fillOpacity
	 */
	public float getFillOpacity() {
		return mFillOpacity;
	}
	/**
	 * @param pFillOpacity the fillOpacity to set
	 */
	public void setFillOpacity(float pFillOpacity) {
		this.mFillOpacity = pFillOpacity;
		updateData();
	}

//	Geometry circleGeometry = null;
//	Symbol circleSymbol = null;
	private void updateData(){
		if(mMapPolygonImpl != null){
			mMapPolygonImpl.updateData();
		}
	}
	private Graphic getNewGraphic(){
		 //构建多边形
		Geometry polygonGeometry = new Geometry();
		Symbol circleSymbol = new Symbol();
  		
  		MapPoint[] mps = new MapPoint[mMapPoints.size()];
  		mMapPoints.toArray(mps);
		polygonGeometry.setPolygon(mps);
		
		Symbol.Color fillColor = circleSymbol.new Color(mFillColor);
		fillColor.alpha = (int)(mFillOpacity * 255);
		Symbol.Color strokeColor = circleSymbol.new Color(mStrokeColor);
		strokeColor.alpha = (int)(mStrokeOpacity * 255);
		circleSymbol.setSurface(fillColor,1,0, new Stroke((int)mLineWidth, strokeColor));
		Graphic polygonGraphic = new Graphic(polygonGeometry, circleSymbol);
		return polygonGraphic;
	}
	class MapPolygon extends MyGraphicsOverlay {

		public MapPolygon(MapView arg0) {
			super(arg0);
			updateData();
		}

		@Override
		protected void updateData() {
			// TODO Auto-generated method stub
			updateData(getNewGraphic());
		}
		
	}
}
