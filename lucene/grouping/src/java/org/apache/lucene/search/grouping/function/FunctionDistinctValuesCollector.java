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
name|grouping
operator|.
name|AbstractDistinctValuesCollector
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
name|SearchGroup
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
name|*
import|;
end_import
begin_comment
comment|/**  * Function based implementation of {@link org.apache.lucene.search.grouping.AbstractDistinctValuesCollector}.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|FunctionDistinctValuesCollector
specifier|public
class|class
name|FunctionDistinctValuesCollector
extends|extends
name|AbstractDistinctValuesCollector
argument_list|<
name|FunctionDistinctValuesCollector
operator|.
name|GroupCount
argument_list|>
block|{
DECL|field|vsContext
specifier|private
specifier|final
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|vsContext
decl_stmt|;
DECL|field|groupSource
specifier|private
specifier|final
name|ValueSource
name|groupSource
decl_stmt|;
DECL|field|countSource
specifier|private
specifier|final
name|ValueSource
name|countSource
decl_stmt|;
DECL|field|groupMap
specifier|private
specifier|final
name|Map
argument_list|<
name|MutableValue
argument_list|,
name|GroupCount
argument_list|>
name|groupMap
decl_stmt|;
DECL|field|groupFiller
specifier|private
name|FunctionValues
operator|.
name|ValueFiller
name|groupFiller
decl_stmt|;
DECL|field|countFiller
specifier|private
name|FunctionValues
operator|.
name|ValueFiller
name|countFiller
decl_stmt|;
DECL|field|groupMval
specifier|private
name|MutableValue
name|groupMval
decl_stmt|;
DECL|field|countMval
specifier|private
name|MutableValue
name|countMval
decl_stmt|;
DECL|method|FunctionDistinctValuesCollector
specifier|public
name|FunctionDistinctValuesCollector
parameter_list|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|vsContext
parameter_list|,
name|ValueSource
name|groupSource
parameter_list|,
name|ValueSource
name|countSource
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|MutableValue
argument_list|>
argument_list|>
name|groups
parameter_list|)
block|{
name|this
operator|.
name|vsContext
operator|=
name|vsContext
expr_stmt|;
name|this
operator|.
name|groupSource
operator|=
name|groupSource
expr_stmt|;
name|this
operator|.
name|countSource
operator|=
name|countSource
expr_stmt|;
name|groupMap
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|SearchGroup
argument_list|<
name|MutableValue
argument_list|>
name|group
range|:
name|groups
control|)
block|{
name|groupMap
operator|.
name|put
argument_list|(
name|group
operator|.
name|groupValue
argument_list|,
operator|new
name|GroupCount
argument_list|(
name|group
operator|.
name|groupValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getGroups
specifier|public
name|List
argument_list|<
name|GroupCount
argument_list|>
name|getGroups
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|groupMap
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|groupFiller
operator|.
name|fillValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|GroupCount
name|groupCount
init|=
name|groupMap
operator|.
name|get
argument_list|(
name|groupMval
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupCount
operator|!=
literal|null
condition|)
block|{
name|countFiller
operator|.
name|fillValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|groupCount
operator|.
name|uniqueValues
operator|.
name|add
argument_list|(
name|countMval
operator|.
name|duplicate
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|FunctionValues
name|values
init|=
name|groupSource
operator|.
name|getValues
argument_list|(
name|vsContext
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|groupFiller
operator|=
name|values
operator|.
name|getValueFiller
argument_list|()
expr_stmt|;
name|groupMval
operator|=
name|groupFiller
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|values
operator|=
name|countSource
operator|.
name|getValues
argument_list|(
name|vsContext
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|countFiller
operator|=
name|values
operator|.
name|getValueFiller
argument_list|()
expr_stmt|;
name|countMval
operator|=
name|countFiller
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
comment|/** Holds distinct values for a single group.    *    * @lucene.experimental */
DECL|class|GroupCount
specifier|public
specifier|static
class|class
name|GroupCount
extends|extends
name|AbstractDistinctValuesCollector
operator|.
name|GroupCount
argument_list|<
name|MutableValue
argument_list|>
block|{
DECL|method|GroupCount
name|GroupCount
parameter_list|(
name|MutableValue
name|groupValue
parameter_list|)
block|{
name|super
argument_list|(
name|groupValue
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
comment|// TODO, maybe we don't?
block|}
block|}
end_class
end_unit
