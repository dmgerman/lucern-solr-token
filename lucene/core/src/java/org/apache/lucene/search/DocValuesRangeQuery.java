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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Objects
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
name|DocValuesType
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
name|SortedNumericDocValues
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
name|SortedSetDocValues
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
name|Bits
operator|.
name|MatchNoBits
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
name|ToStringUtils
import|;
end_import
begin_comment
comment|/**  * A range query that works on top of the doc values APIs. Such queries are  * usually slow since they do not use an inverted index. However, in the  * dense case where most documents match this query, it<b>might</b> be as  * fast or faster than a regular {@link NumericRangeQuery}.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DocValuesRangeQuery
specifier|public
specifier|final
class|class
name|DocValuesRangeQuery
extends|extends
name|Query
block|{
comment|/** Create a new numeric range query on a numeric doc-values field. The field    *  must has been indexed with either {@link DocValuesType#NUMERIC} or    *  {@link DocValuesType#SORTED_NUMERIC} doc values. */
DECL|method|newLongRange
specifier|public
specifier|static
name|Query
name|newLongRange
parameter_list|(
name|String
name|field
parameter_list|,
name|Long
name|lowerVal
parameter_list|,
name|Long
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
return|return
operator|new
name|DocValuesRangeQuery
argument_list|(
name|field
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
comment|/** Create a new numeric range query on a numeric doc-values field. The field    *  must has been indexed with {@link DocValuesType#SORTED} or    *  {@link DocValuesType#SORTED_SET} doc values. */
DECL|method|newBytesRefRange
specifier|public
specifier|static
name|Query
name|newBytesRefRange
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|lowerVal
parameter_list|,
name|BytesRef
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
return|return
operator|new
name|DocValuesRangeQuery
argument_list|(
name|field
argument_list|,
name|deepCopyOf
argument_list|(
name|lowerVal
argument_list|)
argument_list|,
name|deepCopyOf
argument_list|(
name|upperVal
argument_list|)
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
DECL|method|deepCopyOf
specifier|private
specifier|static
name|BytesRef
name|deepCopyOf
parameter_list|(
name|BytesRef
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|b
argument_list|)
return|;
block|}
block|}
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|lowerVal
DECL|field|upperVal
specifier|private
specifier|final
name|Object
name|lowerVal
decl_stmt|,
name|upperVal
decl_stmt|;
DECL|field|includeLower
DECL|field|includeUpper
specifier|private
specifier|final
name|boolean
name|includeLower
decl_stmt|,
name|includeUpper
decl_stmt|;
DECL|method|DocValuesRangeQuery
specifier|private
name|DocValuesRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Object
name|lowerVal
parameter_list|,
name|Object
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|lowerVal
operator|=
name|lowerVal
expr_stmt|;
name|this
operator|.
name|upperVal
operator|=
name|upperVal
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|DocValuesRangeQuery
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|DocValuesRangeQuery
name|that
init|=
operator|(
name|DocValuesRangeQuery
operator|)
name|obj
decl_stmt|;
return|return
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|lowerVal
argument_list|,
name|that
operator|.
name|lowerVal
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|upperVal
argument_list|,
name|that
operator|.
name|upperVal
argument_list|)
operator|&&
name|includeLower
operator|==
name|that
operator|.
name|includeLower
operator|&&
name|includeUpper
operator|==
name|that
operator|.
name|includeUpper
operator|&&
name|getBoost
argument_list|()
operator|==
name|that
operator|.
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|field
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|,
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|==
literal|false
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|includeLower
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|lowerVal
operator|==
literal|null
condition|?
literal|"*"
else|:
name|lowerVal
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|upperVal
operator|==
literal|null
condition|?
literal|"*"
else|:
name|upperVal
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|includeUpper
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lowerVal
operator|==
literal|null
operator|&&
name|upperVal
operator|==
literal|null
condition|)
block|{
specifier|final
name|FieldValueQuery
name|rewritten
init|=
operator|new
name|FieldValueQuery
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|rewritten
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rewritten
return|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lowerVal
operator|==
literal|null
operator|&&
name|upperVal
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Both min and max values cannot be null, call rewrite first"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|DocValuesRangeQuery
operator|.
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|float
name|score
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Bits
name|docsWithField
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getDocsWithField
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|docsWithField
operator|==
literal|null
operator|||
name|docsWithField
operator|instanceof
name|MatchNoBits
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSetIterator
name|approximation
init|=
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TwoPhaseIterator
name|twoPhaseRange
decl_stmt|;
if|if
condition|(
name|lowerVal
operator|instanceof
name|Long
operator|||
name|upperVal
operator|instanceof
name|Long
condition|)
block|{
specifier|final
name|SortedNumericDocValues
name|values
init|=
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|long
name|min
decl_stmt|;
if|if
condition|(
name|lowerVal
operator|==
literal|null
condition|)
block|{
name|min
operator|=
name|Long
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeLower
condition|)
block|{
name|min
operator|=
operator|(
name|long
operator|)
name|lowerVal
expr_stmt|;
block|}
else|else
block|{
name|min
operator|=
literal|1
operator|+
operator|(
name|long
operator|)
name|lowerVal
expr_stmt|;
block|}
specifier|final
name|long
name|max
decl_stmt|;
if|if
condition|(
name|upperVal
operator|==
literal|null
condition|)
block|{
name|max
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeUpper
condition|)
block|{
name|max
operator|=
operator|(
name|long
operator|)
name|upperVal
expr_stmt|;
block|}
else|else
block|{
name|max
operator|=
operator|-
literal|1
operator|+
operator|(
name|long
operator|)
name|upperVal
expr_stmt|;
block|}
if|if
condition|(
name|min
operator|>
name|max
condition|)
block|{
return|return
literal|null
return|;
block|}
name|twoPhaseRange
operator|=
operator|new
name|TwoPhaseNumericRange
argument_list|(
name|values
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|approximation
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerVal
operator|instanceof
name|BytesRef
operator|||
name|upperVal
operator|instanceof
name|BytesRef
condition|)
block|{
specifier|final
name|SortedSetDocValues
name|values
init|=
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|long
name|minOrd
decl_stmt|;
if|if
condition|(
name|lowerVal
operator|==
literal|null
condition|)
block|{
name|minOrd
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|ord
init|=
name|values
operator|.
name|lookupTerm
argument_list|(
operator|(
name|BytesRef
operator|)
name|lowerVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|minOrd
operator|=
operator|-
literal|1
operator|-
name|ord
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeLower
condition|)
block|{
name|minOrd
operator|=
name|ord
expr_stmt|;
block|}
else|else
block|{
name|minOrd
operator|=
name|ord
operator|+
literal|1
expr_stmt|;
block|}
block|}
specifier|final
name|long
name|maxOrd
decl_stmt|;
if|if
condition|(
name|upperVal
operator|==
literal|null
condition|)
block|{
name|maxOrd
operator|=
name|values
operator|.
name|getValueCount
argument_list|()
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|ord
init|=
name|values
operator|.
name|lookupTerm
argument_list|(
operator|(
name|BytesRef
operator|)
name|upperVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|maxOrd
operator|=
operator|-
literal|2
operator|-
name|ord
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeUpper
condition|)
block|{
name|maxOrd
operator|=
name|ord
expr_stmt|;
block|}
else|else
block|{
name|maxOrd
operator|=
name|ord
operator|-
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|minOrd
operator|>
name|maxOrd
condition|)
block|{
return|return
literal|null
return|;
block|}
name|twoPhaseRange
operator|=
operator|new
name|TwoPhaseOrdRange
argument_list|(
name|values
argument_list|,
name|minOrd
argument_list|,
name|maxOrd
argument_list|,
name|approximation
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
return|return
operator|new
name|RangeScorer
argument_list|(
name|this
argument_list|,
name|twoPhaseRange
argument_list|,
name|score
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|class|TwoPhaseNumericRange
specifier|private
specifier|static
class|class
name|TwoPhaseNumericRange
extends|extends
name|TwoPhaseIterator
block|{
DECL|field|approximation
specifier|private
specifier|final
name|DocIdSetIterator
name|approximation
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|SortedNumericDocValues
name|values
decl_stmt|;
DECL|field|min
DECL|field|max
specifier|private
specifier|final
name|long
name|min
decl_stmt|,
name|max
decl_stmt|;
DECL|field|acceptDocs
specifier|private
specifier|final
name|Bits
name|acceptDocs
decl_stmt|;
DECL|method|TwoPhaseNumericRange
name|TwoPhaseNumericRange
parameter_list|(
name|SortedNumericDocValues
name|values
parameter_list|,
name|long
name|min
parameter_list|,
name|long
name|max
parameter_list|,
name|DocIdSetIterator
name|approximation
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|approximation
operator|=
name|approximation
expr_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|acceptDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|approximation
specifier|public
name|DocIdSetIterator
name|approximation
parameter_list|()
block|{
return|return
name|approximation
return|;
block|}
annotation|@
name|Override
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|doc
init|=
name|approximation
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|acceptDocs
operator|==
literal|null
operator|||
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|values
operator|.
name|count
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
name|count
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|value
init|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|>=
name|min
operator|&&
name|value
operator|<=
name|max
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
DECL|class|TwoPhaseOrdRange
specifier|private
specifier|static
class|class
name|TwoPhaseOrdRange
extends|extends
name|TwoPhaseIterator
block|{
DECL|field|approximation
specifier|private
specifier|final
name|DocIdSetIterator
name|approximation
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|SortedSetDocValues
name|values
decl_stmt|;
DECL|field|minOrd
DECL|field|maxOrd
specifier|private
specifier|final
name|long
name|minOrd
decl_stmt|,
name|maxOrd
decl_stmt|;
DECL|field|acceptDocs
specifier|private
specifier|final
name|Bits
name|acceptDocs
decl_stmt|;
DECL|method|TwoPhaseOrdRange
name|TwoPhaseOrdRange
parameter_list|(
name|SortedSetDocValues
name|values
parameter_list|,
name|long
name|minOrd
parameter_list|,
name|long
name|maxOrd
parameter_list|,
name|DocIdSetIterator
name|approximation
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|minOrd
operator|=
name|minOrd
expr_stmt|;
name|this
operator|.
name|maxOrd
operator|=
name|maxOrd
expr_stmt|;
name|this
operator|.
name|approximation
operator|=
name|approximation
expr_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|acceptDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|approximation
specifier|public
name|DocIdSetIterator
name|approximation
parameter_list|()
block|{
return|return
name|approximation
return|;
block|}
annotation|@
name|Override
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|doc
init|=
name|approximation
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|acceptDocs
operator|==
literal|null
operator|||
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|ord
init|=
name|values
operator|.
name|nextOrd
argument_list|()
init|;
name|ord
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|;
name|ord
operator|=
name|values
operator|.
name|nextOrd
argument_list|()
control|)
block|{
if|if
condition|(
name|ord
operator|>=
name|minOrd
operator|&&
name|ord
operator|<=
name|maxOrd
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
DECL|class|RangeScorer
specifier|private
specifier|static
class|class
name|RangeScorer
extends|extends
name|Scorer
block|{
DECL|field|twoPhaseRange
specifier|private
specifier|final
name|TwoPhaseIterator
name|twoPhaseRange
decl_stmt|;
DECL|field|disi
specifier|private
specifier|final
name|DocIdSetIterator
name|disi
decl_stmt|;
DECL|field|score
specifier|private
specifier|final
name|float
name|score
decl_stmt|;
DECL|method|RangeScorer
name|RangeScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|TwoPhaseIterator
name|twoPhaseRange
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|twoPhaseRange
operator|=
name|twoPhaseRange
expr_stmt|;
name|this
operator|.
name|disi
operator|=
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseRange
argument_list|)
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
name|twoPhaseRange
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|disi
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|disi
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|disi
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|disi
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
