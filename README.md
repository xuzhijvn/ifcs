# IFCS
 Intelligent Face Capture System（智能人脸抓拍系统），本方案是基于CPU实现的，如需更好的性能，需要参考本方案思想改造成GPU的版本。
 
## 1. 背景

A.  智能摄像头（抓拍机）将人脸检测功能集成到网络摄像头内部，价格昂贵，智能检测功能与硬件高度集成，不利于后续功能扩展。

B.  大量已有普通网络摄头没有被充分利用

![](https://github.com/xuzhijvn/ifcs/blob/master/images/background.png)

## 2. 目标

A.  构建一种更通用的AI+视频监控的解决方案

B.  充分利用现有资源，给普通网络摄像头赋能

![](https://github.com/xuzhijvn/ifcs/blob/master/images/aims.png)

IFCS并不是现有抓拍机的简单替代方案，IFCS和抓拍机各有适合其自身的应用场景，两种方案互为补充，力争为不同的场景给出最合适的解决方案。

另外，IFCS的设计思想并不是一个新奇事物，多个厂商早已将类似的功能制成专业设备，例如：带有人脸检测功能的NVR，大华集团的IVSS一体机。这些专业设备全都集成了数量不等的GPU单元，GPU性能差异，决定了专业设备的负载能力。IFCS原型是基于CPU开发，旨在验证方案的可行性。


## 3. 建设方案（单节点版）

在只有数十个摄像头的场景（个人认为30个以下），应用单节点方案就能解决问题。在摄像头更多的场景，应用集群模式是一种更好的解决方案。

* 架构设计

![](https://github.com/xuzhijvn/ifcs/blob/master/images/architecture.png)

* 线程模式

![](https://github.com/xuzhijvn/ifcs/blob/master/images/thread-model.png)


A.  一个线程（生产者）轮询所有的监控摄像头，以固定的频率读取摄像头的实时数据；

B.  将这些实时数据放入摄像头对应的分片中（Partition，同步阻塞队列）；

C.  每个分片都有一个线程（消费者）去消费该分片里面的数据。


过多的线程会造成消费者处理时间延长，并不适合本系统涉及的场景。因此，现在主要的目标就是找到一个合适的N。


## 4. 建设方案（集群版）


[![](https://res.infoq.com/articles/video-stream-analytics-opencv/en/resources/figure1.png)](https://www.infoq.com/articles/video-stream-analytics-opencv "实时视频流处理架构设计")
> 图为：实时视频流处理架构

+ [英文参考链接](https://www.infoq.com/articles/video-stream-analytics-opencv)
+ [中文参考链接](https://infoq.cn/article/video-stream-analytics-opencv)


## 5. 人脸检测&人脸去重

由于，检测对象可能长时间处在视频监控范围之内，因此，该时间段的视频帧充斥着大量重复检测对象。对所有重复对象都去做后续的1：N人脸识别显然不合理，因此需要合理的去重设计。

不同的人脸检测算法，其识别率、资源占用率不尽相同。但总的来说，人脸检测是一种CPU密集型操作，本课题原型采用自OpenCV封装的哈尔级联人脸检测算法。经测试，该算法检测单帧耗时300ms左右，CPU占用率接近80%。


A.  利用openCV人脸检测算法，找出帧里面包含的所有人脸，并保存至临时缓存。（为方便比对，缓存中存放的是经特征提取后的特征值）

B.  将第一步的人脸逐一和缓存中的人脸比对，缓存中没有的人脸则保存下来发往下一步业务逻辑

C.  用第一步得到的人脸替换缓存中的人脸

![](https://github.com/xuzhijvn/ifcs/blob/master/images/deduplicate-1.png)
> 图5.1：去重原理-1

![](https://github.com/xuzhijvn/ifcs/blob/master/images/deduplicate-2.png)
> 图5.2：去重原理-2

![](https://github.com/xuzhijvn/ifcs/blob/master/images/deduplicate-3.png)
> 图5.3：去重原理-3

![](https://github.com/xuzhijvn/ifcs/blob/master/images/deduplicate-4.png)
> 图5.4：去重原理-4

## 6. 性能测试（单节点版）

实验结果表明，一个消费者（线程）任务耗时300ms、CPU占用率达到80%，随着消费者数目增多，任务耗时延长，CPU占用率进一步增高；当消费者（线程）任务达到8个时，平均任务完成耗时为2000ms，CPU占用率高达95%以上。（英特尔 Core i7-8550U @ 1.80GHz 四核 ，核心数: 4 / 线程数: 8）

![](https://github.com/xuzhijvn/ifcs/blob/master/images/performance-1.png)

抓拍机以海康威视的产品举例，其价格在2000元左右，IFCS节点服务器为一台价格4800元的个人电脑，普通网络摄像头在200元左右。从数据中可以得知，当监控数量等于3时，本方案能节省10%的成本，并且响应时间和抓拍机基本一致；当监控数量等于6时，本方案能节省50%的成本，并且最长响应时间不足2秒；随着监控点数量越多，节省的成本越多，相应的响应时间越长。

![](https://github.com/xuzhijvn/ifcs/blob/master/images/performance-2.png)


    
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

增加GPU图像处理单元，提升图像处理效率，以便单个IFCS节点能负载更多的摄像机。

