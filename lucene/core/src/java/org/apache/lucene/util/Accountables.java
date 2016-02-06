begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
begin_comment
comment|/**   * Helper methods for constructing nested resource descriptions  * and debugging RAM usage.  *<p>  * {@code toString(Accountable}} can be used to quickly debug the nested  * structure of any Accountable.  *<p>  * The {@code namedAccountable} and {@code namedAccountables} methods return  * type-safe, point-in-time snapshots of the provided resources.  */
end_comment
begin_class
DECL|class|Accountables
specifier|public
class|class
name|Accountables
block|{
DECL|method|Accountables
specifier|private
name|Accountables
parameter_list|()
block|{}
comment|/**     * Returns a String description of an Accountable and any nested resources.    * This is intended for development and debugging.    */
DECL|method|toString
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|Accountable
name|a
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|toString
argument_list|(
name|sb
argument_list|,
name|a
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toString
specifier|private
specifier|static
name|StringBuilder
name|toString
parameter_list|(
name|StringBuilder
name|dest
parameter_list|,
name|Accountable
name|a
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|depth
condition|;
name|i
operator|++
control|)
block|{
name|dest
operator|.
name|append
argument_list|(
literal|"    "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|depth
operator|>
literal|0
condition|)
block|{
name|dest
operator|.
name|append
argument_list|(
literal|"|-- "
argument_list|)
expr_stmt|;
block|}
name|dest
operator|.
name|append
argument_list|(
name|a
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|dest
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|dest
operator|.
name|append
argument_list|(
name|RamUsageEstimator
operator|.
name|humanReadableUnits
argument_list|(
name|a
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|dest
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Accountable
name|child
range|:
name|a
operator|.
name|getChildResources
argument_list|()
control|)
block|{
name|toString
argument_list|(
name|dest
argument_list|,
name|child
argument_list|,
name|depth
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|dest
return|;
block|}
comment|/**    * Augments an existing accountable with the provided description.    *<p>    * The resource description is constructed in this format:    * {@code description [toString()]}    *<p>    * This is a point-in-time type safe view: consumers     * will not be able to cast or manipulate the resource in any way.    */
DECL|method|namedAccountable
specifier|public
specifier|static
name|Accountable
name|namedAccountable
parameter_list|(
name|String
name|description
parameter_list|,
name|Accountable
name|in
parameter_list|)
block|{
return|return
name|namedAccountable
argument_list|(
name|description
operator|+
literal|" ["
operator|+
name|in
operator|+
literal|"]"
argument_list|,
name|in
operator|.
name|getChildResources
argument_list|()
argument_list|,
name|in
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
return|;
block|}
comment|/**     * Returns an accountable with the provided description and bytes.    */
DECL|method|namedAccountable
specifier|public
specifier|static
name|Accountable
name|namedAccountable
parameter_list|(
name|String
name|description
parameter_list|,
name|long
name|bytes
parameter_list|)
block|{
return|return
name|namedAccountable
argument_list|(
name|description
argument_list|,
name|Collections
operator|.
expr|<
name|Accountable
operator|>
name|emptyList
argument_list|()
argument_list|,
name|bytes
argument_list|)
return|;
block|}
comment|/**     * Converts a map of resources to a collection.     *<p>    * The resource descriptions are constructed in this format:    * {@code prefix 'key' [toString()]}    *<p>    * This is a point-in-time type safe view: consumers     * will not be able to cast or manipulate the resources in any way.    */
DECL|method|namedAccountables
specifier|public
specifier|static
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|namedAccountables
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
extends|extends
name|Accountable
argument_list|>
name|in
parameter_list|)
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|?
argument_list|,
name|?
extends|extends
name|Accountable
argument_list|>
name|kv
range|:
name|in
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|resources
operator|.
name|add
argument_list|(
name|namedAccountable
argument_list|(
name|prefix
operator|+
literal|" '"
operator|+
name|kv
operator|.
name|getKey
argument_list|()
operator|+
literal|"'"
argument_list|,
name|kv
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|resources
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Accountable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Accountable
name|o1
parameter_list|,
name|Accountable
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|resources
argument_list|)
return|;
block|}
comment|/**     * Returns an accountable with the provided description, children and bytes.    *<p>    * The resource descriptions are constructed in this format:    * {@code description [toString()]}    *<p>    * This is a point-in-time type safe view: consumers     * will not be able to cast or manipulate the resources in any way, provided    * that the passed in children Accountables (and all their descendants) were created    * with one of the namedAccountable functions.    */
DECL|method|namedAccountable
specifier|public
specifier|static
name|Accountable
name|namedAccountable
parameter_list|(
specifier|final
name|String
name|description
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|children
parameter_list|,
specifier|final
name|long
name|bytes
parameter_list|)
block|{
return|return
operator|new
name|Accountable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|children
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|description
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
