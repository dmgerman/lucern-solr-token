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
name|index
operator|.
name|IndexReader
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
name|util
operator|.
name|PriorityQueue
import|;
end_import
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
comment|/**  * Expert: A hit queue for sorting by hits by terms in more than one field.  * Uses<code>FieldCache.DEFAULT</code> for maintaining internal term lookup tables.  *  *<p>Created: Dec 8, 2003 12:56:03 PM  *  * @since   lucene 1.4  * @version $Id$  * @see Searcher#search(Query,Filter,int,Sort)  * @see FieldCache  * @deprecated see {@link FieldValueHitQueue}  */
end_comment
begin_class
DECL|class|FieldSortedHitQueue
specifier|public
class|class
name|FieldSortedHitQueue
extends|extends
name|PriorityQueue
block|{
comment|/**    * Creates a hit queue sorted by the given list of fields.    * @param reader  Index to use.    * @param fields Fieldable names, in priority order (highest priority first).  Cannot be<code>null</code> or empty.    * @param size  The number of hits to retain.  Must be greater than zero.    * @throws IOException    */
DECL|method|FieldSortedHitQueue
specifier|public
name|FieldSortedHitQueue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|SortField
index|[]
name|fields
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|n
init|=
name|fields
operator|.
name|length
decl_stmt|;
name|comparators
operator|=
operator|new
name|ScoreDocComparator
index|[
name|n
index|]
expr_stmt|;
name|this
operator|.
name|fields
operator|=
operator|new
name|SortField
index|[
name|n
index|]
expr_stmt|;
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
condition|;
operator|++
name|i
control|)
block|{
name|String
name|fieldname
init|=
name|fields
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
decl_stmt|;
name|comparators
index|[
name|i
index|]
operator|=
name|getCachedComparator
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getParser
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getLocale
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getFactory
argument_list|()
argument_list|)
expr_stmt|;
comment|// new SortField instances must only be created when auto-detection is in use
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|AUTO
condition|)
block|{
if|if
condition|(
name|comparators
index|[
name|i
index|]
operator|.
name|sortType
argument_list|()
operator|==
name|SortField
operator|.
name|STRING
condition|)
block|{
name|this
operator|.
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldname
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getLocale
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldname
argument_list|,
name|comparators
index|[
name|i
index|]
operator|.
name|sortType
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
name|comparators
index|[
name|i
index|]
operator|.
name|sortType
argument_list|()
operator|==
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
assert|;
name|this
operator|.
name|fields
index|[
name|i
index|]
operator|=
name|fields
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/** Stores a comparator corresponding to each field being sorted by */
DECL|field|comparators
specifier|protected
name|ScoreDocComparator
index|[]
name|comparators
decl_stmt|;
comment|/** Stores the sort criteria being used. */
DECL|field|fields
specifier|protected
name|SortField
index|[]
name|fields
decl_stmt|;
comment|/** Stores the maximum score value encountered, needed for normalizing. */
DECL|field|maxscore
specifier|protected
name|float
name|maxscore
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
comment|/** returns the maximum score encountered by elements inserted via insert()    */
DECL|method|getMaxScore
specifier|public
name|float
name|getMaxScore
parameter_list|()
block|{
return|return
name|maxscore
return|;
block|}
comment|// Update maxscore.
DECL|method|updateMaxScore
specifier|private
specifier|final
name|void
name|updateMaxScore
parameter_list|(
name|FieldDoc
name|fdoc
parameter_list|)
block|{
name|maxscore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxscore
argument_list|,
name|fdoc
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
comment|// The signature of this method takes a FieldDoc in order to avoid
comment|// the unneeded cast to retrieve the score.
comment|// inherit javadoc
DECL|method|insert
specifier|public
name|boolean
name|insert
parameter_list|(
name|FieldDoc
name|fdoc
parameter_list|)
block|{
name|updateMaxScore
argument_list|(
name|fdoc
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|insert
argument_list|(
name|fdoc
argument_list|)
return|;
block|}
comment|// This overrides PriorityQueue.insert() so that insert(FieldDoc) that
comment|// keeps track of the score isn't accidentally bypassed.
comment|// inherit javadoc
DECL|method|insert
specifier|public
name|boolean
name|insert
parameter_list|(
name|Object
name|fdoc
parameter_list|)
block|{
return|return
name|insert
argument_list|(
operator|(
name|FieldDoc
operator|)
name|fdoc
argument_list|)
return|;
block|}
comment|// This overrides PriorityQueue.insertWithOverflow() so that
comment|// updateMaxScore(FieldDoc) that keeps track of the score isn't accidentally
comment|// bypassed.
DECL|method|insertWithOverflow
specifier|public
name|Object
name|insertWithOverflow
parameter_list|(
name|Object
name|element
parameter_list|)
block|{
name|updateMaxScore
argument_list|(
operator|(
name|FieldDoc
operator|)
name|element
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|insertWithOverflow
argument_list|(
name|element
argument_list|)
return|;
block|}
comment|/**    * Returns whether<code>a</code> is less relevant than<code>b</code>.    * @param a ScoreDoc    * @param b ScoreDoc    * @return<code>true</code> if document<code>a</code> should be sorted after document<code>b</code>.    */
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
specifier|final
name|Object
name|a
parameter_list|,
specifier|final
name|Object
name|b
parameter_list|)
block|{
specifier|final
name|ScoreDoc
name|docA
init|=
operator|(
name|ScoreDoc
operator|)
name|a
decl_stmt|;
specifier|final
name|ScoreDoc
name|docB
init|=
operator|(
name|ScoreDoc
operator|)
name|b
decl_stmt|;
comment|// run comparators
specifier|final
name|int
name|n
init|=
name|comparators
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
name|c
operator|=
operator|(
name|fields
index|[
name|i
index|]
operator|.
name|reverse
operator|)
condition|?
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|docB
argument_list|,
name|docA
argument_list|)
else|:
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|docA
argument_list|,
name|docB
argument_list|)
expr_stmt|;
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
comment|/**    * Given a FieldDoc object, stores the values used    * to sort the given document.  These values are not the raw    * values out of the index, but the internal representation    * of them.  This is so the given search hit can be collated    * by a MultiSearcher with other search hits.    * @param  doc  The FieldDoc to store sort values into.    * @return  The same FieldDoc passed in.    * @see Searchable#search(Weight,Filter,int,Sort)    */
DECL|method|fillFields
name|FieldDoc
name|fillFields
parameter_list|(
specifier|final
name|FieldDoc
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|comparators
operator|.
name|length
decl_stmt|;
specifier|final
name|Comparable
index|[]
name|fields
init|=
operator|new
name|Comparable
index|[
name|n
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
name|n
condition|;
operator|++
name|i
control|)
name|fields
index|[
name|i
index|]
operator|=
name|comparators
index|[
name|i
index|]
operator|.
name|sortValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
comment|//if (maxscore> 1.0f) doc.score /= maxscore;   // normalize scores
return|return
name|doc
return|;
block|}
comment|/** Returns the SortFields being used by this hit queue. */
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
DECL|method|getCachedComparator
specifier|static
name|ScoreDocComparator
name|getCachedComparator
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|type
parameter_list|,
name|FieldCache
operator|.
name|Parser
name|parser
parameter_list|,
name|Locale
name|locale
parameter_list|,
name|SortComparatorSource
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|type
operator|==
name|SortField
operator|.
name|DOC
condition|)
return|return
name|ScoreDocComparator
operator|.
name|INDEXORDER
return|;
if|if
condition|(
name|type
operator|==
name|SortField
operator|.
name|SCORE
condition|)
return|return
name|ScoreDocComparator
operator|.
name|RELEVANCE
return|;
name|FieldCacheImpl
operator|.
name|Entry
name|entry
init|=
operator|(
name|factory
operator|!=
literal|null
operator|)
condition|?
operator|new
name|FieldCacheImpl
operator|.
name|Entry
argument_list|(
name|field
argument_list|,
name|factory
argument_list|)
else|:
operator|(
operator|(
name|parser
operator|!=
literal|null
operator|)
condition|?
operator|new
name|FieldCacheImpl
operator|.
name|Entry
argument_list|(
name|field
argument_list|,
name|type
argument_list|,
name|parser
argument_list|)
else|:
operator|new
name|FieldCacheImpl
operator|.
name|Entry
argument_list|(
name|field
argument_list|,
name|type
argument_list|,
name|locale
argument_list|)
operator|)
decl_stmt|;
return|return
operator|(
name|ScoreDocComparator
operator|)
name|Comparators
operator|.
name|get
argument_list|(
name|reader
argument_list|,
name|entry
argument_list|)
return|;
block|}
comment|/** Internal cache of comparators. Similar to FieldCache, only    *  caches comparators instead of term values. */
DECL|field|Comparators
specifier|static
specifier|final
name|FieldCacheImpl
operator|.
name|Cache
name|Comparators
init|=
operator|new
name|FieldCacheImpl
operator|.
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|FieldCacheImpl
operator|.
name|Entry
name|entryKey
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldCacheImpl
operator|.
name|Entry
name|entry
init|=
operator|(
name|FieldCacheImpl
operator|.
name|Entry
operator|)
name|entryKey
decl_stmt|;
name|String
name|fieldname
init|=
name|entry
operator|.
name|field
decl_stmt|;
name|int
name|type
init|=
name|entry
operator|.
name|type
decl_stmt|;
name|Locale
name|locale
init|=
name|entry
operator|.
name|locale
decl_stmt|;
name|FieldCache
operator|.
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|SortComparatorSource
name|factory
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|custom
operator|instanceof
name|SortComparatorSource
condition|)
block|{
name|factory
operator|=
operator|(
name|SortComparatorSource
operator|)
name|entry
operator|.
name|custom
expr_stmt|;
block|}
else|else
block|{
name|parser
operator|=
operator|(
name|FieldCache
operator|.
name|Parser
operator|)
name|entry
operator|.
name|custom
expr_stmt|;
block|}
name|ScoreDocComparator
name|comparator
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SortField
operator|.
name|AUTO
case|:
name|comparator
operator|=
name|comparatorAuto
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|INT
case|:
name|comparator
operator|=
name|comparatorInt
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
operator|(
name|FieldCache
operator|.
name|IntParser
operator|)
name|parser
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|FLOAT
case|:
name|comparator
operator|=
name|comparatorFloat
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
operator|(
name|FieldCache
operator|.
name|FloatParser
operator|)
name|parser
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|LONG
case|:
name|comparator
operator|=
name|comparatorLong
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
operator|(
name|FieldCache
operator|.
name|LongParser
operator|)
name|parser
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|DOUBLE
case|:
name|comparator
operator|=
name|comparatorDouble
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
operator|(
name|FieldCache
operator|.
name|DoubleParser
operator|)
name|parser
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|SHORT
case|:
name|comparator
operator|=
name|comparatorShort
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
operator|(
name|FieldCache
operator|.
name|ShortParser
operator|)
name|parser
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|BYTE
case|:
name|comparator
operator|=
name|comparatorByte
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
operator|(
name|FieldCache
operator|.
name|ByteParser
operator|)
name|parser
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|STRING
case|:
if|if
condition|(
name|locale
operator|!=
literal|null
condition|)
name|comparator
operator|=
name|comparatorStringLocale
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
name|locale
argument_list|)
expr_stmt|;
else|else
name|comparator
operator|=
name|comparatorString
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|CUSTOM
case|:
name|comparator
operator|=
name|factory
operator|.
name|newComparator
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown field type: "
operator|+
name|type
argument_list|)
throw|;
block|}
return|return
name|comparator
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Returns a comparator for sorting hits according to a field containing bytes.    * @param reader  Index to use.    * @param fieldname  Fieldable containing integer values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorByte
specifier|static
name|ScoreDocComparator
name|comparatorByte
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|FieldCache
operator|.
name|ByteParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|fieldOrder
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getBytes
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|int
name|fi
init|=
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|int
name|fj
init|=
name|fieldOrder
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|fi
operator|<
name|fj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|fi
operator|>
name|fj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|Byte
operator|.
name|valueOf
argument_list|(
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|BYTE
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing shorts.    * @param reader  Index to use.    * @param fieldname  Fieldable containing integer values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorShort
specifier|static
name|ScoreDocComparator
name|comparatorShort
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|FieldCache
operator|.
name|ShortParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|short
index|[]
name|fieldOrder
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getShorts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|int
name|fi
init|=
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|int
name|fj
init|=
name|fieldOrder
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|fi
operator|<
name|fj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|fi
operator|>
name|fj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|Short
operator|.
name|valueOf
argument_list|(
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|SHORT
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing integers.    * @param reader  Index to use.    * @param fieldname  Fieldable containing integer values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorInt
specifier|static
name|ScoreDocComparator
name|comparatorInt
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|FieldCache
operator|.
name|IntParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|fieldOrder
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getInts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|int
name|fi
init|=
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|int
name|fj
init|=
name|fieldOrder
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|fi
operator|<
name|fj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|fi
operator|>
name|fj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|INT
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing integers.    * @param reader  Index to use.    * @param fieldname  Fieldable containing integer values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorLong
specifier|static
name|ScoreDocComparator
name|comparatorLong
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|FieldCache
operator|.
name|LongParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|long
index|[]
name|fieldOrder
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getLongs
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|long
name|li
init|=
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|long
name|lj
init|=
name|fieldOrder
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|li
operator|<
name|lj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|li
operator|>
name|lj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|LONG
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing floats.    * @param reader  Index to use.    * @param fieldname  Fieldable containing float values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorFloat
specifier|static
name|ScoreDocComparator
name|comparatorFloat
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|FieldCache
operator|.
name|FloatParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|float
index|[]
name|fieldOrder
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|float
name|fi
init|=
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|float
name|fj
init|=
name|fieldOrder
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|fi
operator|<
name|fj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|fi
operator|>
name|fj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|Float
operator|.
name|valueOf
argument_list|(
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|FLOAT
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing doubles.    * @param reader  Index to use.    * @param fieldname  Fieldable containing float values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorDouble
specifier|static
name|ScoreDocComparator
name|comparatorDouble
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|FieldCache
operator|.
name|DoubleParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|double
index|[]
name|fieldOrder
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|double
name|di
init|=
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|double
name|dj
init|=
name|fieldOrder
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|di
operator|<
name|dj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|di
operator|>
name|dj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|Double
operator|.
name|valueOf
argument_list|(
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|DOUBLE
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing strings.    * @param reader  Index to use.    * @param fieldname  Fieldable containing string values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorString
specifier|static
name|ScoreDocComparator
name|comparatorString
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|FieldCache
operator|.
name|StringIndex
name|index
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getStringIndex
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|int
name|fi
init|=
name|index
operator|.
name|order
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|int
name|fj
init|=
name|index
operator|.
name|order
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|fi
operator|<
name|fj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|fi
operator|>
name|fj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|index
operator|.
name|lookup
index|[
name|index
operator|.
name|order
index|[
name|i
operator|.
name|doc
index|]
index|]
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|STRING
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing strings.    * @param reader  Index to use.    * @param fieldname  Fieldable containing string values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorStringLocale
specifier|static
name|ScoreDocComparator
name|comparatorStringLocale
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|Locale
name|locale
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Collator
name|collator
init|=
name|Collator
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
decl_stmt|;
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|index
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getStrings
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
name|String
name|is
init|=
name|index
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
name|String
name|js
init|=
name|index
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|is
operator|==
name|js
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|js
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|collator
operator|.
name|compare
argument_list|(
name|is
argument_list|,
name|js
argument_list|)
return|;
block|}
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|index
index|[
name|i
operator|.
name|doc
index|]
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|STRING
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to values in the given field.    * The terms in the field are looked at to determine whether they contain integers,    * floats or strings.  Once the type is determined, one of the other static methods    * in this class is called to get the comparator.    * @param reader  Index to use.    * @param fieldname  Fieldable containing values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorAuto
specifier|static
name|ScoreDocComparator
name|comparatorAuto
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
name|Object
name|lookupArray
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getAuto
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|lookupArray
operator|instanceof
name|FieldCache
operator|.
name|StringIndex
condition|)
block|{
return|return
name|comparatorString
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|lookupArray
operator|instanceof
name|int
index|[]
condition|)
block|{
return|return
name|comparatorInt
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
literal|null
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|lookupArray
operator|instanceof
name|long
index|[]
condition|)
block|{
return|return
name|comparatorLong
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
literal|null
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|lookupArray
operator|instanceof
name|float
index|[]
condition|)
block|{
return|return
name|comparatorFloat
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
literal|null
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|lookupArray
operator|instanceof
name|String
index|[]
condition|)
block|{
return|return
name|comparatorString
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown data type in field '"
operator|+
name|field
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
