;(function(plus){

    var B = plus.bridge,
		T = plus.tools,
		MAP_FEATURE = "Maps",
		MAP_CREATEOBJECT = "createObject",
		MAP_UPDATEOBJECT = "updateObject",
		MAP_EXECMETHOD = "execMethod",
		utils ={
			callback : [],
			pushCallback : function (id,fun,nokeep) {
					this.callback[id] = { fun:fun, nokeep:nokeep};
				},
			execCallback : function (id,args) {
				if (this.callback[id] ) {
					if (this.callback[id].fun)
						this.callback[id].fun(id, args);
					if (this.callback[id].nokeep)
							delete this.callback[id];
				}
			}
		};

    function createOject( uuid, type, args )
    {
        if ( T.ANDROID == T.platform )
            return B.exec( MAP_FEATURE, MAP_CREATEOBJECT, [T.stringify([uuid, type, args])], null );
        else
            return B.exec( MAP_FEATURE, MAP_CREATEOBJECT, [uuid, type, args], null );
    }
    function updateObject( uuid, type, args )
    {
        if ( T.ANDROID == T.platform )
            return B.exec( MAP_FEATURE, MAP_UPDATEOBJECT,  [T.stringify([uuid, [type, args]])], null );
        else
        return B.exec( MAP_FEATURE, MAP_UPDATEOBJECT, [uuid, [type, args]], null );
    }

    function execMethod( className, type, args )
    {
        if ( T.ANDROID == T.platform )
            return B.exec( MAP_FEATURE, MAP_EXECMETHOD, [T.stringify([className, [type, args]])], null );
        else
            return B.exec( MAP_FEATURE, MAP_EXECMETHOD, [className, [type, args]], null );
    }

    function Map (container)
    {
        var farther = this;
        this._UUID_ = T.UUID('map');
        this._ui_div_id_ = container;
        this.__showUserLocationVisable__ = false;
        this.center = new plus.maps.Point(116.39716, 39.91669);
        this.zoom  = 12;
        this.userLocation = null;
        this.mapType = 'MAPTYPE_NORMAL';
        this.zoomControlsVisable = false;
        this.trafficVisable = false;
        this.visable = true;
        this.onclick = function ( point ) {};
        function __onclick__(id,args)
        {
            if (farther.onclick)
                farther.onclick(args);
        }
        utils.pushCallback(this._UUID_, __onclick__);
        {
            var div = document.getElementById(this._ui_div_id_);
            if(plus.tools.platform == plus.tools.ANDROID){
	            document.addEventListener("plusorientationchange",function(){
	            	setTimeout(function (){
	            		var args = [div.offsetLeft, div.offsetTop,div.offsetWidth,div.offsetHeight];
	            		console.log("reszie=" + args);
	                	updateObject(farther._UUID_, "resize", args);
	            	},200);
	            },false);
            }else{
	            div.addEventListener("resize", function(){
	            	var args = [div.offsetLeft, div.offsetTop,div.offsetWidth,div.offsetHeight];
	                updateObject(farther._UUID_, "resize", args);
	            }, false);
            }
            var args = [div.offsetLeft, div.offsetTop, div.offsetWidth, div.offsetHeight];
            createOject( this._UUID_, "mapview", args );
        }
    }
	var mapProto = Map.prototype;
    
    mapProto.centerAndZoom = function(pt,zoom)
     {
        if (pt instanceof Point && typeof(zoom) == 'number')
        {
            this.center = pt;
            this.zoom = zoom;
            var args = [pt,zoom];
            updateObject( this._UUID_, "centerAndZoom", args );
        }
    };

    mapProto.setCenter = function(pt)
    {
        if (pt instanceof Point )
        {
            this.center = pt;
            var args = [pt];
            updateObject( this._UUID_, "setCenter", args );
        }
    };

    mapProto.getCenter = function()
    {
          return this.center;
    };

    mapProto.setZoom = function(zoom)
    {
        if ( typeof(zoom) == 'number' )
        {
            this.zoom = zoom;
            updateObject( this._UUID_, "setZoom", [zoom] );
        }
    };

    mapProto.getZoom = function()
    {
        return this.zoom; 
    };

    mapProto.setMapType = function(type)
    {
        if ( type == 'MAPTYPE_NORMAL' || type == 'MAPTYPE_SATELLITE' )
        {
            this.mapType = type;
            updateObject( this._UUID_, "setMapType", [type] );
        }
    };

    mapProto.getMapType = function()
    {
        return this.mapType;
    };

    mapProto.showUserLocation = function(visable)
    {
        if ( typeof(visable) == 'boolean' 
            && this.__showUserLocationVisable__ != visable)
        {
            this.__showUserLocationVisable__ = visable;
            var args = [visable];
            updateObject( this._UUID_, "showUserLocation", args );
        }
    };

    mapProto.isShowUserLocation = function()
    {
        return this.__showUserLocationVisable__;
    };

    mapProto.getUserLocation = function( callback )
    {
        if ( typeof(callback) == 'function' ) 
        {
            function __callback__(id, args)
            {
                if (callback) 
                    callback(args.state, args.point);
            }
            var callbackID = T.UUID('callback');
            utils.pushCallback(callbackID, __callback__, true );
            var args = [callbackID];
            updateObject( this._UUID_, "getUserLocation", args );
            return true;
        }
        return false;
    };

    mapProto.getCurrentCenter = function( callback )
    {
        if ( typeof(callback) == 'function' ) 
        {   
            function __callback__(id, args)
            {
                if (callback) 
                    callback(args.state, args.point);
            }
            var callbackID = B.callbackId( __callback__ );
            utils.pushCallback(callbackID, __callback__, true );
            var args = [callbackID];
            updateObject( this._UUID_, "getCurrentCenter", args );
            return true;
        }
        return false;
    };

    mapProto.setTraffic = function(traffic)
    {
        if ( typeof(traffic) == 'boolean' && traffic != this.trafficVisable ) 
        {
            this.trafficVisable = traffic;
            var args = [traffic];
            updateObject( this._UUID_, "setTraffic", args );
        }
    };

    mapProto.isTraffic = function()
    {
        return this.trafficVisable;
    };

    mapProto.showZoomControls = function(visable)
    {
        if ( typeof(visable) == 'boolean' && visable != this.zoomControlsVisable ) 
        {
            this.zoomControlsVisable = visable;
            var args = [visable];
            updateObject( this._UUID_, "showZoomControls", args );
        }
    };

    mapProto.isShowZoomControls = function()
    {
        return this.zoomControlsVisable;
    };

    mapProto.reset = function()
    {
        updateObject( this._UUID_, "reset", [null] );
    };

    mapProto.show = function()
    {
        if ( this.visable != true ) 
        { 
            this.visable = true; 
            var div = document.getElementById(this._ui_div_id_);
            div.style.display = "";
            var args =[ div.offsetLeft, div.offsetTop, div.offsetWidth, div.offsetHeight];
            updateObject( this._UUID_, "show", args );
        }
    }

    mapProto.hide = function()
    {
        if ( this.visable != false ) 
        { 
            this.visable = false; 
            document.getElementById(this._ui_div_id_).style.display = "none";
            updateObject( this._UUID_, "hide", [null] );
        }
    };

    mapProto.addOverlay = function(overlay)
    {
        if ( overlay instanceof Circle
            || overlay instanceof Polygon
            || overlay instanceof Polyline
            || overlay instanceof Route 
            || overlay instanceof Marker ) 
        {
            var args = [overlay._UUID_];
            updateObject( this._UUID_, "addOverlay", args );
            return true;
        }
        return false;
    };

    mapProto.removeOverlay = function(overlay)
    {
        if ( overlay instanceof Circle
            || overlay instanceof Polygon
            || overlay instanceof Polyline
            || overlay instanceof Route
            || overlay instanceof Marker  ) 
        {
            var args = [overlay._UUID_];
            updateObject( this._UUID_, "removeOverlay", args );
            return true;
        }
        return false;
    };

    mapProto.clearOverlays = function()
    {
        var args = [null];
        updateObject( this._UUID_, "clearOverlays", args );
    };

    /*
    ===========================================
    *@Bubble对象的构造
    *==========================================
    */
    
    function Bubble(label)
    {
        this._UUID_ = T.UUID('Bubble');//'bubble'+(_uuid_++);
        this.label = typeof(label)=='string' ? label : '';
        this.icon = null;
        this.marker = null;
        this.onclick = function(bubble){};
    }
	var bubbleProto = Bubble.prototype;

    bubbleProto.setIcon = function(icon)
    {
        if ( typeof(icon) ==  'string' )
        {
            this.icon = icon;
            if ( this.marker )
            {
                updateObject( this.marker._UUID_, "setBubbleIcon", [this.icon] );
            }
        }
    }

    bubbleProto.getLabel = function()
    {
        return this.label;
    }

    bubbleProto.setLabel = function(label)
    {
        if ( typeof(label) ==  'string' )
        {
            this.label = label;
            if ( this.marker )
            {
                updateObject( this.marker._UUID_, "setBubbleLabel", [this.label] );
            }
        }
    }

    bubbleProto.belongMarker = function()
    {
        return this.marker;
    }

    /*
    ===========================================
    *@Point对象用于表示地图元素的坐标。
    *通常在对地图上元素进行定位时使用。
    *==========================================
    */
    function Point(longitude, latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
    }
	var pointProto = Point.prototype;

    pointProto.setLng = function(longitude)
    {
        this.longitude = longitude;
    }

    pointProto.getLng = function()
    {
        return this.longitude;
    }

    pointProto.setLat = function(latitude)
    {
        this.latitude = latitude;
    }

    pointProto.getLat = function()
    {
        return this.latitude;
    }

    pointProto.equals = function(pt)
    {
        return this.longitude == pt.longitude && this.latitude == pt.latitude;
    }
    
    /*
    ===========================================
    *@Overlay对象不能实例化，用于作为其它覆盖物的基类
    *==========================================
    */
    function Overlay()
    {
        this._UUID_ = null;
        this.visable = true; //是否添加到了地图
    }
	var overlayProto = Overlay.prototype;

    overlayProto.show = function()
    {
        if ( this.visable != true ) 
        { 
            this.visable = true; 
            updateObject( this._UUID_, "show", ['true'] );
        }
    }

    overlayProto.hide = function()
    {
        if ( this.visable != false ) 
        { 
            this.visable = false; 
            updateObject( this._UUID_, "hide", ['false'] );
        }
    }

    overlayProto.isVisible = function()
    { 
        return this.visable; 
    }
    
    /*
    ===========================================
    *@Marker创建地图标点Marker对象
    *==========================================
    */
    function Marker (pt)
    {
        var __father__ = this;
        this._UUID_ = T.UUID('marker');//'marker'+(_uuid_++);
        this.point = pt;
        this.icon = '';
        this.caption = '';
        this.bubble = null;
        
        /*
        ===========================================
        *@summay:onclick事件在用户点击地图标点时触发
        *==========================================
        */
        this.onclick = function(marker){};
        function __onclick__(id, args)
        {
            if ( 'bubbleclick' == args.type ) 
            {
                if ( __father__.bubble && __father__.bubble.onclick ) 
                     __father__.bubble.onclick(__father__.bubble);   
            }
            else if ( 'markerclick' == args.type )
            {
                if ( __father__.onclick ) 
                     __father__.onclick(__father__);   
            }
            
        }
        utils.pushCallback(this._UUID_, __onclick__);

        createOject( this._UUID_, "marker", [pt] );
    }
	Marker.prototype = new Overlay();
    var markerProto = Marker.prototype;
    markerProto.constructor = Marker;

    /*
    markerProto.toJSON = function()
    {
        return{
            'uuid'    : this._UUID_,
            //'point'   : this.point.toJSON(),
            'icon'    : this.icon,
            'caption' : this.caption,
            //'bubble'  : this.bubble.toJSON()
        };
    }*/
    markerProto.setPoint = function(pt)
    {
        if ( pt instanceof Point )
        {
            this.point = pt;
            var args = [pt];
            updateObject( this._UUID_, "setPoint", args );
        }
    }

    markerProto.getPoint = function()
    {
        return this.point;
    }

    markerProto.setIcon = function(icon)
    {
        if ( typeof(icon) == 'string' )
        {
            this.icon = icon;
            updateObject( this._UUID_, "setIcon", [icon] );
        }
    }

    markerProto.setLabel = function(label)
    {
        if ( typeof(label) == 'string' )
        {
            this.caption = label;
            updateObject( this._UUID_, "setLabel", [label] );
        }
    }

    markerProto.getLabel = function()
    {
        return this.caption;
    }

    markerProto.setBubble = function(bubble)
    {
        if ( bubble instanceof Bubble )
        {
           var marker = bubble.marker;
            if ( marker )
            {
                marker.bubble = null;
                var args = [null, null];
                updateObject( marker._UUID_, "setBubble", args );
            }
            bubble.marker = this;
            this.bubble = bubble;
            var args = [this.bubble.label, this.bubble.icon];
            updateObject( this._UUID_, "setBubble", args );
        }
        
    }

    markerProto.getBubble = function()
    {
        return this.bubble;
    }

    /*
    ===========================================
    *@Overlay对象不能实例化，用于作为其它覆盖物的基类
    *==========================================
    */
    
    function Shape()
    {
        this.strokeColor = '#FFFFFF'; //圆圈的边框颜色 Android/iOS 5.0+
        this.strokeOpacity = 1.0;  //圆圈的边框颜色透明度  Android/iOS 5.0+
        this.fillColor = '#FFFFFF';  //圆圈的填充颜色 Android/iOS 5.0+
        this.fillOpacity = 1.0;  //圆圈的填充颜色透明度  Android/iOS 5.0+
        this.lineWidth = 5;  //圆圈边框的宽度
        this.visable = true; //是否添加到了地图
    }

    Shape.prototype = new Overlay();
    var shapeProto = Shape.prototype;
    shapeProto.constructor = Shape;

    shapeProto.setStrokeColor = function(strokeColor)
    {
        if ( typeof(strokeColor) == 'string' )
        {
            this.strokeColor = strokeColor;
            updateObject( this._UUID_, "setStrokeColor", [strokeColor] );
        }
    }

    shapeProto.getStrokeColor = function()
    {
        return this.strokeColor;
    }
    
    shapeProto.setStrokeOpacity = function(strokeOpacity)
    {
        if( typeof(strokeOpacity) == 'number' )
        {
            if ( strokeOpacity < 0 )
            { strokeOpacity = 0; }
            else if ( strokeOpacity > 1 ) 
            { strokeOpacity = 1; }

            this.strokeOpacity = strokeOpacity;
            updateObject( this._UUID_, "setStrokeOpacity", [strokeOpacity] );
        }
    }

    shapeProto.getStrokeOpacity = function()
    {
        return this.strokeOpacity;
    }

    shapeProto.setFillColor= function(fillColor)
    {
        if ( typeof(fillColor) == 'string' )
        {
            this.fillColor = fillColor;
            updateObject( this._UUID_, "setFillColor", [fillColor] );
        }
    }

    shapeProto.getFillColor = function()
    {
        return this.fillColor;
    }

    shapeProto.setFillOpacity = function(fillOpacity)
    {
        if( typeof(fillOpacity) == 'number' )
        {
            if ( fillOpacity < 0 )
            { fillOpacity = 0; }
            else if ( fillOpacity > 1 ) 
            { fillOpacity = 1; }

            this.fillOpacity = fillOpacity;
            updateObject( this._UUID_, "setFillOpacity", [fillOpacity] );
        }
    }

    shapeProto.getFillOpacity = function()
    {
        return this.fillOpacity;
    }

    shapeProto.setLineWidth = function(lineWidth)
    {
        if ( typeof(lineWidth) == 'number' ) 
        {
            if ( lineWidth < 0 ) 
            { lineWidth = 0; }
            this.lineWidth = lineWidth;
            updateObject( this._UUID_, "setLineWidth", [lineWidth] );
        }
    }

    shapeProto.getLineWidth = function()
    {
        return this.lineWidth;
    }
/*
    ===========================================
    *@Circle对象用于在地图上显示的圆圈，从Overlay对象继承而来，
    **可通过Map对象的addOverlay()方法将对象添加地图中
    *==========================================
    */
    function Circle(center, radius)
    {
        this.center = center;   //圆圈中心点的经纬度坐标 Android/iOS 5.0+
        this.radius = radius;  //圆圈的半径   Android/iOS 5.0+
        this._UUID_ = T.UUID('circle');//'circle'+(_uuid_++);
        createOject(this._UUID_, "circle", [center,radius]);
    }
	
    Circle.prototype = new Shape();
    var circleProto = Circle.prototype;
    circleProto.constructor = Circle;

    circleProto.setCenter = function(center)
    {
        if ( center instanceof Point ) 
        {
            this.center = center;
            updateObject( this._UUID_, "setCenter", [center] );
        }
    }

    circleProto.getCenter = function()
    {
        return this.center;
    }
    circleProto.setRadius = function(radius)
    {
        if ( typeof(radius) == 'number' && radius >= 0)
        {
            this.radius = radius;
            updateObject( this._UUID_, "setRadius", [radius] );
        }
    }

    circleProto.getRadius = function()
    {
        return this.radius;
    }
    
    /*
    ===========================================
    *@Polygon对象用于在地图上显示的多边形
    *==========================================
    */
    function Polygon(path)
    {
        this.path = path;   //圆圈中心点的经纬度坐标 Android/iOS 5.0+
        this._UUID_ = T.UUID('polygon');//'Polygon'+(_uuid_++);
        createOject( this._UUID_, "polygon", [path] );
    }
	
    Polygon.prototype = new Shape();
    var polyProto = Polygon.prototype;
    polyProto.constructor = Polygon;
    polyProto.setPath = function(path)
    {
        this.path = path;
        updateObject( this._UUID_, "setPath", [path] );
    }
    /*
    ===========================================
    *@Polyline对象用于在地图上显示的折线，从Overlay对象继承而来
    *==========================================
    */
    function Polyline(path)
    {
        this.path = path;   //折线的顶点坐标
        this._UUID_ = T.UUID('polyline');//'polyline'+(_uuid_++);
        createOject( this._UUID_, "polyline", [path] );
    }
	
    Polyline.prototype = new Shape();
    var plProto = Polyline.prototype;
    plProto.constructor = Polyline;
    plProto.setPath = function(path)
    {
        this.path = path;
        updateObject( this._UUID_, "setPath", [path] );
    }
/*
    ===========================================
    *@Route对象用于定义地图中的路线对象，从Overlay对象继承而来，
    *可通过Map对象的addOverlay()方法将对象添加地图中
    *==========================================
    */
    function Route(ptStart, ptEnd, jscreate)
    {
        this._UUID_ = T.UUID('route');//'route' + (_uuid_++);
       // this.__jscreate__ = jscreate;
        this.startPoint = ptStart;
        this.endPoint   = ptEnd;
        this.pointCount = 0;
        this.pointList  = [];
        this.distance   = 0;
        this.routeTip   ='';
        if ( typeof(jscreate) == 'undefined' )
        {
            createOject(this._UUID_, "route", [ptStart, ptEnd, jscreate] );
        };
    }
    Route.prototype = new Overlay();
    Route.prototype.constructor = Route;

    /*
    ===========================================
    *@Polyline对象用于在地图上显示的折线，从Overlay对象继承而来
    *==========================================
    */
    function Position(pt)
    {
        this.point    = pt;
        this.address  = '';
        this.city     = '';
        this.name     = '';
        this.phone    = '';
        this.postcode ='';
    }

    /*
    ===========================================
    *@用于保存线路搜索返回的结果
    *==========================================
    */
    function SearchRouteResult()
    {
        this.__state__ = 0;
        this.__type__ = 1;
        this.startPosition = null;
        this.endPosition = null;
        this.routeNumber = 0;
        this.routeList = [];
    }

    SearchRouteResult.prototype.getRoute = function(index)
    {
        if ( index >= 0 && index < this.routeNumber )
        { return this.routeList[index]; }
        return null;
    }

    /*
    ===========================================
    *@用于保存位置检索、周边检索和范围检索返回的结果
    *==========================================
    */
    function SearchPoiResult()
    {
        this.__state__ = 0;
        this.__type__ = 0;
        this.totalNumber = 0;
        this.currentNumber = 0;
        this.pageNumber = 0;
        this.pageIndex = 0;
        this.poiList = [];
    }

    SearchPoiResult.prototype.getPosition = function(index)
    {
        if ( index >= 0 && index < this.currentNumber )
        { return this.poiList[index]; }
        return null;
    }
/*
    ===========================================
    *@Search对象用于管理地图上的检索功能，主要功能包括
    *用于位置检索、周边检索和范围检索。
    *==========================================
    */
    function Search(map)
    {
        var father = this;
        this._UUID_ = T.UUID('search');//'search'+(_uuid_++);
        this.pageCapacity = 10;
        this.map = map;
        /*
        ===========================================
        *@summay:callback 事件在兴趣点搜索完成时调用 
        *@Param: state   Number  搜索结果状态号，0表示正确返回，其它表示错误号 必选
        *@Param: result  SearchPoiResult POI检索结果 必选
        *==========================================
        */
        this.onPoiSearchComplete = function( state, result ){};

        /*
        ===========================================
        *@summay:callback 事件在路径搜索完成时调用 
        *@Param: state   Number  搜索结果状态号，0表示正确返回，其它表示错误号 必选
        *@Param: result  SearchPoiResult POI检索结果 必选
        *==========================================
        */
        this.onRouteSearchComplete = function( state, result ){};

        function searchCallBack(id, args)
        {
            if ( 0 == args.__type__  ) 
            {/*
                alert( args.getPosition(0).point.latitude);
                alert( args.getPosition(0).point.longitude);
                alert( args.getPosition(0).address);
                alert( args.getPosition(0).city);
                alert( args.getPosition(0).name);
                alert( args.getPosition(0).phone);
                alert( args.getPosition(0).postcode);*/
                if ( father.onPoiSearchComplete ) 
                     father.onPoiSearchComplete(args.__state__, args);
            }
            else if( 1 == args.__type__  )
             {/*
                    alert(args.startPosition.longitude);
                    alert(args.startPosition.latitude);
                    alert(args.endPosition.longitude);
                    alert(args.endPosition.latitude);
                    alert(args.routeList[0].pointCount);
                    alert(args.routeList[0].distance);
                    alert(args.routeList[0].routeTip);
                    alert(args.routeList[0].pointList[0].longitude);
                    alert(args.routeList[0].pointCount);
                    alert(args.routeList[0]._UUID_);*/
                if ( father.onRouteSearchComplete ) 
                     father.onRouteSearchComplete(args.__state__, args);
            }      
        }

        utils.pushCallback(this._UUID_, searchCallBack);
     
        createOject( this._UUID_, "search", [null] );
        
    }

    /*
    ===========================================
    *@summay:用于设置检索返回结果每页的容量，默认值为10 
    *@Param: capacity   Number  指定检索每页返回结果最大数目  必选
    *==========================================
    */
	var searchProto =  Search.prototype;
    searchProto.setPageCapacity = function(capacity)
    {
        this.pageCapacity = capacity;
        var args = [capacity];
        updateObject( this._UUID_, "setPageCapacity", args );
    }

    /*
    ===========================================
    *@summay: 获取检索返回结果每页的容量
    *==========================================
    */
    searchProto.getPageCapacity = function()
    {
        return this.pageCapacity;
    }

    /*
    ===========================================
    *@用于城市兴趣点检索，搜索完成后触发onPoiSearchComplete()事件  
    *@Param: city String  检索的城市名称，如果设置为空字符串则在地图所在的当前城市内进行检索   必选
    *@Parma: key String  检索的关键字  必选
    *@Parma: index   Number  检索结果的页面，默认值为0
    *==========================================
    */
    searchProto.poiSearchInCity = function( city, key, index )
    {
        if ( typeof(city) == 'string' && typeof(key) == 'string' )
        {
            var args = [city, key, index];
            updateObject( this._UUID_, "poiSearchInCity", args );
            return true;  
        }
        return false;
    }

    /*
    ===========================================
    *@用于周边检索，根据中心点、半径与检索词进行检索，搜索完成后触发onPoiSearchComplete()事件 
    *@Param: key    String  检索的关键字  必选
    *@Param: pt  Point   检索的中心点坐标    必选
    *@Param: radius  Number  检索的半径，单位为米  必选
    *@Param: index   Number  检索结果的页面，默认值为0   可选
    *==========================================
    */
    searchProto.poiSearchNearBy = function( key, pt, radius, index )
    {
        if ( typeof(key) == 'string' 
            && pt instanceof Point && typeof(radius) == 'number')
        {
            var args = [key, pt, radius, index];
            updateObject( this._UUID_, "poiSearchNearBy", args );
            return true;
        }
        return false;
    }

    /*
    ===========================================
    *@用于根据范围和检索词发起范围检索，搜索完成后触发onPoiSearchComplete()事件
    *@Param: key    String  检索的关键字  必选
    *@Param: ptLB    Point   检索范围的左下角坐标  必选
    *@Param: ptRT    Point   检索范围的右上角坐标  必选
    *@Param: index   Number  检索结果的页面，默认值为0   可选
    *==========================================
    */
    searchProto.poiSearchInbounds = function( key, ptLB, ptRT, index )
    {
        if ( typeof(key) == 'string' && ptLB instanceof Point 
             && ptRT instanceof Point )
        {
            var args = [key, ptLB, ptRT, index];
            updateObject( this._UUID_, "poiSearchInbounds", args );
            return true;
        }
        return false;
    }

    /*
    ===========================================
    *@用于公交路线搜索策略，默认采用maps .SearchPolicy.TRANSIT_TIME_FIRST策略
    *@Param: policy Number  公交线路搜索策略，
            可取值为
            maps .SearchPolicy.TRANSIT_TIME_FIRST、
            maps .SearchPolicy.TRANSIT_TRANSFER_FIRST、
            maps .SearchPolicy.TRANSIT_WALK_FIRST、
            CircleMap.SearchPolicy.TRANSIT_NO_SUBWAY    必选
    *==========================================
    */
    searchProto.setTransitPolicy = function( policy )
    {
        var args = [policy];
        updateObject( this._UUID_, "setTransitPolicy", args );
    }

    /*
    ===========================================
    *@用于公交路线搜索，搜索完成后触发onRouteSearchComplete()事件
    *@Param: start  String/Point    公交线路搜索的起点，可以为关键字、坐标两种方式 必选
    *@Param: end String/Point    公交线路搜索的终点，可以为关键字、坐标两种方式 必选
    *@Param: city    String  搜索范围的城市名称   必选
    *==========================================
    */
    searchProto.transitSearch = function( start, end, city )
    {
        if ( (start instanceof Point || typeof(start) == "string" )
            && (end instanceof Point || typeof(end) == "string")
            && typeof(city) == "string" ) 
        {
            var args = [start, end, city ];
            updateObject( this._UUID_, "transitSearch", args );
            return true; 
        }
        return false;        
    }

    /*
    ===========================================
    *@用于驾车路线搜索策略，默认采用maps .SearchPolicy.DRIVING_TIME_FIRST策略
    *@Param: policy Number  驾车线路搜索策略，
        可取值为:
        maps .SearchPolicy.DRIVING_TIME_FIRST、
        maps .SearchPolicy.DRIVING_DIS_FIRST、
        maps .SearchPolicy.DRIVING_FEE_FIRST 必选
    *==========================================
    */
    searchProto.setDrivingPolicy = function( policy )
    {
        var args = [policy];
        updateObject( this._UUID_, "setDrivingPolicy", args );
    }

    /*
    ===========================================
    *@用于驾车路线搜索策略，默认采用maps .SearchPolicy.DRIVING_TIME_FIRST策略
    *@Param: start   String/Point    驾车线路搜索的起点，可以为关键字、坐标两种方式 必选
    *@Param: startCity   String  驾车线路搜索的起点所在城市，如果start为坐标则可填入空字符串    必选
    *@Param: end String/Point    驾车线路搜索的终点，可以为关键字、坐标两种方式 必选
    *@Param: endCity String  驾车线路搜索的终点所在城市，如果end为坐标则可填入空字符串  必选
    *==========================================
    */
    searchProto.drivingSearch = function( start, startCity, end, endCity )
    {
        if ( ( start instanceof Point || typeof(start) == "string")
            && ( end instanceof Point || typeof(end) == "string")
            && typeof(startCity) == "string" 
            && typeof(endCity) == 'string') 
        {
            var args = [start, startCity, end, endCity];
            updateObject( this._UUID_, "drivingSearch", args );
            return true;
        }
        return false;
    }

    /*
    ===========================================
    *@用于步行路线搜索，搜索完成后触发onRouteSearchComplete()事件
    *@Param: start  String/Point    步行线路搜索的起点，可以为关键字、坐标两种方式 必选
    *@Param: startCity   String  步行线路搜索的起点所在城市，如果start为坐标则可传入空字符串    必选
    *@Param: end String/Point    步行线路搜索的终点，可以为关键字、坐标两种方式 必选
    *@Param: endCity String  步行线路搜索的终点所在城市，如果end为坐标则可传入空字符串  必选
    *==========================================
    */
    searchProto.walkingSearch = function(  start, startCity, end, endCity )
    {
        if ( (start instanceof Point || typeof(start) == "string")
            && ( end instanceof Point || typeof(end) == "string")
            && typeof(startCity) == "string" 
            && typeof(endCity) == 'string') 
        {
            var args = [start, startCity, end, endCity];
            updateObject( this._UUID_, "walkingSearch", args );
            return true;
        }
        return false;
    }

    /*
    ===========================================
    *@Map是地图控件抽象对象，如果需要在页面中显示地图控件则需要先创建Map对象，
    *并将对象关联的页面的div元素。
    ===========================================
    */
    plus.maps =  {
        Map       : Map,
        openSysMap: function (dst, des, src)
                      {
                         if ( dst instanceof Point && src instanceof Point  ) 
                         {
                             execMethod("map", "openSysMap", [dst, des, src]);  
                         }
                      },
        MapType   : {
                        MAPTYPE_SATELLITE: "MAPTYPE_SATELLITE",
                        MAPTYPE_NORMAL : "MAPTYPE_NORMAL"
                     },
        Marker    : Marker,
        Bubble    : Bubble,
        Point     : Point,
        Circle    : Circle,
        Polygon   : Polygon,
        Polyline  : Polyline,
        Position  : Position,
        Route     : Route,
        Search    : Search,
        SearchPolicy : {
                            TRANSIT_TIME_FIRST : 'TRANSIT_TIME_FIRST',
                            TRANSIT_TRANSFER_FIRST : 'TRANSIT_TRANSFER_FIRST', 
                            TRANSIT_WALK_FIRST : 'TRANSIT_WALK_FIRST',
                            TRANSIT_FEE_FIRST  : 'TRANSIT_FEE_FIRST',
                            DRIVING_TIME_FIRST : 'DRIVING_TIME_FIRST',
                            DRIVING_NO_EXPRESSWAY  : 'DRIVING_NO_EXPRESSWAY',
                            DRIVING_FEE_FIRST  : 'DRIVING_FEE_FIRST' 
                        },
        // 以下变量在内部使用
        __SearchRouteResult__ : SearchRouteResult,
        __SearchPoiResult__ : SearchPoiResult,
        __bridge__ : utils
    };

})(window.plus);
