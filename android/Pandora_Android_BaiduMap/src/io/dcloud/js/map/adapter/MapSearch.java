package io.dcloud.js.map.adapter;

import io.dcloud.DHInterface.IWebview;
import io.dcloud.js.map.JsMapManager;
import io.dcloud.js.map.JsMapRoute;
import io.dcloud.js.map.MapJsUtil;
import io.dcloud.util.PdrUtil;

import java.util.ArrayList;

import org.json.JSONArray;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKLine;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKRoutePlan;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;


/**
 * <p>Description:管理地图上的检索功能</p>
 *
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-11-6 上午10:07:29 created.
 * 
 * <pre><p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-6 上午10:07:29</pre>
 */
public class MapSearch {
	
	/**
	 * 搜索对象
	 */
	private MKSearch mSearchHandler;
	
	/**
	 * 页面对象
	 */
	private IWebview mIWebview;
	/**
	 * 对应js对象的id
	 */
	public String mCallbackId;
	/**
	 * 检索返回结果每页的容量
	 */
	private int mPageCapacity;
	/**
	 * 检索结果的页面
	 */
	private int mIndex = 0;
	/**
	 * 公交路线搜索策略
	 */
	private int mTransitPolicy = TRANSIT_TIME_FIRST;
	/**
	 * 驾车路线搜索策略
	 */
	private int mDrivingPolicy = DRIVING_DIS_FIRST;
	/**
	 * 搜索策略常量（公交、驾车）
	 */
	private final static int TRANSIT_NO_SUBWAY = MKSearch.EBUS_NO_SUBWAY;
	private final static int TRANSIT_TIME_FIRST = MKSearch.EBUS_TIME_FIRST;
	private final static int TRANSIT_TRANSFER_FIRST = MKSearch.EBUS_TRANSFER_FIRST;
	private final static int TRANSIT_WALK_FIRST = MKSearch.EBUS_WALK_FIRST;
//	private final static int TRANSIT_FEE_FIRST = MKSearch.ECAR_FEE_FIRST;
	
	private final static int DRIVING_DIS_FIRST = MKSearch.ECAR_DIS_FIRST;
//	private final static int DRIVING_NO_EXPRESSWAY = MKSearch.ECAR_AVOID_JAM;
	private final static int DRIVING_AVOID_JAM = MKSearch.ECAR_AVOID_JAM;
	private final static int DRIVING_FEE_FIRST = MKSearch.ECAR_FEE_FIRST;
	private final static int DRIVING_TIME_FIRST = MKSearch.ECAR_TIME_FIRST;
	
	
	private final static int SEARCH_RESULT_TYPE_POSITION = 0;
	private final static int SEARCH_RESULT_TYPE_ROUTE = 1;
	/**
	 * Description: 构造函数 
	 * @param pFrameView
	 * @param pJsId 
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-8 下午3:50:14</pre>
	 */
	public MapSearch(IWebview pIWebview) {
		mIWebview = pIWebview;
		mSearchHandler = new MKSearch();
		mSearchHandler.init(DHMapFrameItem.getBMapManager(), listener);
	}

	/**
	 * @return the pageCapacity
	 */
	public int getPageCapacity() {
		return mPageCapacity;
	}
	/**
	 * @param pageCapacity the pageCapacity to set
	 */
	public void setPageCapacity(String pageCapacity) {
		mPageCapacity = PdrUtil.parseInt(pageCapacity,10);
	}

	/**
	 * @param pTransitPolicy the mBusPolicy to set
	 */
	public void setTransitPolicy(int pTransitPolicy) {
		this.mTransitPolicy = pTransitPolicy;
	}

	/**
	 * @param pDrivingPolicy the mDrivingPolicy to set
	 */
	public void setDrivingPolicy(int pDrivingPolicy) {
		this.mDrivingPolicy = pDrivingPolicy;
	}

	/**
	 * 
	 * Description:根据城市搜索
	 * @param pCity
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-9 上午11:57:28</pre>
	 */
	public boolean poiSearchInCity(String pCity,String pKeyCode, String index){//onGetPoiResult
		mIndex = PdrUtil.parseInt(index, 0);
//		mSearchHandler = new MKSearch();
		initSearchData();
		return 0 == mSearchHandler.poiSearchInCity(pCity, pKeyCode);
	}
	/**
	 * 
	 * Description:根据中心点搜索周边
	 * @param pCenter
	 * @param pRadius
	 * @param pKeyCode
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-9 上午11:57:05</pre>
	 */
	public boolean poiSearchNearBy(String pKeyCode,MapPoint pCenter,String pRadius,String index){//onGetPoiResult
		int _radius = PdrUtil.parseInt(pRadius, 0);
		mIndex = PdrUtil.parseInt(index, 0);
//			mSearchHandler = new MKSearch();
		initSearchData();
		return 0 == mSearchHandler.poiSearchNearBy(pKeyCode, pCenter, _radius);
	}
	/**
	 * 
	 * Description:根据范围和检索词发起范围检索
	 * @param pKeyCode
	 * @param ptLB
	 * @param ptRT
	 * @param index
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-16 下午4:04:45</pre>
	 */
	public boolean poiSearchInbounds(String pKeyCode,MapPoint ptLB,MapPoint ptRT,String index){//onGetPoiResult
		mIndex = PdrUtil.parseInt(index, 0);
//		mSearchHandler = new MKSearch();
		initSearchData();
		return 0 == mSearchHandler.poiSearchInbounds(pKeyCode, ptLB, ptRT);
	}

	/**
	 * 
	 * Description:用于公交路线搜索策略。
	 * @return
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-16 下午4:07:19</pre>
	 */
	public boolean setTransitPolicy(String pPolicy){
		boolean _ret = true;
		if("TRANSIT_FEE_FIRST".equals(pPolicy)){
//			mTransitPolicy = TRANSIT_FEE_FIRST;
		}else if("TRANSIT_TIME_FIRST".equals(pPolicy)){
			mTransitPolicy = TRANSIT_TIME_FIRST;
		}else if("TRANSIT_TRANSFER_FIRST".equals(pPolicy)){
			mTransitPolicy = TRANSIT_TRANSFER_FIRST;
		}else if("TRANSIT_WALK_FIRST".equals(pPolicy)){
			mTransitPolicy = TRANSIT_WALK_FIRST;
		}else{
			_ret = false;
		}
		return _ret;
	}
	/**
	 * 
	 * Description:设置驾车线路
	 * @param policy
	 * @return
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-22 上午11:40:14</pre>
	 */
	public boolean setDrivingPolicy(String pPolicy){
		boolean _ret = true;
		if("DRIVING_DIS_FIRST".equals(pPolicy)){
			mDrivingPolicy = DRIVING_DIS_FIRST;
		}else if("DRIVING_FEE_FIRST".equals(pPolicy)){
			mDrivingPolicy = DRIVING_FEE_FIRST;
		}else if("DRIVING_NO_EXPRESSWAY".equals(pPolicy)){
//			mDrivingPolicy = DRIVING_NO_EXPRESSWAY;
		}else{
			_ret = false;
		}
		return _ret;
	}
	
	/**
	 * 
	 * Description:Description:用于公交路线搜索
	 * @param pStart
	 * @param pEnd
	 * @param city
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-22 下午12:05:01</pre>
	 */
	public void transitSearch(Object pStart,Object pEnd,String city){//onGetTransitRouteResult
		initSearchData();
		MKPlanNode sNode = new MKPlanNode();
		if(pStart instanceof MapPoint){
			sNode.pt = (MapPoint)pStart;
		}else{
			sNode.name = (String)pStart;
		}
		MKPlanNode eNode = new MKPlanNode();
		if(pEnd instanceof MapPoint){
			eNode.pt = (MapPoint)pEnd;
		}else{
			eNode.name = (String)pEnd;
		}
		mSearchHandler.transitSearch(city, sNode, eNode);
	}
	/**
	 * 
	 * Description:用于驾车路线搜索（start,end为point点)
	 * @param start
	 * @param startCity
	 * @param end
	 * @param endCity
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-18 下午5:18:54</pre>
	 */
	public void drivingSearch(Object pStart,String pStartCity,Object pEnd, String pEndCity){//onGetDrivingRouteResult
		initSearchData();
		MKPlanNode sNode = new MKPlanNode();
		if(pStart instanceof MapPoint){
			sNode.pt = (MapPoint)pStart;
		}else{
			sNode.name = (String)pStart;
		}
		MKPlanNode eNode = new MKPlanNode();
		if(pEnd instanceof MapPoint){
			eNode.pt = (MapPoint)pEnd;
		}else{
			eNode.name = (String)pEnd;
		}
		mSearchHandler.drivingSearch(pStartCity, sNode, pEndCity, eNode);
	}
	private void initSearchData(){
		mSearchHandler.goToPoiPage(mIndex);
		mSearchHandler.setPoiPageCapacity(mPageCapacity);
		mSearchHandler.setDrivingPolicy(this.mDrivingPolicy);
	}
	public void walkingSearch(Object pStart, String string, Object pEnd, String string2) {//onGetWalkingRouteResult
		initSearchData();
		MKPlanNode sNode = new MKPlanNode();
		if(pStart instanceof MapPoint){
			sNode.pt = (MapPoint)pStart;
		}else{
			sNode.name = (String)pStart;
		}
		MKPlanNode eNode = new MKPlanNode();
		if(pEnd instanceof MapPoint){
			eNode.pt = (MapPoint)pEnd;
		}else{
			eNode.name = (String)pEnd;
		}
		mSearchHandler.walkingSearch(string, sNode, string2, eNode);
	}
	
	private static final int POISEARCH_TYPE = 0;
	private static final int ROUTESEARCH_TYPE = 1;
	/**
	 * 
	 * @param searchType [onPoiSearchComplete|onRouteSearchComplete]
	 * @param pScript 
	 * @param pJsVar
	 */
	private void onSearchComplete(int searchType,String pScript){
		if(searchType == POISEARCH_TYPE){
			MapJsUtil.execCallback(mIWebview, mCallbackId,pScript);
		}else if(searchType == ROUTESEARCH_TYPE){
			MapJsUtil.execCallback(mIWebview, mCallbackId,pScript);
		}
	}
	
	/**
	 * 
	 * Description:创建CMap.Point对象
	 * @param pPoint
	 * @param pName
	 * @param pRet
	 * @return
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-12-4 上午10:13:02</pre>
	 */
	private String newJS_Point_Obj(GeoPoint pPoint, String pName){
		StringBuffer sb = new StringBuffer();
		float _lat = (float) ((pPoint.getLatitudeE6())/1e6);
		float _lng = (float) ((pPoint.getLongitudeE6())/1e6);
		MapJsUtil.newJsVar(sb, pName, "plus.maps.Point", _lng + "," + _lat);
		return sb.toString();
	}
	
	private StringBuffer newJS_SearchPoiResult_Obj( String pName, StringBuffer pRet){
		MapJsUtil.newJsVar(pRet, pName, "plus.maps.__SearchPoiResult__", null);
		return pRet;
	}
	
	private String newJS_Position_Obj(MKPoiInfo poi){
		StringBuffer sb = new StringBuffer();
		String ptName = "p";
		sb.append(newJS_Point_Obj(poi.pt,ptName));
		String posName = "pos";
		MapJsUtil.newJsVar(sb, posName, "plus.maps.Position", ptName);
		MapJsUtil.assignJsVar(sb, posName, "address", poi.address);
		MapJsUtil.assignJsVar(sb, posName, "city", poi.city);
		MapJsUtil.assignJsVar(sb, posName, "name", poi.name);
		MapJsUtil.assignJsVar(sb, posName, "phone", poi.phoneNum);
		MapJsUtil.assignJsVar(sb, posName, "postcode", poi.postCode);
		return MapJsUtil.wrapJsEvalString(sb.toString(), posName);
	}
	private String newJS_Position_Obj(GeoPoint pt,String posName){
		StringBuffer sb = new StringBuffer();
		String ptName = "p";
		sb.append(newJS_Point_Obj(pt,ptName));
		MapJsUtil.newJsVar(sb, posName, "plus.maps.Position", ptName);
		return sb.toString();
	}
	
	private JSONArray toPositionArray(ArrayList<MKPoiInfo> list){
		JSONArray arr = new JSONArray();
		for(MKPoiInfo poi : list){
			arr.put(newJS_Position_Obj(poi));
		}
		return arr;
	}
	
	/**
	 * 
	 * Description: 创建CMap.Route对象
	 * @param pStart
	 * @param pEnd
	 * @param pIndex
	 * @param pRet
	 * @return
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-12-3 下午4:52:36</pre>
	 */
	private String newJS_Route_Obj(GeoPoint pStart, GeoPoint pEnd, int pIndex, StringBuffer pRet){
		String _jsStartPoint = newJS_Point_Obj(pStart,"start_"+pIndex);
		String _jsEndPoint = newJS_Point_Obj(pStart,"end_"+pIndex);
		//构造Route对象
		String _route = "_route" + pIndex;
		MapJsUtil.newJsVar(pRet, _route, "plus.maps.Route", _jsStartPoint + "," +_jsEndPoint);
		return _route;
	}
	
	private String newJS_Route_Obj(MKRoute route){
		// 5+ js map 规范
//		startPoint：Point，只读属性，路线起点地理坐标点
//		endPoint：Point，只读属性，路线终点地理坐标点
//		pointCount：Point，只读属性，路线坐标点段数
//		pointList：Array，只读属性，路线的地理坐标点数组，数组中保存Point对象。
//		distance：Number，只读属性，路线从起始点到终点的距离，单位为米。
//		routeTip：DOMString，只读属性，线路提示信息，没有提示信息则返回空字符串。
		
		StringBuffer sb = new StringBuffer();
		String sptName = "sp";
		sb.append(newJS_Point_Obj(route.getStart(),sptName));
		String eptName = "ep";
		sb.append(newJS_Point_Obj(route.getEnd(),eptName));
		String routeName = "route";
		MapJsUtil.newJsVar(sb, routeName, "plus.maps.Route", sptName + "," + eptName + ",false");//不调用至native层
		MapJsUtil.assignJsVar(sb, routeName, "pointCount", route.getArrayPoints().get(0).size());
		MapJsUtil.assignJsVar(sb, routeName, "distance", route.getDistance());
		MapJsUtil.assignJsVar(sb, routeName, "routeTip", route.getTip());
		String uuid = "Route_" + route.hashCode();
		//构造native层对象，与js层一一对应
		MapJsUtil.assignJsVar(sb, routeName, "_UUID_", uuid);//修改uuid使native层保存对象于js层一一对应
		JsMapRoute routeNativeObj = new JsMapRoute(mIWebview);
		routeNativeObj.setRoute(route);
		JsMapManager.getJsMapManager().putJsObject(uuid, routeNativeObj);
		
		JSONArray ptsArr = new JSONArray();
		for(ArrayList<GeoPoint> geoArr : route.getArrayPoints()){
			for(GeoPoint p : geoArr){
				ptsArr.put(newJS_Point_Obj(p,null));
			}
		}
		MapJsUtil.assignJsVar(sb, routeName, "pointList", ptsArr);
		return MapJsUtil.wrapJsEvalString(sb.toString(),routeName);
	}
	private String newJS_Route_Obj(MKTransitRoutePlan routePlan){
		// 5+ js map 规范
//		startPoint：Point，只读属性，路线起点地理坐标点
//		endPoint：Point，只读属性，路线终点地理坐标点
//		pointCount：Point，只读属性，路线坐标点段数
//		pointList：Array，只读属性，路线的地理坐标点数组，数组中保存Point对象。
//		distance：Number，只读属性，路线从起始点到终点的距离，单位为米。
//		routeTip：DOMString，只读属性，线路提示信息，没有提示信息则返回空字符串。
		
		StringBuffer sb = new StringBuffer();
		String sptName = "sp";
		sb.append(newJS_Point_Obj(routePlan.getStart(),sptName));
		String eptName = "ep";
		sb.append(newJS_Point_Obj(routePlan.getEnd(),eptName));
		String routeName = "route";
		MapJsUtil.newJsVar(sb, routeName, "plus.maps.Route", sptName + "," + eptName + ",false");//不调用至native层
		MapJsUtil.assignJsVar(sb, routeName, "distance", routePlan.getDistance());
		MapJsUtil.assignJsVar(sb, routeName, "routeTip", routePlan.getContent());
		//构造native层对象，与js层一一对应
		String uuid = "Route_" + routePlan.hashCode();
		MapJsUtil.assignJsVar(sb, routeName, "_UUID_", uuid);//修改uuid使native层保存对象于js层一一对应
		JsMapRoute routeNativeObj = new JsMapRoute(mIWebview);
		routeNativeObj.setRoute(routePlan);
		JsMapManager.getJsMapManager().putJsObject(uuid, routeNativeObj);
		
		int lineLen = routePlan.getNumLines();//公交
		JSONArray ptsArr = new JSONArray();
		for(int l = 0; l < lineLen; l++){
			for(GeoPoint p : routePlan.getLine(l).getPoints()){
				ptsArr.put(newJS_Point_Obj(p,null));
			}
		}
		
		int routeLen = routePlan.getNumRoute();//公交中需要的步行
		for(int r = 0; r < routeLen; r++){
			for(GeoPoint p : routePlan.getRoute(r).getArrayPoints().get(0)){
				ptsArr.put(newJS_Point_Obj(p,null));
			}
		}
		
		MapJsUtil.assignJsVar(sb, routeName, "pointCount", ptsArr.length());
		MapJsUtil.assignJsVar(sb, routeName, "pointList", ptsArr);
		return MapJsUtil.wrapJsEvalString(sb.toString(),routeName);
	}
	
	
	private JSONArray toRouteArray(MKWalkingRouteResult routes){//walkingSearch
		JSONArray arr = new JSONArray();
//		for(int i = 0; i < routes.getNumPlan(); i++){
			MKRoutePlan plan = routes.getPlan(0);
			int routeLen = plan.getNumRoutes();//步行
			for(int r = 0; r < routeLen; r++){
				arr.put(newJS_Route_Obj(plan.getRoute(r)));
			}
//		}
		return arr;
	}
	private JSONArray toRouteArray(MKDrivingRouteResult routes){//drivingSearch
		JSONArray arr = new JSONArray();
//		for(int i = 0; i < routes.getNumPlan(); i++){
			MKRoutePlan plan = routes.getPlan(0);
			int routeLen = plan.getNumRoutes();//驾车
			for(int r = 0; r < routeLen; r++){
				arr.put(newJS_Route_Obj(plan.getRoute(r)));
			}
//		}
		return arr;
	}
	
	private JSONArray toRouteArray(MKTransitRouteResult routes){//transitSearch
		JSONArray arr = new JSONArray();
		for(int i = 0; i < routes.getNumPlan(); i++){
			MKTransitRoutePlan plan = routes.getPlan(i);
			arr.put(newJS_Route_Obj(plan));
		}
		return arr;
	}
	
	private StringBuffer newJS_SearchRouteResult_Obj( String pName, StringBuffer pRet){
		MapJsUtil.newJsVar(pRet, pName, "plus.maps.__SearchRouteResult__", null);
		return pRet;
	}
	
	MKSearchListener listener = new MKSearchListener(){

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		}

		
		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
		}
		
		/**
		 * @param type 返回结果类型: 
		 * 当预设城市有搜索结果时，type为 MKSearch.TYPE_POI_LIST，在预设城市没有搜索结果，
		 * 但在其他城市找到时返回其他城市列表, type为 MKSearch.TYPE_CITY_LIST
		 * @param iError - 错误号，0表示正确返回
		 */
		@Override
		public void onGetPoiResult(MKPoiResult pMKPoiResult, int type, int iError) {
			//poiSearchInCity	poiSearchNearBy		poiSearchInbounds
			String spr = "spr";
			StringBuffer js = new StringBuffer();
			newJS_SearchPoiResult_Obj(spr, js);
			MapJsUtil.assignJsVar(js, spr, "__state__", iError);
			MapJsUtil.assignJsVar(js, spr, "__type__", POISEARCH_TYPE);
			MapJsUtil.assignJsVar(js, spr, "totalNumber", pMKPoiResult.getAllPoi().size());
			MapJsUtil.assignJsVar(js, spr, "currentNumber", pMKPoiResult.getCurrentNumPois());
			MapJsUtil.assignJsVar(js, spr, "pageNumber", pMKPoiResult.getNumPages());
			MapJsUtil.assignJsVar(js, spr, "pageIndex", pMKPoiResult.getPageIndex());
			JSONArray poiList = toPositionArray(pMKPoiResult.getAllPoi());
			MapJsUtil.assignJsVar(js, spr, "poiList", poiList);
			onSearchComplete(POISEARCH_TYPE, MapJsUtil.wrapJsEvalString(js.toString(), spr));
		}
		
		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		}
		
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
			//drivingSearch
			route_callback_js(result.getStart().pt, 
					result.getEnd().pt,
					result.getNumPlan(),
					iError,
					ROUTESEARCH_TYPE, 
					toRouteArray(result));
			
		}
		
		private void route_callback_js(GeoPoint sPoint,GeoPoint ePoint,int routeNumber,int state,int type,JSONArray routeList){
			String srrJSName = "srr";//SearchRouteResult
			StringBuffer js = new StringBuffer();
			newJS_SearchRouteResult_Obj(srrJSName, js);
			MapJsUtil.assignJsVar(js, srrJSName, "__state__", state);
			MapJsUtil.assignJsVar(js, srrJSName, "__type__", ROUTESEARCH_TYPE);
			MapJsUtil.assignJsVar(js, srrJSName, "startPosition", MapJsUtil.wrapJsEvalString(newJS_Position_Obj(sPoint,"startPosition"),"startPosition"),false);
			MapJsUtil.assignJsVar(js, srrJSName, "endPosition", MapJsUtil.wrapJsEvalString(newJS_Position_Obj(ePoint,"endPosition"),"endPosition"),false);
			MapJsUtil.assignJsVar(js, srrJSName, "routeNumber", routeNumber);
			MapJsUtil.assignJsVar(js, srrJSName, "routeList", routeList);
			onSearchComplete(ROUTESEARCH_TYPE, MapJsUtil.wrapJsEvalString(js.toString(), srrJSName));
		}
		
		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult pMKTransitRouteResult, int iError) {
			//transitSearch
			route_callback_js(pMKTransitRouteResult.getStart().pt, 
					pMKTransitRouteResult.getEnd().pt,
					pMKTransitRouteResult.getNumPlan(),
					iError,
					ROUTESEARCH_TYPE, 
					toRouteArray(pMKTransitRouteResult));
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
			//walkingSearch
			route_callback_js(result.getStart().pt, 
					result.getEnd().pt,
					result.getNumPlan(),
					iError,
					ROUTESEARCH_TYPE, 
					toRouteArray(result));
		}
		
	};
	MapView mMapView = null;
	public void setMapView(MapView pMapView) {
		mMapView = pMapView;
	}
	
}
