# AND OR执行顺序

```python
var1 = True
var2 = False
var3 = False

if var1 or var2 and var3:
 print("True")
else:
 print("False")
```

上述代码的输出结果是`True`,这里涉及到`AND`,`OR`操作链的执行书序问题，一个简单的记忆方式是：

```
A good way to remember this is to think of it mathematically.

AND as * (multiply)
OR as + (addition)
TRUE as 1
FALSE as 0
So thinking of it as simple math you get this:

0 * 0 = 0
1 * 0 = 0
1 * 1 = 1
0 + 0 = 0
1 + 0 = 1
1 + 1 = 1
Only thing that may be a tiny bit confusing is 1 + 1 = 1, but a bit can't go above 1.

So with this in mind you can then apply this logic:

if(cond1 AND cond2 AND cond3 OR cond4 AND cond5 AND cond6)

Becomes:

if(cond1 * cond2 * cond3 + cond4 * cond5 * cond6)

```
参见[wikipedia链接](https://en.wikipedia.org/wiki/Order_of_operations)
