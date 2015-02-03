package io.dcloud.js.map;

import io.dcloud.DHInterface.IWebview;
import io.dcloud.js.map.adapter.IFJsOverlay;
import io.dcloud.js.map.adapter.MapMarker;
import io.dcloud.util.JSONUtil;

import org.json.JSONArray;

/**
 * <p>
 * Description:对应JS Marker对象
 * </p>
 * 
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-10-31 下午3:19:54 created.
 * 
 *       <pre>
 * <p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-10-31 下午3:19:54
 * </pre>
 */
class JsMapMarker extends JsMapObject implements IFJsOverlay {

	private MapMarker mMapMarker;

	/**
	 * Description: 构造函数
	 * 
	 * @param pFrameView
	 * @param pJsId
	 * 
	 *            <pre>
	 * <p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-8 下午3:48:42
	 * </pre>
	 */
	public JsMapMarker(IWebview pWebview) {
		super(pWebview);
	}

	private void init(JsMapPoint pMapPoint) {
		mMapMarker = new MapMarker(pMapPoint.getMapPoint());
		mMapMarker.setUuid(mUUID);
	}
	@Override
	protected void createObject(JSONArray pJsArgs) {
		JsMapPoint _point = JsMapManager.getJsMapManager().getMapPoint(
				mWebview, JSONUtil.getJSONObject(pJsArgs,0));
		init(_point);
	}

	@Override
	protected void updateObject(String pStrEvent, JSONArray pJsArgs) {
		if ("setPoint".equals(pStrEvent)) {
			JsMapPoint _point = JsMapManager.getJsMapManager().getMapPoint(
					mWebview, JSONUtil.getJSONObject(pJsArgs,0));
			mMapMarker.setMapPoint(_point.getMapPoint());
		} else if ("setLabel".equals(pStrEvent)) {
			mMapMarker.setLabel(JSONUtil.getString(pJsArgs,0));
		} else if ("setBubble".equals(pStrEvent)) {
			mMapMarker.setBubbleLabel(JSONUtil.getString(pJsArgs,0));
		} else if ("setIcon".equals(pStrEvent)) {
			String iconPath = mWebview.obtainFrameView().obtainApp().convert2AbsFullPath(mWebview.obtainFullUrl(),JSONUtil.getString(pJsArgs,0));
			mMapMarker.setIcon(iconPath);
		} else if ("setBubbleIcon".equals(pStrEvent)) {
			mMapMarker.setBubbleIcon(JSONUtil.getString(pJsArgs,0));
		} else if ("setBubbleLabel".equals(pStrEvent)) {
			mMapMarker.setBubbleLabel(JSONUtil.getString(pJsArgs,0));
		}
	}

	@Override
	public Object getMapOverlay() {
		return mMapMarker;
	}

}
