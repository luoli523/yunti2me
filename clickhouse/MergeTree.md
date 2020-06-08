### MergeTree

* 如何知道一个表有多少分区

可以通过查`system.parts`表来获取一个表的分区，如下：

```sql
SELECT
    partition,
    name,
    active
FROM system.parts
WHERE table = 'visits'
```

```
┌─partition─┬─name───────────┬─active─┐
│ 201901    │ 201901_1_3_1   │      0 │
│ 201901    │ 201901_1_9_2   │      1 │
│ 201901    │ 201901_8_8_0   │      0 │
│ 201901    │ 201901_9_9_0   │      0 │
│ 201902    │ 201902_4_6_1   │      1 │
│ 201902    │ 201902_10_10_0 │      1 │
│ 201902    │ 201902_11_11_0 │      1 │
└───────────┴────────────────┴────────┘
```

* 通常使用`SummingMergeTree`或者`AggregatingMergeTree`的时候，通常把所有的维度列都加入到`ORDER BY`列中是一个比较好的实践。这样`ORDER BY`的表达式会是一长串的列组成，并且这组列还会因为新加维度必须频繁更新。这种情况下，`Primary Key`中仅仅预留少量的column以保证高效的范围扫描，而将剩下的维度列全部放入`ORDER BY`的tuple中，这样合理。