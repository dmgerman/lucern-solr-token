begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Collection
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
name|Iterator
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
name|TimeZone
import|;
end_import
begin_comment
comment|/**  * This class has some code from HttpClient DateUtil.  */
end_comment
begin_class
DECL|class|DateUtil
specifier|public
class|class
name|DateUtil
block|{
comment|//start HttpClient
comment|/**    * Date format pattern used to parse HTTP date headers in RFC 1123 format.    */
DECL|field|PATTERN_RFC1123
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN_RFC1123
init|=
literal|"EEE, dd MMM yyyy HH:mm:ss zzz"
decl_stmt|;
comment|/**    * Date format pattern used to parse HTTP date headers in RFC 1036 format.    */
DECL|field|PATTERN_RFC1036
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN_RFC1036
init|=
literal|"EEEE, dd-MMM-yy HH:mm:ss zzz"
decl_stmt|;
comment|/**    * Date format pattern used to parse HTTP date headers in ANSI C    *<code>asctime()</code> format.    */
DECL|field|PATTERN_ASCTIME
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN_ASCTIME
init|=
literal|"EEE MMM d HH:mm:ss yyyy"
decl_stmt|;
comment|//These are included for back compat
DECL|field|DEFAULT_HTTP_CLIENT_PATTERNS
specifier|private
specifier|static
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|DEFAULT_HTTP_CLIENT_PATTERNS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|PATTERN_ASCTIME
argument_list|,
name|PATTERN_RFC1036
argument_list|,
name|PATTERN_RFC1123
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_TWO_DIGIT_YEAR_START
specifier|private
specifier|static
specifier|final
name|Date
name|DEFAULT_TWO_DIGIT_YEAR_START
decl_stmt|;
static|static
block|{
name|Calendar
name|calendar
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|calendar
operator|.
name|set
argument_list|(
literal|2000
argument_list|,
name|Calendar
operator|.
name|JANUARY
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DEFAULT_TWO_DIGIT_YEAR_START
operator|=
name|calendar
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
DECL|field|GMT
specifier|private
specifier|static
specifier|final
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
comment|//end HttpClient
comment|//---------------------------------------------------------------------------------------
comment|/**    * A suite of default date formats that can be parsed, and thus transformed to the Solr specific format    */
DECL|field|DEFAULT_DATE_FORMATS
specifier|public
specifier|static
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|DEFAULT_DATE_FORMATS
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|DEFAULT_DATE_FORMATS
operator|.
name|add
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss'Z'"
argument_list|)
expr_stmt|;
name|DEFAULT_DATE_FORMATS
operator|.
name|add
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss"
argument_list|)
expr_stmt|;
name|DEFAULT_DATE_FORMATS
operator|.
name|add
argument_list|(
literal|"yyyy-MM-dd"
argument_list|)
expr_stmt|;
name|DEFAULT_DATE_FORMATS
operator|.
name|add
argument_list|(
literal|"yyyy-MM-dd hh:mm:ss"
argument_list|)
expr_stmt|;
name|DEFAULT_DATE_FORMATS
operator|.
name|add
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss"
argument_list|)
expr_stmt|;
name|DEFAULT_DATE_FORMATS
operator|.
name|add
argument_list|(
literal|"EEE MMM d hh:mm:ss z yyyy"
argument_list|)
expr_stmt|;
name|DEFAULT_DATE_FORMATS
operator|.
name|addAll
argument_list|(
name|DEFAULT_HTTP_CLIENT_PATTERNS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a formatter that can be use by the current thread if needed to    * convert Date objects to the Internal representation.    *    * @param d The input date to parse    * @return The parsed {@link java.util.Date}    * @throws java.text.ParseException If the input can't be parsed    */
DECL|method|parseDate
specifier|public
specifier|static
name|Date
name|parseDate
parameter_list|(
name|String
name|d
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|parseDate
argument_list|(
name|d
argument_list|,
name|DEFAULT_DATE_FORMATS
argument_list|)
return|;
block|}
DECL|method|parseDate
specifier|public
specifier|static
name|Date
name|parseDate
parameter_list|(
name|String
name|d
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|fmts
parameter_list|)
throws|throws
name|ParseException
block|{
comment|// 2007-04-26T08:05:04Z
if|if
condition|(
name|d
operator|.
name|endsWith
argument_list|(
literal|"Z"
argument_list|)
operator|&&
name|d
operator|.
name|length
argument_list|()
operator|>
literal|20
condition|)
block|{
return|return
name|getThreadLocalDateFormat
argument_list|()
operator|.
name|parse
argument_list|(
name|d
argument_list|)
return|;
block|}
return|return
name|parseDate
argument_list|(
name|d
argument_list|,
name|fmts
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Slightly modified from org.apache.commons.httpclient.util.DateUtil.parseDate    *<p>    * Parses the date value using the given date formats.    *    * @param dateValue   the date value to parse    * @param dateFormats the date formats to use    * @param startDate   During parsing, two digit years will be placed in the range    *<code>startDate</code> to<code>startDate + 100 years</code>. This value may    *                    be<code>null</code>. When<code>null</code> is given as a parameter, year    *<code>2000</code> will be used.    * @return the parsed date    * @throws ParseException if none of the dataFormats could parse the dateValue    */
DECL|method|parseDate
specifier|public
specifier|static
name|Date
name|parseDate
parameter_list|(
name|String
name|dateValue
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|dateFormats
parameter_list|,
name|Date
name|startDate
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|dateValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dateValue is null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|dateFormats
operator|==
literal|null
condition|)
block|{
name|dateFormats
operator|=
name|DEFAULT_HTTP_CLIENT_PATTERNS
expr_stmt|;
block|}
if|if
condition|(
name|startDate
operator|==
literal|null
condition|)
block|{
name|startDate
operator|=
name|DEFAULT_TWO_DIGIT_YEAR_START
expr_stmt|;
block|}
comment|// trim single quotes around date if present
comment|// see issue #5279
if|if
condition|(
name|dateValue
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|dateValue
operator|.
name|startsWith
argument_list|(
literal|"'"
argument_list|)
operator|&&
name|dateValue
operator|.
name|endsWith
argument_list|(
literal|"'"
argument_list|)
condition|)
block|{
name|dateValue
operator|=
name|dateValue
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|dateValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|SimpleDateFormat
name|dateParser
init|=
literal|null
decl_stmt|;
name|Iterator
name|formatIter
init|=
name|dateFormats
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|formatIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|format
init|=
operator|(
name|String
operator|)
name|formatIter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|dateParser
operator|==
literal|null
condition|)
block|{
name|dateParser
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
name|format
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
name|dateParser
operator|.
name|setTimeZone
argument_list|(
name|GMT
argument_list|)
expr_stmt|;
name|dateParser
operator|.
name|set2DigitYearStart
argument_list|(
name|startDate
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dateParser
operator|.
name|applyPattern
argument_list|(
name|format
argument_list|)
expr_stmt|;
block|}
try|try
block|{
return|return
name|dateParser
operator|.
name|parse
argument_list|(
name|dateValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// ignore this exception, we will try the next format
block|}
block|}
comment|// we were unable to parse the date
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unable to parse the date "
operator|+
name|dateValue
argument_list|,
literal|0
argument_list|)
throw|;
block|}
comment|/**    * Returns a formatter that can be use by the current thread if needed to    * convert Date objects to the Internal representation.    *    * @return The {@link java.text.DateFormat} for the current thread    */
DECL|method|getThreadLocalDateFormat
specifier|public
specifier|static
name|DateFormat
name|getThreadLocalDateFormat
parameter_list|()
block|{
return|return
name|fmtThreadLocal
operator|.
name|get
argument_list|()
return|;
block|}
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
DECL|field|fmtThreadLocal
specifier|private
specifier|static
name|ThreadLocalDateFormat
name|fmtThreadLocal
init|=
operator|new
name|ThreadLocalDateFormat
argument_list|()
decl_stmt|;
DECL|class|ThreadLocalDateFormat
specifier|private
specifier|static
class|class
name|ThreadLocalDateFormat
extends|extends
name|ThreadLocal
argument_list|<
name|DateFormat
argument_list|>
block|{
DECL|field|proto
name|DateFormat
name|proto
decl_stmt|;
DECL|method|ThreadLocalDateFormat
specifier|public
name|ThreadLocalDateFormat
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|//2007-04-26T08:05:04Z
name|SimpleDateFormat
name|tmp
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|tmp
operator|.
name|setTimeZone
argument_list|(
name|UTC
argument_list|)
expr_stmt|;
name|proto
operator|=
name|tmp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialValue
specifier|protected
name|DateFormat
name|initialValue
parameter_list|()
block|{
return|return
operator|(
name|DateFormat
operator|)
name|proto
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
comment|/** Formats the date and returns the calendar instance that was used (which may be reused) */
DECL|method|formatDate
specifier|public
specifier|static
name|Calendar
name|formatDate
parameter_list|(
name|Date
name|date
parameter_list|,
name|Calendar
name|cal
parameter_list|,
name|Appendable
name|out
parameter_list|)
throws|throws
name|IOException
block|{
comment|// using a stringBuilder for numbers can be nice since
comment|// a temporary string isn't used (it's added directly to the
comment|// builder's buffer.
name|StringBuilder
name|sb
init|=
name|out
operator|instanceof
name|StringBuilder
condition|?
operator|(
name|StringBuilder
operator|)
name|out
else|:
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|cal
operator|==
literal|null
condition|)
name|cal
operator|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
name|cal
operator|.
name|setTime
argument_list|(
name|date
argument_list|)
expr_stmt|;
name|int
name|i
init|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|i
operator|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|)
operator|+
literal|1
expr_stmt|;
comment|// 0 based, so add 1
if|if
condition|(
name|i
operator|<
literal|10
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|i
operator|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|10
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'T'
argument_list|)
expr_stmt|;
name|i
operator|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
expr_stmt|;
comment|// 24 hour time format
if|if
condition|(
name|i
operator|<
literal|10
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|i
operator|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|10
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|i
operator|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|10
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|i
operator|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|100
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|10
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// handle canonical format specifying fractional
comment|// seconds shall not end in '0'.  Given the slowness of
comment|// integer div/mod, simply checking the last character
comment|// is probably the fastest way to check.
name|int
name|lastIdx
init|=
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|sb
operator|.
name|charAt
argument_list|(
name|lastIdx
argument_list|)
operator|==
literal|'0'
condition|)
block|{
name|lastIdx
operator|--
expr_stmt|;
if|if
condition|(
name|sb
operator|.
name|charAt
argument_list|(
name|lastIdx
argument_list|)
operator|==
literal|'0'
condition|)
block|{
name|lastIdx
operator|--
expr_stmt|;
block|}
name|sb
operator|.
name|setLength
argument_list|(
name|lastIdx
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'Z'
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|!=
name|sb
condition|)
name|out
operator|.
name|append
argument_list|(
name|sb
argument_list|)
expr_stmt|;
return|return
name|cal
return|;
block|}
block|}
end_class
end_unit
