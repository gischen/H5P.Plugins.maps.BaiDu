package io.dcloud.js.map.adapter;

import io.dcloud.DHInterface.IWebview;
import io.dcloud.adapter.util.PlatformUtil;
import io.dcloud.js.map.IFMapDispose;
import io.dcloud.js.map.MapJsUtil;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.TextOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.vi.VMsg;

public class DHMapView extends MapView implements IFMapDispose{
	public boolean mAutoPopFromStack = false;
	static int aaaaaaaaaaa = 0;
	protected IWebview mWebView;
	/**
	 * 地图上所有覆盖图层集
	 */
	private List<Overlay> mOverlays = null;
	/**
	 * 地图控制者
	 */
	private MapController mMapController;
	/**
	 * marker覆盖图层集
	 */
	private MarkersOverlay mMarkersOverlay;
	/**
	 * 
	 */
	private TextOverlay mMarkersTextOverlay;
	/**
	 * 用户当前所在点
	 */
	private MyLocationOverlay mLocationOverlay;
	LocationData locData = null;
	
	/**
	 * 地图模式
	 */
	public static final int MAPTYPE_NORMAL = 0;
	public static final int MAPTYPE_SATELLITE = 1;
	public static final int MAPTYPE_TRAFFIC = 1001;
	public static final int MAPTYPE_UNTRAFFIC = 1002;
	
	public String mUUID = null;
	private String flag="";
	public DHMapView(Context pContext,IWebview pWebView) {
		super(pContext);flag = "我是编号：" + aaaaaaaaaaa++;
		mWebView = pWebView;
		onResume();
		VMsg.init();
		initMap();
	}
	/**解决地图第一次出现闪一下问题*/
	boolean show = false;
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if(show){
			super.dispatchDraw(canvas);
		}else{
			show = true; 
			postDelayed(new Runnable(){
				@Override
				public void run() {
					invalidate();
				}}, 1);
		}
	}
	public void initMap(){
		mOverlays = getOverlays();
		initMarkerOverlays();
		initUserLocationOverlay();
		setBuiltInZoomControls(true);
		regMapTouchListner(new MKMapTouchListener() {
			private static final String POINT_TEMPLATE = "new plus.maps.Point(%f, %f)";
			@Override
			public void onMapLongClick(GeoPoint arg0) {
//				MapJsUtil.execCallback(mWebView,mUUID, String.format(POINT_TEMPLATE, arg0.getLongitudeE6()/10e6,arg0.getLatitudeE6()/10e6));
			}
			
			@Override
			public void onMapDoubleClick(GeoPoint arg0) {
//				MapJsUtil.execCallback(mWebView,mUUID, String.format(POINT_TEMPLATE, arg0.getLongitudeE6()/10e6,arg0.getLatitudeE6()/10e6));
			}
			
			@Override
			public void onMapClick(GeoPoint arg0) {
				MapJsUtil.execCallback(mWebView,mUUID, String.format(POINT_TEMPLATE, arg0.getLongitudeE6()/1e6,arg0.getLatitudeE6()/1e6));
			}
		});
//		setClickable(false);//设置地图是否可点击。
//		setDoubleClickZooming(false);//设置MapView是否支持双击放大效果。
//		setMapMoveEnable(false);//设置地图是否可以移动 。
		mMapController = getController();
	}
	
	/**
	 * 
	 * Description:初始化Marker覆盖图层
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-5 上午10:49:35</pre>
	 */
	public void initMarkerOverlays(){
		Bitmap b = BitmapFactory.decodeStream(PlatformUtil.getResInputStream("res/point.png"));
		Drawable pointImg  = new BitmapDrawable(b);
		pointImg.setBounds(0, 0, b.getWidth(), b.getHeight());
		mMarkersOverlay = new MarkersOverlay(pointImg,DHMapView.this);
		mMarkersTextOverlay = new TextOverlay(DHMapView.this);
		mOverlays.add(mMarkersOverlay);
		mOverlays.add(mMarkersTextOverlay);
	}
	/**
	 * 
	 * Description:初始化用户当前所在点
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-5 下午2:40:56</pre>
	 */
	public void initUserLocationOverlay() {
		mLocClient = new LocationClient(getContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType(COORTYPE); // 设置坐标类型 
		option.setScanSpan(SCAN_SPAN_TIME);
//		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		mLocClient.setLocOption(option);
//		mLocClient.start();

		// 定位图层初始化
		mLocationOverlay = new MyLocationOverlay(this);
		BDLocation bdl = mLocClient.getLastKnownLocation();
		if(bdl != null ){
			locData.latitude = bdl.getLatitude();
			locData.longitude = bdl.getLongitude();
			mLocated = isRightLocation(locData.latitude,locData.longitude);;
		}else{
			locData = new LocationData();
		}
		mLocationOverlay.setLocationMode(LocationMode.NORMAL);
//		// 设置定位数据
//		mLocationOverlay.setData(locData);
//		// 添加定位图层
//		 mOverlays.add(mLocationOverlay);
		mLocationOverlay.enableCompass();
//		// 修改定位数据后刷新图层生效
//		refresh();

	}
	
	public void dispose() {
		mLocClient.unRegisterLocationListener(myListener);
	  destroy();  
	}

	/**
	 * 
	 * Description:设置地图中心点
	 * @param pCenter
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-10-31 下午4:30:45</pre>
	 */
	public void setCenter(final GeoPoint pCenter){
		// 定位设置起点
//		mMapController.setCenter(pCenter);
		// 定位到指定坐标动画
		post(new Runnable() {
			@Override
			public void run() {
				mMapController.animateTo(pCenter);
				refresh();
			}
		});
	}
	/**
	 * 
	 * Description:设置地图缩放大小
	 * @param pZoom
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-10-31 下午4:29:28</pre>
	 */
	public void setZoom(int pZoom){
		// 设置倍数
		mMapController.setZoom(pZoom);
		refresh();
	}
	/**
	 * 
	 * Description:设置是否地图显示
	 * @param pIsVisible
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-22 下午2:15:40</pre>
	 */
	protected void setVisible(boolean pIsVisible){
		if (pIsVisible) {
			DHMapView.this.setVisibility(View.VISIBLE);
		}else {
			DHMapView.this.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 
	 * Description:添加图层对象
	 * @param pMarker
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-1 上午11:56:38</pre>
	 */
	public void addOverlay(Object pOverlay){
		if(pOverlay instanceof MapMarker){
			MapMarker pMarker = (MapMarker) pOverlay;
			OverlayItem _marker = pMarker.getMarkerOverlay();
			mMarkersOverlay.addOverlayItem(_marker);
			mMarkersTextOverlay.addText(pMarker.getMarkerTextItem());
			refresh();
		}else if(pOverlay instanceof Overlay){
			mOverlays.add((Overlay) pOverlay);
		}else if(pOverlay instanceof MapRoute){
			Object _route = ((MapRoute) pOverlay).getRoute();
			if(_route instanceof Overlay){
				mOverlays.add((Overlay)_route);
			}
		}
	}
	
	/**
	 * 
	 * Description:删除覆盖物对象
	 * @param pMarker
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-5 上午10:45:15</pre>
	 */
	public void removeOverlay(Object pOverlay){
		if(pOverlay instanceof MapMarker){
			MapMarker pMarker = (MapMarker) pOverlay;
			OverlayItem _marker = pMarker.getMarkerOverlay();
			if(mMarkersOverlay.removeOverlayItem(_marker)){
				mMarkersTextOverlay.removeText(pMarker.getMarkerTextItem());
				refresh();
				MarkersOverlay.hideBubbleView();
			}
		}else if(pOverlay instanceof Overlay){
			mOverlays.remove((Overlay) pOverlay);
		}else if(pOverlay instanceof MapRoute){
			Object _route = ((MapRoute) pOverlay).getRoute();
			if(_route instanceof Overlay){
				mOverlays.remove((Overlay)_route);
			}
		}
	}
	/**
	 * 
	 * Description:清除所有的overlays
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-2 下午3:36:57</pre>
	 */
	public void clearOverlays(){
		mMarkersOverlay.clearOverlayItem();
		mMarkersTextOverlay.removeAll();
		mOverlays.clear();
		mOverlays.add(mMarkersTextOverlay);
		mOverlays.add(mMarkersOverlay);
		MarkersOverlay.hideBubbleView();
		refresh();
	}
	/**
	 * 
	 * Description:设置是否显示地图内置缩放控件
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-16 下午12:11:55</pre>
	 */
	public void showZoomControls(boolean pDisplay ){
		setBuiltInZoomControls(pDisplay);
	}
	/**
	 * 
	 * Description:设置地图类型
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-5 上午10:35:12</pre>
	 */
	public void setMapType(int pType){
		
		switch (pType) {
		case MAPTYPE_SATELLITE:
			setSatellite(true);
			break;
		case MAPTYPE_TRAFFIC:
			setTraffic(true);
			break;
		case MAPTYPE_UNTRAFFIC:
			setTraffic(false);
			break;
		case MAPTYPE_NORMAL:
			setSatellite(false);
			break;
		default:
			setSatellite(false);
			break;
		}
	
	}
	/**是否显示userLocation*/
	private boolean mShowUserLoc = false;
	/**是否执行过showUserLocation*/
	private boolean mShowUserLocEnd = false;
	/**
	 * 
	 * Description:在地图中显示用户位置信息
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-5 下午2:35:56</pre>
	 */
	public void showUserLocation(boolean pDisplay ){
		if(pDisplay){
			mLocClient.start();
			  LocationClientOption option = mLocClient.getLocOption();
		      option.setOpenGps(true);//打开gps
		      option.setCoorType(COORTYPE);     //设置坐标类型
		      option.setScanSpan(SCAN_SPAN_TIME);
		      mOverlays.add(mLocationOverlay);
		      if(mLocated){
		    	  mLocationOverlay.setData(locData);
		    	  mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
		    	  refresh();
		    	  mShowUserLocEnd = true;
		      }else{
		    	  mShowUserLocEnd = false;
		      }
		}else{
			mLocClient.stop();
			LocationClientOption option = mLocClient.getLocOption();
			option.setOpenGps(false);// 打开gps
			mLocClient.setLocOption(null);
			mOverlays.remove(mLocationOverlay);
			refresh();
		}
		mShowUserLoc = pDisplay;
	}
	
	IWebview tGetUserLocWebview = null;
	String tGetUserLocCallbackId = null;
	static final String GET_USER_LOCATION_TEMPLATE = "{state:%s,point:%s}" ;
	static final String PLUS_MAPS_POINT_TEMPLATE = "new plus.maps.Point(%s,%s)";
	
	public void getUserLocation(IWebview webview,String callBackId){
//		if(locData.latitude != 0){
//			userLocationCallback(webview, callBackId,locData);
//		}else
		{
			//plus.maps.getUserLocation 当收到位置回调时立刻通知
			tGetUserLocWebview = webview;
			tGetUserLocCallbackId = callBackId;
			if(!mLocClient.isStarted()){
				mLocClient.start();
			}
		}
	}
	private void userLocationCallback(IWebview webview, String callBackId,LocationData ld) {
		String js = String.format(GET_USER_LOCATION_TEMPLATE, 0,String.format(PLUS_MAPS_POINT_TEMPLATE, ld.longitude,ld.latitude));
		MapJsUtil.execCallback(webview, callBackId, js);
	}
	
	
	
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	/**已经定位了*/
	boolean mLocated = false;
	
	static final String COORTYPE = "bd09ll";//返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09 返回百度经纬度坐标系 ：bd09ll
	static final int SCAN_SPAN_TIME = 10000;
	public static boolean isRightLocation(double lat,double lng){
		return lat != 4.9E-324 && lng != 4.9E-324;
	}
	/**
	 * 位置监听器
	 *
	 * @version 1.0
	 * @author yanglei Email:yanglei@dcloud.io
	 * @Date 2014-4-3 上午11:58:09 created.
	 * 
	 * <br/>Create By: yanglei Email:yanglei@dcloud.io at 2014-4-3 上午11:58:09
	 */
	class MyLocationListenner implements BDLocationListener {
    	
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            if(isRightLocation(location.getLatitude(),location.getLongitude())){//是否定位成功
            	LocationClientOption option = mLocClient.getLocOption();
            	option.setOpenGps(true);// 打开gps
            	option.setCoorType(COORTYPE); // 设置坐标类型 
            	locData.latitude = location.getLatitude();
            	locData.longitude = location.getLongitude();
            	mLocated = true;
            	//如果不显示定位精度圈，将accuracy赋值为0即可
            	locData.accuracy = location.getRadius();
            	// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
            	locData.direction = location.getDirection();
            	//是手动触发请求或首次定位时，移动到定位点
            	if (mLocated && !mShowUserLocEnd && mShowUserLoc){
            		//更新定位数据
            		mLocationOverlay.setData(locData);
            		//移动地图到定位点
            		mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
            		mShowUserLocEnd = true;
            		refresh();
            	}
            	//如果plus.maps.getUserLocation不能立刻获取当前位置，则当收到位置回调时立刻通知
            	if(tGetUserLocWebview != null){
            		userLocationCallback(tGetUserLocWebview, tGetUserLocCallbackId, locData);
            		tGetUserLocWebview = null;
            		tGetUserLocCallbackId = null;
            	}
            	if(mLocClient.isStarted()){
            		mLocClient.stop();
            	}
            }else{
            	Log.w("onReceiveLocation", "warning location lat=" + location.getLatitude() + ";lng=" + location.getLongitude());
            }
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
	
	@Override
	protected void onDetachedFromWindow() {
		if(!mAutoPopFromStack){//当执行自动出栈逻辑致使mapview丢失Window，不调用真正的onDetachedFromWindow处理逻辑
			super.onDetachedFromWindow();
		}
	}
	
}
