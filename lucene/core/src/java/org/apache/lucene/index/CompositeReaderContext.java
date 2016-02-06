begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
begin_comment
comment|/**  * {@link IndexReaderContext} for {@link CompositeReader} instance.  */
end_comment
begin_class
DECL|class|CompositeReaderContext
specifier|public
specifier|final
class|class
name|CompositeReaderContext
extends|extends
name|IndexReaderContext
block|{
DECL|field|children
specifier|private
specifier|final
name|List
argument_list|<
name|IndexReaderContext
argument_list|>
name|children
decl_stmt|;
DECL|field|leaves
specifier|private
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|CompositeReader
name|reader
decl_stmt|;
DECL|method|create
specifier|static
name|CompositeReaderContext
name|create
parameter_list|(
name|CompositeReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|reader
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Creates a {@link CompositeReaderContext} for intermediate readers that aren't    * not top-level readers in the current context    */
DECL|method|CompositeReaderContext
name|CompositeReaderContext
parameter_list|(
name|CompositeReaderContext
name|parent
parameter_list|,
name|CompositeReader
name|reader
parameter_list|,
name|int
name|ordInParent
parameter_list|,
name|int
name|docbaseInParent
parameter_list|,
name|List
argument_list|<
name|IndexReaderContext
argument_list|>
name|children
parameter_list|)
block|{
name|this
argument_list|(
name|parent
argument_list|,
name|reader
argument_list|,
name|ordInParent
argument_list|,
name|docbaseInParent
argument_list|,
name|children
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a {@link CompositeReaderContext} for top-level readers with parent set to<code>null</code>    */
DECL|method|CompositeReaderContext
name|CompositeReaderContext
parameter_list|(
name|CompositeReader
name|reader
parameter_list|,
name|List
argument_list|<
name|IndexReaderContext
argument_list|>
name|children
parameter_list|,
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|reader
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|children
argument_list|,
name|leaves
argument_list|)
expr_stmt|;
block|}
DECL|method|CompositeReaderContext
specifier|private
name|CompositeReaderContext
parameter_list|(
name|CompositeReaderContext
name|parent
parameter_list|,
name|CompositeReader
name|reader
parameter_list|,
name|int
name|ordInParent
parameter_list|,
name|int
name|docbaseInParent
parameter_list|,
name|List
argument_list|<
name|IndexReaderContext
argument_list|>
name|children
parameter_list|,
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|ordInParent
argument_list|,
name|docbaseInParent
argument_list|)
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|children
argument_list|)
expr_stmt|;
name|this
operator|.
name|leaves
operator|=
name|leaves
operator|==
literal|null
condition|?
literal|null
else|:
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|leaves
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|leaves
specifier|public
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
parameter_list|()
throws|throws
name|UnsupportedOperationException
block|{
if|if
condition|(
operator|!
name|isTopLevel
condition|)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This is not a top-level context."
argument_list|)
throw|;
assert|assert
name|leaves
operator|!=
literal|null
assert|;
return|return
name|leaves
return|;
block|}
annotation|@
name|Override
DECL|method|children
specifier|public
name|List
argument_list|<
name|IndexReaderContext
argument_list|>
name|children
parameter_list|()
block|{
return|return
name|children
return|;
block|}
annotation|@
name|Override
DECL|method|reader
specifier|public
name|CompositeReader
name|reader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
DECL|class|Builder
specifier|private
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|reader
specifier|private
specifier|final
name|CompositeReader
name|reader
decl_stmt|;
DECL|field|leaves
specifier|private
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|leafDocBase
specifier|private
name|int
name|leafDocBase
init|=
literal|0
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|CompositeReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
DECL|method|build
specifier|public
name|CompositeReaderContext
name|build
parameter_list|()
block|{
return|return
operator|(
name|CompositeReaderContext
operator|)
name|build
argument_list|(
literal|null
argument_list|,
name|reader
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|build
specifier|private
name|IndexReaderContext
name|build
parameter_list|(
name|CompositeReaderContext
name|parent
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|int
name|ord
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
if|if
condition|(
name|reader
operator|instanceof
name|LeafReader
condition|)
block|{
specifier|final
name|LeafReader
name|ar
init|=
operator|(
name|LeafReader
operator|)
name|reader
decl_stmt|;
specifier|final
name|LeafReaderContext
name|atomic
init|=
operator|new
name|LeafReaderContext
argument_list|(
name|parent
argument_list|,
name|ar
argument_list|,
name|ord
argument_list|,
name|docBase
argument_list|,
name|leaves
operator|.
name|size
argument_list|()
argument_list|,
name|leafDocBase
argument_list|)
decl_stmt|;
name|leaves
operator|.
name|add
argument_list|(
name|atomic
argument_list|)
expr_stmt|;
name|leafDocBase
operator|+=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
return|return
name|atomic
return|;
block|}
else|else
block|{
specifier|final
name|CompositeReader
name|cr
init|=
operator|(
name|CompositeReader
operator|)
name|reader
decl_stmt|;
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|IndexReader
argument_list|>
name|sequentialSubReaders
init|=
name|cr
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|IndexReaderContext
argument_list|>
name|children
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|IndexReaderContext
index|[
name|sequentialSubReaders
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
specifier|final
name|CompositeReaderContext
name|newParent
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|newParent
operator|=
operator|new
name|CompositeReaderContext
argument_list|(
name|cr
argument_list|,
name|children
argument_list|,
name|leaves
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newParent
operator|=
operator|new
name|CompositeReaderContext
argument_list|(
name|parent
argument_list|,
name|cr
argument_list|,
name|ord
argument_list|,
name|docBase
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
name|int
name|newDocBase
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|c
init|=
name|sequentialSubReaders
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|c
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|IndexReader
name|r
init|=
name|sequentialSubReaders
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|children
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|build
argument_list|(
name|newParent
argument_list|,
name|r
argument_list|,
name|i
argument_list|,
name|newDocBase
argument_list|)
argument_list|)
expr_stmt|;
name|newDocBase
operator|+=
name|r
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
assert|assert
name|newDocBase
operator|==
name|cr
operator|.
name|maxDoc
argument_list|()
assert|;
return|return
name|newParent
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
