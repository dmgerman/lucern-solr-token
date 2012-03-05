begin_unit
begin_package
DECL|package|org.apache.lucene.search.grouping.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
operator|.
name|function
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|AtomicReaderContext
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
name|search
operator|.
name|FieldComparator
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
name|Scorer
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
name|Sort
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
name|SortField
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
name|grouping
operator|.
name|AbstractAllGroupHeadsCollector
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
name|mutable
operator|.
name|MutableValue
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
name|Collection
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
begin_comment
comment|/**  * An implementation of {@link AbstractAllGroupHeadsCollector} for retrieving the most relevant groups when grouping  * by {@link ValueSource}.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|FunctionAllGroupHeadsCollector
specifier|public
class|class
name|FunctionAllGroupHeadsCollector
extends|extends
name|AbstractAllGroupHeadsCollector
argument_list|<
name|FunctionAllGroupHeadsCollector
operator|.
name|GroupHead
argument_list|>
block|{
DECL|field|groupBy
specifier|private
specifier|final
name|ValueSource
name|groupBy
decl_stmt|;
DECL|field|vsContext
specifier|private
specifier|final
name|Map
name|vsContext
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|Map
argument_list|<
name|MutableValue
argument_list|,
name|GroupHead
argument_list|>
name|groups
decl_stmt|;
DECL|field|sortWithinGroup
specifier|private
specifier|final
name|Sort
name|sortWithinGroup
decl_stmt|;
DECL|field|filler
specifier|private
name|FunctionValues
operator|.
name|ValueFiller
name|filler
decl_stmt|;
DECL|field|mval
specifier|private
name|MutableValue
name|mval
decl_stmt|;
DECL|field|readerContext
specifier|private
name|AtomicReaderContext
name|readerContext
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
comment|/**    * Constructs a {@link FunctionAllGroupHeadsCollector} instance.    *    * @param groupBy The {@link ValueSource} to group by    * @param vsContext The ValueSource context    * @param sortWithinGroup The sort within a group    */
DECL|method|FunctionAllGroupHeadsCollector
specifier|public
name|FunctionAllGroupHeadsCollector
parameter_list|(
name|ValueSource
name|groupBy
parameter_list|,
name|Map
name|vsContext
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|)
block|{
name|super
argument_list|(
name|sortWithinGroup
operator|.
name|getSort
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|groups
operator|=
operator|new
name|HashMap
argument_list|<
name|MutableValue
argument_list|,
name|GroupHead
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|sortWithinGroup
operator|=
name|sortWithinGroup
expr_stmt|;
name|this
operator|.
name|groupBy
operator|=
name|groupBy
expr_stmt|;
name|this
operator|.
name|vsContext
operator|=
name|vsContext
expr_stmt|;
specifier|final
name|SortField
index|[]
name|sortFields
init|=
name|sortWithinGroup
operator|.
name|getSort
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
name|sortFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|reversed
index|[
name|i
index|]
operator|=
name|sortFields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|1
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|retrieveGroupHeadAndAddIfNotExist
specifier|protected
name|void
name|retrieveGroupHeadAndAddIfNotExist
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|filler
operator|.
name|fillValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|GroupHead
name|groupHead
init|=
name|groups
operator|.
name|get
argument_list|(
name|mval
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupHead
operator|==
literal|null
condition|)
block|{
name|MutableValue
name|groupValue
init|=
name|mval
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|groupHead
operator|=
operator|new
name|GroupHead
argument_list|(
name|groupValue
argument_list|,
name|sortWithinGroup
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|groups
operator|.
name|put
argument_list|(
name|groupValue
argument_list|,
name|groupHead
argument_list|)
expr_stmt|;
name|temporalResult
operator|.
name|stop
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|temporalResult
operator|.
name|stop
operator|=
literal|false
expr_stmt|;
block|}
name|this
operator|.
name|temporalResult
operator|.
name|groupHead
operator|=
name|groupHead
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|getCollectedGroupHeads
specifier|protected
name|Collection
argument_list|<
name|GroupHead
argument_list|>
name|getCollectedGroupHeads
parameter_list|()
block|{
return|return
name|groups
operator|.
name|values
argument_list|()
return|;
block|}
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
for|for
control|(
name|GroupHead
name|groupHead
range|:
name|groups
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|comparator
range|:
name|groupHead
operator|.
name|comparators
control|)
block|{
name|comparator
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|readerContext
operator|=
name|context
expr_stmt|;
name|FunctionValues
name|docValues
init|=
name|groupBy
operator|.
name|getValues
argument_list|(
name|vsContext
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|filler
operator|=
name|docValues
operator|.
name|getValueFiller
argument_list|()
expr_stmt|;
name|mval
operator|=
name|filler
operator|.
name|getValue
argument_list|()
expr_stmt|;
for|for
control|(
name|GroupHead
name|groupHead
range|:
name|groups
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groupHead
operator|.
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|groupHead
operator|.
name|comparators
index|[
name|i
index|]
operator|=
name|groupHead
operator|.
name|comparators
index|[
name|i
index|]
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|GroupHead
class|class
name|GroupHead
extends|extends
name|AbstractAllGroupHeadsCollector
operator|.
name|GroupHead
argument_list|<
name|MutableValue
argument_list|>
block|{
DECL|field|comparators
specifier|final
name|FieldComparator
argument_list|<
name|?
argument_list|>
index|[]
name|comparators
decl_stmt|;
DECL|method|GroupHead
specifier|private
name|GroupHead
parameter_list|(
name|MutableValue
name|groupValue
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupValue
argument_list|,
name|doc
operator|+
name|readerContext
operator|.
name|docBase
argument_list|)
expr_stmt|;
specifier|final
name|SortField
index|[]
name|sortFields
init|=
name|sort
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|comparators
operator|=
operator|new
name|FieldComparator
index|[
name|sortFields
operator|.
name|length
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
name|sortFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparators
index|[
name|i
index|]
operator|=
name|sortFields
index|[
name|i
index|]
operator|.
name|getComparator
argument_list|(
literal|1
argument_list|,
name|i
argument_list|)
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|copy
argument_list|(
literal|0
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|setBottom
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|compIDX
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|comparators
index|[
name|compIDX
index|]
operator|.
name|compareBottom
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|updateDocHead
specifier|public
name|void
name|updateDocHead
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|comparator
range|:
name|comparators
control|)
block|{
name|comparator
operator|.
name|copy
argument_list|(
literal|0
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|comparator
operator|.
name|setBottom
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|doc
operator|=
name|doc
operator|+
name|readerContext
operator|.
name|docBase
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
