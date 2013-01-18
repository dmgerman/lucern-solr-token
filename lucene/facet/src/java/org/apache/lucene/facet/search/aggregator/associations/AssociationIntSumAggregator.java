begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search.aggregator.associations
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|aggregator
operator|.
name|associations
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
operator|.
name|CategoryIntAssociation
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
name|associations
operator|.
name|IntAssociationsIterator
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
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|search
operator|.
name|aggregator
operator|.
name|Aggregator
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
name|util
operator|.
name|IntsRef
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
name|collections
operator|.
name|IntToIntMap
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * An {@link Aggregator} which computes the weight of a category as the sum of  * the integer values associated with it in the result documents.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|AssociationIntSumAggregator
specifier|public
class|class
name|AssociationIntSumAggregator
implements|implements
name|Aggregator
block|{
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|sumArray
specifier|protected
specifier|final
name|int
index|[]
name|sumArray
decl_stmt|;
DECL|field|associations
specifier|protected
specifier|final
name|IntAssociationsIterator
name|associations
decl_stmt|;
DECL|method|AssociationIntSumAggregator
specifier|public
name|AssociationIntSumAggregator
parameter_list|(
name|int
index|[]
name|sumArray
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|CategoryListParams
operator|.
name|DEFAULT_FIELD
argument_list|,
name|sumArray
argument_list|)
expr_stmt|;
block|}
DECL|method|AssociationIntSumAggregator
specifier|public
name|AssociationIntSumAggregator
parameter_list|(
name|String
name|field
parameter_list|,
name|int
index|[]
name|sumArray
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
name|associations
operator|=
operator|new
name|IntAssociationsIterator
argument_list|(
name|field
argument_list|,
operator|new
name|CategoryIntAssociation
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|sumArray
operator|=
name|sumArray
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|aggregate
specifier|public
name|void
name|aggregate
parameter_list|(
name|int
name|docID
parameter_list|,
name|float
name|score
parameter_list|,
name|IntsRef
name|ordinals
parameter_list|)
throws|throws
name|IOException
block|{
name|IntToIntMap
name|values
init|=
name|associations
operator|.
name|getAssociations
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
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
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ord
init|=
name|ordinals
operator|.
name|ints
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|containsKey
argument_list|(
name|ord
argument_list|)
condition|)
block|{
name|sumArray
index|[
name|ord
index|]
operator|+=
name|values
operator|.
name|get
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
operator|==
literal|null
operator|||
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AssociationIntSumAggregator
name|that
init|=
operator|(
name|AssociationIntSumAggregator
operator|)
name|obj
decl_stmt|;
return|return
name|that
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|&&
name|that
operator|.
name|sumArray
operator|==
name|sumArray
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
name|field
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|boolean
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|associations
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
return|;
block|}
block|}
end_class
end_unit
