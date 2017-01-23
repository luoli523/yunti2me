# spark note

* [RDD API使用场景示例](RDD_API.md)
  * [aggregate/aggregateByKey](rdd_apis/aggregate.md)
  * [fold/foldByKey/reduce/reduceByKey](rdd_apis/fold_reduce.md)
  * [combineByKey](best_practice/combineByKey.md)
  * [mapPartitions/mapPartitionsWithIndex](rdd_apis/mappartitions_\&_mappartitionswithindex.md)
  * [glom](rdd_apis/glom.md)
* Spark编程最佳实践
  * [使用scala partial function处理脏数据](best_practice/deal_bad_input.md)
  * [不要collect大数据集](best_practice/dont_collect_large_rdd.md)
  * [reduceByKey性能比groupByKey好](best_practice/reduceByKey_better_than_groupByKey.md)
  * [combineByKey性能比groupByKey好](best_practice/combineByKey.md)
  * [使用partitionBy函数优化性能](best_practice/use_partitionBy.md)
* Spark小技巧
  * [用mapPartitionsWithIndex去除RDD中的第一行数据](tips/使用mapPartitionsWithIndex去除RDD中的第一行数据.md)
  * [查看各rdd_items在partitions上的分布](tips/查看各rdd_items在partitions上的分布\(mappartitionswithindex\).md)
* Spark调优
  * [Serialization](tips/serialization.md)
  * [内存调优](tips/memory_tunning.md)
* 实践 
  * [将txt数据case成table](practice/TxtParseToTable.md)
* Scala Notes
  * [scala从字符串中利用正则表达式抽取子字符串](scala_tips/extract_substring_using_regex.md)
  * [scala中匹配Seq的技巧](scala_tips/seq_matches.md)
  * [case和PartialFunction](scala_tips/case_partial_function.md)
