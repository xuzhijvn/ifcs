# ims
 Intelligent Monitoring System（智能监控系统）
 
# 背景

1. 智能摄像头（抓拍机）将人脸检测功能集成到网络摄像头内部，价格昂贵，智能检测功能与硬件高度集成，不利于后续功能扩展。

2. 大量已有普通网络摄头没有被充分利用

![](https://github.com/qq783175223/ims/blob/master/images/background.png)

# 目标

1. 构建一种更通用的AI+视频监控的解决方案

2. 充分利用现有资源，给普通网络摄像头赋能

![](https://github.com/qq783175223/ims/blob/master/images/aims.png)

基于IMS（Intelligent Monitoring System）实时视频流处理解决方案，具有价格低廉、功能可控的优点。
以上述假定的价格举例计算，当摄像头数量大于2个时，基于IMS的解决方案价格优势开始体现。（200 * 3 + 4800 < 2000 * 3）


# 原理(单节点)

在只有数十个摄像头的场景（个人认为30个以下），应用单节点方案就能解决问题。在摄像头更多的场景，应用集群模式是一种更好的解决方案。本小节只介绍单节点版本，集群版本下一节介绍。


1. 一个线程（生产者）轮询所有的监控摄像头，以固定的频率读取摄像头的实时数据；

2. 将这些实时数据放入摄像头对应的分片中（Partition，同步阻塞队列）；

3.  每个分片都有一个线程（消费者）去消费该分片里面的数据。

![](https://github.com/qq783175223/ims/blob/master/images/thread-model.png)

过多的线程会造成消费者处理时间延长，并不适合本系统涉及的场景。因此，现在主要的目标就是找到一个合适的N。


# 原理(集群)
#### 1、实时视频流处理模块
                
+ 架构设计

[![](https://res.infoq.com/articles/video-stream-analytics-opencv/en/resources/figure1.png)](https://www.infoq.com/articles/video-stream-analytics-opencv "实时视频流处理架构设计")
> 图为：实时视频流处理架构

+ [英文参考链接](https://www.infoq.com/articles/video-stream-analytics-opencv)
+ [中文参考链接](https://infoq.cn/article/video-stream-analytics-opencv)


#### 2、去重模块

由于，检测对象可能长时间处在视频监控范围之内，因此，该时间段的视频帧充斥着大量重复检测对象。对所有重复对象都去做后续的1：N人脸识别显然不合理，因此需要合理的去重设计。

![](https://github.com/qq783175223/ims/blob/master/images/deduplicate.png)
> 图为：去重原理

“时刻1”处理的视频帧包含人脸A B C D，而缓存中没有人脸数据，当前视频帧与缓存的交集等于A B C D，将交集送往后续1：N人脸识别，同时将人脸A B C D缓存；同理，“时刻2”处理的视频帧包含人脸A B C E，人脸A B C E与缓存人脸A B C D的交集等于E，将交集送往后续1：N人脸识别，同时将人脸A B C E缓存。

#### 3、人脸识别模块
+ 系统架构
+ windows环境配置
    + 列表二-1
    + 列表二-2
    + 列表二-3
+ 列表三
    * 列表一
    * 列表二
    * 列表三
    
    
# 环境配置
+ linux
    + [Zookeeper配置](https://github.com)
    + [Kafka配置](https://github.com)
    + [Hadoop配置](https://github.com)
+ windows
    + [Zookeeper配置](https://www.jianshu.com/p/f7037105db46)
    + [Kafka配置](https://www.jianshu.com/p/64d25dcf8300)
    + Hadoop配置：
      * [下载Haddop](http://hadoop.apache.org/releases.html)
      * 修改hadoop-x.x.x/etc/hadoop/hadoop-env.cmd中的JAVA_HOME (路径不能空格)
