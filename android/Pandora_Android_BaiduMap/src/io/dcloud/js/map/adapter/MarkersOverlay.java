package io.dcloud.js.map.adapter;

import io.dcloud.DHInterface.IWebview;
import io.dcloud.adapter.util.Logger;
import io.dcloud.js.map.MapJsUtil;
import io.dcloud.util.PdrUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;


/**
 * <p>Description:所有Marker的覆盖图层</p>
 *
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-10-31 下午6:43:11 created.
 * 
 * <pre><p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-10-31 下午6:43:11</pre>
 */
public class MarkersOverlay extends ItemizedOverlay<OverlayItem> {
	
	
	/** 存放所有item */
	private List<OverlayItem> mItems = new ArrayList<OverlayItem>();
	private PopupOverlay   pop  = null;
	/**
	 * 标志气泡是显示还是未显示
	 */
	private static boolean mOpen;
	/**
	 * 显示的地图
	 */
	private DHMapView mMapView;
	
	private IWebview mWebView;
	/**
	 * Description: 构造函数 
	 * @param arg0 
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-10-31 下午6:43:20</pre>
	 */
	public MarkersOverlay(Drawable pDefaultMarker,DHMapView pMapView) {
		super(pDefaultMarker,pMapView);
		mMapView = pMapView;
		mWebView = pMapView.mWebView;
		pMapView.refresh();
		addItem(mItems);
		initBubbleView(pMapView.getContext());
	}

	TextView mbubbleViewDesc = null;
	private void initBubbleView(Context context){
		 /**
         * 创建一个popupoverlay
         */
        PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
				 pop.hidePop();
				 mMapView.refresh();
				try {
					if(mCurItem != null){
						MapMarker mm = mCurItem.getMapMarker();
						Logger.d("MarkersOverlay","onClickedPopup index=" + index + ";mCurItem=" + mm.getUuid());
						MapJsUtil.execCallback(mWebView,mm.getUuid(), "{type:'bubbleclick'}");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        };
        pop = new PopupOverlay(mMapView,popListener);
        mPopViewLayout = new PopViewLayout(context);
	}

	/* (non-Javadoc)
	 * @see com.baidu.mapapi.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int location) {
		return mItems.get(location);
	}

	public void addOverlayItem(OverlayItem oi) {//需要先清除之前添加的，然后在重新添加
		removeAll();
		mItems.add(oi);
		addItem(mItems);
	}
	/**
	 * 
	 * @param oi 要删除的OverlayItem
	 * @return 返回是否删除成功
	 * <br/>Create By: yanglei Email:yanglei@dcloud.io at 2014-5-28 下午04:19:30
	 */
	public boolean removeOverlayItem(OverlayItem oi){
		if(mItems.contains(oi)){
			removeAll();
			mItems.remove(oi);
			addItem(mItems);
			return true;
		}
		return false;
	}
	
	public void clearOverlayItem(){
		removeAll();
		mItems.clear();
		addItem(mItems);
	}

	/* (non-Javadoc)
	 * @see com.baidu.mapapi.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		return mItems.size();
	}
	MapMarker.MarkerOverlay mCurItem;
	PopViewLayout mPopViewLayout = null;
	/* (non-Javadoc)
	 * @see com.amap.mapapi.map.ItemizedOverlay#onTap(int)
	 */
	@Override
	protected boolean onTap(int pIndex) {
		MapMarker.MarkerOverlay item = (MapMarker.MarkerOverlay)getItem(pIndex);
		mCurItem = item ;
		boolean show = false;
		MapMarker mm = item.getMapMarker();
		String bubbleLabel = mm.getBubbleLabel();
		if(!PdrUtil.isEmpty(bubbleLabel)){
			show |= true;
			mPopViewLayout.setBubbleLabel(bubbleLabel);
		}
		String iconPath = mm.getBubbleIcon();
		if(iconPath != null){
			try {
				InputStream is = mWebView.obtainFrameView().obtainApp().obtainResInStream(mWebView.obtainFullUrl(),iconPath);
				Options o = new Options();
				o.inScaled = false;
				Bitmap b = BitmapFactory.decodeStream( is,null,o);
				mPopViewLayout.mBubbleIconWidth = b.getWidth();
				mPopViewLayout.mBubbleIconHeight = b.getHeight();
				mPopViewLayout.setBubbleIcon(new BitmapDrawable(b));
				show |= true;
			} catch (Exception e) {
				show |= false;
			}
		}
		if(show){
			mPopViewLayout.didOnLayout = false;
			pop.showPopup(mPopViewLayout,item.getPoint(),5);
		}
		Logger.d("MarkersOverlay","onTap pIndex=" + pIndex + ";mCurItem=" + mCurItem.getMapMarker().getUuid());
		MapJsUtil.execCallback(mWebView,mCurItem.getMapMarker().getUuid(), "{type:'markerclick'}");
		return true;
	}
	
	
	@Override
	public boolean onTap(GeoPoint pt , MapView mMapView){
		if (pop != null){
            pop.hidePop();
		}
		return false;
	}
	/**
	 * 	
	 * Description:隐藏气泡
	 *
	 * <pre><p>ModifiedLog:</p>
	 * Log ID: 1.0 (Log编号 依次递增)
	 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-26 上午10:44:55</pre>
	 */
	protected static void hideBubbleView(){
//		 pop.hidePop();
		mOpen = false;
	}
}
