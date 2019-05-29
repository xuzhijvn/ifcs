# ims
 Intelligent Monitoring System（智能监控系统），本方案是基于CPU实现的，如需更好的性能，需要参考本方案思想改造成GPU的版本。
 
## 1. 背景

1. 智能摄像头（抓拍机）将人脸检测功能集成到网络摄像头内部，价格昂贵，智能检测功能与硬件高度集成，不利于后续功能扩展。

2. 大量已有普通网络摄头没有被充分利用

![](https://github.com/xuzhijvn/ims/blob/master/images/background.png)

## 2. 目标

1. 构建一种更通用的AI+视频监控的解决方案

2. 充分利用现有资源，给普通网络摄像头赋能

![](https://github.com/xuzhijvn/ims/blob/master/images/aims.png)

基于IMS（Intelligent Monitoring System）实时视频流处理解决方案，具有价格低廉、功能可控的优点。
以上述假定的价格举例计算，当摄像头数量大于2个时，基于IMS的解决方案价格优势开始体现。（200 * 3 + 4800 < 2000 * 3）


## 3. 建设方案（单节点版）

在只有数十个摄像头的场景（个人认为30个以下），应用单节点方案就能解决问题。在摄像头更多的场景，应用集群模式是一种更好的解决方案。

* 架构设计

![](https://github.com/xuzhijvn/ims/blob/master/images/architecture.png)

* 线程模式

![](https://github.com/xuzhijvn/ims/blob/master/images/thread-model.png)

1. 一个线程（生产者）轮询所有的监控摄像头，以固定的频率读取摄像头的实时数据；

2. 将这些实时数据放入摄像头对应的分片中（Partition，同步阻塞队列）；

3.  每个分片都有一个线程（消费者）去消费该分片里面的数据。


过多的线程会造成消费者处理时间延长，并不适合本系统涉及的场景。因此，现在主要的目标就是找到一个合适的N。


## 4. 建设方案（集群版）


[![](https://res.infoq.com/articles/video-stream-analytics-opencv/en/resources/figure1.png)](https://www.infoq.com/articles/video-stream-analytics-opencv "实时视频流处理架构设计")
> 图为：实时视频流处理架构

+ [英文参考链接](https://www.infoq.com/articles/video-stream-analytics-opencv)
+ [中文参考链接](https://infoq.cn/article/video-stream-analytics-opencv)


## 5. 去重模块原理

由于，检测对象可能长时间处在视频监控范围之内，因此，该时间段的视频帧充斥着大量重复检测对象。对所有重复对象都去做后续的1：N人脸识别显然不合理，因此需要合理的去重设计。

1. 从新的一帧照片中检测出所有的人脸

2. 将第一步的人脸逐一和缓存中的人脸比对，缓存中没有的人脸则保存下来发往下一步业务逻辑

3. 用第一步得到的人脸替换缓存中的人脸

![](https://github.com/xuzhijvn/ims/blob/master/images/deduplicate-1.png)
> 图5.1：去重原理-1

![](https://github.com/xuzhijvn/ims/blob/master/images/deduplicate-2.png)
> 图5.2：去重原理-2

![](https://github.com/xuzhijvn/ims/blob/master/images/deduplicate-3.png)
> 图5.3：去重原理-3

![](https://github.com/xuzhijvn/ims/blob/master/images/deduplicate-4.png)
> 图5.4：去重原理-4

## 6. 性能测试（单节点版）

实验结果表明，一个消费者（线程）任务耗时300ms、CPU占用率达到80%，随着消费者数目增多，任务耗时延长，CPU占用率进一步增高；当消费者（线程）任务达到8个时，平均任务完成耗时为2000ms，CPU占用率高达95%以上。（英特尔 Core i7-8550U @ 1.80GHz 四核 ，核心数: 4 / 线程数: 8）

![](https://github.com/xuzhijvn/ims/blob/master/images/performance-1.png)

抓拍机以海康威视的产品举例，其价格在2000元左右，IMS节点服务器为一台价格4800元的个人电脑，普通网络摄像头在200元左右。从数据中可以得知，当监控数量等于3时，本方案能节省10%的成本，并且响应时间和抓拍机基本一致；当监控数量等于6时，本方案能节省50%的成本，并且最长响应时间不足2秒；随着监控点数量越多，节省的成本越多，相应的响应时间越长。

![](https://github.com/xuzhijvn/ims/blob/master/images/performance-2.png)


    
## 7. 环境配置
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
     
## 8. 展望

增加GPU图像处理单元，提升图像处理效率，以便单个IMS节点能负载更多的摄像机。

