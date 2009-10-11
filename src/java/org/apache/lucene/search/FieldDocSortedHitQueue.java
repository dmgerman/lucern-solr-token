begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|PriorityQueue
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
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
begin_comment
comment|/**  * Expert: Collects sorted results from Searchable's and collates them.  * The elements put into this queue must be of type FieldDoc.  *  *<p>Created: Feb 11, 2004 2:04:21 PM  *  * @since   lucene 1.4  */
end_comment
begin_class
DECL|class|FieldDocSortedHitQueue
class|class
name|FieldDocSortedHitQueue
extends|extends
name|PriorityQueue
argument_list|<
name|FieldDoc
argument_list|>
block|{
comment|// this cannot contain AUTO fields - any AUTO fields should
comment|// have been resolved by the time this class is used.
DECL|field|fields
specifier|volatile
name|SortField
index|[]
name|fields
decl_stmt|;
comment|// used in the case where the fields are sorted by locale
comment|// based strings
DECL|field|collators
specifier|volatile
name|Collator
index|[]
name|collators
decl_stmt|;
comment|/** 	 * Creates a hit queue sorted by the given list of fields. 	 * @param fields Fieldable names, in priority order (highest priority first). 	 * @param size  The number of hits to retain.  Must be greater than zero. 	 */
DECL|method|FieldDocSortedHitQueue
name|FieldDocSortedHitQueue
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|this
operator|.
name|collators
operator|=
name|hasCollators
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Allows redefinition of sort fields if they are<code>null</code>. 	 * This is to handle the case using ParallelMultiSearcher where the 	 * original list contains AUTO and we don't know the actual sort 	 * type until the values come back.  The fields can only be set once. 	 * This method is thread safe. 	 * @param fields 	 */
DECL|method|setFields
specifier|synchronized
name|void
name|setFields
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|fields
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|this
operator|.
name|collators
operator|=
name|hasCollators
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Returns the fields being used to sort. */
DECL|method|getFields
name|SortField
index|[]
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
comment|/** Returns an array of collators, possibly<code>null</code>.  The collators 	 * correspond to any SortFields which were given a specific locale. 	 * @param fields Array of sort fields. 	 * @return Array, possibly<code>null</code>. 	 */
DECL|method|hasCollators
specifier|private
name|Collator
index|[]
name|hasCollators
parameter_list|(
specifier|final
name|SortField
index|[]
name|fields
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Collator
index|[]
name|ret
init|=
operator|new
name|Collator
index|[
name|fields
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Locale
name|locale
init|=
name|fields
index|[
name|i
index|]
operator|.
name|getLocale
argument_list|()
decl_stmt|;
if|if
condition|(
name|locale
operator|!=
literal|null
condition|)
name|ret
index|[
name|i
index|]
operator|=
name|Collator
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/** 	 * Returns whether<code>a</code> is less relevant than<code>b</code>. 	 * @param a ScoreDoc 	 * @param b ScoreDoc 	 * @return<code>true</code> if document<code>a</code> should be sorted after document<code>b</code>. 	 */
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
specifier|final
name|FieldDoc
name|docA
parameter_list|,
specifier|final
name|FieldDoc
name|docB
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|fields
operator|.
name|length
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
operator|&&
name|c
operator|==
literal|0
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|type
init|=
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SortField
operator|.
name|SCORE
case|:
block|{
name|float
name|r1
init|=
operator|(
operator|(
name|Float
operator|)
name|docA
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|float
name|r2
init|=
operator|(
operator|(
name|Float
operator|)
name|docB
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|r1
operator|>
name|r2
condition|)
name|c
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|r1
operator|<
name|r2
condition|)
name|c
operator|=
literal|1
expr_stmt|;
break|break;
block|}
case|case
name|SortField
operator|.
name|DOC
case|:
case|case
name|SortField
operator|.
name|INT
case|:
block|{
name|int
name|i1
init|=
operator|(
operator|(
name|Integer
operator|)
name|docA
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|int
name|i2
init|=
operator|(
operator|(
name|Integer
operator|)
name|docB
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|i1
operator|<
name|i2
condition|)
name|c
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|i1
operator|>
name|i2
condition|)
name|c
operator|=
literal|1
expr_stmt|;
break|break;
block|}
case|case
name|SortField
operator|.
name|LONG
case|:
block|{
name|long
name|l1
init|=
operator|(
operator|(
name|Long
operator|)
name|docA
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|l2
init|=
operator|(
operator|(
name|Long
operator|)
name|docB
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|l1
operator|<
name|l2
condition|)
name|c
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|l1
operator|>
name|l2
condition|)
name|c
operator|=
literal|1
expr_stmt|;
break|break;
block|}
case|case
name|SortField
operator|.
name|STRING
case|:
block|{
name|String
name|s1
init|=
operator|(
name|String
operator|)
name|docA
operator|.
name|fields
index|[
name|i
index|]
decl_stmt|;
name|String
name|s2
init|=
operator|(
name|String
operator|)
name|docB
operator|.
name|fields
index|[
name|i
index|]
decl_stmt|;
comment|// null values need to be sorted first, because of how FieldCache.getStringIndex()
comment|// works - in that routine, any documents without a value in the given field are
comment|// put first.  If both are null, the next SortField is used
if|if
condition|(
name|s1
operator|==
literal|null
condition|)
name|c
operator|=
operator|(
name|s2
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|s2
operator|==
literal|null
condition|)
name|c
operator|=
literal|1
expr_stmt|;
comment|//
elseif|else
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|getLocale
argument_list|()
operator|==
literal|null
condition|)
block|{
name|c
operator|=
name|s1
operator|.
name|compareTo
argument_list|(
name|s2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|c
operator|=
name|collators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
case|case
name|SortField
operator|.
name|FLOAT
case|:
block|{
name|float
name|f1
init|=
operator|(
operator|(
name|Float
operator|)
name|docA
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|float
name|f2
init|=
operator|(
operator|(
name|Float
operator|)
name|docB
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|f1
operator|<
name|f2
condition|)
name|c
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|f1
operator|>
name|f2
condition|)
name|c
operator|=
literal|1
expr_stmt|;
break|break;
block|}
case|case
name|SortField
operator|.
name|DOUBLE
case|:
block|{
name|double
name|d1
init|=
operator|(
operator|(
name|Double
operator|)
name|docA
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|double
name|d2
init|=
operator|(
operator|(
name|Double
operator|)
name|docB
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|d1
operator|<
name|d2
condition|)
name|c
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|d1
operator|>
name|d2
condition|)
name|c
operator|=
literal|1
expr_stmt|;
break|break;
block|}
case|case
name|SortField
operator|.
name|BYTE
case|:
block|{
name|int
name|i1
init|=
operator|(
operator|(
name|Byte
operator|)
name|docA
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|byteValue
argument_list|()
decl_stmt|;
name|int
name|i2
init|=
operator|(
operator|(
name|Byte
operator|)
name|docB
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|byteValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|i1
operator|<
name|i2
condition|)
name|c
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|i1
operator|>
name|i2
condition|)
name|c
operator|=
literal|1
expr_stmt|;
break|break;
block|}
case|case
name|SortField
operator|.
name|SHORT
case|:
block|{
name|int
name|i1
init|=
operator|(
operator|(
name|Short
operator|)
name|docA
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|shortValue
argument_list|()
decl_stmt|;
name|int
name|i2
init|=
operator|(
operator|(
name|Short
operator|)
name|docB
operator|.
name|fields
index|[
name|i
index|]
operator|)
operator|.
name|shortValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|i1
operator|<
name|i2
condition|)
name|c
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|i1
operator|>
name|i2
condition|)
name|c
operator|=
literal|1
expr_stmt|;
break|break;
block|}
case|case
name|SortField
operator|.
name|CUSTOM
case|:
block|{
name|c
operator|=
name|docA
operator|.
name|fields
index|[
name|i
index|]
operator|.
name|compareTo
argument_list|(
name|docB
operator|.
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|SortField
operator|.
name|AUTO
case|:
block|{
comment|// we cannot handle this - even if we determine the type of object (Float or
comment|// Integer), we don't necessarily know how to compare them (both SCORE and
comment|// FLOAT contain floats, but are sorted opposite of each other). Before
comment|// we get here, each AUTO should have been replaced with its actual value.
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"FieldDocSortedHitQueue cannot use an AUTO SortField"
argument_list|)
throw|;
block|}
default|default:
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"invalid SortField type: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
condition|)
block|{
name|c
operator|=
operator|-
name|c
expr_stmt|;
block|}
block|}
comment|// avoid random sort order that could lead to duplicates (bug #31241):
if|if
condition|(
name|c
operator|==
literal|0
condition|)
return|return
name|docA
operator|.
name|doc
operator|>
name|docB
operator|.
name|doc
return|;
return|return
name|c
operator|>
literal|0
return|;
block|}
block|}
end_class
end_unit
