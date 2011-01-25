begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|NumericRangeQuery
import|;
end_import
begin_comment
comment|// for javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
import|;
end_import
begin_comment
comment|// for javadocs
end_comment
begin_comment
comment|/**  * Provides support for converting dates to strings and vice-versa.  * The strings are structured so that lexicographic sorting orders   * them by date, which makes them suitable for use as field values   * and search terms.  *   *<P>This class also helps you to limit the resolution of your dates. Do not  * save dates with a finer resolution than you really need, as then  * RangeQuery and PrefixQuery will require more memory and become slower.  *   *<P>  * Another approach is {@link NumericUtils}, which provides  * a sortable binary representation (prefix encoded) of numeric values, which  * date/time are.  * For indexing a {@link Date} or {@link Calendar}, just get the unix timestamp as  *<code>long</code> using {@link Date#getTime} or {@link Calendar#getTimeInMillis} and  * index this as a numeric value with {@link NumericField}  * and use {@link NumericRangeQuery} to query it.  */
end_comment
begin_class
DECL|class|DateTools
specifier|public
class|class
name|DateTools
block|{
DECL|class|DateFormats
specifier|private
specifier|static
specifier|final
class|class
name|DateFormats
block|{
DECL|field|GMT
specifier|final
specifier|static
name|TimeZone
name|GMT
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
decl_stmt|;
DECL|field|YEAR_FORMAT
specifier|final
name|SimpleDateFormat
name|YEAR_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
DECL|field|MONTH_FORMAT
specifier|final
name|SimpleDateFormat
name|MONTH_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyyMM"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
DECL|field|DAY_FORMAT
specifier|final
name|SimpleDateFormat
name|DAY_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyyMMdd"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
DECL|field|HOUR_FORMAT
specifier|final
name|SimpleDateFormat
name|HOUR_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyyMMddHH"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
DECL|field|MINUTE_FORMAT
specifier|final
name|SimpleDateFormat
name|MINUTE_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyyMMddHHmm"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
DECL|field|SECOND_FORMAT
specifier|final
name|SimpleDateFormat
name|SECOND_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyyMMddHHmmss"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
DECL|field|MILLISECOND_FORMAT
specifier|final
name|SimpleDateFormat
name|MILLISECOND_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyyMMddHHmmssSSS"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
block|{
comment|// times need to be normalized so the value doesn't depend on the
comment|// location the index is created/used:
name|YEAR_FORMAT
operator|.
name|setTimeZone
parameter_list|(
name|GMT
parameter_list|)
constructor_decl|;
name|MONTH_FORMAT
operator|.
name|setTimeZone
parameter_list|(
name|GMT
parameter_list|)
constructor_decl|;
name|DAY_FORMAT
operator|.
name|setTimeZone
parameter_list|(
name|GMT
parameter_list|)
constructor_decl|;
name|HOUR_FORMAT
operator|.
name|setTimeZone
parameter_list|(
name|GMT
parameter_list|)
constructor_decl|;
name|MINUTE_FORMAT
operator|.
name|setTimeZone
parameter_list|(
name|GMT
parameter_list|)
constructor_decl|;
name|SECOND_FORMAT
operator|.
name|setTimeZone
parameter_list|(
name|GMT
parameter_list|)
constructor_decl|;
name|MILLISECOND_FORMAT
operator|.
name|setTimeZone
parameter_list|(
name|GMT
parameter_list|)
constructor_decl|;
block|}
DECL|field|calInstance
specifier|final
name|Calendar
name|calInstance
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|GMT
argument_list|)
decl_stmt|;
block|}
DECL|field|FORMATS
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|DateFormats
argument_list|>
name|FORMATS
init|=
operator|new
name|ThreadLocal
argument_list|<
name|DateFormats
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|DateFormats
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|DateFormats
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|// cannot create, the class has static methods only
DECL|method|DateTools
specifier|private
name|DateTools
parameter_list|()
block|{}
comment|/**    * Converts a Date to a string suitable for indexing.    *     * @param date the date to be converted    * @param resolution the desired resolution, see    *  {@link #round(Date, DateTools.Resolution)}    * @return a string in format<code>yyyyMMddHHmmssSSS</code> or shorter,    *  depending on<code>resolution</code>; using GMT as timezone     */
DECL|method|dateToString
specifier|public
specifier|static
name|String
name|dateToString
parameter_list|(
name|Date
name|date
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
return|return
name|timeToString
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|resolution
argument_list|)
return|;
block|}
comment|/**    * Converts a millisecond time to a string suitable for indexing.    *     * @param time the date expressed as milliseconds since January 1, 1970, 00:00:00 GMT    * @param resolution the desired resolution, see    *  {@link #round(long, DateTools.Resolution)}    * @return a string in format<code>yyyyMMddHHmmssSSS</code> or shorter,    *  depending on<code>resolution</code>; using GMT as timezone    */
DECL|method|timeToString
specifier|public
specifier|static
name|String
name|timeToString
parameter_list|(
name|long
name|time
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
specifier|final
name|DateFormats
name|formats
init|=
name|FORMATS
operator|.
name|get
argument_list|()
decl_stmt|;
name|formats
operator|.
name|calInstance
operator|.
name|setTimeInMillis
argument_list|(
name|round
argument_list|(
name|time
argument_list|,
name|resolution
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Date
name|date
init|=
name|formats
operator|.
name|calInstance
operator|.
name|getTime
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|resolution
condition|)
block|{
case|case
name|YEAR
case|:
return|return
name|formats
operator|.
name|YEAR_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
case|case
name|MONTH
case|:
return|return
name|formats
operator|.
name|MONTH_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
case|case
name|DAY
case|:
return|return
name|formats
operator|.
name|DAY_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
case|case
name|HOUR
case|:
return|return
name|formats
operator|.
name|HOUR_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
case|case
name|MINUTE
case|:
return|return
name|formats
operator|.
name|MINUTE_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
case|case
name|SECOND
case|:
return|return
name|formats
operator|.
name|SECOND_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
case|case
name|MILLISECOND
case|:
return|return
name|formats
operator|.
name|MILLISECOND_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown resolution "
operator|+
name|resolution
argument_list|)
throw|;
block|}
comment|/**    * Converts a string produced by<code>timeToString</code> or    *<code>dateToString</code> back to a time, represented as the    * number of milliseconds since January 1, 1970, 00:00:00 GMT.    *     * @param dateString the date string to be converted    * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT    * @throws ParseException if<code>dateString</code> is not in the     *  expected format     */
DECL|method|stringToTime
specifier|public
specifier|static
name|long
name|stringToTime
parameter_list|(
name|String
name|dateString
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|stringToDate
argument_list|(
name|dateString
argument_list|)
operator|.
name|getTime
argument_list|()
return|;
block|}
comment|/**    * Converts a string produced by<code>timeToString</code> or    *<code>dateToString</code> back to a time, represented as a    * Date object.    *     * @param dateString the date string to be converted    * @return the parsed time as a Date object     * @throws ParseException if<code>dateString</code> is not in the     *  expected format     */
DECL|method|stringToDate
specifier|public
specifier|static
name|Date
name|stringToDate
parameter_list|(
name|String
name|dateString
parameter_list|)
throws|throws
name|ParseException
block|{
specifier|final
name|DateFormats
name|formats
init|=
name|FORMATS
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|4
condition|)
block|{
return|return
name|formats
operator|.
name|YEAR_FORMAT
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|6
condition|)
block|{
return|return
name|formats
operator|.
name|MONTH_FORMAT
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|8
condition|)
block|{
return|return
name|formats
operator|.
name|DAY_FORMAT
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|10
condition|)
block|{
return|return
name|formats
operator|.
name|HOUR_FORMAT
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|12
condition|)
block|{
return|return
name|formats
operator|.
name|MINUTE_FORMAT
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|14
condition|)
block|{
return|return
name|formats
operator|.
name|SECOND_FORMAT
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|17
condition|)
block|{
return|return
name|formats
operator|.
name|MILLISECOND_FORMAT
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
return|;
block|}
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Input is not valid date string: "
operator|+
name|dateString
argument_list|,
literal|0
argument_list|)
throw|;
block|}
comment|/**    * Limit a date's resolution. For example, the date<code>2004-09-21 13:50:11</code>    * will be changed to<code>2004-09-01 00:00:00</code> when using    *<code>Resolution.MONTH</code>.     *     * @param resolution The desired resolution of the date to be returned    * @return the date with all values more precise than<code>resolution</code>    *  set to 0 or 1    */
DECL|method|round
specifier|public
specifier|static
name|Date
name|round
parameter_list|(
name|Date
name|date
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|round
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|resolution
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Limit a date's resolution. For example, the date<code>1095767411000</code>    * (which represents 2004-09-21 13:50:11) will be changed to     *<code>1093989600000</code> (2004-09-01 00:00:00) when using    *<code>Resolution.MONTH</code>.    *     * @param resolution The desired resolution of the date to be returned    * @return the date with all values more precise than<code>resolution</code>    *  set to 0 or 1, expressed as milliseconds since January 1, 1970, 00:00:00 GMT    */
DECL|method|round
specifier|public
specifier|static
name|long
name|round
parameter_list|(
name|long
name|time
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
specifier|final
name|Calendar
name|calInstance
init|=
name|FORMATS
operator|.
name|get
argument_list|()
operator|.
name|calInstance
decl_stmt|;
name|calInstance
operator|.
name|setTimeInMillis
argument_list|(
name|time
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|resolution
condition|)
block|{
case|case
name|YEAR
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|MONTH
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|DAY
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|HOUR
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|MINUTE
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|SECOND
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|MILLISECOND
case|:
comment|// don't cut off anything
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown resolution "
operator|+
name|resolution
argument_list|)
throw|;
block|}
return|return
name|calInstance
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
comment|/** Specifies the time granularity. */
DECL|enum|Resolution
specifier|public
specifier|static
enum|enum
name|Resolution
block|{
DECL|enum constant|YEAR
DECL|enum constant|MONTH
DECL|enum constant|DAY
DECL|enum constant|HOUR
DECL|enum constant|MINUTE
DECL|enum constant|SECOND
DECL|enum constant|MILLISECOND
name|YEAR
block|,
name|MONTH
block|,
name|DAY
block|,
name|HOUR
block|,
name|MINUTE
block|,
name|SECOND
block|,
name|MILLISECOND
block|;
comment|/** this method returns the name of the resolution      * in lowercase (for backwards compatibility) */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
