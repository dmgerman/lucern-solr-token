begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
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
name|io
operator|.
name|StringWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
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
name|TimeZone
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|util
operator|.
name|DateParseException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|util
operator|.
name|DateUtil
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
name|SolrInputDocument
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
name|SolrParams
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
name|util
operator|.
name|ContentStream
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
name|util
operator|.
name|ContentStreamBase
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
name|util
operator|.
name|XML
import|;
end_import
begin_comment
comment|/**  * TODO? should this go in common?  *   * @author ryan  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|ClientUtils
specifier|public
class|class
name|ClientUtils
block|{
comment|// Standard Content types
DECL|field|TEXT_XML
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_XML
init|=
literal|"text/xml; charset=utf-8"
decl_stmt|;
comment|/**    * Take a string and make it an iterable ContentStream    */
DECL|method|toContentStreams
specifier|public
specifier|static
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|toContentStreams
parameter_list|(
specifier|final
name|String
name|str
parameter_list|,
specifier|final
name|String
name|contentType
parameter_list|)
block|{
name|ContentStreamBase
name|ccc
init|=
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|ccc
operator|.
name|setContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
decl_stmt|;
name|streams
operator|.
name|add
argument_list|(
name|ccc
argument_list|)
expr_stmt|;
return|return
name|streams
return|;
block|}
comment|//------------------------------------------------------------------------
comment|//------------------------------------------------------------------------
DECL|method|writeFieldValue
specifier|private
specifier|static
name|void
name|writeFieldValue
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|Float
name|boost
parameter_list|,
name|Object
name|fieldValue
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldValue
operator|instanceof
name|Date
condition|)
block|{
name|fieldValue
operator|=
name|fmtThreadLocal
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
operator|(
name|Date
operator|)
name|fieldValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
name|XML
operator|.
name|writeXML
argument_list|(
name|writer
argument_list|,
literal|"field"
argument_list|,
name|fieldValue
operator|.
name|toString
argument_list|()
argument_list|,
literal|"name"
argument_list|,
name|fieldName
argument_list|,
literal|"boost"
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldValue
operator|!=
literal|null
condition|)
block|{
name|XML
operator|.
name|writeXML
argument_list|(
name|writer
argument_list|,
literal|"field"
argument_list|,
name|fieldValue
operator|.
name|toString
argument_list|()
argument_list|,
literal|"name"
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeXML
specifier|public
specifier|static
name|void
name|writeXML
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doc
operator|.
name|getBoost
argument_list|(
literal|null
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<doc boost=\""
operator|+
name|doc
operator|.
name|getBoost
argument_list|(
literal|null
argument_list|)
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<doc>"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|name
range|:
name|doc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
name|Float
name|boost
init|=
name|doc
operator|.
name|getBoost
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|doc
operator|.
name|getFieldValues
argument_list|(
name|name
argument_list|)
control|)
block|{
name|writeFieldValue
argument_list|(
name|writer
argument_list|,
name|name
argument_list|,
name|boost
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"</doc>"
argument_list|)
expr_stmt|;
block|}
DECL|method|toXML
specifier|public
specifier|static
name|String
name|toXML
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
block|{
name|StringWriter
name|str
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
try|try
block|{
name|writeXML
argument_list|(
name|doc
argument_list|,
name|str
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//---------------------------------------------------------------------------------------
DECL|field|fmts
specifier|public
specifier|static
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|fmts
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|fmts
operator|.
name|add
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss'Z'"
argument_list|)
expr_stmt|;
name|fmts
operator|.
name|add
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss"
argument_list|)
expr_stmt|;
name|fmts
operator|.
name|add
argument_list|(
literal|"yyyy-MM-dd"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a formatter that can be use by the current thread if needed to    * convert Date objects to the Internal representation.    * @throws ParseException     * @throws DateParseException     */
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
throws|,
name|DateParseException
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
name|DateUtil
operator|.
name|parseDate
argument_list|(
name|d
argument_list|,
name|fmts
argument_list|)
return|;
block|}
comment|/**    * Returns a formatter that can be use by the current thread if needed to    * convert Date objects to the Internal representation.    */
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
comment|/**    * See: http://lucene.apache.org/java/docs/queryparsersyntax.html#Escaping Special Characters    */
DECL|method|escapeQueryChars
specifier|public
specifier|static
name|String
name|escapeQueryChars
parameter_list|(
name|String
name|input
parameter_list|)
block|{
name|char
name|buff
index|[]
init|=
name|input
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|(
name|buff
operator|.
name|length
operator|+
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|char
name|c
range|:
name|buff
control|)
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'+'
case|:
case|case
literal|'-'
case|:
case|case
literal|'&'
case|:
case|case
literal|'|'
case|:
case|case
literal|'('
case|:
case|case
literal|')'
case|:
case|case
literal|'{'
case|:
case|case
literal|'}'
case|:
case|case
literal|'['
case|:
case|case
literal|']'
case|:
case|case
literal|'^'
case|:
case|case
literal|'"'
case|:
case|case
literal|'*'
case|:
case|case
literal|':'
case|:
case|case
literal|'\\'
case|:
name|str
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
name|str
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toQueryString
specifier|public
specifier|static
name|String
name|toQueryString
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|boolean
name|xml
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|128
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|amp
init|=
name|xml
condition|?
literal|"&amp;"
else|:
literal|"&"
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|names
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|names
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
index|[]
name|valarr
init|=
name|params
operator|.
name|getParams
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|valarr
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|first
condition|?
literal|"?"
else|:
name|amp
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|val
range|:
name|valarr
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|first
condition|?
literal|"?"
else|:
name|amp
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|val
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// can't happen
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
