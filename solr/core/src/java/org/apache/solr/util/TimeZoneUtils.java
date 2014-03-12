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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|regex
operator|.
name|Pattern
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
name|Matcher
import|;
end_import
begin_comment
comment|/**  * Simple utilities for working with TimeZones  * @see java.util.TimeZone  */
end_comment
begin_class
DECL|class|TimeZoneUtils
specifier|public
specifier|final
class|class
name|TimeZoneUtils
block|{
DECL|method|TimeZoneUtils
specifier|private
name|TimeZoneUtils
parameter_list|()
block|{
comment|// :NOOP:
block|}
comment|/**    * An immutable Set of all TimeZone IDs supported by the TimeZone class     * at the moment the TimeZoneUtils was initialized.    *     * @see TimeZone#getAvailableIDs    */
DECL|field|KNOWN_TIMEZONE_IDS
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|KNOWN_TIMEZONE_IDS
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|TimeZone
operator|.
name|getAvailableIDs
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * This method is provided as a replacement for TimeZone.getTimeZone but     * without the annoying behavior of returning "GMT" for gibberish input.    *<p>    * This method will return null unless the input is either:    *</p>    *<ul>    *<li>Included in the set of known TimeZone IDs</li>    *<li>A "CustomID" specified as a numeric offset from "GMT"</li>    *</ul>    *     * @param ID Either a TimeZone ID found in KNOWN_TIMEZONE_IDS, or a "CustomID" specified as a GMT offset.    * @return A TimeZone object corresponding to the input, or null if no such TimeZone is supported.    * @see #KNOWN_TIMEZONE_IDS    * @see TimeZone    */
DECL|method|getTimeZone
specifier|public
specifier|static
specifier|final
name|TimeZone
name|getTimeZone
parameter_list|(
specifier|final
name|String
name|ID
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|ID
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|KNOWN_TIMEZONE_IDS
operator|.
name|contains
argument_list|(
name|ID
argument_list|)
condition|)
return|return
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|ID
argument_list|)
return|;
name|Matcher
name|matcher
init|=
name|CUSTOM_ID_REGEX
operator|.
name|matcher
argument_list|(
name|ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|int
name|hour
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|hour
operator|<
literal|0
operator|||
literal|23
operator|<
name|hour
condition|)
return|return
literal|null
return|;
specifier|final
name|String
name|minStr
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|minStr
condition|)
block|{
name|int
name|min
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|minStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|min
operator|<
literal|0
operator|||
literal|59
operator|<
name|min
condition|)
return|return
literal|null
return|;
block|}
return|return
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|ID
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|field|CUSTOM_ID_REGEX
specifier|private
specifier|static
name|Pattern
name|CUSTOM_ID_REGEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"GMT(?:\\+|\\-)(\\d{1,2})(?::?(\\d{2}))?"
argument_list|)
decl_stmt|;
block|}
end_class
end_unit
