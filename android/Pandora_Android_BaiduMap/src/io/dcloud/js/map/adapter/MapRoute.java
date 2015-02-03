package io.dcloud.js.map.adapter;

import io.dcloud.DHInterface.IWebview;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Point;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;


/**
 * <p>Description:地图路径对象</p>
 *
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-11-6 上午10:44:54 created.
 * 
 * <pre><p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-6 上午10:44:54</pre>
 */
public class MapRoute {
	
	MapPoint mStart;
	MapPoint mEnd;
	/**
	 * 具体路径对象
	 */
	private Object mRoute;
	/**
	 * 绘制路径的画笔
	 */
	private Paint mPaint;
	/**
	 * Description: 构造函数 
	 * @param pFrameView
	 * @param pJsId 
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-8 下午3:50:48</pre>
	 */
	public MapRoute() {
		initPaint();
	}
	/**
	 * 
	 * Description:根据起始点终点设置路径
	 * @param pStart 起始点
	 * @param pEnd	终点
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-26 上午9:55:06</pre>
	 */
	public void setRoute(MapPoint pStart,MapPoint pEnd){
		mStart = pStart;
		mEnd = pEnd;
		
	}
	IWebview mWebview;
	MapView mMapview;
	public void initMapRoute(IWebview pWebview,MapView mapview){
		mWebview = pWebview;
		mMapview = mapview;
		if(mStart != null && mEnd != null){
			mRoute = new MapLine(mapview,mStart, mEnd);
		}
	}
	/**
	 * 
	 * Description:设置搜索出来的路径
	 * @param pRoute 路径对象
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-26 上午9:56:28</pre>
	 */
	public void setRoute(Object pRoute){
		mRoute = pRoute;
	}
	
	/**
	 * 
	 * Description:获取路径对象
	 * @return 路径（MapLine/BusLineItem/Route）
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-26 上午9:58:00</pre>
	 */
	public Object getRoute(){
		if(!(mRoute instanceof Overlay)){
			if(mRoute instanceof MKRoute){
				RouteOverlay routeOverlay = new RouteOverlay(mWebview.getActivity(), mMapview);
				routeOverlay.setData((MKRoute)mRoute);
				mRoute = routeOverlay;
			}else if(mRoute instanceof MKTransitRoutePlan){
				TransitOverlay transitOverlay = new TransitOverlay (mWebview.getActivity(), mMapview);
				transitOverlay.setData((MKTransitRoutePlan)mRoute);
				mRoute = transitOverlay;
			}
		}
		return mRoute;
	}
	/**
	 * 
	 * <p>Description:起始点终点的路径</p>
	 *
	 * @version 1.0
	 * @author cuidengfeng Email:cuidengfeng@dcloud.io
	 * @Date 2012-11-26 下午6:07:15 created.
	 * 
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-26 下午6:07:15</pre>
	 */
	protected class MapLine extends GraphicsOverlay{
		//应该目前用不到这里
		public MapLine(MapView mapview,GeoPoint pStartPos,GeoPoint pEndPos) {
			super(mapview);
			Geometry lineGeometry = new Geometry();
			GeoPoint[] linePoints = new GeoPoint[3];
	  		linePoints[0] = pStartPos;
	  		linePoints[1] = pEndPos;
	  		lineGeometry.setPolyLine(linePoints);
	  	//设定样式
	  		Symbol lineSymbol = new Symbol();
	  		Symbol.Color lineColor = lineSymbol.new Color();
	  		lineColor.red = 255;
	  		lineColor.green = 0;
	  		lineColor.blue = 0;
	  		lineColor.alpha = 255;
	  		lineSymbol.setLineSymbol(lineColor, 10);
	  		//生成Graphic对象
	  		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
	  		setData(lineGraphic);
		}
	}
	/**
	 * 
	 * Description:初始化 画笔
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-26 下午6:05:05</pre>
	 */
	private void initPaint() {
		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(Color.rgb(54, 114, 227));
		mPaint.setAlpha(180);
		mPaint.setStrokeWidth(5.5f);
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setStrokeCap(Cap.ROUND);
		mPaint.setAntiAlias(true);
	}
	/**
	 * 
	 * Description:坐标点转像素点
	 * @param mapView
	 * @param pt
	 * @return
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-26 下午6:05:13</pre>
	 */
	protected static Point geoToPoint(MapView mapView, GeoPoint pt) {
		Projection pj = mapView.getProjection();
		return pj.toPixels(pt, null);
	}
	/**
	 * 
	 * <p>Description:公交线路路径</p>
	 *
	 * @version 1.0
	 * @author cuidengfeng Email:cuidengfeng@dcloud.io
	 * @Date 2012-11-26 下午6:07:48 created.
	 * 
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-26 下午6:07:48</pre>
	 */
//	protected class BusLineOverlay extends Overlay {
//		protected BusLineItem busLine;
//		private Path path;
//		
//		public BusLineOverlay(BusLineItem busLine) {
//			this.busLine = busLine;
//			initPaint();
//		}
//		
//		public BusLineItem getBusLine() {
//			return busLine;
//		}
//		
//		public void draw(Canvas canvas, MapView mapView,boolean shadow) {
//			ArrayList<Point> stack = new ArrayList<Point>();
//			buildStack(mapView, stack);
//			if (stack.size() > 0) {
//				drawLines(canvas, mapView, stack);
//				stack.clear();
//			}
//		}
//		
//		private boolean isNear(Point p0, Point p1) {
//			final int MaxDis = 2;
//			return Math.abs(p0.x - p1.x) <= MaxDis
//					&& Math.abs(p0.y - p1.y) <= MaxDis;
//		}
//		
//		private int buildStack(MapView mapView, ArrayList<Point> stack) {
//			int index = 0;
//			ArrayList<GeoPoint> geoPts = busLine.getmXys();
//			Point p0 = geoToPoint(mapView, geoPts.get(index));
//			Point p1 = null;
//			while (index < geoPts.size() - 1) {
//				index++;
//				p1 = geoToPoint(mapView, geoPts.get(index));
//				if (stack.size() == 0) {
//					stack.add(p0);
//					stack.add(p1);
//				} else {
//					if (isNear(p0, p1)) {
//						stack.set(stack.size() - 1, p1);
//					} else {
//						stack.add(p1);
//					}
//				}
//				p0 = p1;
//			}
//			if (stack.size() > 2 && isNear(stack.get(0), stack.get(1))) {
//				stack.remove(1);
//			}
//			return index;
//		}
//		
//		private void drawLines(android.graphics.Canvas canvas, MapView mapView,
//				ArrayList<Point> stack) {
//			if (path == null) {
//				path = new Path();
//			}
//			boolean isFirst = true;
//			int count = stack.size();
//			Point point = null;
//			for (int i = 0; i < count; i++) {
//				point = stack.get(i);
//				if (isFirst) {
//					path.moveTo(point.x, point.y);
//					isFirst = false;
//				} else {
//					path.lineTo(point.x, point.y);
//				}
//			}
//			canvas.drawPath(path, mPaint);
//			path.reset();
//		}
//		
//	}

}
