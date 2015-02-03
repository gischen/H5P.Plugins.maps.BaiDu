package io.dcloud.js.map.adapter;

import io.dcloud.util.PdrUtil;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Stroke;

//import com.amap.mapapi.map.MapView;
//import com.amap.mapapi.map.Overlay;
//import com.amap.mapapi.map.Projection;

/**
 * <p>Description:地图上的圆对象</p>
 *
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-11-12 上午11:12:18 created.
 * 
 * <pre><p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-12 上午11:12:18</pre>
 */
public class MapCircleProxy {
	
	
	/**
	 * JS对应的ID
	 */
	private String mJsId;
	/**
	 * 圈的中心位置
	 */
	private MapPoint mCenter;
	/**
	 * 圈的半径
	 */
	private double mRadius;
	/**
	 * 圈的边框颜色
	 */
	private int mStrokeColor = 0xFF000000;
	/**
	 * 圈的边框透明度
	 */
	private double mStrokeOpacity = 1;
	/**
	 * 圆圈的填充颜色
	 */
	private String mFillColor = "rgba(0,0,0,0)";
	/**
	 * 圆圈的填充颜色透明度
	 */
	private double mFillOpacity;
	/**
	 * 圆圈边框的宽度
	 */
	private double mLineWidth = 5;
	
//	Geometry circleGeometry = null;
//	Symbol circleSymbol = null;
	/**
	 * 
	 * Description: 构造函数 
	 * @param pMapview 父类GraphicsOverlay需要MapView
	 * @param pCen
	 * @param pRad 
	 *</pre> Create By: yanglei Email:yanglei@dcloud.io at 2014-5-30 下午04:33:23
	 */
	public MapCircleProxy(MapPoint pCen,double pRad){
		mCenter = pCen;
		mRadius = pRad;
		
		//---------------------------------------
	   	//构建圆
//  		circleGeometry = new Geometry();
  	
  		//设置圆中心点坐标和半径
//  		circleGeometry.setCircle(pCen, (int)mRadius);
  		//设置样式
//  		circleSymbol = new Symbol();
  		//--------------------------
	}

	public double getRadius() {
		return mRadius;
	}
	public void setRadius(double pRadius) {
		this.mRadius = pRadius;
//		circleGeometry.setCircle(mCenter, (int)mRadius);
		updateData();
	}
	public int getStrokeColor() {
		return mStrokeColor;
	}
	/**
	 * 
	 * Description:这里在地图显示的颜色必须要是8位切前面 有2位表示透明度FF为不透明所以需要加上
	 * @param pStrokeColor
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-12 上午11:21:29</pre>
	 */
	public void setStrokeColor(int pStrokeColor) {
		this.mStrokeColor = 0xFF000000 | pStrokeColor;
//		Symbol.Color circleColor = circleSymbol.new Color(PdrUtil.stringToColor(mFillColor));
//  		circleSymbol.setSurface(circleColor,1,3, new Stroke(3, circleSymbol.new Color(mStrokeColor)));
  		updateData();
	}
	
	/**
	 * @return the center
	 */
	public MapPoint getCenter() {
		return mCenter;
	}

	/**
	 * @param pCenter the center to set
	 */
	public void setCenter(MapPoint pCenter) {
		this.mCenter = pCenter;
		
//		circleGeometry.setCircle(mCenter, (int)mRadius);
		updateData();
	}

	/**
	 * @return the strokeOpacity
	 */
	public double getStrokeOpacity() {
		return mStrokeOpacity;
	}

	/**
	 * @param pStrokeOpacity the strokeOpacity to set
	 */
	public void setStrokeOpacity(double pStrokeOpacity) {
		this.mStrokeOpacity = pStrokeOpacity;
//		setFillOrStroke(3, PdrUtil.stringToColor(mFillColor), mFillOpacity, mStrokeColor, pStrokeOpacity);
		updateData();
	}

//	private void setFillOrStroke(int lineWidth,int fillColor,double fillOpacity,double strokeColor, double strokeOpacity){
//		Symbol circleSymbol = new Symbol();
//		Symbol.Color circleColor = circleSymbol.new Color(fillColor);
//		circleColor.alpha = (byte)fillOpacity;
//		Symbol.Color StrokeColor = circleSymbol.new Color((int)strokeColor);
//		StrokeColor.alpha = (int)strokeOpacity;
//		circleSymbol.setSurface(circleColor,1,lineWidth, new Stroke(3, StrokeColor));
//		updateData();
//	}
	/**
	 * @return the fillStyle
	 */
	public String getFillColor() {
		return mFillColor;
	}

	/**
	 * @param pFillStyle the fillStyle to set
	 */
	public void setFillColor(String pFillColor) {
		this.mFillColor = pFillColor;
//  		setFillOrStroke(3, PdrUtil.stringToColor(mFillColor), mFillOpacity, mStrokeOpacity, mStrokeOpacity);
  		updateData();
	}

	/**
	 * @return the fillOpacity
	 */
	public double getFillOpacity() {
		return mFillOpacity;
	}

	/**
	 * @param pFillOpacity the fillOpacity to set
	 */
	public void setFillOpacity(double pFillOpacity) {
		this.mFillOpacity = pFillOpacity;
//		setFillOrStroke(3, PdrUtil.stringToColor(mFillColor), mFillOpacity, mStrokeOpacity, mStrokeOpacity);
		updateData();
	}

	/**
	 * @return the lineWidth
	 */
	public double getLineWidth() {
		return mLineWidth;
	}

	/**
	 * @param pLineWidth the lineWidth to set
	 */
	public void setLineWidth(double pLineWidth) {
		this.mLineWidth = pLineWidth;
//		setFillOrStroke((int)mLineWidth, PdrUtil.stringToColor(mFillColor), mFillOpacity, mStrokeOpacity, mStrokeOpacity);
		updateData();
	}
	
	public void initMapCircle(MapView mapview){
		mMapCircle = new MapCircle(mapview);
	}
	public MapCircle getMapCircle(){
		return mMapCircle;
	}
	
	private Graphic getNewGraphic(){
		Symbol circleSymbol = new Symbol();
		Symbol.Color circleColor = circleSymbol.new Color(PdrUtil.stringToColor(mFillColor));
		circleColor.alpha = (int)(255 * mFillOpacity);
		Symbol.Color StrokeColor = circleSymbol.new Color(mStrokeColor);
		StrokeColor.alpha = (int)(255 * mStrokeOpacity);
		circleSymbol.setSurface(circleColor,1,0, new Stroke((int)mLineWidth, StrokeColor));
		
//		Symbol.Color circleColor = circleSymbol.new Color(PdrUtil.stringToColor(mFillColor));
//  		circleSymbol.setSurface(circleColor,1,lineWidth, new Stroke(3, circleSymbol.new Color(mStrokeColor)));
  		//生成Graphic对象
  		Geometry circleGeometry = new Geometry();
  		//设置圆中心点坐标和半径
  		circleGeometry.setCircle(mCenter, (int)mRadius);
  		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);
  		return circleGraphic;
	}
	private void updateData(){
		if(mMapCircle != null){
			mMapCircle.updateData();
		}
	}
	MapCircle mMapCircle = null;
	class MapCircle  extends MyGraphicsOverlay{
		public MapCircle(MapView arg0) {
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
