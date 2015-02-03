package io.dcloud.js.map.adapter;

import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import io.dcloud.DHInterface.IWebview;
import io.dcloud.adapter.util.CanvasHelper;
import android.content.Intent;
import android.net.Uri;



/**
 * <p>Description:地图工具</p>
 *
 * @version 1.0
 * @author cuidengfeng Email:cuidengfeng@dcloud.io
 * @Date 2012-11-8 下午3:09:24 created.
 * 
 * <pre><p>ModifiedLog:</p>
 * Log ID: 1.0 (Log编号 依次递增)
 * Modified By: cuidengfeng Email:cuidengfeng@dcloud.io at 2012-11-8 下午3:09:24</pre>
 */
public class DHMapUtil {
	
	 public static TextItem getDrawText(GeoPoint gp,String msg){
		   	//构建文字
		   	TextItem item = new TextItem();
	    	//设置文字位置
	    	item.pt = gp;
	    	//设置文件内容
	    	item.text = msg;
	    	//设文字大小
	    	item.fontSize = (int)CanvasHelper.getViablePx(12);
	    	Symbol symbol = new Symbol();
	    	Symbol.Color bgColor = symbol.new Color();
	    	//设置文字背景色
	    	bgColor.red = 0;
	    	bgColor.blue = 0;
	    	bgColor.green = 0;
	    	bgColor.alpha = 0;
	    	
	    	Symbol.Color fontColor = symbol.new Color();
	    	//设置文字着色
	    	fontColor.alpha = 255;
	    	fontColor.red = 0;
	    	fontColor.green = 0;
	    	fontColor.blue  = 0;
	    	//设置对齐方式
	    	item.align = TextItem.ALIGN_CENTER;
	    	//设置文字颜色和背景颜色
	    	item.fontColor = fontColor;
	    	item.bgColor  = bgColor ; 
	    	return item;
	    }
	 
	/**
	 * 开启手机第三方地图
	 * @param pWebView 执行所在webview
	 * @param callbackId 回调callbackid
	 * @param points 点（[lat,lng]）的数组
	 * @param pDdes 描述信息
	 * <br/>Create By: yanglei Email:yanglei@dcloud.io at 2014-5-30 下午04:16:51
	 */
	public static void openSysMap(IWebview pWebView,String callbackId,String[][] points,String pDdes){
		try {
			Uri _uri;
			/**
			 * 当没有传递过来描述的时候URI不需要传递参数
			 */
			if(pDdes!=null){
				_uri = Uri.parse("geo:"+points[0][0]+ ","+points[0][1]+"?q="+pDdes); 
			}else{
				_uri = Uri.parse("geo:"+points[0][0]+ ","+points[0][1]);
			}
			Intent _intent = new Intent(Intent.ACTION_VIEW,_uri); 
			pWebView.getActivity().startActivity(_intent); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
