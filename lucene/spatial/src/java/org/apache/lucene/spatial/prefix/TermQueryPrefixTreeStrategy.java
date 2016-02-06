begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
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
name|List
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
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
name|TermsQuery
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
name|search
operator|.
name|Query
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|Cell
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|CellIterator
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgs
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
name|spatial
operator|.
name|query
operator|.
name|SpatialOperation
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
name|spatial
operator|.
name|query
operator|.
name|UnsupportedSpatialOperation
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
name|BytesRefBuilder
import|;
end_import
begin_comment
comment|/**  * A basic implementation of {@link PrefixTreeStrategy} using a large  * {@link TermsQuery} of all the cells from  * {@link SpatialPrefixTree#getTreeCellIterator(com.spatial4j.core.shape.Shape, int)}.  * It only supports the search of indexed Point shapes.  *<p>  * The precision of query shapes (distErrPct) is an important factor in using  * this Strategy. If the precision is too precise then it will result in many  * terms which will amount to a slower query.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TermQueryPrefixTreeStrategy
specifier|public
class|class
name|TermQueryPrefixTreeStrategy
extends|extends
name|PrefixTreeStrategy
block|{
DECL|field|simplifyIndexedCells
specifier|protected
name|boolean
name|simplifyIndexedCells
init|=
literal|false
decl_stmt|;
DECL|method|TermQueryPrefixTreeStrategy
specifier|public
name|TermQueryPrefixTreeStrategy
parameter_list|(
name|SpatialPrefixTree
name|grid
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|grid
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newCellToBytesRefIterator
specifier|protected
name|CellToBytesRefIterator
name|newCellToBytesRefIterator
parameter_list|()
block|{
comment|//Ensure we don't have leaves, as this strategy doesn't handle them.
return|return
operator|new
name|CellToBytesRefIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|cellIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|cellIter
operator|.
name|next
argument_list|()
operator|.
name|getTokenBytesNoLeaf
argument_list|(
name|bytesRef
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|makeQuery
specifier|public
name|Query
name|makeQuery
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
specifier|final
name|SpatialOperation
name|op
init|=
name|args
operator|.
name|getOperation
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|!=
name|SpatialOperation
operator|.
name|Intersects
condition|)
throw|throw
operator|new
name|UnsupportedSpatialOperation
argument_list|(
name|op
argument_list|)
throw|;
name|Shape
name|shape
init|=
name|args
operator|.
name|getShape
argument_list|()
decl_stmt|;
name|int
name|detailLevel
init|=
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|args
operator|.
name|resolveDistErr
argument_list|(
name|ctx
argument_list|,
name|distErrPct
argument_list|)
argument_list|)
decl_stmt|;
comment|//--get a List of BytesRef for each term we want (no parents, no leaf bytes))
specifier|final
name|int
name|GUESS_NUM_TERMS
decl_stmt|;
if|if
condition|(
name|shape
operator|instanceof
name|Point
condition|)
name|GUESS_NUM_TERMS
operator|=
name|detailLevel
expr_stmt|;
comment|//perfect guess
else|else
name|GUESS_NUM_TERMS
operator|=
literal|4096
expr_stmt|;
comment|//should this be a method on SpatialPrefixTree?
name|BytesRefBuilder
name|masterBytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
comment|//shared byte array for all terms
name|List
argument_list|<
name|BytesRef
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|GUESS_NUM_TERMS
argument_list|)
decl_stmt|;
name|CellIterator
name|cells
init|=
name|grid
operator|.
name|getTreeCellIterator
argument_list|(
name|shape
argument_list|,
name|detailLevel
argument_list|)
decl_stmt|;
while|while
condition|(
name|cells
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Cell
name|cell
init|=
name|cells
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|cell
operator|.
name|isLeaf
argument_list|()
condition|)
continue|continue;
name|BytesRef
name|term
init|=
name|cell
operator|.
name|getTokenBytesNoLeaf
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|//null because we want a new BytesRef
comment|//We copy out the bytes because it may be re-used across the iteration. This also gives us the opportunity
comment|// to use one contiguous block of memory for the bytes of all terms we need.
name|masterBytes
operator|.
name|grow
argument_list|(
name|masterBytes
operator|.
name|length
argument_list|()
operator|+
name|term
operator|.
name|length
argument_list|)
expr_stmt|;
name|masterBytes
operator|.
name|append
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|term
operator|.
name|bytes
operator|=
literal|null
expr_stmt|;
comment|//don't need; will reset later
name|term
operator|.
name|offset
operator|=
name|masterBytes
operator|.
name|length
argument_list|()
operator|-
name|term
operator|.
name|length
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
comment|//doing this now because if we did earlier, it's possible the bytes needed to grow()
for|for
control|(
name|BytesRef
name|byteRef
range|:
name|terms
control|)
block|{
name|byteRef
operator|.
name|bytes
operator|=
name|masterBytes
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
comment|//unfortunately TermsQuery will needlessly sort& dedupe
comment|//TODO an automatonQuery might be faster?
return|return
operator|new
name|TermsQuery
argument_list|(
name|getFieldName
argument_list|()
argument_list|,
name|terms
argument_list|)
return|;
block|}
block|}
end_class
end_unit
