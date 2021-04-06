### Clickhouse日期函数

```sql
-- 五、时间日期函数
--->>>>>> 时间日期函数
SELECT
toDateTime('2019-07-30 10:10:10') AS time,
-- 将DateTime转换成Unix时间戳
toUnixTimestamp(time) as unixTimestamp,
-- 保留 时-分-秒
toDate(time) as date_local,
toTime(time) as date_time,-- 将DateTime中的日期转换为一个固定的日期，同时保留时间部分。
-- 获取年份，月份，季度，小时，分钟，秒钟
toYear(time) as get_year,
toMonth(time) as get_month,
-- 一年分为四个季度。1（一季度:1-3）,2（二季度:4-6）,3（三季度:7-9）,4（四季度:10-12）
toQuarter(time) as get_quarter,
toHour(time) as get_hour,
toMinute(time) as get_minute,
toSecond(time) as get_second,
-- 获取 DateTime中的当前日期是当前年份的第几天，当前月份的第几日，当前星期的周几
toDayOfYear(time) as "当前年份中的第几天",
toDayOfMonth(time) as "当前月份的第几天",
toDayOfWeek(time) as "星期",
toDate(time, 'Asia/Shanghai') AS date_shanghai,
toDateTime(time, 'Asia/Shanghai') AS time_shanghai,
-- 得到当前年份的第一天,当前月份的第一天，当前季度的第一天，当前日期的开始时刻
toStartOfYear(time),
toStartOfMonth(time),
toStartOfQuarter(time),
toStartOfDay(time) AS cur_start_daytime,
toStartOfHour(time) as cur_start_hour,
toStartOfMinute(time) AS cur_start_minute,
-- 从过去的某个固定的时间开始，以此得到当前指定的日期的编号
toRelativeYearNum(time),
toRelativeQuarterNum(time);



SELECT
toDateTime('2019-07-30 14:27:30') as time,
toISOYear(time) AS iso_year,
toISOWeek(time) AS iso_week,
now() AS cur_dateTime1, -- 返回当前时间yyyy-MM-dd HH:mm:ss
today() AS cur_dateTime2, -- 其功能与'toDate(now())'相同
yesterday() AS yesterday, -- 当前日期的上一天
-- timeSlot(1) AS timeSlot_1, -- 出现异常！！将时间向前取整半小时
toDate(time) as getY_M_d;



-- 目前只有这三种格式，没有什么toYYYY(),toYYYddmm()之类的函数，不要想当然。
SELECT
now() as nowTime,
-- 将Date或DateTime转换为包含年份和月份编号的UInt32类型的数字（YYYY * 100 + MM）
toYYYYMMDDhhmmss(nowTime),
toYYYYMMDD(nowTime),
toYYYYMM(nowTime);



-- formatDateTime(Time, Format[,Timezone])函数引用
SELECT
now() as now_time,
toDateTime('2019-07-31 18:20:30') AS def_datetime,
formatDateTime(now_time, '%D') AS now_time_day_month_year,-- 07/30/19
-- toDateTime('2019-07-31 18:20:30', 'Asia/Shanghai') AS def_datetime1, -- 指定时区
formatDateTime(def_datetime, '%Y') AS def_datetime_year, -- 2019（指定日期为2019年）
formatDateTime(def_datetime, '%y') AS def_datetime_year_litter, -- 19（指定日期为19年,Year, last two digits (00-99),本世纪的第19年）
formatDateTime(def_datetime, '%H') AS hour24, -- 18 下午六点
formatDateTime(def_datetime, '%I') AS hour12, -- 06下午六点
formatDateTime(def_datetime, '%p') AS PMorAM, -- 指定时间是上午还是下午
formatDateTime(def_datetime, '%w') AS def_datetime_get_curWeek,-- 3(指定日期为星期三)
formatDateTime(def_datetime, '%F') AS def_datetime_get_date,-- 2019-07-31
formatDateTime(def_datetime, '%T') AS def_datetime_get_time,-- 18:20:30
formatDateTime(def_datetime, '%M') AS def_datetime_get_minute,-- 20(得到指定事件的“分”，minute (00-59))
formatDateTime(def_datetime, '%S') AS def_datetime_get_second;-- 30(得到指定事件的“秒”，second (00-59))
 
 
 

-- 1.跳转到之后的日期函数
-- 第一种，日期格式（指定日期，需注意时区的问题）
WITH
toDate('2019-09-09') AS date,
toDateTime('2019-09-09 00:00:00') AS date_time
SELECT
addYears(date, 1) AS add_years_with_date,
addYears(date_time, 0) AS add_years_with_date_time;
-- 第二种，日期格式（当前，本地时间）
WITH
toDate(now()) as date,
toDateTime(now()) as date_time
SELECT
now() as now_time,-- 当前时间
addYears(date, 1) AS add_years_with_date,-- 之后1年
addYears(date_time, 1) AS add_years_with_date_time,
addMonths(date, 1) AS add_months_with_date,-- 之后1月
addMonths(date_time, 1) AS add_months_with_date_time,
addWeeks(date, 1) AS add_weeks_with_date,--之后1周
addWeeks(date_time, 1) AS add_weeks_with_date_time,
addDays(date, 1) AS add_days_with_date,-- 之后1天
addDays(date_time, 1) AS add_days_with_date_time,
addHours(date_time, 1) AS add_hours_with_date_time,--之后1小时
addMinutes(date_time, 1) AS add_minutes_with_date_time,--之后1分中
addSeconds(date_time, 10) AS add_seconds_with_date_time,-- 之后10秒钟
addQuarters(date, 1) AS add_quarters_with_date, -- 之后1个季度
addQuarters(date_time, 1) AS add_quarters_with_date_time;



-- 2.跳转到当前日期之前的函数(函数将Date/DateTime减去一段时间间隔，然后返回Date/DateTime)
WITH
toDate(now()) as date,
toDateTime(now()) as date_time
SELECT
subtractYears(date, 1) AS subtract_years_with_date,
subtractYears(date_time, 1) AS subtract_years_with_date_time,
subtractQuarters(date, 1) AS subtract_Quarters_with_date,
subtractQuarters(date_time, 1) AS subtract_Quarters_with_date_time,
subtractMonths(date, 1) AS subtract_Months_with_date,
subtractMonths(date_time, 1) AS subtract_Months_with_date_time,
subtractWeeks(date, 1) AS subtract_Weeks_with_date,
subtractWeeks(date_time, 1) AS subtract_Weeks_with_date_time,
subtractDays(date, 1) AS subtract_Days_with_date,
subtractDays(date_time, 1) AS subtract_Days_with_date_time,
subtractHours(date_time, 1) AS subtract_Hours_with_date_time,
subtractMinutes(date_time, 1) AS subtract_Minutes_with_date_time,
subtractSeconds(date_time, 1) AS subtract_Seconds_with_date_time;



SELECT toDate('2019-07-31', 'Asia/GuangZhou') as date_guangzhou;
SELECT toDate('2019-07-31'), toDate('2019-07-31', 'Asia/Beijing') as date_beijing;
-- 亚洲只能加载上海的timezone？？？
SELECT toDateTime('2019-07-31 10:10:10', 'Asia/Shanghai') as date_shanghai;




-- 计算连个时刻在不同时间单位下的差值
-- 第一种：指定时间计算差值示例
WITH
toDateTime('2019-07-30 10:10:10', 'Asia/Shanghai') as date_shanghai_one,
toDateTime('2020-10-31 11:20:30', 'Asia/Shanghai') as date_shanghai_two
SELECT
dateDiff('year', date_shanghai_one, date_shanghai_two) as diff_years,
dateDiff('month', date_shanghai_one, date_shanghai_two) as diff_months,
dateDiff('week', date_shanghai_one, date_shanghai_two) as diff_week,
dateDiff('day', date_shanghai_one, date_shanghai_two) as diff_days,
dateDiff('hour', date_shanghai_one, date_shanghai_two) as diff_hours,
dateDiff('minute', date_shanghai_one, date_shanghai_two) as diff_minutes,
dateDiff('second', date_shanghai_one, date_shanghai_two) as diff_seconds;

-- 第二种：本地当前时间示例
WITH
now() as date_time
SELECT
dateDiff('year', date_time, addYears(date_time, 1)) as diff_years,
dateDiff('month', date_time, addMonths(date_time, 2)) as diff_months,
dateDiff('week', date_time, addWeeks(date_time, 3)) as diff_week,
dateDiff('day', date_time, addDays(date_time, 3)) as diff_days,
dateDiff('hour', date_time, addHours(date_time, 3)) as diff_hours,
dateDiff('minute', date_time, addMinutes(date_time, 30)) as diff_minutes,
dateDiff('second', date_time, addSeconds(date_time, 35)) as diff_seconds;



-- timeSlot(StartTime, Duration, [,Size])
-- 它返回一个时间数组，其中包括从从“StartTime”开始到“StartTime + Duration 秒”内的所有符合“size”（以秒为单位）步长的时间点
-- 作用:搜索在相应会话中综合浏览量是非常有用的。
SELECT
timeSlots(toDateTime('2012-01-01 12:20:00'), toUInt32(600)) as dateTimeArray,
dateTimeArray[0] as arr_index_0, -- no result.
dateTimeArray[1] as arr_index_1, -- 2012-01-01 20:00:00
dateTimeArray[2] as arr_index_2, -- 2012-01-01 20:30:00
dateTimeArray[3] as arr_index_3, -- no result.
dateTimeArray[4] as arr_index_4; -- no result.


-- toUInt32(600) 表示之后间距20秒的时刻
SELECT
timeSlots(now(), toUInt32(600), 20) as dateTimeArray, -- 类似于：引用地址
dateTimeArray[0] as arr_index_0, -- no result.为什么？
dateTimeArray[1] as arr_index_1,
dateTimeArray[2] as arr_index_2,
dateTimeArray[3] as arr_index_3,
dateTimeArray[4] as arr_index_4,
dateTimeArray[5] as arr_index_5;


-- 指定时间为基准，之后每个元素增加20秒
SELECT
timeSlots(toDateTime('2012-01-01 12:20:00'), toUInt32(600), 20) as cur_dateTimeArray, -- 类似于：引用地址
cur_dateTimeArray[0] as arr_index_0, -- no result.为什么？
cur_dateTimeArray[1] as arr_index_1, -- 2012-01-01 20:20:00
cur_dateTimeArray[2] as arr_index_2, -- 2012-01-01 20:20:20
cur_dateTimeArray[3] as arr_index_3, -- 2012-01-01 20:20:40
cur_dateTimeArray[4] as arr_index_4, -- 2012-01-01 20:21:00
cur_dateTimeArray[5] as arr_index_5; -- 2012-01-01 20:21:20
```

