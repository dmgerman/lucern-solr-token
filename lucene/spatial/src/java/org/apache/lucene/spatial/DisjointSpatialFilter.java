begin_unit
begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
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
name|queries
operator|.
name|ChainedFilter
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
name|BitsFilteredDocIdSet
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
name|Filter
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
name|util
operator|.
name|Bits
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
begin_comment
comment|/**  * A Spatial Filter implementing {@link SpatialOperation#IsDisjointTo} in terms  * of a {@link SpatialStrategy}'s support for {@link SpatialOperation#Intersects}.  * A document is considered disjoint if it has spatial data that does not  * intersect with the query shape.  Another way of looking at this is that it's  * a way to invert a query shape.  *  * @lucene.experimental  *  * @deprecated See https://issues.apache.org/jira/browse/LUCENE-5692  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|DisjointSpatialFilter
specifier|public
class|class
name|DisjointSpatialFilter
extends|extends
name|Filter
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
comment|//maybe null
DECL|field|intersectsFilter
specifier|private
specifier|final
name|Filter
name|intersectsFilter
decl_stmt|;
comment|/**    *    * @param strategy Needed to compute intersects    * @param args Used in spatial intersection    * @param field This field is used to determine which docs have spatial data via    *               {@link AtomicReader#getDocsWithField(String)}.    *              Passing null will assume all docs have spatial data.    */
DECL|method|DisjointSpatialFilter
specifier|public
name|DisjointSpatialFilter
parameter_list|(
name|SpatialStrategy
name|strategy
parameter_list|,
name|SpatialArgs
name|args
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
comment|// TODO consider making SpatialArgs cloneable
name|SpatialOperation
name|origOp
init|=
name|args
operator|.
name|getOperation
argument_list|()
decl_stmt|;
comment|//copy so we can restore
name|args
operator|.
name|setOperation
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|)
expr_stmt|;
comment|//temporarily set to intersects
name|intersectsFilter
operator|=
name|strategy
operator|.
name|makeFilter
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|args
operator|.
name|setOperation
argument_list|(
name|origOp
argument_list|)
expr_stmt|;
comment|//restore so it looks like it was
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|DisjointSpatialFilter
name|that
init|=
operator|(
name|DisjointSpatialFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|?
operator|!
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
argument_list|)
else|:
name|that
operator|.
name|field
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|intersectsFilter
operator|.
name|equals
argument_list|(
name|that
operator|.
name|intersectsFilter
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
name|int
name|result
init|=
name|field
operator|!=
literal|null
condition|?
name|field
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|intersectsFilter
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Bits
name|docsWithField
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|docsWithField
operator|=
literal|null
expr_stmt|;
comment|//all docs
block|}
else|else
block|{
comment|//NOTE By using the FieldCache we re-use a cache
comment|// which is nice but loading it in this way might be slower than say using an
comment|// intersects filter against the world bounds. So do we add a method to the
comment|// strategy, perhaps?  But the strategy can't cache it.
name|docsWithField
operator|=
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docsWithField
operator|.
name|length
argument_list|()
operator|!=
name|maxDoc
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Bits length should be maxDoc ("
operator|+
name|maxDoc
operator|+
literal|") but wasn't: "
operator|+
name|docsWithField
argument_list|)
throw|;
if|if
condition|(
name|docsWithField
operator|instanceof
name|Bits
operator|.
name|MatchNoBits
condition|)
block|{
return|return
literal|null
return|;
comment|//match nothing
block|}
elseif|else
if|if
condition|(
name|docsWithField
operator|instanceof
name|Bits
operator|.
name|MatchAllBits
condition|)
block|{
name|docsWithField
operator|=
literal|null
expr_stmt|;
comment|//all docs
block|}
block|}
comment|//not so much a chain but a way to conveniently invert the Filter
name|DocIdSet
name|docIdSet
init|=
operator|new
name|ChainedFilter
argument_list|(
operator|new
name|Filter
index|[]
block|{
name|intersectsFilter
block|}
argument_list|,
name|ChainedFilter
operator|.
name|ANDNOT
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
name|docIdSet
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
block|}
end_class
end_unit
