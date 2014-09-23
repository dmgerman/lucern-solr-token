begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|index
operator|.
name|LeafReader
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
name|DocsEnum
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
name|FixedBitSet
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
comment|/**  * Base class for Lucene Filters on SpatialPrefixTree fields.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|AbstractPrefixTreeFilter
specifier|public
specifier|abstract
class|class
name|AbstractPrefixTreeFilter
extends|extends
name|Filter
block|{
DECL|field|queryShape
specifier|protected
specifier|final
name|Shape
name|queryShape
decl_stmt|;
DECL|field|fieldName
specifier|protected
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|grid
specifier|protected
specifier|final
name|SpatialPrefixTree
name|grid
decl_stmt|;
comment|//not in equals/hashCode since it's implied for a specific field
DECL|field|detailLevel
specifier|protected
specifier|final
name|int
name|detailLevel
decl_stmt|;
DECL|method|AbstractPrefixTreeFilter
specifier|public
name|AbstractPrefixTreeFilter
parameter_list|(
name|Shape
name|queryShape
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SpatialPrefixTree
name|grid
parameter_list|,
name|int
name|detailLevel
parameter_list|)
block|{
name|this
operator|.
name|queryShape
operator|=
name|queryShape
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|grid
operator|=
name|grid
expr_stmt|;
name|this
operator|.
name|detailLevel
operator|=
name|detailLevel
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
operator|!
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
name|AbstractPrefixTreeFilter
name|that
init|=
operator|(
name|AbstractPrefixTreeFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|detailLevel
operator|!=
name|that
operator|.
name|detailLevel
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fieldName
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|queryShape
operator|.
name|equals
argument_list|(
name|that
operator|.
name|queryShape
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
name|queryShape
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|fieldName
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|detailLevel
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Holds transient state and docid collecting utility methods as part of    * traversing a {@link TermsEnum}. */
DECL|class|BaseTermsEnumTraverser
specifier|public
specifier|abstract
class|class
name|BaseTermsEnumTraverser
block|{
DECL|field|context
specifier|protected
specifier|final
name|LeafReaderContext
name|context
decl_stmt|;
DECL|field|acceptDocs
specifier|protected
name|Bits
name|acceptDocs
decl_stmt|;
DECL|field|maxDoc
specifier|protected
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|termsEnum
specifier|protected
name|TermsEnum
name|termsEnum
decl_stmt|;
comment|//remember to check for null in getDocIdSet
DECL|field|docsEnum
specifier|protected
name|DocsEnum
name|docsEnum
decl_stmt|;
DECL|method|BaseTermsEnumTraverser
specifier|public
name|BaseTermsEnumTraverser
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|acceptDocs
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|Terms
name|terms
init|=
name|reader
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
name|this
operator|.
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|collectDocs
specifier|protected
name|void
name|collectDocs
parameter_list|(
name|FixedBitSet
name|bitSet
parameter_list|)
throws|throws
name|IOException
block|{
comment|//WARN: keep this specialization in sync
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|docsEnum
argument_list|,
name|DocsEnum
operator|.
name|FLAG_NONE
argument_list|)
expr_stmt|;
name|int
name|docid
decl_stmt|;
while|while
condition|(
operator|(
name|docid
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|docid
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* Eventually uncomment when needed.      protected void collectDocs(Collector collector) throws IOException {       //WARN: keep this specialization in sync       assert termsEnum != null;       docsEnum = termsEnum.docs(acceptDocs, docsEnum, DocsEnum.FLAG_NONE);       int docid;       while ((docid = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {         collector.collect(docid);       }     }      public abstract class Collector {       abstract void collect(int docid) throws IOException;     }     */
block|}
block|}
end_class
end_unit
