/*
 *------------------------------------------------------------------
 *  pandora/feature/map/pg_map.h
 *  Description:
 *      地图插件头文件
 *      负责和js层代码交互，js native层对象维护
 *  DCloud Confidential Proprietary
 *  Copyright (c) Department of Research and Development/Beijing/DCloud.
 *  All Rights Reserved.
 *
 *  Changelog:
 *	number	author	modify date modify record
 *   0       xty     2012-12-07 创建文件
 *------------------------------------------------------------------
 */

#import <UIKit/UIKit.h>
#import "BMKMapManager.h"

#import "PGPlugin.h"
#import "PGMethod.h"

@interface PGMap : PGPlugin
{
    //js中创建的地图字典
    NSMutableDictionary *_nativeObjectDict;
}

@property(nonatomic, readonly)NSDictionary *nativeOjbectDict;

//创建js native层对象
- (void)createObject:(PGMethod*)command;
// js属性更改同步更新native对象
- (void)updateObject:(PGMethod*)command;
- (void)execMethod:(PGMethod*)command;
//native
- (void)insertGisOverlay:(id)object withKey:(NSString*)key;

@end
