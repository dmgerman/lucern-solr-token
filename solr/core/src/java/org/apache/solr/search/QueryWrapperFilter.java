begin_unit
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|search
operator|.
name|BoostQuery
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
name|ConstantScoreQuery
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|IndexSearcher
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
name|NumericRangeQuery
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
name|search
operator|.
name|Weight
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
begin_comment
comment|/**   * Constrains search results to only match those which also match a provided  * query.    *  *<p> This could be used, for example, with a {@link NumericRangeQuery} on a suitably  * formatted date field to implement date filtering.  One could re-use a single  * CachingWrapperFilter(QueryWrapperFilter) that matches, e.g., only documents modified   * within the last week.  This would only need to be reconstructed once per day.  */
end_comment
begin_class
DECL|class|QueryWrapperFilter
specifier|public
class|class
name|QueryWrapperFilter
extends|extends
name|Filter
block|{
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
comment|/** Constructs a filter which only matches documents matching    *<code>query</code>.    */
DECL|method|QueryWrapperFilter
specifier|public
name|QueryWrapperFilter
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
name|query
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Query may not be null"
argument_list|)
throw|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
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
return|return
operator|new
name|BoostQuery
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|query
argument_list|)
argument_list|,
literal|0f
argument_list|)
return|;
block|}
comment|/** returns the inner Query */
DECL|method|getQuery
specifier|public
specifier|final
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// get a private context that is used to rewrite, createWeight and score eventually
specifier|final
name|LeafReaderContext
name|privateContext
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getContext
argument_list|()
decl_stmt|;
specifier|final
name|Weight
name|weight
init|=
operator|new
name|IndexSearcher
argument_list|(
name|privateContext
argument_list|)
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DocIdSet
name|set
init|=
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|scorer
argument_list|(
name|privateContext
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
block|}
decl_stmt|;
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
name|set
argument_list|,
name|acceptDocs
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
return|return
literal|"QueryWrapperFilter("
operator|+
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|QueryWrapperFilter
operator|)
name|o
operator|)
operator|.
name|query
argument_list|)
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
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|query
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit