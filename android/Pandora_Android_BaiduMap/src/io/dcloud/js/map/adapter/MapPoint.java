package io.dcloud.js.map.adapter;

import com.baidu.platform.comapi.basestruct.GeoPoint;

//import com.amap.mapapi.core.GeoPoint;

/**
 * <p>Description:map上的点位置</p>
 *
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-12-25 下午2:05:19 created.
 * 
 * <pre><p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-12-25 下午2:05:19</pre>
 */
public class MapPoint extends GeoPoint {
	/**
	 * 点的经度
	 */
	private String mLongitude;
	/**
	 * 点的纬度
	 */
	private String mLatitude;
	
	/**
	 * 
	 * Description: 构造函数 
	 * @param pLongitude 经度
	 * @param pLatitude 纬度
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-14 下午3:38:35</pre>
	 */
	public MapPoint(String pLongitude, String pLatitude) {
		super(getPiontlat(pLatitude),getPiontlng(pLongitude));
		mLongitude = pLongitude;
		mLatitude = pLatitude;
	}
	
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return mLongitude;
	}
	/**
	 * @param pLongitude the longitude to set
	 */
	public void setLongitude(String pLongitude) {
		this.mLongitude = pLongitude;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return mLatitude;
	}
	/**
	 * @param pLatitude the latitude to set
	 */
	public void setLatitude(String pLatitude) {
		this.mLatitude = pLatitude;
	}
	
	/**
	 * 
	 * Description:获取6位整数经度
	 * @return
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-10-31 下午3:25:17</pre>
	 */
	private static int getPiontlng(String pLongitude){
		try {
			double _lng = Double.parseDouble(pLongitude);
			return (int)(_lng*1E6);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 
	 * Description:获取6位整数纬度
	 * @return
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-10-31 下午3:24:49</pre>
	 */
	private static int getPiontlat(String pLatitude){
		try {
			double _lat = Double.parseDouble(pLatitude);
			return (int)(_lat*1E6);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
