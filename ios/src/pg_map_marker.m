/*
 *------------------------------------------------------------------
 *  pandora/feature/map/pg_map_marker.mm
 *  Description:
 *      地图标记和标记视图实现文件
 *  DCloud Confidential Proprietary
 *  Copyright (c) Department of Research and Development/Beijing/DCloud.
 *  All Rights Reserved.
 *
 *  Changelog:
 *	number	author	modify date  modify record
 *   0       xty     2012-12-10  创建文件
 *   Reviewed @ 20130105 by Lin Xinzheng
 *------------------------------------------------------------------
 */

#import "pg_map_marker.h"
#import "pg_map_view.h"
#import "pg_map.h"
#import "PTPathUtil.h"

// 标记排版时图片和文字之间的间隙
#define PG_MAP_MARKERVIEW_GAP 2.0f
// 标记使用文字的尺寸
#define PG_MAP_MARKERVIEW_TEXTFONTSIZE 12.0f


@implementation PGMapCoordinate

@synthesize latitude;
@synthesize longitude;

/*
 *------------------------------------------------
 *@summay:  根据经纬度生成PGMapCoordinate对象
 *@param 
 *  [1] longitude
 *  [2] latitude
 *@return
 *   PGMapCoordinate*
 *@remark
 *------------------------------------------------
 */
+(PGMapCoordinate*)pointWithLongitude:(CLLocationDegrees)longitude latitude:(CLLocationDegrees)latitude
{
    PGMapCoordinate *point = [[[PGMapCoordinate alloc] init] autorelease];
    point.latitude = latitude;
    point.longitude = longitude;
    return point;
}

/*
 *------------------------------------------------
 *@summay: 根据json数据数组生成PGMapCoordinate对象数组
 *@param jsonObj 
 *@return
 *     NSArray* PGMapCoordinate对象数组
 *@remark
 *------------------------------------------------
 */
+(NSArray*)arrayWithJSON:(NSArray*)jsonObj
{
    if ( !jsonObj )
        return nil;
    if ( ![jsonObj isKindOfClass:[NSArray class]] )
        return nil;
    
    NSMutableArray *objects = [NSMutableArray arrayWithCapacity:10];
    for ( NSMutableDictionary *dict in jsonObj )
    {
        PGMapCoordinate *point =  [PGMapCoordinate pointWithJSON:dict];
        if ( point )
            [objects addObject:point];
    }
    return objects;
}

/*
 *------------------------------------------------
 *@summay: 根据json数据生成PGMapCoordinate对象
 *@param jsonObj
 *@return
 *     PGMapCoordinate* PGMapCoordinate对象
 *@remark
 *------------------------------------------------
 */
+(PGMapCoordinate*)pointWithJSON:(NSMutableDictionary*)jsonObj
{
    if ( !jsonObj )
        return nil;
    if ( ![jsonObj isKindOfClass:[NSMutableDictionary class]] )
        return nil;
    
    PGMapCoordinate *point = [[[PGMapCoordinate alloc] init] autorelease];
    
    NSNumber *longitude = [jsonObj objectForKey:@"longitude"];
    if ( longitude && [longitude isKindOfClass:[NSNumber class]] )
        point.longitude = [longitude floatValue];
    
    NSNumber *latitude = [jsonObj objectForKey:@"latitude"];
    if ( latitude && [latitude isKindOfClass:[NSNumber class]] )
        point.latitude = [latitude floatValue];

    return point;
}

/*
 *------------------------------------------------
 *@summay: 将PGMapCoordinate转化为js对象
 *@param 
 *@return
 *     NSString* 生成js对象的function
 *@remark
 *------------------------------------------------
 */
-(NSString*)JSObject
{
    NSString *jsonObjectFormat =
    @"function (){\
        var point = new plus.maps.Point(%f, %f);\
        return point;\
    }()";
    return [NSString stringWithFormat:jsonObjectFormat, self.longitude, self.latitude];
    /*
    NSString *jsonObjectFormat = @"{ \"point\":{\"longitude\":%f, \"latitude\":%f }}";
    return [NSString stringWithFormat:jsonObjectFormat, self.longitude, self.latitude];*/
}

/*
 *------------------------------------------------
 *@summay: 将经纬度字符串转化为经纬度数组
 *@param
 * coordinateList 格式：log1,lat1,log2,lat2....
 *@return
 *     NSArray* PGMapCoordinate对象数组
 *@remark
 *------------------------------------------------
 */
+(NSArray*)coordinateListString2Array:(NSString*)coordinateList
{
    if ( coordinateList )
    {
        NSArray *coordinateLists = [coordinateList componentsSeparatedByString:@","];
        if ( [coordinateLists count] )
        {
            NSMutableArray *points = [NSMutableArray arrayWithCapacity:10];
            for (int index = 0; index < [coordinateLists count]; index+=2 )
            {
                PGMapCoordinate *point = [PGMapCoordinate pointWithLongitude:[[coordinateLists objectAtIndex:index] doubleValue]
                                                            latitude:[[coordinateLists objectAtIndex:index+1] doubleValue]];
                if ( point )
                    [points addObject:point];
            }
            return points;
        }
    }
    return nil;
}

/*
 *------------------------------------------------
 *@summay: 将经纬度字符串转化为经纬度数组
 *@param
 * coordinateList 格式：log1,lat1,log2,lat2....
 *@return
 *     NSArray* PGMapCoordinate对象数组
 *@remark
 *------------------------------------------------
 */
+(NSArray*)coordinateListWithPoints:(BMKMapPoint *)points count:(NSUInteger)count
{
    if ( points )
    {
        NSMutableArray *pointList = [NSMutableArray arrayWithCapacity:10];
        for (int index = 0; index < count; index++)
        {
            BMKMapPoint point = points[index];
            PGMapCoordinate *pdrPt = [PGMapCoordinate pointWithLongitude:point.x latitude:point.y];
            [pointList addObject:pdrPt ];
        }
        return pointList;
    }
    return nil;
}

/*
 *------------------------------------------------
 *@summay: 获取CLLocationCoordinate2D格式的经纬度
 *@param
 * 
 *@return
 *     CLLocationCoordinate2D 经纬度
 *@remark
 *------------------------------------------------
 */
-(CLLocationCoordinate2D)point2CLCoordinate
{
    CLLocationCoordinate2D coordinate = { self.latitude, self.longitude };
    return coordinate;
}

/*
 *------------------------------------------------
 *@summay: 获取coordinates经纬度数组
 *@param
 *      coordinates PGMapCoordinate*对象数组
 *@return
 *     CLLocationCoordinate2D* 经纬度数组
 *@remark
 *------------------------------------------------
 */
+(CLLocationCoordinate2D*)array2CLCoordinatesAlloc:(NSArray*)coordinates
{
    NSInteger count = [coordinates count];
    if ( coordinates && count)
    {
        CLLocationCoordinate2D* points =  malloc( sizeof(CLLocationCoordinate2D)*count);
        for ( int i = 0; i < count; i++ )
        {
            PGMapCoordinate *point = (PGMapCoordinate*)[coordinates objectAtIndex:i];
            points[i] = [point point2CLCoordinate];
        }
        return points;
    }
    return NULL;
}

@end

#pragma PGMapBubble
#pragma mark -----------------
@implementation PGMapBubble
@synthesize label;
@synthesize icon;

-(void)dealloc
{
    [label release];
    [icon release];
    [super dealloc];
}

/*
 *------------------------------------------------
 *@summay: 根据json数据创建bubble对象
 *@param jsonObj js 对象
 *@return
 *   PGMapBubble *    
 *@remark
 *------------------------------------------------
 */
+(PGMapBubble*)bubbleWithJSON:(NSMutableDictionary*)jsonObj
{
    if ( !jsonObj )
        return nil;
    if ( ![jsonObj isKindOfClass:[NSMutableDictionary class]] )
        return nil;
    
    PGMapBubble *bubble = [[[PGMapBubble alloc] init] autorelease];
    
    NSString *lable = [jsonObj objectForKey:@"label"];
    if ( lable && [lable isKindOfClass:[NSString class]] )
        bubble.label = lable;
    
    NSString *icon = [jsonObj objectForKey:@"icon"];
    if ( icon && [icon isKindOfClass:[NSString class]] )
        bubble.icon = icon;
    
    return bubble;
}

@end

#pragma PGMapBubble
#pragma mark -----------------
// 标记排版时图片和文字之间的间隙
#define MKEYMAP_MARKERVIEW_GAP 4.0f
// 标记使用文字的尺寸
#define MKEYMAP_MARKERVIEW_TEXTFONTSIZE 15.0f
// 二级标题文字的尺寸
#define MKEYMAP_MARKERVIEW_SUBTITLEFONTSIZE 12.0f
#define MEKYMAP_MARKERVIEW_FONT [UIFont systemFontOfSize:MKEYMAP_MARKERVIEW_TEXTFONTSIZE]
//气泡使用的字体
#define MEKYMAP_MARKERVIEW_BUBBLE_FONT MEKYMAP_MARKERVIEW_FONT

//static CGFloat kTransitionDuration = 0.45f;

/*
 ** @气泡视图实现
 *
 */
@implementation PGMapBubbleView

@synthesize delegate;
@synthesize bubbleLabel;
@synthesize bubbleImage;

- (id)initWithFrame:(CGRect)frame
{
    if ( (self = [super initWithFrame:frame] ) )
    {
        UIImage *imageNormal, *imageHighlighted;
        imageNormal = [[UIImage imageNamed:@"mapapi.bundle/images/icon_paopao_middle_left"] stretchableImageWithLeftCapWidth:10 topCapHeight:13];
        imageHighlighted = [[UIImage imageNamed:@"mapapi.bundle/images/icon_paopao_middle_left_highlighted"]
                            stretchableImageWithLeftCapWidth:10 topCapHeight:13];
        UIImageView *leftBgd = [[UIImageView alloc] initWithImage:imageNormal
                                                 highlightedImage:imageHighlighted];
        leftBgd.tag = 11;
        
        imageNormal = [[UIImage imageNamed:@"mapapi.bundle/images/icon_paopao_middle_right"] stretchableImageWithLeftCapWidth:10 topCapHeight:13];
        imageHighlighted = [[UIImage imageNamed:@"mapapi.bundle/images/icon_paopao_middle_right_highlighted"]
                            stretchableImageWithLeftCapWidth:10 topCapHeight:13];
        UIImageView *rightBgd = [[UIImageView alloc] initWithImage:imageNormal
                                                  highlightedImage:imageHighlighted];
        rightBgd.tag = 12;
        
        [self addSubview:leftBgd];
        [self sendSubviewToBack:leftBgd];
        [self addSubview:rightBgd];
        [self sendSubviewToBack:rightBgd];
        [leftBgd release];
        [rightBgd release];
        
        UITapGestureRecognizer *taprecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapCallback:)];
        taprecognizer.numberOfTouchesRequired = 1;
        taprecognizer.numberOfTapsRequired = 1;
        taprecognizer.cancelsTouchesInView = NO;
        // taprecognizer.delaysTouchesBegan = YES;
        // taprecognizer.delaysTouchesEnded = YES;
        [self addGestureRecognizer:taprecognizer];
        [taprecognizer release];
    }
    return self;
}

- (void) reload
{
    //加上箭头的高度
    
	
}
-(void)dealloc
{
    [_textView removeFromSuperview];
    [_textView release];
    [_iconView removeFromSuperview];
    [_iconView release];
    [super dealloc];
}

-(void)layoutSubviews
{
    CGSize size = CGSizeZero;
    size.width += 4*MKEYMAP_MARKERVIEW_GAP;
    size.height += 2*MKEYMAP_MARKERVIEW_GAP;
    
    if ( _textView )
    {
        CGRect textRect = _textView.bounds;
        textRect.origin.x = 2*MKEYMAP_MARKERVIEW_GAP;
        textRect.origin.y = MKEYMAP_MARKERVIEW_GAP;
        size.width += _textView.bounds.size.width;
        size.height += _textView.bounds.size.height;
        _textView.frame = textRect;
    }
    
    if( _iconView )
    {
        size.width += _iconView.bounds.size.width;
        if ( _textView.bounds.size.height < _iconView.bounds.size.height )
        { size.height += (_iconView.bounds.size.height - _textView.bounds.size.height); }
        CGRect imgRect = _iconView.bounds;
        if ( _textView )
        { imgRect.origin.x = _textView.bounds.size.width + 2*MKEYMAP_MARKERVIEW_GAP; }
        imgRect.origin.y = MKEYMAP_MARKERVIEW_GAP;
        _iconView.frame = imgRect;
    }
    
    //加上箭头的高度
	size.height += 12;
    
    CGRect rect0 = self.bounds;
	rect0.size = CGSizeMake( size.width, size.height);
	//self.frame = rect0;
    
    CGFloat halfWidth = rect0.size.width/2;
    UIView *image = [self viewWithTag:11];
    CGRect iRect = CGRectZero;
    iRect.size.width = halfWidth;
    iRect.size.height = rect0.size.height;
    image.frame = iRect;
    image = [self viewWithTag:12];
    iRect.origin.x = halfWidth;
    image.frame = iRect;
    self.bounds = CGRectMake(0, 0, size.width, size.height);
}

-(void)setBubbleLabel:(NSString *)text
{
    if ( !text )
    { return; }
    
    if ( !_textView )
    {
        _textView = [[UILabel alloc] init];
        _textView.backgroundColor = [UIColor clearColor];
        _textView.textColor = [UIColor blackColor];
        _textView.font = [UIFont systemFontOfSize:PG_MAP_MARKERVIEW_TEXTFONTSIZE];
        [self addSubview:_textView];
    }
    NSArray *subTexts = [text componentsSeparatedByString:@"\n"];
    if ( [subTexts count] > 1 )
    { _textView.numberOfLines = [subTexts count]; }
    _textView.text = text;
    [_textView sizeToFit];
    
    [self layoutSubviews];
}


-(void)setBubbleImage:(UIImage *)img
{
    if ( !img )
    { return; }
    
    if ( !_iconView )
    {
        _iconView = [[UIImageView alloc] init];
        [self addSubview:_iconView];
    }
    _iconView.image = img;
    [_iconView sizeToFit];
    [self layoutSubviews];
}
-(void)tapCallback:(UITapGestureRecognizer*)sender
//-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
   // [super touchesBegan:touches withEvent:event];
    if ( self.delegate )
    { [self.delegate click:self]; }
}


@end

#pragma PGMapBubble
#pragma mark -----------------
@implementation PGMapMarker

@synthesize belongMapview;
@synthesize UUID;
@synthesize label;
@synthesize icon;
@synthesize bubble;
@synthesize hidden;
@synthesize baseURL = _baseURL;

- (void)dealloc
{
    [_baseURL release];
    [UUID release];
    [label release];
    [icon release];
    [bubble release];
    [super dealloc];
}

/*
 *------------------------------------------------
 *@summay: 更新marker对象
 *@param jsonObj 
 *@return
 *@remark
 *------------------------------------------------
 */

- (BOOL)updateObject:(NSMutableArray*)jsonObj
{
    BOOL bRet = FALSE;
    if ( !jsonObj )
        return FALSE;
    if ( ![jsonObj isKindOfClass:[NSMutableArray class]] )
        return FALSE;
    
    NSString *property = [jsonObj objectAtIndex:0];
    if ( property && [property isKindOfClass:[NSString class]] )
    {
        if ( [property isEqualToString:@"setPoint"] )
        {
            NSArray *args = [jsonObj objectAtIndex:1];
            if ( args && [args isKindOfClass:[NSArray class]] )
            {
                NSMutableDictionary *dict = [args objectAtIndex:0];
                if ( dict && [dict isKindOfClass:[NSMutableDictionary class]] )
                {
                    PGMapCoordinate *point = [PGMapCoordinate pointWithJSON:dict];
                    if ( point )
                    {
                        [self setCoordinate:[point point2CLCoordinate]];
                        bRet = TRUE;
                    }
                }
            }
        }
        else if( [property isEqualToString:@"setIcon"] )
        {
            NSArray *args = [jsonObj objectAtIndex:1];
            if ( args && [args isKindOfClass:[NSArray class]] )
            {
                self.icon = nil;
                NSString *value = [args objectAtIndex:0];
                if ( value && [value isKindOfClass:[NSString class]] )
                    self.icon = [self getFullPath:value];
                bRet = TRUE;
            }
        }
        else if( [property isEqualToString:@"setLabel"] )
        {
            NSArray *args = [jsonObj objectAtIndex:1];
            if ( args && [args isKindOfClass:[NSArray class]] )
            {
                self.label = nil;
                NSString *value = [args objectAtIndex:0];
                if ( value && [value isKindOfClass:[NSString class]] )
                    self.label = value;
                bRet =  TRUE;
            }
        }
        else if( [property isEqualToString:@"setBubble"] )
        {
            NSArray *args = [jsonObj objectAtIndex:1];
            if ( args && [args isKindOfClass:[NSArray class]] )
            {
                self.bubble.icon = nil;
                self.bubble.label = nil;
                NSString *labelValue = [args objectAtIndex:0];
                if ( labelValue && [labelValue isKindOfClass:[NSString class]] )
                    self.bubble.label = labelValue;
                NSString *value = [args objectAtIndex:1];
                if ( value && [value isKindOfClass:[NSString class]] )
                    self.bubble.icon = [self getFullPath:value];
                
                bRet = TRUE;
            }
        }
        else if( [property isEqualToString:@"show"]
                ||[property isEqualToString:@"hide"])
        {
            NSArray *args = [jsonObj objectAtIndex:1];
            if ( args && [args isKindOfClass:[NSArray class]] )
            {
                NSString *visable = [args objectAtIndex:0];
                if ( visable && [visable isKindOfClass:[NSString class]] )
                {
                    self.hidden = ![visable boolValue];
                    if ( self.belongMapview )
                    {
                        BMKAnnotationView *view = [self.belongMapview viewForAnnotation:self];
                        view.hidden = self.hidden;
                        view.enabled = !self.hidden;
                        if ( view.rightCalloutAccessoryView
                            && view.rightCalloutAccessoryView.superview
                            && view.rightCalloutAccessoryView.superview.superview)
                        { view.rightCalloutAccessoryView.superview.superview.hidden = self.hidden; }
                    }
                }
            }
           
            bRet = FALSE;
        }
        else if( [property isEqualToString:@"setBubbleIcon"] )
        {
            NSArray *args = [jsonObj objectAtIndex:1];
            if ( args && [args isKindOfClass:[NSArray class]] )
            {
                self.bubble.icon = nil;
                NSString *value = [args objectAtIndex:0];
                if ( value && [value isKindOfClass:[NSString class]] )
                    self.bubble.icon = [self getFullPath:value];
                bRet = TRUE;
            }
        }
        else if( [property isEqualToString:@"setBubbleLabel"] )
        {
            NSArray *args = [jsonObj objectAtIndex:1];
            if ( args && [args isKindOfClass:[NSArray class]] )
            {
                self.bubble.label = nil;
                NSString *labelValue = [args objectAtIndex:0];
                if ( labelValue && [labelValue isKindOfClass:[NSString class]] )
                    self.bubble.label = labelValue;
                bRet = TRUE;
            }
        }
        
        if ( bRet )
        {
            PGMapView *belongView = self.belongMapview;
            if ( belongView )
            {
                [belongView removeMarker:self];
                [belongView addMarker:self];
            }
            return TRUE;
        }
    }
    return FALSE;
}

/*
 *------------------------------------------------
 *@summay: 根据json格式的js对象生成native对象
 *@param
 *       [1] jsonObj json对象
 *       [2] baseURL baseurl
 *@return
 *        PGMapMarker*
 *@remark
 *------------------------------------------------
 */
+(PGMapMarker*)markerWithArray:(NSArray*)jsonObj baseURL:(NSString*)baseURL
{
    if ( !jsonObj )
        return nil;
    if ( ![jsonObj isKindOfClass:[NSArray class]] )
        return nil;
    
    PGMapMarker *marker = [[[PGMapMarker alloc] init] autorelease];
    
    marker.baseURL = [baseURL retain];
    
    PGMapCoordinate *point = [PGMapCoordinate pointWithJSON:[jsonObj objectAtIndex:0]];
    marker.coordinate = [point point2CLCoordinate];
    
    if ( !marker.bubble )
        marker.bubble = [[[PGMapBubble alloc] init] autorelease];
    
    return marker;
}

/*
 *------------------------------------------------
 *@summay: 根据json格式的js对象生成native对象
 *@param 
 *       [1] jsonObj json对象
 *       [2] baseURL baseurl
 *@return
 *        PGMapMarker*
 *@remark   
 *------------------------------------------------
 */
+(PGMapMarker*)markerWithJSON:(NSMutableDictionary*)jsonObj baseURL:(NSString*)baseURL
{
    if ( !jsonObj )
        return nil;
    if ( ![jsonObj isKindOfClass:[NSMutableDictionary class]] )
        return nil;
    
    PGMapMarker *marker = [[[PGMapMarker alloc] init] autorelease];
    
    marker.baseURL = [baseURL retain];
    
    NSString *UUID = [jsonObj objectForKey:@"_UUID_"];
    if ( UUID && [UUID isKindOfClass:[NSString class]])
        marker.UUID = UUID;
    
    NSString *label = [jsonObj objectForKey:@"caption"];
    if ( label && [label isKindOfClass:[NSString class]])
        marker.label = label;
    
    NSString *icon = [jsonObj objectForKey:@"icon"];
    if ( icon && [icon isKindOfClass:[NSString class]])
        marker.icon = [marker getFullPath:icon];
    
    marker.bubble = [PGMapBubble bubbleWithJSON:[jsonObj objectForKey:@"bubble"]];
    {
        PGMapCoordinate *point = [PGMapCoordinate pointWithJSON:[jsonObj objectForKey:@"point"]];
       // [marker setCoordinate:[point point2CLCoordinate]];
        marker.coordinate = [point point2CLCoordinate];
    }
    if ( !marker.bubble )
        marker.bubble = [[[PGMapBubble alloc] init] autorelease];
    
    if ( marker.bubble.icon ) 
        marker.bubble.icon = [marker getFullPath:marker.bubble.icon];
    return marker;
}

- (NSString *)title
{
  //  return @" ";
    if ( self.bubble )
        return self.bubble.label;
    return nil;
}

/*
 *------------------------------------------------
 *@summay: 获取文件的全路径
 *@param fileName NSString*
 *@return
 *@remark
 *    NSString*
 *------------------------------------------------
 */
- (NSString*)getFullPath:(NSString*)fileName
{
    if ( [fileName isAbsolutePath] ) {
        fileName = [@"_www" stringByAppendingPathComponent:fileName];
    }
    return [PTPathUtil absolutePath:fileName];
}

- (void)setBaseURL:(NSString*)baseURL
{
    [_baseURL release];
    _baseURL = [[baseURL stringByDeletingLastPathComponent] retain];
}
@end

@implementation PGMapMarkerView

-(void)dealloc
{
    [self addTapGestureRecognizer];
   // [_drawImage release];
    [super dealloc];
}

/*
 *------------------------------------------------
 *@summay: 刷新标记
 *@param 
 *@return
 *@remark
 *------------------------------------------------
 */
- (void)reload
{
    PGMapMarker *marker = (PGMapMarker*)self.annotation;
    //标记不知为什么不能自定义尺寸，在这里只能采用低效率的方法先填充个图片
    if ( marker.icon )
    { _drawImage = [UIImage getRetainImage:marker.icon]; }
    if ( !_drawImage )
    { _drawImage = [UIImage imageNamed:@"mapapi.bundle/images/pin_purple"]; }//map-redpin.png
    
    self.image = [self drawImage:marker];
    
    //self.centerOffset = CGPointMake(0, -self.frame.size.height/2+_textHeight );
    [self reloadBubble];
}
/*
 *------------------------------------------------
 *@summay: 刷新气泡视图
 *@param 
 *@return
 *@remark
 *------------------------------------------------   
 */
-(void)reloadBubble
{
    PGMapMarker *marker = (PGMapMarker*)self.annotation;
    self.canShowCallout = NO;
    self.paopaoView = nil;
    PGMapBubble *bubble = marker.bubble;
    if ( bubble )
    {
        UIImage *icon = [UIImage getRetainImage:bubble.icon];
        /*if ( icon || (bubble.label && [bubble.label length]) )
        {
          //  PGMapBubbleView *bubbleView = [[[PGMapBubbleView alloc] init] autorelease];
          //  [bubbleView setBubbleImage:icon];
          //  [bubbleView setBubbleLabel:bubble.label];//bubble.label];
           // bubbleView.delegate = self;
           // self.canShowCallout = YES;
           // self.leftCalloutAccessoryView = bubbleView;
            self.canShowCallout = YES;
            UIImageView *imageView = [[[UIImageView alloc] initWithImage:icon] autorelease];
            self.leftCalloutAccessoryView = imageView;
        }*/
        
        if ( icon || (bubble.label && [bubble.label length]) )
        {
            PGMapBubbleView *bubbleView = [[[PGMapBubbleView alloc] init] autorelease];
            [bubbleView setBubbleImage:icon];
            [bubbleView setBubbleLabel:bubble.label];//bubble.label];
            bubbleView.delegate = self;
            self.canShowCallout = YES;
           // self.leftCalloutAccessoryView = bubbleView;
            BMKActionPaopaoView *paopaoView = [[BMKActionPaopaoView alloc] initWithCustomView:bubbleView];
            self.paopaoView = paopaoView;
            [paopaoView autorelease];
        }
    }
}

/*
- (void)setAnnotation:(id<MAAnnotation>)annotation
{
    [super setAnnotation:annotation];
    [self reload];
    [self reloadBubble];
}*/

//计算view的大小
- (CGRect)size
{
    // 图片在上文字在下取各项的最大宽度为宽
    // 各项之和为各项之长
    CGRect frame = CGRectZero;
    PGMapMarker *mapMarker = (PGMapMarker*)self.annotation;
    if ( _drawImage )
    {
        UIImage *image = _drawImage;
        frame.size.width = MAX(frame.size.width, image.size.width);
        frame.size.height += image.size.height;
        frame.size.height += PG_MAP_MARKERVIEW_GAP;
    }
    
    if ( mapMarker.label )
    {
        CGSize textSize = [ mapMarker.label sizeWithFont:[UIFont systemFontOfSize:PG_MAP_MARKERVIEW_TEXTFONTSIZE]];
        frame.size.width = MAX(frame.size.width, textSize.width);
        frame.size.height += textSize.height;
        _textHeight = textSize.height;
    }
    
    return frame;
}
/*
 *------------------------------------------------
 *@summay: 生成标记显示的图片
 *@param annotation id <MAAnnotation>
 *@return
 *@remark
 *    高德地图标记视图不知道为什么不能设置大小,只能暂时自己生成一副图片
 *------------------------------------------------
 */
- (UIImage*)drawImage:(id <BMKAnnotation>)annotation
{
    UIImage *image = nil;
    CGRect rect = [self size];
    CGFloat width = rect.size.width;
    CGFloat height = rect.size.height;
    PGMapMarker *mapMarker = (PGMapMarker*)self.annotation;
    if ( mapMarker && width && height )
    {
        if (NULL != UIGraphicsBeginImageContextWithOptions)
        {
            CGFloat scale = [UIScreen mainScreen].scale;
            UIGraphicsBeginImageContextWithOptions(CGSizeMake(width, height), NO, scale);
        }
        else
        {
            UIGraphicsBeginImageContext(CGSizeMake(width, height));
        }
        
        CGContextRef context = UIGraphicsGetCurrentContext();
        if ( context )
        {
            CGFloat heightOffset = 0.0f;
            // 绘制图片
            if ( _drawImage )
            {
                //计算图片的绘制位置
                CGRect imgRect = CGRectZero;
                imgRect.size = _drawImage.size;
                imgRect.origin.x = (rect.size.width - _drawImage.size.width)/2.0f;
                [_drawImage drawInRect:imgRect];
                heightOffset = (_drawImage.size.height + PG_MAP_MARKERVIEW_GAP);
            }
            // 绘制文字
            if ( mapMarker.label )
            {
                CGContextSetFillColorWithColor(context, [UIColor blackColor].CGColor );
                [mapMarker.label drawInRect:CGRectMake(0, heightOffset, rect.size.width, _textHeight)
                                   withFont:[UIFont systemFontOfSize:PG_MAP_MARKERVIEW_TEXTFONTSIZE]
                              lineBreakMode:NSLineBreakByCharWrapping
                                  alignment:NSTextAlignmentCenter];
            }
            image = UIGraphicsGetImageFromCurrentImageContext();
            UIGraphicsEndImageContext();
        }
    }
    /* for debug
    NSString *filePath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask, YES) objectAtIndex:0];
    NSData *imgData = UIImagePNGRepresentation(image);
    [imgData writeToFile:[filePath stringByAppendingPathComponent:@"test.png"] atomically:NO];
     */
    return image;
}

/*
 *------------------------------------------------
 *@summay: 气泡点击时间回调
 *@param rect CGRect
 *@return
 *@remark
 *------------------------------------------------
 */
-(void)click:(id)sender
{
    if ( [sender isKindOfClass:[PGMapBubbleView class]] )
    {
        //PGMapBubbleView *bubbleView = (PGMapBubbleView*)sender;
        PGMapMarker *marker = (PGMapMarker*)self.annotation;
        if ( marker && [marker isKindOfClass:[PGMapMarker class]] )
        {
            NSString *jsObjectF =
            @"window.plus.maps.__bridge__.execCallback('%@', {type:'bubbleclick'});";
            NSString *javaScript = [NSString stringWithFormat:jsObjectF, marker.UUID];
            [marker.belongMapview.jsBridge asyncWriteJavascript:javaScript];
        }
    }
}

- (void)addTapGestureRecognizer{
    UITapGestureRecognizer *taprecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapCallback:)];
    taprecognizer.numberOfTouchesRequired = 1;
    taprecognizer.numberOfTapsRequired = 1;
    taprecognizer.cancelsTouchesInView = NO;
    // taprecognizer.delaysTouchesBegan = YES;
    // taprecognizer.delaysTouchesEnded = YES;
    [self addGestureRecognizer:taprecognizer];
    [taprecognizer release];
}
-(void)tapCallback:(UITapGestureRecognizer*)sender
{
    id<BMKAnnotation> annotation = self.annotation;
    if ( annotation && [annotation isKindOfClass:[PGMapMarker class]] )
    {
        PGMapMarker *marker = (PGMapMarker*)annotation;
        NSString * jsObjectF = @"var args = {type:'markerclick'};\
        window.plus.maps.__bridge__.execCallback('%@', args);";
        NSString *javaScript = [NSString stringWithFormat:jsObjectF, marker.UUID];
        [marker.belongMapview.jsBridge asyncWriteJavascript:javaScript];
    }
}

@end