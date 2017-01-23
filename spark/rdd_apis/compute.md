## compute


执行dependencies并计算出最终的RDD。这个函数用户不需要主动调用。是一个developer API

函数原型

  def compute(split: Partition, context: TaskContext): Iterator[T]
  


