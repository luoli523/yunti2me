### 使用mapPartitionsWithIndex去除RDD中的第一行数据

测试的时候，常常会有这种情况：测试数据是一个表，以文本的形式保存，第一行为表的collum name，从第二行开始为每一行数据的具体values，如下表所示：

```
name age
luoli 34
guaishushu 50
weisuonan 38
```

那么测试对数据进行处理之前，常常需要将第一行去掉。使用mapPartitionsWithIndex可以方便的对已有RDD进行这项操作。

```scala
val inputData = sc.parallelize(Array("Skip this line XXXXX","Start here instead AAAA","Second line to work with BBB"))

def skipLines(index: Int, lines: Iterator[String], num: Int): Iterator[String] = {
    if (index == 0) {
      lines.drop(num)
    }
    lines
  }

val valuesRDD = inputData.mapPartitionsWithIndex((idx, iter) => skipLines(idx, iter, numLinesToSkip))

valuesRDD.collect.foreach(println)
```
