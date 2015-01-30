# H5P.Plugins.maps.BaiDu
该工程为HTML5 plus 百度地图插件实现,参见https://github.com/dcloudio/H5P.Core
# 目录结构
 Android Android地图插件Native层实现
 iOS iOS地图插件Native层实现
 js 地图插件JS Api实现
#集成步骤
iOS:<br/>
准备工作:<br/>
1. 去 [百度LBS开放平台](http://developer.baidu.com/map/)申请Appkey<br/>
2. 下载运行环境https://github.com/dcloudio/H5P.Core<br/>
配置:<br/>
1. 使用XCode打开Pandora.xcodeproj,新建类型为CocoaTouch Static Library的 target<br/>
2. 将本工程中src添加到新建的target<br/>
3. 拷贝libs目录下库文件到Pandora工程libs目录下<br/>
4. Pandora.xcodeproj添加宏定义 PDR_PLUS_MAP 添加百度库到编译选项中<br/>
5. 在info.plist 中添加key为baidu的项类型为Dictionary，在Dictionary中添加key为appkey项目取值为申请的百度appkey

  
