begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|LuceneTestCase
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
name|util
operator|.
name|BitSet
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
name|Map
import|;
end_import
begin_class
DECL|class|TestPositionBasedTermVectorMapper
specifier|public
class|class
name|TestPositionBasedTermVectorMapper
extends|extends
name|LuceneTestCase
block|{
DECL|field|tokens
specifier|protected
name|String
index|[]
name|tokens
decl_stmt|;
DECL|field|thePositions
specifier|protected
name|int
index|[]
index|[]
name|thePositions
decl_stmt|;
DECL|field|offsets
specifier|protected
name|TermVectorOffsetInfo
index|[]
index|[]
name|offsets
decl_stmt|;
DECL|field|numPositions
specifier|protected
name|int
name|numPositions
decl_stmt|;
DECL|method|TestPositionBasedTermVectorMapper
specifier|public
name|TestPositionBasedTermVectorMapper
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|tokens
operator|=
operator|new
name|String
index|[]
block|{
literal|"here"
block|,
literal|"is"
block|,
literal|"some"
block|,
literal|"text"
block|,
literal|"to"
block|,
literal|"test"
block|,
literal|"extra"
block|}
expr_stmt|;
name|thePositions
operator|=
operator|new
name|int
index|[
name|tokens
operator|.
name|length
index|]
index|[]
expr_stmt|;
name|offsets
operator|=
operator|new
name|TermVectorOffsetInfo
index|[
name|tokens
operator|.
name|length
index|]
index|[]
expr_stmt|;
name|numPositions
operator|=
literal|0
expr_stmt|;
comment|//save off the last one so we can add it with the same positions as some of the others, but in a predictable way
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tokens
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|thePositions
index|[
name|i
index|]
operator|=
operator|new
name|int
index|[
literal|2
operator|*
name|i
operator|+
literal|1
index|]
expr_stmt|;
comment|//give 'em all some positions
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|thePositions
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|thePositions
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|numPositions
operator|++
expr_stmt|;
block|}
name|offsets
index|[
name|i
index|]
operator|=
operator|new
name|TermVectorOffsetInfo
index|[
name|thePositions
index|[
name|i
index|]
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|offsets
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|offsets
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
operator|new
name|TermVectorOffsetInfo
argument_list|(
name|j
argument_list|,
name|j
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|//the actual value here doesn't much matter
block|}
block|}
name|thePositions
index|[
name|tokens
operator|.
name|length
operator|-
literal|1
index|]
operator|=
operator|new
name|int
index|[
literal|1
index|]
expr_stmt|;
name|thePositions
index|[
name|tokens
operator|.
name|length
operator|-
literal|1
index|]
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
comment|//put this at the same position as "here"
name|offsets
index|[
name|tokens
operator|.
name|length
operator|-
literal|1
index|]
operator|=
operator|new
name|TermVectorOffsetInfo
index|[
literal|1
index|]
expr_stmt|;
name|offsets
index|[
name|tokens
operator|.
name|length
operator|-
literal|1
index|]
index|[
literal|0
index|]
operator|=
operator|new
name|TermVectorOffsetInfo
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|PositionBasedTermVectorMapper
name|mapper
init|=
operator|new
name|PositionBasedTermVectorMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|setExpectations
argument_list|(
literal|"test"
argument_list|,
name|tokens
operator|.
name|length
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//Test single position
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tokens
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|token
init|=
name|tokens
index|[
name|i
index|]
decl_stmt|;
name|mapper
operator|.
name|map
argument_list|(
name|token
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|thePositions
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|Map
name|map
init|=
name|mapper
operator|.
name|getFieldToTerms
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"map is null and it shouldn't be"
argument_list|,
name|map
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"map Size: "
operator|+
name|map
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|map
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Map
name|positions
init|=
operator|(
name|Map
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"thePositions is null and it shouldn't be"
argument_list|,
name|positions
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"thePositions Size: "
operator|+
name|positions
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
name|numPositions
argument_list|,
name|positions
operator|.
name|size
argument_list|()
operator|==
name|numPositions
argument_list|)
expr_stmt|;
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|numPositions
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|positions
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|PositionBasedTermVectorMapper
operator|.
name|TVPositionInfo
name|info
init|=
operator|(
name|PositionBasedTermVectorMapper
operator|.
name|TVPositionInfo
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"info is null and it shouldn't be"
argument_list|,
name|info
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|int
name|pos
init|=
operator|(
operator|(
name|Integer
operator|)
name|entry
operator|.
name|getKey
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|getPosition
argument_list|()
operator|+
literal|" does not equal: "
operator|+
name|pos
argument_list|,
name|info
operator|.
name|getPosition
argument_list|()
operator|==
name|pos
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"info.getOffsets() is null and it shouldn't be"
argument_list|,
name|info
operator|.
name|getOffsets
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|==
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"info.getTerms() Size: "
operator|+
name|info
operator|.
name|getTerms
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|info
operator|.
name|getTerms
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|//need a test for multiple terms at one pos
name|assertTrue
argument_list|(
literal|"info.getOffsets() Size: "
operator|+
name|info
operator|.
name|getOffsets
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|info
operator|.
name|getOffsets
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"info.getTerms() Size: "
operator|+
name|info
operator|.
name|getTerms
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|info
operator|.
name|getTerms
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|//need a test for multiple terms at one pos
name|assertTrue
argument_list|(
literal|"info.getOffsets() Size: "
operator|+
name|info
operator|.
name|getOffsets
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|info
operator|.
name|getOffsets
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Bits are not all on"
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
operator|==
name|numPositions
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
