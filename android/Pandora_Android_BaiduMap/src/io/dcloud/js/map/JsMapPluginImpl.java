package io.dcloud.js.map;


import io.dcloud.DHInterface.AbsMgr;
import io.dcloud.DHInterface.IFeature;
import io.dcloud.DHInterface.IWebview;
import io.dcloud.adapter.util.MessageHandler;
import io.dcloud.adapter.util.MessageHandler.IMessages;
import io.dcloud.js.map.adapter.DHMapUtil;
import io.dcloud.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 
 * Description:map相关js扩展
 *
 * @version 1.0
 * @author yanglei Email:yanglei@dcloud.io
 * @Date 2012-11-19 上午11:02:17 created.
 * 
 * <br/>ModifiedLog:
 * <br/>Log ID: 1.0 (Log编号 依次递增)
 * <br/>Modified By: yanglei Email:yanglei@dcloud.io at 2012-11-19 上午11:02:17
 */
public class JsMapPluginImpl implements IFeature{
	
	IFMapDispose iFMapDispose;
	JsMapManager mMapManager;
	@Override
	public String execute(IWebview pWebViewImpl, String pActionName,
			String[] pJsArgs) {
		String _result = null;
		final JSONArray _arr = JSONUtil.createJSONArray(pJsArgs[0]);
		String subActionName = JSONUtil.getString(_arr,1);
		if("createObject".equals(pActionName)){
			JsMapObject _jsMapObject = null;
			if("mapview".equals(subActionName)){
				_jsMapObject = new JsMapView(pWebViewImpl);
				iFMapDispose = (IFMapDispose) _jsMapObject;
			}else if("marker".equals(subActionName)){
				_jsMapObject = new JsMapMarker(pWebViewImpl);
			}else if("search".equals(subActionName)){
				_jsMapObject = new JsMapSearch(pWebViewImpl);
			}else if("polyline".equals(subActionName)){
				_jsMapObject = new JsMapPolyline(pWebViewImpl);
			}else if("polygon".equals(subActionName)){
				_jsMapObject = new JsMapPolygon(pWebViewImpl);
			}else if("circle".equals(subActionName)){
				_jsMapObject =  new JsMapCircle(pWebViewImpl);
			}
			_jsMapObject.setUUID( JSONUtil.getString(_arr,0) );
			_jsMapObject.createObject(JSONUtil.getJSONArray(_arr,2));
			mMapManager.putJsObject(JSONUtil.getString(_arr,0), _jsMapObject);
		}else if("updateObject".equals(pActionName)){
			MessageHandler.sendMessage(new IMessages() {
				@Override
				public void execute(Object pArgs) {
					// TODO Auto-generated method stub
					
					JsMapObject _jsMapObject = mMapManager.getJsObject(JSONUtil.getString(_arr,0));
					if(_jsMapObject != null){
						JSONArray _arrS = JSONUtil.getJSONArray(_arr,1);
						_jsMapObject.updateObject(JSONUtil.getString(_arrS,0), JSONUtil.getJSONArray(_arrS,1));
					}
				}
			},null);
		}else if("execMethod".equals(pActionName)){
			JSONArray _arrs = JSONUtil.getJSONArray(_arr,1);
			subActionName = JSONUtil.getString(_arrs,0);
			if("openSysMap".equals(subActionName)){
				JSONArray __arrs__ = JSONUtil.getJSONArray(_arrs,1);
				
				JSONObject _dst = JSONUtil.getJSONObject(__arrs__,0);
				String _destLongitude = JSONUtil.getString(_dst,"longitude");
				String _destLatitude =  JSONUtil.getString(_dst,"latitude");
				
				String desp = JSONUtil.getString(__arrs__,1);
				
				JSONObject _src = JSONUtil.getJSONObject(__arrs__,2);
				String _srcLongitude = JSONUtil.getString(_src,"longitude");
				String _srcLatitude =  JSONUtil.getString(_src,"latitude");
				
				DHMapUtil.openSysMap(pWebViewImpl, "", new String[][]{{_destLatitude,_destLongitude}, {_srcLatitude,_srcLongitude}},desp);
			}
		}
		return _result;
	}
	@Override
	public void init(AbsMgr pFeatureMgr, String pFeatureName) {
		mMapManager = JsMapManager.getJsMapManager();
	}

	@Override
	public void dispose(String pAppid) {
		if(iFMapDispose != null)
			iFMapDispose.dispose();
	}

}
