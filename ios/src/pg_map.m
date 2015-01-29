/*
 *------------------------------------------------------------------
 *  pandora/feature/map/pg_map.m
 *  Description:
 *      地图插件实现文件
 *      负责和js层代码交互，js native层对象维护
 *  DCloud Confidential Proprietary
 *  Copyright (c) Department of Research and Development/Beijing/DCloud.
 *  All Rights Reserved.
 *
 *  Changelog:
 *	number	author	modify date modify record
 *   0       xty     2012-12-07 创建文件
 *   Reviewed @ 20130105 by Lin Xinzheng
 *------------------------------------------------------------------
 */

#import "PGMethod.h"
#import "pg_map.h"
#import "pg_map_view.h"
#import "pg_map_marker.h"
#import "pg_map_overlay.h"
#import "pg_gis_overlay.h"
#import "pg_gis_search.h"
#import "PGObject.h"
#import "PDRCoreAppFrame.h"

@implementation PGMap

@synthesize nativeOjbectDict = _nativeObjectDict;

- (void)dealloc
{
    NSArray *allViews = [_nativeObjectDict allValues];
    for ( PGMapView *target in allViews ) {
        if ( [target isKindOfClass:[PGMapView class]] ) {
            [target close];
        }
    }
    [_nativeObjectDict release];
    [super dealloc];
}

/*
 *------------------------------------------------------------------
 * @Summary:
 *      js执行native类方法
 * @Parameters:
 *       [1] command, js传入格式应该为 [uuid, [args]]
 * @Returns:
 *      BOOL 是否执行成功
 * @Remark:
 *    该方法会自动调用各自对象的execMethod
 * @Changelog:
 *------------------------------------------------------------------
 */
- (void)execMethod:(PGMethod*)command
{
    if ( !command || !command.arguments )
    { return; }
    NSString *UUID = [command.arguments objectAtIndex:0];
    if ( UUID && [UUID isKindOfClass:[NSString class]] )
    {
        if ( [UUID isEqualToString:@"map"] )
        {
            NSArray *args = [command.arguments objectAtIndex:1];
            if ( args )
            {
                [PGMapView openSysMap:[args objectAtIndex:1]];
            }
        }
    }
}

/*
 *------------------------------------------------------------------
 * @Summary:
 *      js执行native对象方法
 * @Parameters:
 *       [1] command, js传入格式应该为 [uuid, [args]]
 * @Returns:
 *      BOOL 是否执行成功
 * @Remark:
 *    该方法会自动调用各自对象的updateobject
 * @Changelog:
 *------------------------------------------------------------------
 */
- (void)updateObject:(PGMethod*)command
{
    if ( !command || !command.arguments )
    { return; }
    NSString *UUID = [command.arguments objectAtIndex:0];
    if ( UUID && [UUID isKindOfClass:[NSString class]] )
    {
        NSObject *object = [_nativeObjectDict objectForKey:UUID];
        if ( [object isKindOfClass:[PGMapMarker class]] )
        {
            [object updateObject:(NSArray*)[command.arguments objectAtIndex:1]];
        }
        else if ( [object respondsToSelector:@selector(updateObject:) ] )
        {
            [object updateObject:(NSArray*)[command.arguments objectAtIndex:1]];
        }
    }
}

/*
 *------------------------------------------------------------------
 * @Summary:
 *      创建js native对象
 * @Parameters:
 *    [1] command, js调用格式应该为 [uuid, type, [args]]
 * @Returns:
 *    无
 * @Remark:
 *   
 * @Changelog:
 *------------------------------------------------------------------
 */
- (void)createObject:(PGMethod*)command
{
    if ( !command || !command.arguments )
    { return; }

    NSString *UUID = [command.arguments objectAtIndex:0];
    
    if ( UUID && [UUID isKindOfClass:[NSString class]] )
    {
        if ( !_nativeObjectDict )
        { _nativeObjectDict = [[NSMutableDictionary alloc] initWithCapacity:10]; }
        NSString *type = [command.arguments objectAtIndex:1];
        if ( type && [type isKindOfClass:[NSString class]] )
        {
            //如果创建过就不在创建
            if ( [_nativeObjectDict objectForKey:UUID] )
            { return; }
            
            if ( [type isEqualToString:@"marker"] )
            {
                NSString *baseURL = [self writeJavascript:@"window.location.href" ];
                PGMapMarker *mapMarker = [PGMapMarker markerWithArray:[command.arguments objectAtIndex:2] baseURL:baseURL];
                mapMarker.UUID = UUID;
                if ( mapMarker )
                {
                    [_nativeObjectDict setObject:mapMarker forKey:mapMarker.UUID];
                }
            }
            else if ( [type isEqualToString:@"circle"] )
            {
                PGMapCircle *circle = [[PGMapCircle alloc] initWithUUID:UUID args:[command.arguments objectAtIndex:2]];
                if ( circle )
                {
                    [_nativeObjectDict setObject:circle forKey:circle.UUID];
                    [circle release];
                }
            }
            else if ( [type isEqualToString:@"polygon"] )
            {
                PGMapPolygon *polygon = [[PGMapPolygon alloc] initWithUUID:UUID args:[command.arguments objectAtIndex:2]];
                if ( polygon )
                {
                    [_nativeObjectDict setObject:polygon forKey:polygon.UUID];
                    [polygon release];
                }
            }
            else if ( [type isEqualToString:@"polyline"] )
            {
                PGMapPolyline *polyline = [[PGMapPolyline alloc] initWithUUID:UUID args:[command.arguments objectAtIndex:2]];
                if ( polyline )
                {
                    [_nativeObjectDict setObject:polyline forKey:polyline.UUID];
                    [polyline release];
                }
            }
            else if ( [type isEqualToString:@"route"] )
            {
                PGGISRoute* gisRoute = [[PGGISRoute alloc]initWithUUID:UUID args:[command.arguments objectAtIndex:2]];
                if ( gisRoute )
                {
                    [_nativeObjectDict setObject:gisRoute forKey:gisRoute.UUID];
                    [gisRoute release];
                }
            }
            else if ( [type isEqualToString:@"search"] )
            {
                PGGISSearch *search = [[PGGISSearch alloc] initWithUUID:UUID];
                if ( search )
                {
                    search.jsBridge = self;
                    [_nativeObjectDict setObject:search forKey:UUID];
                    [search release];
                }
            }
            else if ( [type isEqualToString:@"mapview"] )
            {
                PGMapView *mapView = [PGMapView viewWithArray:[command.arguments objectAtIndex:2]];
                if ( mapView )
                {
                    mapView.jsBridge = self;
                    mapView.UUID = UUID;
                    [self.JSFrameContext.webView.scrollView addSubview:mapView];
                    [_nativeObjectDict setObject:mapView forKey:UUID];
                }
            }
        }
    }
}

/**
 *invake js marker object
 *@param command PGMethod*
 *@return 无
 */
- (void)insertGisOverlay:(id)object withKey:(NSString*)key
{
    if( !key || !object )
        return;
    
    if ( !_nativeObjectDict )
    {
        _nativeObjectDict = [[NSMutableDictionary alloc] initWithCapacity:10];
    }
    [_nativeObjectDict setObject:object forKey:key];
}

@end
