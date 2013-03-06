begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrRequestInfo
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
import|;
end_import
begin_comment
comment|//jdoc
end_comment
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
name|Calendar
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
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
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_comment
comment|/**  * A Simple Utility class for parsing "math" like strings relating to Dates.  *  *<p>  * The basic syntax support addition, subtraction and rounding at various  * levels of granularity (or "units").  Commands can be chained together  * and are parsed from left to right.  '+' and '-' denote addition and  * subtraction, while '/' denotes "round".  Round requires only a unit, while  * addition/subtraction require an integer value and a unit.  * Command strings must not include white space, but the "No-Op" command  * (empty string) is allowed....    *</p>  *  *<pre>  *   /HOUR  *      ... Round to the start of the current hour  *   /DAY  *      ... Round to the start of the current day  *   +2YEARS  *      ... Exactly two years in the future from now  *   -1DAY  *      ... Exactly 1 day prior to now  *   /DAY+6MONTHS+3DAYS  *      ... 6 months and 3 days in the future from the start of  *          the current day  *   +6MONTHS+3DAYS/DAY  *      ... 6 months and 3 days in the future from now, rounded  *          down to nearest day  *</pre>  *  *<p>  * (Multiple aliases exist for the various units of time (ie:  *<code>MINUTE</code> and<code>MINUTES</code>;<code>MILLI</code>,  *<code>MILLIS</code>,<code>MILLISECOND</code>, and  *<code>MILLISECONDS</code>.)  The complete list can be found by  * inspecting the keySet of {@link #CALENDAR_UNITS})  *</p>  *  *<p>  * All commands are relative to a "now" which is fixed in an instance of  * DateMathParser such that  *<code>p.parseMath("+0MILLISECOND").equals(p.parseMath("+0MILLISECOND"))</code>  * no matter how many wall clock milliseconds elapse between the two  * distinct calls to parse (Assuming no other thread calls  * "<code>setNow</code>" in the interim).  The default value of 'now' is   * the time at the moment the<code>DateMathParser</code> instance is   * constructed, unless overridden by the {@link CommonParams#NOW NOW}  * request param.  *</p>  *  *<p>  * All commands are also affected to the rules of a specified {@link TimeZone}  * (including the start/end of DST if any) which determine when each arbitrary   * day starts.  This not only impacts rounding/adding of DAYs, but also   * cascades to rounding of HOUR, MIN, MONTH, YEAR as well.  The default   *<code>TimeZone</code> used is<code>UTC</code> unless  overridden by the   * {@link CommonParams#TZ TZ}  * request param.  *</p>  *  * @see SolrRequestInfo#getClientTimeZone  * @see SolrRequestInfo#getNOW  */
end_comment
begin_class
DECL|class|DateMathParser
specifier|public
class|class
name|DateMathParser
block|{
DECL|field|UTC
specifier|public
specifier|static
name|TimeZone
name|UTC
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
decl_stmt|;
comment|/** Default TimeZone for DateMath rounding (UTC) */
DECL|field|DEFAULT_MATH_TZ
specifier|public
specifier|static
specifier|final
name|TimeZone
name|DEFAULT_MATH_TZ
init|=
name|UTC
decl_stmt|;
comment|/** Default Locale for DateMath rounding (Locale.ROOT) */
DECL|field|DEFAULT_MATH_LOCALE
specifier|public
specifier|static
specifier|final
name|Locale
name|DEFAULT_MATH_LOCALE
init|=
name|Locale
operator|.
name|ROOT
decl_stmt|;
comment|/**    * A mapping from (uppercased) String labels idenyifying time units,    * to the corresponding Calendar constant used to set/add/roll that unit    * of measurement.    *    *<p>    * A single logical unit of time might be represented by multiple labels    * for convenience (ie:<code>DATE==DAY</code>,    *<code>MILLI==MILLISECOND</code>)    *</p>    *    * @see Calendar    */
DECL|field|CALENDAR_UNITS
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|CALENDAR_UNITS
init|=
name|makeUnitsMap
argument_list|()
decl_stmt|;
comment|/** @see #CALENDAR_UNITS */
DECL|method|makeUnitsMap
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|makeUnitsMap
parameter_list|()
block|{
comment|// NOTE: consciously choosing not to support WEEK at this time,
comment|// because of complexity in rounding down to the nearest week
comment|// arround a month/year boundry.
comment|// (Not to mention: it's not clear what people would *expect*)
comment|//
comment|// If we consider adding some time of "week" support, then
comment|// we probably need to change "Locale loc" to default to something
comment|// from a param via SolrRequestInfo as well.
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|units
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|13
argument_list|)
decl_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"YEAR"
argument_list|,
name|Calendar
operator|.
name|YEAR
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"YEARS"
argument_list|,
name|Calendar
operator|.
name|YEAR
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"MONTH"
argument_list|,
name|Calendar
operator|.
name|MONTH
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"MONTHS"
argument_list|,
name|Calendar
operator|.
name|MONTH
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"DAY"
argument_list|,
name|Calendar
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"DAYS"
argument_list|,
name|Calendar
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"DATE"
argument_list|,
name|Calendar
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"HOUR"
argument_list|,
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"HOURS"
argument_list|,
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"MINUTE"
argument_list|,
name|Calendar
operator|.
name|MINUTE
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"MINUTES"
argument_list|,
name|Calendar
operator|.
name|MINUTE
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"SECOND"
argument_list|,
name|Calendar
operator|.
name|SECOND
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"SECONDS"
argument_list|,
name|Calendar
operator|.
name|SECOND
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"MILLI"
argument_list|,
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"MILLIS"
argument_list|,
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"MILLISECOND"
argument_list|,
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
expr_stmt|;
name|units
operator|.
name|put
argument_list|(
literal|"MILLISECONDS"
argument_list|,
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
expr_stmt|;
return|return
name|units
return|;
block|}
comment|/**    * Modifies the specified Calendar by "adding" the specified value of units    *    * @exception IllegalArgumentException if unit isn't recognized.    * @see #CALENDAR_UNITS    */
DECL|method|add
specifier|public
specifier|static
name|void
name|add
parameter_list|(
name|Calendar
name|c
parameter_list|,
name|int
name|val
parameter_list|,
name|String
name|unit
parameter_list|)
block|{
name|Integer
name|uu
init|=
name|CALENDAR_UNITS
operator|.
name|get
argument_list|(
name|unit
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|uu
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Adding Unit not recognized: "
operator|+
name|unit
argument_list|)
throw|;
block|}
name|c
operator|.
name|add
argument_list|(
name|uu
operator|.
name|intValue
argument_list|()
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
comment|/**    * Modifies the specified Calendar by "rounding" down to the specified unit    *    * @exception IllegalArgumentException if unit isn't recognized.    * @see #CALENDAR_UNITS    */
DECL|method|round
specifier|public
specifier|static
name|void
name|round
parameter_list|(
name|Calendar
name|c
parameter_list|,
name|String
name|unit
parameter_list|)
block|{
name|Integer
name|uu
init|=
name|CALENDAR_UNITS
operator|.
name|get
argument_list|(
name|unit
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|uu
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Rounding Unit not recognized: "
operator|+
name|unit
argument_list|)
throw|;
block|}
name|int
name|u
init|=
name|uu
operator|.
name|intValue
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|u
condition|)
block|{
case|case
name|Calendar
operator|.
name|YEAR
case|:
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|)
expr_stmt|;
comment|/* fall through */
case|case
name|Calendar
operator|.
name|MONTH
case|:
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|)
expr_stmt|;
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|DAY_OF_WEEK
argument_list|)
expr_stmt|;
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|DAY_OF_WEEK_IN_MONTH
argument_list|)
expr_stmt|;
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|DAY_OF_YEAR
argument_list|)
expr_stmt|;
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|WEEK_OF_MONTH
argument_list|)
expr_stmt|;
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|WEEK_OF_YEAR
argument_list|)
expr_stmt|;
comment|/* fall through */
case|case
name|Calendar
operator|.
name|DATE
case|:
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
expr_stmt|;
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|HOUR
argument_list|)
expr_stmt|;
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|AM_PM
argument_list|)
expr_stmt|;
comment|/* fall through */
case|case
name|Calendar
operator|.
name|HOUR_OF_DAY
case|:
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
expr_stmt|;
comment|/* fall through */
case|case
name|Calendar
operator|.
name|MINUTE
case|:
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|)
expr_stmt|;
comment|/* fall through */
case|case
name|Calendar
operator|.
name|SECOND
case|:
name|c
operator|.
name|clear
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No logic for rounding value ("
operator|+
name|u
operator|+
literal|") "
operator|+
name|unit
argument_list|)
throw|;
block|}
block|}
DECL|field|zone
specifier|private
name|TimeZone
name|zone
decl_stmt|;
DECL|field|loc
specifier|private
name|Locale
name|loc
decl_stmt|;
DECL|field|now
specifier|private
name|Date
name|now
decl_stmt|;
comment|/**    * Default constructor that assumes UTC should be used for rounding unless     * otherwise specified in the SolrRequestInfo    *     * @see SolrRequestInfo#getClientTimeZone    * @see #DEFAULT_MATH_LOCALE    */
DECL|method|DateMathParser
specifier|public
name|DateMathParser
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|DEFAULT_MATH_LOCALE
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param tz The TimeZone used for rounding (to determine when hours/days begin).  If null, then this method defaults to the value dicated by the SolrRequestInfo if it     * exists -- otherwise it uses UTC.    * @param l The Locale used for rounding (to determine when weeks begin).  If null, then this method defaults to en_US.    * @see #DEFAULT_MATH_TZ    * @see #DEFAULT_MATH_LOCALE    * @see Calendar#getInstance(TimeZone,Locale)    * @see SolrRequestInfo#getClientTimeZone    */
DECL|method|DateMathParser
specifier|public
name|DateMathParser
parameter_list|(
name|TimeZone
name|tz
parameter_list|,
name|Locale
name|l
parameter_list|)
block|{
name|loc
operator|=
operator|(
literal|null
operator|!=
name|l
operator|)
condition|?
name|l
else|:
name|DEFAULT_MATH_LOCALE
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|tz
condition|)
block|{
name|SolrRequestInfo
name|reqInfo
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
name|tz
operator|=
operator|(
literal|null
operator|!=
name|reqInfo
operator|)
condition|?
name|reqInfo
operator|.
name|getClientTimeZone
argument_list|()
else|:
name|DEFAULT_MATH_TZ
expr_stmt|;
block|}
name|zone
operator|=
operator|(
literal|null
operator|!=
name|tz
operator|)
condition|?
name|tz
else|:
name|DEFAULT_MATH_TZ
expr_stmt|;
block|}
comment|/**     * Defines this instance's concept of "now".    * @see #getNow    */
DECL|method|setNow
specifier|public
name|void
name|setNow
parameter_list|(
name|Date
name|n
parameter_list|)
block|{
name|now
operator|=
name|n
expr_stmt|;
block|}
comment|/**     * Returns a cloned of this instance's concept of "now".    *    * If setNow was never called (or if null was specified) then this method     * first defines 'now' as the value dictated by the SolrRequestInfo if it     * exists -- otherwise it uses a new Date instance at the moment getNow()     * is first called.    * @see #setNow    * @see SolrRequestInfo#getNOW    */
DECL|method|getNow
specifier|public
name|Date
name|getNow
parameter_list|()
block|{
if|if
condition|(
name|now
operator|==
literal|null
condition|)
block|{
name|SolrRequestInfo
name|reqInfo
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|reqInfo
operator|==
literal|null
condition|)
block|{
comment|// fall back to current time if no request info set
name|now
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|now
operator|=
name|reqInfo
operator|.
name|getNOW
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|(
name|Date
operator|)
name|now
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/**    * Parses a string of commands relative "now" are returns the resulting Date.    *     * @exception ParseException positions in ParseExceptions are token positions, not character positions.    */
DECL|method|parseMath
specifier|public
name|Date
name|parseMath
parameter_list|(
name|String
name|math
parameter_list|)
throws|throws
name|ParseException
block|{
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|zone
argument_list|,
name|loc
argument_list|)
decl_stmt|;
name|cal
operator|.
name|setTime
argument_list|(
name|getNow
argument_list|()
argument_list|)
expr_stmt|;
comment|/* check for No-Op */
if|if
condition|(
literal|0
operator|==
name|math
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|cal
operator|.
name|getTime
argument_list|()
return|;
block|}
name|String
index|[]
name|ops
init|=
name|splitter
operator|.
name|split
argument_list|(
name|math
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|ops
operator|.
name|length
condition|)
block|{
if|if
condition|(
literal|1
operator|!=
name|ops
index|[
name|pos
index|]
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Multi character command found: \""
operator|+
name|ops
index|[
name|pos
index|]
operator|+
literal|"\""
argument_list|,
name|pos
argument_list|)
throw|;
block|}
name|char
name|command
init|=
name|ops
index|[
name|pos
operator|++
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|command
condition|)
block|{
case|case
literal|'/'
case|:
if|if
condition|(
name|ops
operator|.
name|length
operator|<
name|pos
operator|+
literal|1
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Need a unit after command: \""
operator|+
name|command
operator|+
literal|"\""
argument_list|,
name|pos
argument_list|)
throw|;
block|}
try|try
block|{
name|round
argument_list|(
name|cal
argument_list|,
name|ops
index|[
name|pos
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unit not recognized: \""
operator|+
name|ops
index|[
name|pos
operator|-
literal|1
index|]
operator|+
literal|"\""
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
throw|;
block|}
break|break;
case|case
literal|'+'
case|:
comment|/* fall through */
case|case
literal|'-'
case|:
if|if
condition|(
name|ops
operator|.
name|length
operator|<
name|pos
operator|+
literal|2
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Need a value and unit for command: \""
operator|+
name|command
operator|+
literal|"\""
argument_list|,
name|pos
argument_list|)
throw|;
block|}
name|int
name|val
init|=
literal|0
decl_stmt|;
try|try
block|{
name|val
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|ops
index|[
name|pos
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Not a Number: \""
operator|+
name|ops
index|[
name|pos
operator|-
literal|1
index|]
operator|+
literal|"\""
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
throw|;
block|}
if|if
condition|(
literal|'-'
operator|==
name|command
condition|)
block|{
name|val
operator|=
literal|0
operator|-
name|val
expr_stmt|;
block|}
try|try
block|{
name|String
name|unit
init|=
name|ops
index|[
name|pos
operator|++
index|]
decl_stmt|;
name|add
argument_list|(
name|cal
argument_list|,
name|val
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unit not recognized: \""
operator|+
name|ops
index|[
name|pos
operator|-
literal|1
index|]
operator|+
literal|"\""
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
throw|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unrecognized command: \""
operator|+
name|command
operator|+
literal|"\""
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
throw|;
block|}
block|}
return|return
name|cal
operator|.
name|getTime
argument_list|()
return|;
block|}
DECL|field|splitter
specifier|private
specifier|static
name|Pattern
name|splitter
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\b|(?<=\\d)(?=\\D)"
argument_list|)
decl_stmt|;
block|}
end_class
end_unit
