package io.dcloud.js.map.adapter;

import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;

public abstract class MyGraphicsOverlay extends GraphicsOverlay {

	public MyGraphicsOverlay(MapView arg0) {
		super(arg0);
	}

	private long lastData = 0;
	@Override
	public long setData(Graphic arg0) {
		lastData = super.setData(arg0);
		return lastData;
	}
	
	public long updateData(Graphic arg0){
		super.removeAll();
//		super.removeGraphic(lastData);
		return this.setData(arg0);
	}
	
	protected abstract void updateData();
}
