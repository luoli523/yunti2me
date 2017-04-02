# scala从字符串中利用正则表达式抽取子字符串

* 问题描述：
  * 希望通过正则表达式从字符串中提取一个或多个符合匹配模式的子字符串
  
* 方案：
  * 定义正则表达式
  * 将需要提取的匹配模式用小括号'()'括起来，每个小括号的pattern匹配的字符串即为提取子串
  * 将定义好的正则pattern应用到目标字符串上
  
```scala
val pattern = "([0-9]+) ([A-Za-z]+)".r
val pattern(count, fruit) = "100 Bananas"
```

```
scala> val pattern = "([0-9]+) ([A-Za-z]+)".rpattern: scala.util.matching.Regex = ([0-9]+) ([A-Za-z]+)scala> val pattern(count, fruit) = "100 Bananas" count: String = 100fruit: String = Bananas

```

这种语法看上去让人感觉有一点奇怪，好像定义了pattern两次，但实际上这种语法在实际情况下更加直观，代码读起来也比较容易理解。

例如，假设我们需要写一段代码，来识别用户对电影的各种不同搜索方式的检索，比如如以下用户输入字符串：

```
"movies near 80301""movies 80301""80301 movies""movie: 80301""movies: 80301""movies near boulder, co""movies near boulder, colorado"
```

为了识别这些搜索条件，我们可以写一些pattern来分别识别不同的输入，比如：

```scala
// match "movies 80301"val MoviesZipRE = "movies (\\d{5})".r // match "movies near boulder, co"val MoviesNearCityStateRE = "movies near ([a-z]+), ([a-z]{2})".r
```

当定义了这些pattern以后，我们可以使用scala的`match`语句来分别进行匹配，并将匹配后提取的子字符串进行相应的函数调用，如下：

```scala
textUserTyped match {
  case MoviesZipRE(zip) => getSearchResults(zip)
  case MoviesNearCityStateRE(city, state) => getSearchResults(city, state) 
  case _ => println("did not match a regex")
}
```

可以从以上语法中看出，代码非常的简洁直观。
