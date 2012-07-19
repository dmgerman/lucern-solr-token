begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
package|;
end_package
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
name|*
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
name|util
operator|.
name|BytesRef
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
name|WeakHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import
begin_comment
comment|/**  * @lucene.internal  */
end_comment
begin_class
DECL|class|ShapeFieldCacheProvider
specifier|public
specifier|abstract
class|class
name|ShapeFieldCacheProvider
parameter_list|<
name|T
extends|extends
name|Shape
parameter_list|>
block|{
DECL|field|log
specifier|private
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// it may be a List<T> or T
DECL|field|sidx
name|WeakHashMap
argument_list|<
name|IndexReader
argument_list|,
name|ShapeFieldCache
argument_list|<
name|T
argument_list|>
argument_list|>
name|sidx
init|=
operator|new
name|WeakHashMap
argument_list|<
name|IndexReader
argument_list|,
name|ShapeFieldCache
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|defaultSize
specifier|protected
specifier|final
name|int
name|defaultSize
decl_stmt|;
DECL|field|shapeField
specifier|protected
specifier|final
name|String
name|shapeField
decl_stmt|;
DECL|method|ShapeFieldCacheProvider
specifier|public
name|ShapeFieldCacheProvider
parameter_list|(
name|String
name|shapeField
parameter_list|,
name|int
name|defaultSize
parameter_list|)
block|{
name|this
operator|.
name|shapeField
operator|=
name|shapeField
expr_stmt|;
name|this
operator|.
name|defaultSize
operator|=
name|defaultSize
expr_stmt|;
block|}
DECL|method|readShape
specifier|protected
specifier|abstract
name|T
name|readShape
parameter_list|(
name|BytesRef
name|term
parameter_list|)
function_decl|;
DECL|method|getCache
specifier|public
specifier|synchronized
name|ShapeFieldCache
argument_list|<
name|T
argument_list|>
name|getCache
parameter_list|(
name|AtomicReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|ShapeFieldCache
argument_list|<
name|T
argument_list|>
name|idx
init|=
name|sidx
operator|.
name|get
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|!=
literal|null
condition|)
block|{
return|return
name|idx
return|;
block|}
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|log
operator|.
name|fine
argument_list|(
literal|"Building Cache ["
operator|+
name|reader
operator|.
name|maxDoc
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|idx
operator|=
operator|new
name|ShapeFieldCache
argument_list|<
name|T
argument_list|>
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|defaultSize
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|shapeField
argument_list|)
decl_stmt|;
name|TermsEnum
name|te
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|te
operator|=
name|terms
operator|.
name|iterator
argument_list|(
name|te
argument_list|)
expr_stmt|;
name|BytesRef
name|term
init|=
name|te
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
name|T
name|shape
init|=
name|readShape
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|!=
literal|null
condition|)
block|{
name|docs
operator|=
name|te
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Integer
name|docid
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
while|while
condition|(
name|docid
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|idx
operator|.
name|add
argument_list|(
name|docid
argument_list|,
name|shape
argument_list|)
expr_stmt|;
name|docid
operator|=
name|docs
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
name|term
operator|=
name|te
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
name|sidx
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|long
name|elapsed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|log
operator|.
name|fine
argument_list|(
literal|"Cached: ["
operator|+
name|count
operator|+
literal|" in "
operator|+
name|elapsed
operator|+
literal|"ms] "
operator|+
name|idx
argument_list|)
expr_stmt|;
return|return
name|idx
return|;
block|}
block|}
end_class
end_unit
