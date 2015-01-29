/*
 *------------------------------------------------------------------
 *  pandora/feature/map/pg_map_marker.h
 *  Description:
 *      地图标记和标记视图头文件
 *  DCloud Confidential Proprietary
 *  Copyright (c) Department of Research and Development/Beijing/DCloud.
 *  All Rights Reserved.
 *
 *  Changelog:
 *	number	author	modify date  modify record
 *   0       xty     2012-12-10  创建文件
 *------------------------------------------------------------------
 */

#import <Foundation/Foundation.h>

#import "BMapKit.h"
#import "BMKAnnotation.h"

@class PGMapView;

@interface PGMapCoordinate : NSObject
@property(nonatomic, assign)CLLocationDegrees latitude;//标点的文本标注
@property(nonatomic, assign)CLLocationDegrees longitude; //标点的图标;

-(NSString*)JSObject;
+(PGMapCoordinate*)pointWithJSON:(NSMutableDictionary*)jsonObj;
+(NSArray*)arrayWithJSON:(NSArray*)jsonObj;

//工具类封装
-(CLLocationCoordinate2D)point2CLCoordinate;
+(CLLocationCoordinate2D*)array2CLCoordinatesAlloc:(NSArray*)coordinates;
+(PGMapCoordinate*)pointWithLongitude:(CLLocationDegrees)longitude latitude:(CLLocationDegrees)latitude;
+(NSArray*)coordinateListString2Array:(NSString*)coordinateList;
+(NSArray*)coordinateListWithPoints:(BMKMapPoint *)points count:(NSUInteger)count;

@end

@interface PGMapBubble : NSObject
@property(nonatomic, copy)NSString *label;//标点的文本标注
@property(nonatomic, copy)NSString *icon; //标点的图标
+(PGMapBubble*)bubbleWithJSON:(NSMutableDictionary*)jsonObj;
@end

/*
 ===========================================
 *@Marker创建的气泡对象对象
 *==========================================
 */
 
@protocol PGMapBubbleViewDelegate<NSObject>
-(void)click:(id)sender;
@end

//气泡视图
@interface PGMapBubbleView : UIView
{
@private
    //描述文本
    UILabel *_textView;
    UIImageView *_iconView;
}
@property (nonatomic, assign)id<PGMapBubbleViewDelegate> delegate;
@property(nonatomic, retain)NSString *bubbleLabel;
@property(nonatomic, retain)UIImage *bubbleImage;
//当气泡内容改变时更细气泡
- (void) reload;

@end

/*
 ===========================================
 *@Marker创建地图标点Marker对象
 *==========================================
 */
@interface PGMapMarker:BMKPointAnnotation
{
    @private
    NSString *_baseURL;
}
@property(nonatomic, assign)BOOL hidden;
@property(nonatomic, assign)PGMapView *belongMapview;
@property(nonatomic, retain)NSString *baseURL;
@property(nonatomic, retain)NSString *UUID;
@property(nonatomic, copy)NSString *label;//标点的文本标注
@property(nonatomic, copy)NSString *icon; //标点的图标
@property(nonatomic, retain)PGMapBubble *bubble; //关联的气泡

/**
 *转化js marker obj to
 */
+(PGMapMarker*)markerWithJSON:(NSMutableDictionary*)jsonObj baseURL:(NSString*)baseUL;
+(PGMapMarker*)markerWithArray:(NSArray*)jsonObj baseURL:(NSString*)baseURL;
//- (void)setCoordinate:(CLLocationCoordinate2D)newCoordinate;
- (BOOL)updateObject:(NSMutableArray*)command;
- (NSString*)getFullPath:(NSString*)fileName;
- (void)setBaseURL:(NSString*)baseURL;

@end

/*
*@Marker创建地图标点MarkervView对象
*/
@interface PGMapMarkerView : BMKAnnotationView<PGMapBubbleViewDelegate>
{
    // 文字的高度
    CGFloat _textHeight;
    UIImage *_drawImage;
}
//- (void)setAnnotation:(id<MAAnnotation>)annotation;
- (void)reload;
- (void)click:(id)sender;
- (void)addTapGestureRecognizer;
@end