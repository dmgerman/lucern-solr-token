begin_unit
begin_package
DECL|package|org.apache.lucene.facet.sortedset
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sortedset
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
name|Arrays
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
name|Map
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
name|facet
operator|.
name|FacetsConfig
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
name|AtomicReader
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
name|SlowCompositeReaderWrapper
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
name|BytesRef
import|;
end_import
begin_comment
comment|/** Wraps a {@link IndexReader} and resolves ords  *  using existing {@link SortedSetDocValues} APIs without a  *  separate taxonomy index.  This only supports flat facets  *  (dimension + label), and it makes faceting a bit  *  slower, adds some cost at reopen time, but avoids  *  managing the separate taxonomy index.  It also requires  *  less RAM than the taxonomy index, as it manages the flat  *  (2-level) hierarchy more efficiently.  In addition, the  *  tie-break during faceting is now meaningful (in label  *  sorted order).  *  *<p><b>NOTE</b>: creating an instance of this class is  *  somewhat costly, as it computes per-segment ordinal maps,  *  so you should create it once and re-use that one instance  *  for a given {@link IndexReader}. */
end_comment
begin_class
DECL|class|SortedSetDocValuesReaderState
specifier|public
specifier|final
class|class
name|SortedSetDocValuesReaderState
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|topReader
specifier|private
specifier|final
name|AtomicReader
name|topReader
decl_stmt|;
DECL|field|valueCount
specifier|private
specifier|final
name|int
name|valueCount
decl_stmt|;
comment|/** {@link IndexReader} passed to the constructor. */
DECL|field|origReader
specifier|public
specifier|final
name|IndexReader
name|origReader
decl_stmt|;
comment|/** Holds start/end range of ords, which maps to one    *  dimension (someday we may generalize it to map to    *  hierarchies within one dimension). */
DECL|class|OrdRange
specifier|public
specifier|static
specifier|final
class|class
name|OrdRange
block|{
comment|/** Start of range, inclusive: */
DECL|field|start
specifier|public
specifier|final
name|int
name|start
decl_stmt|;
comment|/** End of range, inclusive: */
DECL|field|end
specifier|public
specifier|final
name|int
name|end
decl_stmt|;
comment|/** Start and end are inclusive. */
DECL|method|OrdRange
specifier|public
name|OrdRange
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
block|}
DECL|field|prefixToOrdRange
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OrdRange
argument_list|>
name|prefixToOrdRange
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|OrdRange
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Creates this, pulling doc values from the default {@link    *  FacetsConfig#DEFAULT_INDEX_FIELD_NAME}. */
DECL|method|SortedSetDocValuesReaderState
specifier|public
name|SortedSetDocValuesReaderState
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|FacetsConfig
operator|.
name|DEFAULT_INDEX_FIELD_NAME
argument_list|)
expr_stmt|;
block|}
comment|/** Creates this, pulling doc values from the specified    *  field. */
DECL|method|SortedSetDocValuesReaderState
specifier|public
name|SortedSetDocValuesReaderState
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|origReader
operator|=
name|reader
expr_stmt|;
comment|// We need this to create thread-safe MultiSortedSetDV
comment|// per collector:
name|topReader
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|SortedSetDocValues
name|dv
init|=
name|topReader
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field \""
operator|+
name|field
operator|+
literal|"\" was not indexed with SortedSetDocValues"
argument_list|)
throw|;
block|}
if|if
condition|(
name|dv
operator|.
name|getValueCount
argument_list|()
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"can only handle valueCount< Integer.MAX_VALUE; got "
operator|+
name|dv
operator|.
name|getValueCount
argument_list|()
argument_list|)
throw|;
block|}
name|valueCount
operator|=
operator|(
name|int
operator|)
name|dv
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
comment|// TODO: we can make this more efficient if eg we can be
comment|// "involved" when OrdinalMap is being created?  Ie see
comment|// each term/ord it's assigning as it goes...
name|String
name|lastDim
init|=
literal|null
decl_stmt|;
name|int
name|startOrd
init|=
operator|-
literal|1
decl_stmt|;
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|// TODO: this approach can work for full hierarchy?;
comment|// TaxoReader can't do this since ords are not in
comment|// "sorted order" ... but we should generalize this to
comment|// support arbitrary hierarchy:
for|for
control|(
name|int
name|ord
init|=
literal|0
init|;
name|ord
operator|<
name|valueCount
condition|;
name|ord
operator|++
control|)
block|{
name|dv
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|String
index|[]
name|components
init|=
name|FacetsConfig
operator|.
name|stringToPath
argument_list|(
name|spare
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|components
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this class can only handle 2 level hierarchy (dim/value); got: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|components
argument_list|)
operator|+
literal|" "
operator|+
name|spare
operator|.
name|utf8ToString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|components
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|lastDim
argument_list|)
condition|)
block|{
if|if
condition|(
name|lastDim
operator|!=
literal|null
condition|)
block|{
name|prefixToOrdRange
operator|.
name|put
argument_list|(
name|lastDim
argument_list|,
operator|new
name|OrdRange
argument_list|(
name|startOrd
argument_list|,
name|ord
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|startOrd
operator|=
name|ord
expr_stmt|;
name|lastDim
operator|=
name|components
index|[
literal|0
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lastDim
operator|!=
literal|null
condition|)
block|{
name|prefixToOrdRange
operator|.
name|put
argument_list|(
name|lastDim
argument_list|,
operator|new
name|OrdRange
argument_list|(
name|startOrd
argument_list|,
name|valueCount
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Return top-level doc values. */
DECL|method|getDocValues
specifier|public
name|SortedSetDocValues
name|getDocValues
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|topReader
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/** Returns mapping from prefix to {@link OrdRange}. */
DECL|method|getPrefixToOrdRange
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|OrdRange
argument_list|>
name|getPrefixToOrdRange
parameter_list|()
block|{
return|return
name|prefixToOrdRange
return|;
block|}
comment|/** Returns the {@link OrdRange} for this dimension. */
DECL|method|getOrdRange
specifier|public
name|OrdRange
name|getOrdRange
parameter_list|(
name|String
name|dim
parameter_list|)
block|{
return|return
name|prefixToOrdRange
operator|.
name|get
argument_list|(
name|dim
argument_list|)
return|;
block|}
comment|/** Indexed field we are reading. */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/** Number of unique labels. */
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
block|}
end_class
end_unit
