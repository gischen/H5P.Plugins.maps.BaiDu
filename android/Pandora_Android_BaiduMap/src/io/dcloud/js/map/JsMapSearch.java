package io.dcloud.js.map;

import io.dcloud.DHInterface.IWebview;
import io.dcloud.js.map.adapter.MapSearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.map.MapView;

/**
 * <p>
 * Description:js中的sreach对象
 * </p>
 * 
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-12-25 下午6:00:13 created.
 * 
 *       <pre>
 * <p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-12-25 下午6:00:13
 * </pre>
 */
class JsMapSearch extends JsMapObject {

	private MapSearch mMapSearch;
	/**
	 * 
	 * Description: 构造函数
	 * 
	 * @param pIFrameView
	 * 
	 *            <pre>
	 * <p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-12-25 下午6:01:46
	 * </pre>
	 */
	public JsMapSearch(IWebview pWebview) {
		super(pWebview);
		mMapSearch = new MapSearch(pWebview);
	}
	@Override
	void setUUID(String uuid) {
		super.setUUID(uuid);
		mMapSearch.mCallbackId = mUUID;
	}

	@Override
	protected void createObject(JSONArray pJsArgs) {
	}

	@Override
	protected void updateObject(String pStrEvent, JSONArray pJsArgs) {
		try {
			if("setPageCapacity".equals(pStrEvent)){
				mMapSearch.setPageCapacity(pJsArgs.getString(0));
			}else if("poiSearchInCity".equals(pStrEvent)){
				mMapSearch.poiSearchInCity(pJsArgs.getString(0), pJsArgs.getString(1), pJsArgs.getString(2));
			}else if("poiSearchNearBy".equals(pStrEvent)){
				JsMapPoint mapPoint = JsMapManager.getJsMapManager().getMapPoint(mWebview,pJsArgs.getJSONObject(1));
				mMapSearch.poiSearchNearBy(pJsArgs.getString(0), mapPoint.getMapPoint(), pJsArgs.getString(2), pJsArgs.getString(3));
			}else if("poiSearchInbounds".equals(pStrEvent)){
				JsMapPoint _point1 = JsMapManager.getJsMapManager().getMapPoint(mWebview,pJsArgs.getJSONObject(1));
				JsMapPoint _point2 = JsMapManager.getJsMapManager().getMapPoint(mWebview,pJsArgs.getJSONObject(2));
				mMapSearch.poiSearchInbounds(pJsArgs.getString(0), _point1.getMapPoint(), _point2.getMapPoint(), pJsArgs.getString(3));
			}else if("setTransitPolicy".equals(pStrEvent)){
				mMapSearch.setTransitPolicy(pJsArgs.getString(0));
			}else if("setDrivingPolicy".equals(pStrEvent)){
				mMapSearch.setDrivingPolicy(pJsArgs.getString(0));
			}else if("transitSearch".equals(pStrEvent)){
				if(pJsArgs.get(0) instanceof JSONObject){
					Object _start = JsMapManager.getJsMapManager().getMapPoint(mWebview,pJsArgs.getJSONObject(0));
					Object _end = JsMapManager.getJsMapManager().getMapPoint(mWebview,pJsArgs.getJSONObject(1));
					if(_start instanceof JsMapPoint && _end instanceof JsMapPoint){
						mMapSearch.transitSearch(((JsMapPoint)_start).getMapPoint(), ((JsMapPoint)_end).getMapPoint(), pJsArgs.getString(2));
					}
				}else{
					mMapSearch.transitSearch(pJsArgs.getString(0),pJsArgs.getString(1),pJsArgs.getString(2));
				}
			}else if("drivingSearch".equals(pStrEvent)){
				if(pJsArgs.get(0) instanceof JSONObject){
					Object _start = JsMapManager.getJsMapManager().getMapPoint(mWebview,pJsArgs.getJSONObject(0));
					Object _end =  JsMapManager.getJsMapManager().getMapPoint(mWebview,pJsArgs.getJSONObject(2));
					if(_start instanceof JsMapPoint && _end instanceof JsMapPoint){
						mMapSearch.drivingSearch(((JsMapPoint)_start).getMapPoint(), pJsArgs.getString(1)
								,((JsMapPoint)_end).getMapPoint(), pJsArgs.getString(3));
					}
				}else{
					mMapSearch.drivingSearch(pJsArgs.getString(0),pJsArgs.getString(1),pJsArgs.getString(2),pJsArgs.getString(3));
				}
			}else if("walkingSearch".equals(pStrEvent)){
				if(pJsArgs.get(0) instanceof JSONObject){
					Object _start = JsMapManager.getJsMapManager().getMapPoint(mWebview,pJsArgs.getJSONObject(0));
					Object _end =  JsMapManager.getJsMapManager().getMapPoint(mWebview,pJsArgs.getJSONObject(2));
					if(_start instanceof JsMapPoint && _end instanceof JsMapPoint){
						mMapSearch.walkingSearch(((JsMapPoint)_start).getMapPoint(), pJsArgs.getString(1)
								,((JsMapPoint)_end).getMapPoint(), pJsArgs.getString(3));
					}
				}else{
					mMapSearch.walkingSearch(pJsArgs.getString(0),pJsArgs.getString(1),pJsArgs.getString(2),pJsArgs.getString(3));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onAddToMapView(MapView pMapView) {
		super.onAddToMapView(pMapView);
		mMapSearch.setMapView(pMapView);
	}
}
