begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|util
operator|.
name|ArrayDeque
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
name|Deque
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
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
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
name|index
operator|.
name|DocValues
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
name|index
operator|.
name|NumericDocValues
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
name|index
operator|.
name|ReaderUtil
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
name|index
operator|.
name|Terms
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
name|index
operator|.
name|TermsEnum
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|Bits
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
name|BytesRef
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
name|CharsRefBuilder
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|StringHelper
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
name|FacetParams
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
name|NamedList
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|SchemaField
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
name|schema
operator|.
name|TrieField
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
name|search
operator|.
name|DocIterator
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
name|search
operator|.
name|DocSet
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import
begin_comment
comment|/** Utility class to compute facets on numeric fields. */
end_comment
begin_class
DECL|class|NumericFacets
specifier|final
class|class
name|NumericFacets
block|{
DECL|method|NumericFacets
name|NumericFacets
parameter_list|()
block|{}
DECL|class|HashTable
specifier|static
class|class
name|HashTable
block|{
DECL|field|LOAD_FACTOR
specifier|static
specifier|final
name|float
name|LOAD_FACTOR
init|=
literal|0.7f
decl_stmt|;
DECL|field|bits
name|long
index|[]
name|bits
decl_stmt|;
comment|// bits identifying a value
DECL|field|counts
name|int
index|[]
name|counts
decl_stmt|;
DECL|field|docIDs
name|int
index|[]
name|docIDs
decl_stmt|;
DECL|field|mask
name|int
name|mask
decl_stmt|;
DECL|field|size
name|int
name|size
decl_stmt|;
DECL|field|threshold
name|int
name|threshold
decl_stmt|;
DECL|method|HashTable
name|HashTable
parameter_list|()
block|{
specifier|final
name|int
name|capacity
init|=
literal|64
decl_stmt|;
comment|// must be a power of 2
name|bits
operator|=
operator|new
name|long
index|[
name|capacity
index|]
expr_stmt|;
name|counts
operator|=
operator|new
name|int
index|[
name|capacity
index|]
expr_stmt|;
name|docIDs
operator|=
operator|new
name|int
index|[
name|capacity
index|]
expr_stmt|;
name|mask
operator|=
name|capacity
operator|-
literal|1
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|capacity
operator|*
name|LOAD_FACTOR
argument_list|)
expr_stmt|;
block|}
DECL|method|hash
specifier|private
name|int
name|hash
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|int
name|h
init|=
call|(
name|int
call|)
argument_list|(
name|v
operator|^
operator|(
name|v
operator|>>>
literal|32
operator|)
argument_list|)
decl_stmt|;
name|h
operator|=
operator|(
literal|31
operator|*
name|h
operator|)
operator|&
name|mask
expr_stmt|;
comment|// * 31 to try to use the whole table, even if values are dense
return|return
name|h
return|;
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|long
name|value
parameter_list|,
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|size
operator|>=
name|threshold
condition|)
block|{
name|rehash
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|h
init|=
name|hash
argument_list|(
name|value
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|slot
init|=
name|h
init|;
condition|;
name|slot
operator|=
operator|(
name|slot
operator|+
literal|1
operator|)
operator|&
name|mask
control|)
block|{
if|if
condition|(
name|counts
index|[
name|slot
index|]
operator|==
literal|0
condition|)
block|{
name|bits
index|[
name|slot
index|]
operator|=
name|value
expr_stmt|;
name|docIDs
index|[
name|slot
index|]
operator|=
name|docID
expr_stmt|;
operator|++
name|size
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bits
index|[
name|slot
index|]
operator|!=
name|value
condition|)
block|{
continue|continue;
block|}
name|counts
index|[
name|slot
index|]
operator|+=
name|count
expr_stmt|;
break|break;
block|}
block|}
DECL|method|rehash
specifier|private
name|void
name|rehash
parameter_list|()
block|{
specifier|final
name|long
index|[]
name|oldBits
init|=
name|bits
decl_stmt|;
specifier|final
name|int
index|[]
name|oldCounts
init|=
name|counts
decl_stmt|;
specifier|final
name|int
index|[]
name|oldDocIDs
init|=
name|docIDs
decl_stmt|;
specifier|final
name|int
name|newCapacity
init|=
name|bits
operator|.
name|length
operator|*
literal|2
decl_stmt|;
name|bits
operator|=
operator|new
name|long
index|[
name|newCapacity
index|]
expr_stmt|;
name|counts
operator|=
operator|new
name|int
index|[
name|newCapacity
index|]
expr_stmt|;
name|docIDs
operator|=
operator|new
name|int
index|[
name|newCapacity
index|]
expr_stmt|;
name|mask
operator|=
name|newCapacity
operator|-
literal|1
expr_stmt|;
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|LOAD_FACTOR
operator|*
name|newCapacity
argument_list|)
expr_stmt|;
name|size
operator|=
literal|0
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
name|oldBits
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|oldCounts
index|[
name|i
index|]
operator|>
literal|0
condition|)
block|{
name|add
argument_list|(
name|oldDocIDs
index|[
name|i
index|]
argument_list|,
name|oldBits
index|[
name|i
index|]
argument_list|,
name|oldCounts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|Entry
specifier|private
specifier|static
class|class
name|Entry
block|{
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|bits
name|long
name|bits
decl_stmt|;
block|}
DECL|method|getCounts
specifier|public
specifier|static
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|getCounts
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|mincount
parameter_list|,
name|boolean
name|missing
parameter_list|,
name|String
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|zeros
init|=
name|mincount
operator|<=
literal|0
decl_stmt|;
name|mincount
operator|=
name|Math
operator|.
name|max
argument_list|(
name|mincount
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|SchemaField
name|sf
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
specifier|final
name|FieldType
name|ft
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|FieldType
operator|.
name|LegacyNumericType
name|numericType
init|=
name|ft
operator|.
name|getNumericType
argument_list|()
decl_stmt|;
if|if
condition|(
name|numericType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
comment|// 1. accumulate
specifier|final
name|HashTable
name|hashTable
init|=
operator|new
name|HashTable
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|LeafReaderContext
argument_list|>
name|ctxIt
init|=
name|leaves
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|LeafReaderContext
name|ctx
init|=
literal|null
decl_stmt|;
name|NumericDocValues
name|longs
init|=
literal|null
decl_stmt|;
name|Bits
name|docsWithField
init|=
literal|null
decl_stmt|;
name|int
name|missingCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DocIterator
name|docsIt
init|=
name|docs
operator|.
name|iterator
argument_list|()
init|;
name|docsIt
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|int
name|doc
init|=
name|docsIt
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctx
operator|==
literal|null
operator|||
name|doc
operator|>=
name|ctx
operator|.
name|docBase
operator|+
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
do|do
block|{
name|ctx
operator|=
name|ctxIt
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|ctx
operator|==
literal|null
operator|||
name|doc
operator|>=
name|ctx
operator|.
name|docBase
operator|+
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|)
do|;
assert|assert
name|doc
operator|>=
name|ctx
operator|.
name|docBase
assert|;
switch|switch
condition|(
name|numericType
condition|)
block|{
case|case
name|LONG
case|:
name|longs
operator|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|ctx
operator|.
name|reader
argument_list|()
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
break|break;
case|case
name|INT
case|:
name|longs
operator|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|ctx
operator|.
name|reader
argument_list|()
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
specifier|final
name|NumericDocValues
name|floats
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|ctx
operator|.
name|reader
argument_list|()
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
comment|// TODO: this bit flipping should probably be moved to tie-break in the PQ comparator
name|longs
operator|=
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|long
name|bits
init|=
name|floats
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|bits
operator|<
literal|0
condition|)
name|bits
operator|^=
literal|0x7fffffffffffffffL
expr_stmt|;
return|return
name|bits
return|;
block|}
block|}
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
specifier|final
name|NumericDocValues
name|doubles
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|ctx
operator|.
name|reader
argument_list|()
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
comment|// TODO: this bit flipping should probably be moved to tie-break in the PQ comparator
name|longs
operator|=
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|long
name|bits
init|=
name|doubles
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|bits
operator|<
literal|0
condition|)
name|bits
operator|^=
literal|0x7fffffffffffffffL
expr_stmt|;
return|return
name|bits
return|;
block|}
block|}
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|docsWithField
operator|=
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|ctx
operator|.
name|reader
argument_list|()
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
name|long
name|v
init|=
name|longs
operator|.
name|get
argument_list|(
name|doc
operator|-
name|ctx
operator|.
name|docBase
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|0
operator|||
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
operator|-
name|ctx
operator|.
name|docBase
argument_list|)
condition|)
block|{
name|hashTable
operator|.
name|add
argument_list|(
name|doc
argument_list|,
name|v
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|++
name|missingCount
expr_stmt|;
block|}
block|}
comment|// 2. select top-k facet values
specifier|final
name|int
name|pqSize
init|=
name|limit
operator|<
literal|0
condition|?
name|hashTable
operator|.
name|size
else|:
name|Math
operator|.
name|min
argument_list|(
name|offset
operator|+
name|limit
argument_list|,
name|hashTable
operator|.
name|size
argument_list|)
decl_stmt|;
specifier|final
name|PriorityQueue
argument_list|<
name|Entry
argument_list|>
name|pq
decl_stmt|;
if|if
condition|(
name|FacetParams
operator|.
name|FACET_SORT_COUNT
operator|.
name|equals
argument_list|(
name|sort
argument_list|)
operator|||
name|FacetParams
operator|.
name|FACET_SORT_COUNT_LEGACY
operator|.
name|equals
argument_list|(
name|sort
argument_list|)
condition|)
block|{
name|pq
operator|=
operator|new
name|PriorityQueue
argument_list|<
name|Entry
argument_list|>
argument_list|(
name|pqSize
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Entry
name|a
parameter_list|,
name|Entry
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|count
operator|<
name|b
operator|.
name|count
operator|||
operator|(
name|a
operator|.
name|count
operator|==
name|b
operator|.
name|count
operator|&&
name|a
operator|.
name|bits
operator|>
name|b
operator|.
name|bits
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
name|pq
operator|=
operator|new
name|PriorityQueue
argument_list|<
name|Entry
argument_list|>
argument_list|(
name|pqSize
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Entry
name|a
parameter_list|,
name|Entry
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|bits
operator|>
name|b
operator|.
name|bits
return|;
block|}
block|}
expr_stmt|;
block|}
name|Entry
name|e
init|=
literal|null
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
name|hashTable
operator|.
name|bits
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|hashTable
operator|.
name|counts
index|[
name|i
index|]
operator|>=
name|mincount
condition|)
block|{
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
name|e
operator|=
operator|new
name|Entry
argument_list|()
expr_stmt|;
block|}
name|e
operator|.
name|bits
operator|=
name|hashTable
operator|.
name|bits
index|[
name|i
index|]
expr_stmt|;
name|e
operator|.
name|count
operator|=
name|hashTable
operator|.
name|counts
index|[
name|i
index|]
expr_stmt|;
name|e
operator|.
name|docID
operator|=
name|hashTable
operator|.
name|docIDs
index|[
name|i
index|]
expr_stmt|;
name|e
operator|=
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// 4. build the NamedList
specifier|final
name|ValueSource
name|vs
init|=
name|ft
operator|.
name|getValueSource
argument_list|(
name|sf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|result
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// This stuff is complicated because if facet.mincount=0, the counts needs
comment|// to be merged with terms from the terms dict
if|if
condition|(
operator|!
name|zeros
operator|||
name|FacetParams
operator|.
name|FACET_SORT_COUNT
operator|.
name|equals
argument_list|(
name|sort
argument_list|)
operator|||
name|FacetParams
operator|.
name|FACET_SORT_COUNT_LEGACY
operator|.
name|equals
argument_list|(
name|sort
argument_list|)
condition|)
block|{
comment|// Only keep items we're interested in
specifier|final
name|Deque
argument_list|<
name|Entry
argument_list|>
name|counts
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|pq
operator|.
name|size
argument_list|()
operator|>
name|offset
condition|)
block|{
name|counts
operator|.
name|addFirst
argument_list|(
name|pq
operator|.
name|pop
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Entries from the PQ first, then using the terms dictionary
for|for
control|(
name|Entry
name|entry
range|:
name|counts
control|)
block|{
specifier|final
name|int
name|readerIdx
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|entry
operator|.
name|docID
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|values
init|=
name|vs
operator|.
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|leaves
operator|.
name|get
argument_list|(
name|readerIdx
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|values
operator|.
name|strVal
argument_list|(
name|entry
operator|.
name|docID
operator|-
name|leaves
operator|.
name|get
argument_list|(
name|readerIdx
argument_list|)
operator|.
name|docBase
argument_list|)
argument_list|,
name|entry
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|zeros
operator|&&
operator|(
name|limit
operator|<
literal|0
operator|||
name|result
operator|.
name|size
argument_list|()
operator|<
name|limit
operator|)
condition|)
block|{
comment|// need to merge with the term dict
if|if
condition|(
operator|!
name|sf
operator|.
name|indexed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot use "
operator|+
name|FacetParams
operator|.
name|FACET_MINCOUNT
operator|+
literal|"=0 on field "
operator|+
name|sf
operator|.
name|getName
argument_list|()
operator|+
literal|" which is not indexed"
argument_list|)
throw|;
block|}
comment|// Add zeros until there are limit results
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|alreadySeen
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|pq
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Entry
name|entry
init|=
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
specifier|final
name|int
name|readerIdx
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|entry
operator|.
name|docID
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|values
init|=
name|vs
operator|.
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|leaves
operator|.
name|get
argument_list|(
name|readerIdx
argument_list|)
argument_list|)
decl_stmt|;
name|alreadySeen
operator|.
name|add
argument_list|(
name|values
operator|.
name|strVal
argument_list|(
name|entry
operator|.
name|docID
operator|-
name|leaves
operator|.
name|get
argument_list|(
name|readerIdx
argument_list|)
operator|.
name|docBase
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|alreadySeen
operator|.
name|add
argument_list|(
name|result
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Terms
name|terms
init|=
name|searcher
operator|.
name|getLeafReader
argument_list|()
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|prefixStr
init|=
name|TrieField
operator|.
name|getMainValuePrefix
argument_list|(
name|ft
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|prefix
decl_stmt|;
if|if
condition|(
name|prefixStr
operator|!=
literal|null
condition|)
block|{
name|prefix
operator|=
operator|new
name|BytesRef
argument_list|(
name|prefixStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|prefix
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
switch|switch
condition|(
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
case|case
name|FOUND
case|:
case|case
name|NOT_FOUND
case|:
name|term
operator|=
name|termsEnum
operator|.
name|term
argument_list|()
expr_stmt|;
break|break;
case|case
name|END
case|:
name|term
operator|=
literal|null
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
specifier|final
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|skipped
init|=
name|hashTable
operator|.
name|size
init|;
name|skipped
operator|<
name|offset
operator|&&
name|term
operator|!=
literal|null
operator|&&
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|term
argument_list|,
name|prefix
argument_list|)
condition|;
control|)
block|{
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|term
argument_list|,
name|spare
argument_list|)
expr_stmt|;
specifier|final
name|String
name|termStr
init|=
name|spare
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|alreadySeen
operator|.
name|contains
argument_list|(
name|termStr
argument_list|)
condition|)
block|{
operator|++
name|skipped
expr_stmt|;
block|}
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
for|for
control|(
init|;
name|term
operator|!=
literal|null
operator|&&
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|term
argument_list|,
name|prefix
argument_list|)
operator|&&
operator|(
name|limit
operator|<
literal|0
operator|||
name|result
operator|.
name|size
argument_list|()
operator|<
name|limit
operator|)
condition|;
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
control|)
block|{
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|term
argument_list|,
name|spare
argument_list|)
expr_stmt|;
specifier|final
name|String
name|termStr
init|=
name|spare
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|alreadySeen
operator|.
name|contains
argument_list|(
name|termStr
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|termStr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
else|else
block|{
comment|// sort=index, mincount=0 and we have less than limit items
comment|// => Merge the PQ and the terms dictionary on the fly
if|if
condition|(
operator|!
name|sf
operator|.
name|indexed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot use "
operator|+
name|FacetParams
operator|.
name|FACET_SORT
operator|+
literal|"="
operator|+
name|FacetParams
operator|.
name|FACET_SORT_INDEX
operator|+
literal|" on a field which is not indexed"
argument_list|)
throw|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|counts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|pq
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|Entry
name|entry
init|=
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
specifier|final
name|int
name|readerIdx
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|entry
operator|.
name|docID
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|values
init|=
name|vs
operator|.
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|leaves
operator|.
name|get
argument_list|(
name|readerIdx
argument_list|)
argument_list|)
decl_stmt|;
name|counts
operator|.
name|put
argument_list|(
name|values
operator|.
name|strVal
argument_list|(
name|entry
operator|.
name|docID
operator|-
name|leaves
operator|.
name|get
argument_list|(
name|readerIdx
argument_list|)
operator|.
name|docBase
argument_list|)
argument_list|,
name|entry
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Terms
name|terms
init|=
name|searcher
operator|.
name|getLeafReader
argument_list|()
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|prefixStr
init|=
name|TrieField
operator|.
name|getMainValuePrefix
argument_list|(
name|ft
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|prefix
decl_stmt|;
if|if
condition|(
name|prefixStr
operator|!=
literal|null
condition|)
block|{
name|prefix
operator|=
operator|new
name|BytesRef
argument_list|(
name|prefixStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|prefix
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
switch|switch
condition|(
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
case|case
name|FOUND
case|:
case|case
name|NOT_FOUND
case|:
name|term
operator|=
name|termsEnum
operator|.
name|term
argument_list|()
expr_stmt|;
break|break;
case|case
name|END
case|:
name|term
operator|=
literal|null
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
specifier|final
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
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
name|offset
operator|&&
name|term
operator|!=
literal|null
operator|&&
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|term
argument_list|,
name|prefix
argument_list|)
condition|;
operator|++
name|i
control|)
block|{
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
for|for
control|(
init|;
name|term
operator|!=
literal|null
operator|&&
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|term
argument_list|,
name|prefix
argument_list|)
operator|&&
operator|(
name|limit
operator|<
literal|0
operator|||
name|result
operator|.
name|size
argument_list|()
operator|<
name|limit
operator|)
condition|;
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
control|)
block|{
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|term
argument_list|,
name|spare
argument_list|)
expr_stmt|;
specifier|final
name|String
name|termStr
init|=
name|spare
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Integer
name|count
init|=
name|counts
operator|.
name|get
argument_list|(
name|termStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
block|{
name|count
operator|=
literal|0
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|termStr
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|missing
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|missingCount
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
