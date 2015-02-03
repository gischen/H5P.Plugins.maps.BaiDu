package io.dcloud.js.map;

import io.dcloud.DHInterface.IWebview;
import io.dcloud.js.map.adapter.IFJsOverlay;
import io.dcloud.js.map.adapter.MapRoute;

import org.json.JSONArray;

import com.baidu.mapapi.map.MapView;

/**
 * <p>
 * Description:js中的路径对象
 * </p>
 * 
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-11-6 上午10:44:54 created.
 * 
 *       <pre>
 * <p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-6 上午10:44:54
 * </pre>
 */
public class JsMapRoute extends JsMapObject implements IFJsOverlay{

	private MapRoute mMapRoute;

	/**
	 * Description: 构造函数
	 * 
	 * @param pFrameView
	 * @param pJsId
	 * 
	 *            <pre>
	 * <p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-8 下午3:50:48
	 * </pre>
	 */
	public JsMapRoute(IWebview pWebview) {
		super(pWebview);
		mMapRoute = new MapRoute();
	}

	@Override
	protected void updateObject(String pStrEvent, JSONArray pJsArgs) {
		/*if("setNativeId".equals(pStrEvent)){
			MapPoint _start = mMapManager.getMapPoint(pJsArgs[0]);
			MapPoint _end = mMapManager.getMapPoint(pJsArgs[1]);
			MapRoute _route = new MapRoute();
			_route.setRoute(_start, _end);
			mMapManager.putJsObject(pJsArgs[2], _route);
		}*/
	}

	/**
	 * Description:设置路径
	 * 
	 * @param mapPoint
	 * @param mapPoint2
	 * 
	 *            <pre>
	 * <p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-12-25 下午3:29:00
	 * </pre>
	 */
	public void setRoute(JsMapPoint pStart, JsMapPoint pEnd) {
		mMapRoute.setRoute(pStart.getMapPoint(), pEnd.getMapPoint());
	}
	
	public void setRoute(Object bdRoute) {
		mMapRoute.setRoute(bdRoute);
	}

	@Override
	protected void createObject(JSONArray pJsArgs) {
	}

	@Override
	public Object getMapOverlay() {
		return mMapRoute;
	}
	@Override
	public void onAddToMapView(MapView pMapView) {
		super.onAddToMapView(pMapView);
		mMapRoute.initMapRoute(mWebview,pMapView);
	}
}
