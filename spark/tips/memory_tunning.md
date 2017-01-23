# Memory Tuning

在内存调优中，通常有三点需要考虑：

* 用户数据消耗了多少内存（用户可能需要知道他们的整个数据集要全部放入内存需要消耗多少内存）
* 访问这些对象（Objects）需要消耗多少内存
* GC的消耗（如果对内存消耗量达到了非常高的程度）

默认情况下，对java的对象的访问时非常快的，但也因此比对象本身裸数据量消耗更多的内存（2~5倍不等）。这是由以下几个原因造成的：

* 每一个java对象都包含一个16Bytes的`Object Header`，该header中包含一些诸如指向该class的指针之类的信息。如果一个对象包含的数据量原本就很小（比如一个Int），那么该header就比数据本身还要大。
* Java String对象包含有差不多40 Bytes的字符串数据以外额外的内存消耗（如保存char数组的length等信息），而且每一个Char在java中需要消耗2个Bytes，因为java中默认是使用UTF-16字符编码。因此，一个仅仅包含10个字符的字符串，需要消耗差不多60 Bytes的内存
* Java中常用的容器类（如HashMap，LinkedList等）都使用链表数据结构，而这些数据结构都会对每一个entry（如Map.Entry）对应一个`wrapper`，该object不仅包含有链表header，同时还包含有下一个entry object的指针（通常8个Bytes）。
* 对于原生类型的容器，通常都是存储的其原生类型的包装类（如`java.lang.Integer`）

该笔记会先对Spark中的内存管理进行一个概述，然后介绍一些特殊的用来优化应用程序的内存使用的策略。另外，还将介绍如何来决定用户对象的内存使用的经验以及如何通过修改用户数据的数据结构或者通过序列化格式来优化它们。最后还将介绍一些spark cache size和java GC方面的经验。



## Memory Management Overview

在Spark中内存的消耗通常在两个方面：`execution`和`storage`。Execution memory指的是在计算中用于做shuffle， sorts和aggregations的内存，而Storage memory指的是用来做cache和在cluster内传播数据的内存。在Spark中，execution和storage共享一个统一个内存空间（unified region - M）。当没有execution内存使用的时候，storage可以使用所有的空闲内存用来做cache，反之亦然。Exxcution在必要的时候有可能会释放storage内存，但仅仅会在所有的storage内存的使用率低于某一个阈值（R）之后才会这么做。而storage不会释放execution的内存。

这种设计是为了达到几种目的：

* 对不需要使用caching的应用程序，可以让其使用整个内存空间来进行execution，排除不必要的数据到磁盘的spill。

* 对需要使用caching的应用程序，可以将R阈值设置的非常低，让cache在内存中的数据blocks能够免于被淘汰。
* 这种方式也能够提供一个比较通用合理的机制，让写应用的用户不需要对内存的内部管理和分片十分熟悉，作业也能运行的比较高效。


有两个选项能够对spark作业的内存管理进行配置（通常情况下使用期默认配置即可）：

* `spark.memory.fraction`: default 0.6, 表示一定比例的JVM heap space（比如300MB）内存，这些内存即用来做execution和storage，剩下的内存用来提供给用户计算的消耗
* `spark.memory.storageFraction`: default 0.5, 表示一定比例的M，所以默认的淘汰cache阈值为0.5 * 0.6 * JVM heap size。

`spark.memory.fraction`的配置需要设置的跟JVM的OLD或者“tunured generation”匹配。否则，当大部分的内存用来做caching和execution时，JVM的tenured generation有可能会满，从而导致JVM进行频繁的GC。这里可以参考[Java GC sizing documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/sizing.html)

Tenured generation size是由JVM的`NewRatio`参数来设置的，默认为2，表示tenured generation是new generation的2倍大小。所以，默认情况下，tenured generation占据了2/3或者67%左右的JVM heap size。

A value of 0.6 for spark.memory.fraction keeps storage and execution memory within the old generation with room to spare. If spark.memory.fraction is increased to, say, 0.8, then NewRatio may have to increase to 6 or more.

NewRatio is set as a JVM flag for executors, which means adding spark.executor.extraJavaOptions=-XX:NewRatio=x to a Spark job’s configuration. 






## Determing Memory Consumption

## Tunning Data Structures

## Serialized RDD Storage

## Garbage Collection Tuning