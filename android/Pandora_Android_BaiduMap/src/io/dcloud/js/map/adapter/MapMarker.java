package io.dcloud.js.map.adapter;

import android.graphics.drawable.Drawable;

import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.TextItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;

//import com.amap.mapapi.core.GeoPoint;
//import com.amap.mapapi.core.OverlayItem;

/**
 * <p>Description:地图上的marker对象</p>
 *
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-10-31 下午3:19:54 created.
 * 
 * <pre><p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-10-31 下午3:19:54</pre>
 */
public class MapMarker {
	private String uuid;
	/**
	 * 标志图片
	 */
	private String mIcon;
	/**
	 * 标志标题
	 */
	private String mLabel;
	/**
	 * 点对象
	 */
	private MapPoint mMapPoint;
	/**
	 * 气泡图案
	 */
	private String mBubbleIcon;
	/**
	 * 气泡里面的文本描述
	 */
	private String mBubbleLabel;
	/**
	 * 地图上的marker
	 */
	private OverlayItem mMapMarker;
	/**
	 * Description: 构造函数 
	 * @param pFrameView
	 * @param pJsId 
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-8 下午3:48:42</pre>
	 */
	public MapMarker(MapPoint pMapPoint) {
		mMapPoint = pMapPoint;
	}
	
	/**
	 * @return the mIcon
	 */
	public String getIcon() {
		return mIcon;
	}
	/**
	 * @param mIcon the mIcon to set
	 */
	public void setIcon(String pIcon) {
		this.mIcon = pIcon;
	}
	/**
	 * @return the mLabel
	 */
	public String getLabel() {
		return mLabel;
	}
	/**
	 * @param mLabel the mLabel to set
	 */
	public void setLabel(String pLabel) {
		this.mLabel = pLabel;
		TextItem item = getMarkerTextItem();
		if(item != null){
			item.text = pLabel;
		}
	}
	
	
	/**
	 * @return the mMapPoint
	 */
	public MapPoint getMapPoint() {
		return mMapPoint;
	}
	/**
	 * @param mMapPoint the mMapPoint to set
	 */
	public void setMapPoint(MapPoint pMapPoint) {
		this.mMapPoint = pMapPoint;
	}
	
	/**
	 * @param pBubbleIcon the mBubbleIcon to set
	 */
	public void setBubbleIcon(String pBubbleIcon) {
		this.mBubbleIcon = pBubbleIcon;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the mBubbleIcon
	 */
	public String getBubbleIcon() {
		return mBubbleIcon;
	}

	/**
	 * @param pBubbleLabel the mBubbleLabel to set
	 */
	public void setBubbleLabel(String pBubbleLabel) {
		this.mBubbleLabel = pBubbleLabel;
	}

	/**
	 * @return the mBubbleLabel
	 */
	public String getBubbleLabel() {
		return mBubbleLabel;
	}

	/**
	 * @return the mMapMarker
	 */
	public OverlayItem getMarkerOverlay() {
		if(mMapMarker == null){
			mMapMarker = new MarkerOverlay(mMapPoint, getLabel(), mBubbleLabel);
			if(mIcon != null){
				mMapMarker.setMarker(getMarkerIcon());
			}
		}
		return mMapMarker;
	}
	
	public TextItem getMarkerTextItem(){
		if(mMapMarker != null){
			return ((MarkerOverlay)mMapMarker).item;
		}else{
			return null;
		}
	}
	/**
	 * 
	 * Description:获取图片
	 * @return
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-10-31 下午3:31:51</pre>
	 */
	public Drawable getMarkerIcon(){
		Drawable _ret = null;
		if(mIcon != null){
			String iconPath = mIcon;
			_ret = Drawable.createFromPath(iconPath);
			if(_ret != null){
				int width = _ret.getIntrinsicWidth();
				int height = _ret.getIntrinsicHeight();
				_ret.setBounds(-width/2, -height, width/2, 0);
			}
		}
		return _ret;
	}
	
	class MarkerOverlay extends OverlayItem{
		TextItem item = null;
		private MapMarker mMapMarker;
		/**
		 * Description: 构造函数 
		 * @param arg0
		 * @param arg1
		 * @param arg2 
		 *
		 * <pre><p>ModifiedLog:</p>
		 * Log ID: 1.0 (Log编号 依次递增)
		 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-19 上午11:07:25</pre>
		 */
		public MarkerOverlay(GeoPoint pGeoPoint, String pLabel, String pDes) {
			super(pGeoPoint, pLabel, pDes);
			mMapMarker = MapMarker.this;
			item = DHMapUtil.getDrawText(pGeoPoint, pLabel);
			item.align = TextItem.ALIGN_TOP;
		}
		
		public MapMarker getMapMarker(){
			return mMapMarker;
		}
	}
	
}
