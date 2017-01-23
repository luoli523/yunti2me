# combineByKey


在Spark或Hadoop等框架下的类map-reduce计算模式的作业，有一个很大的优化点就是将需要在shuffle阶段进行shuffle的数据减小到最小，以此来达到优化计算，减小开销的目的。对于计算作业，最好的选择是所有的计算都能够在map端完成，这样就没有任何数据需要在网络上进行跨机器之间的传输。但是在大多数情况下，map-side only的计算就能够完成计算逻辑的场景比较少，如果需要对数据进行类似sort of grouping，sorting或者aggregation等，就势必需要将map端的计算结果发送到reducer端进行计算。即使如此，还是有一些点是可以用来在shuffle方面进行优化的。

## groupByKey代价很高

在`Spark`中，当调用`groupByKey`的时候，每一个key-value对都需要在shuffle阶段在集群的各机器中进行一次传输。当需要做类似group by key的操作的时候，数据的shuffle的确是避免不了的，但有一些技巧能够让我们对shuffle的数据量进行优化。比如我们可以通过`combine/merge`每个key的values来减少需要shuffle的k-v对。更少的k-v对意味着reducer端要处理的数据量也就越少，进一步提升reducer端的计算效率。`groupByKey`操作没有尝试对任何的values进行merging和combining操作，所以其网络消耗以及计算消耗都比较高。

## combineByKey

combineByKey调用就是一种groupByKey的优化方选择。当使用combineByKey对values进行merge的时候，每个partition中的每个key对应的values会被merge成为一个单一的value（且combine后的value的type不一定要跟原本values的type一致）。combineByKey函数需要提供三个函数参数：

* `createCombinerFunction`：第一个函数对每一个key，用来将key的第一个value初始化到一个特定的结构
* `mergeValueFunction`：第二个函数用来在每一个partition中进行对初始化结构和values中的每一个item进行reduce函数运算。也就是说，每一个key对应的values，只有第一个value会被应用到createCombinerFunction上，其后所有的values都会被应用到mergeValueFunction上，并take每个value以及第一个函数产生的结果为参数
* `mergeCombiners`： 第三个函数用来在对每一个partition上产生的结果按照相同的key再次按照第三个reduce函数进行计算

### combineByKey example

这个例子用来计算每个人的平均得分。

```
//type alias for tuples, increases readablity
type ScoreCollector = (Int, Double)
type PersonScores = (String, (Int, Double))

val initialScores = Array(("Fred", 88.0), ("Fred", 95.0), ("Fred", 91.0), ("Wilma", 93.0), ("Wilma", 95.0), ("Wilma", 98.0))

val wilmaAndFredScores = sc.parallelize(initialScores).cache()

val createScoreCombiner = (score: Double) => (1, score)

val scoreCombiner = (collector: ScoreCollector, score: Double) => {
         val (numberScores, totalScore) = collector
        (numberScores + 1, totalScore + score)
      }

val scoreMerger = (collector1: ScoreCollector, collector2: ScoreCollector) => {
      val (numScores1, totalScore1) = collector1
      val (numScores2, totalScore2) = collector2
      (numScores1 + numScores2, totalScore1 + totalScore2)
    }
val scores = wilmaAndFredScores.combineByKey(createScoreCombiner, scoreCombiner, scoreMerger)

val averagingFunction = (personScore: PersonScores) => {
       val (name, (numberScores, totalScore)) = personScore
       (name, totalScore / numberScores)
    }

val averageScores = scores.collectAsMap().map(averagingFunction)

println("Average Scores using CombingByKey")
    averageScores.foreach((ps) => {
      val(name,average) = ps
       println(name+ "'s average score : " + average)
    })
```

来看一下以上例子的说明:

* `createScoreCombiner`接受一个double value并返回一个tuple（Int,Double），`注意，createScoreCombiner会应用到每一个key的第一个value，并产生一个tuple`
* `scoreCombiner`函数take一个ScoreCollector（也就是一个(Int,Double) tuple的别名）以及一个Double作为其参数，这里的第一个参数，ScoreCollector即为第一个函数对每个key的第一个value进行计算后得出的输出，以及以后每一次对其他values计算的输出，而第二个参数即为values中除第一个意外的其他values值。这里的最终结果为每个partition中的每一个key对应一个ScoreCollector输出。
* `scoreMerger`函数用来对每个partition中的key->ScoreCollector进行再一轮的计算，使用scoreMerger函数


以上运行结果为:

```
Average Scores using CombingByKey
Fred's average score : 91.33333333333333
Wilma's average score : 95.33333333333333
```

通过`combineByKey`，这次计算中需要shuffle的k-v对数量就只有key count那么多，而不是整个RDD的items count那么多。大大减少了需要进行shuffle的数据量，同时降低了scoreMerger需要处理的items量。可以看出，combineByKey的优化原理跟`aggregateByKey`，`reduceByKey`是一样的，都是通过在map端进行combine/merge运算，通过减少shuffle数据量的方式来达到优化的效果。