/*
 *------------------------------------------------------------------
 *  pandora/feature/map/pg_map_view.h
 *  Description:
 *      地图视图头文件定义
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
#import "BMKMapView.h"
#import "BMKLocationService.h"

@class PGMap;
@class PGMapMarker;
@class PGGISOverlay;
@class PGMapOverlay;

@interface PGMapZoomControlView : UIStepper

@end

@interface PGMapView : BMKMapView<BMKMapViewDelegate,BMKLocationServiceDelegate>
{
    NSMutableArray *_markersDict;
    NSMutableArray *_overlaysDict;
    NSMutableArray *_gisOverlaysDict;
    PGMapZoomControlView *_zoomControlView;
    NSMutableArray *_jsCallbackDict;
    BMKLocationService *_localService;
}

@property(nonatomic, assign)PGMap* jsBridge;
@property(nonatomic, retain)NSString* UUID;
//@property(nonatomic, assign)int zoom;

+ (void)openSysMap:(NSArray*)command;

//invoke js method
//+ (PGMapView*)viewWithJSON:(NSDictionary*)dict;
+ (PGMapView*)viewWithArray:(NSArray*)dict;

- (void)resizeJS:(NSArray*)args;
- (void)centerAndZoomJS:(NSArray*)args;
- (void)setCenterJS:(NSArray*)args;
- (void)setZoomJS:(NSArray*)args;
- (void)setMapTypeJS:(NSArray*)args;
- (void)showUserLocationJS:(NSArray*)args;
- (void)showZoomControlsJS:(NSArray*)args;
- (void)resetJS:(NSArray*)args;
- (void)setTrafficJS:(NSArray*)args;
- (void)hideJS:(NSArray*)args;
- (void)showJS:(NSArray*)args;
- (void)addOverlayJS:(NSArray*)args;
- (void)removeOverlayJS:(NSArray*)args;
- (void)clearOverlaysJS:(NSArray*)args;

- (void)getCurrentCenterJS:(NSArray*)args;
- (void)getUserLocationJS:(NSArray*)args;

- (id)initWithFrame:(CGRect)frame;
+ (PGMapView*)viewWithFrame:(CGRect)rect;
- (void)close;
//native
- (void)hideZoomControl;
- (void)resizeZoomControl;
- (void)showZoomControl;
//自定义标记管理
- (void)addMarker:(PGMapMarker*)marker;
- (void)removeMarker:(PGMapMarker*)marker;

- (void)addGISOverlay:(PGGISOverlay*)overlay;
- (void)removeGISOverlay:(PGGISOverlay*)overlay;

- (void)addMapOverlay:(PGMapOverlay*)overlay;
- (void)removeMapOverlay:(PGMapOverlay*)overlay;

- (void)removeAllOverlay;
- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id <BMKAnnotation>)annotation;
@end


//导航图标旋转接口
@interface UIImage(InternalMethod)
+ (UIImage*)getRetainImage:(NSString *)filepath;
@end