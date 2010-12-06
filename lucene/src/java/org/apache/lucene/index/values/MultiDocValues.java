begin_unit
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|AttributeSource
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
name|ReaderUtil
import|;
end_import
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|MultiDocValues
specifier|public
class|class
name|MultiDocValues
extends|extends
name|DocValues
block|{
DECL|class|DocValuesIndex
specifier|public
specifier|static
class|class
name|DocValuesIndex
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|final
specifier|static
name|DocValuesIndex
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|DocValuesIndex
index|[
literal|0
index|]
decl_stmt|;
DECL|field|start
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|length
specifier|final
name|int
name|length
decl_stmt|;
DECL|field|docValues
specifier|final
name|DocValues
name|docValues
decl_stmt|;
DECL|method|DocValuesIndex
specifier|public
name|DocValuesIndex
parameter_list|(
name|DocValues
name|docValues
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|docValues
operator|=
name|docValues
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
block|}
DECL|field|docValuesIdx
specifier|private
name|DocValuesIndex
index|[]
name|docValuesIdx
decl_stmt|;
DECL|field|starts
specifier|private
name|int
index|[]
name|starts
decl_stmt|;
DECL|method|MultiDocValues
specifier|public
name|MultiDocValues
parameter_list|()
block|{
name|starts
operator|=
operator|new
name|int
index|[
literal|0
index|]
expr_stmt|;
name|docValuesIdx
operator|=
operator|new
name|DocValuesIndex
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|MultiDocValues
specifier|public
name|MultiDocValues
parameter_list|(
name|DocValuesIndex
index|[]
name|docValuesIdx
parameter_list|)
block|{
name|reset
argument_list|(
name|docValuesIdx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|DocValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiValuesEnum
argument_list|(
name|docValuesIdx
argument_list|,
name|starts
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiSource
argument_list|(
name|docValuesIdx
argument_list|,
name|starts
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|DocValues
name|reset
parameter_list|(
name|DocValuesIndex
index|[]
name|docValuesIdx
parameter_list|)
block|{
name|int
index|[]
name|start
init|=
operator|new
name|int
index|[
name|docValuesIdx
operator|.
name|length
index|]
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
name|docValuesIdx
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|start
index|[
name|i
index|]
operator|=
name|docValuesIdx
index|[
name|i
index|]
operator|.
name|start
expr_stmt|;
block|}
name|this
operator|.
name|starts
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|docValuesIdx
operator|=
name|docValuesIdx
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|class|DummyDocValues
specifier|public
specifier|static
class|class
name|DummyDocValues
extends|extends
name|DocValues
block|{
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|emptySoruce
specifier|final
name|Source
name|emptySoruce
decl_stmt|;
DECL|method|DummyDocValues
specifier|public
name|DummyDocValues
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|emptySoruce
operator|=
operator|new
name|EmptySource
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|DocValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|emptySoruce
operator|.
name|getEnum
argument_list|(
name|attrSource
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|emptySoruce
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|emptySoruce
operator|.
name|type
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|MultiValuesEnum
specifier|private
specifier|static
class|class
name|MultiValuesEnum
extends|extends
name|DocValuesEnum
block|{
DECL|field|docValuesIdx
specifier|private
name|DocValuesIndex
index|[]
name|docValuesIdx
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|currentStart
specifier|private
name|int
name|currentStart
decl_stmt|;
DECL|field|currentMax
specifier|private
name|int
name|currentMax
decl_stmt|;
DECL|field|currentDoc
specifier|private
name|int
name|currentDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentEnum
specifier|private
name|DocValuesEnum
name|currentEnum
decl_stmt|;
DECL|field|starts
specifier|private
specifier|final
name|int
index|[]
name|starts
decl_stmt|;
DECL|method|MultiValuesEnum
specifier|public
name|MultiValuesEnum
parameter_list|(
name|DocValuesIndex
index|[]
name|docValuesIdx
parameter_list|,
name|int
index|[]
name|starts
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|docValuesIdx
index|[
literal|0
index|]
operator|.
name|docValues
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|docValuesIdx
operator|=
name|docValuesIdx
expr_stmt|;
specifier|final
name|DocValuesIndex
name|last
init|=
name|docValuesIdx
index|[
name|docValuesIdx
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|maxDoc
operator|=
name|last
operator|.
name|start
operator|+
name|last
operator|.
name|length
expr_stmt|;
specifier|final
name|DocValuesIndex
name|idx
init|=
name|docValuesIdx
index|[
literal|0
index|]
decl_stmt|;
name|currentEnum
operator|=
name|idx
operator|.
name|docValues
operator|.
name|getEnum
argument_list|(
name|this
operator|.
name|attributes
argument_list|()
argument_list|)
expr_stmt|;
name|currentEnum
operator|.
name|copyReferences
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|intsRef
operator|=
name|currentEnum
operator|.
name|intsRef
expr_stmt|;
name|currentMax
operator|=
name|idx
operator|.
name|length
expr_stmt|;
name|currentStart
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|starts
operator|=
name|starts
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|currentEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|target
operator|>
name|currentDoc
operator|:
literal|"target "
operator|+
name|target
operator|+
literal|" must be> than the current doc "
operator|+
name|currentDoc
assert|;
name|int
name|relativeDoc
init|=
name|target
operator|-
name|currentStart
decl_stmt|;
do|do
block|{
if|if
condition|(
name|target
operator|>=
name|maxDoc
condition|)
comment|// we are beyond max doc
return|return
name|currentDoc
operator|=
name|NO_MORE_DOCS
return|;
if|if
condition|(
name|target
operator|>=
name|currentMax
condition|)
block|{
specifier|final
name|int
name|idx
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|target
argument_list|,
name|starts
argument_list|)
decl_stmt|;
name|currentEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|currentEnum
operator|=
name|docValuesIdx
index|[
name|idx
index|]
operator|.
name|docValues
operator|.
name|getEnum
argument_list|(
name|this
operator|.
name|attributes
argument_list|()
argument_list|)
expr_stmt|;
name|currentEnum
operator|.
name|copyReferences
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|currentStart
operator|=
name|docValuesIdx
index|[
name|idx
index|]
operator|.
name|start
expr_stmt|;
name|currentMax
operator|=
name|currentStart
operator|+
name|docValuesIdx
index|[
name|idx
index|]
operator|.
name|length
expr_stmt|;
name|relativeDoc
operator|=
name|target
operator|-
name|currentStart
expr_stmt|;
block|}
else|else
block|{
return|return
name|currentDoc
operator|=
name|currentStart
operator|+
name|currentEnum
operator|.
name|advance
argument_list|(
name|relativeDoc
argument_list|)
return|;
block|}
block|}
do|while
condition|(
operator|(
name|relativeDoc
operator|=
name|currentEnum
operator|.
name|advance
argument_list|(
name|relativeDoc
argument_list|)
operator|)
operator|==
name|NO_MORE_DOCS
condition|)
do|;
return|return
name|currentDoc
operator|=
name|currentStart
operator|+
name|relativeDoc
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|currentDoc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
name|currentDoc
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
DECL|class|MultiSource
specifier|private
specifier|static
class|class
name|MultiSource
extends|extends
name|Source
block|{
DECL|field|numDocs
specifier|private
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
init|=
literal|0
decl_stmt|;
DECL|field|current
specifier|private
name|Source
name|current
decl_stmt|;
DECL|field|starts
specifier|private
specifier|final
name|int
index|[]
name|starts
decl_stmt|;
DECL|field|docValuesIdx
specifier|private
specifier|final
name|DocValuesIndex
index|[]
name|docValuesIdx
decl_stmt|;
DECL|method|MultiSource
specifier|public
name|MultiSource
parameter_list|(
name|DocValuesIndex
index|[]
name|docValuesIdx
parameter_list|,
name|int
index|[]
name|starts
parameter_list|)
block|{
name|this
operator|.
name|docValuesIdx
operator|=
name|docValuesIdx
expr_stmt|;
name|this
operator|.
name|starts
operator|=
name|starts
expr_stmt|;
assert|assert
name|docValuesIdx
operator|.
name|length
operator|!=
literal|0
assert|;
block|}
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
specifier|final
name|int
name|doc
init|=
name|ensureSource
argument_list|(
name|docID
argument_list|)
decl_stmt|;
return|return
name|current
operator|.
name|getInt
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|ensureSource
specifier|private
specifier|final
name|int
name|ensureSource
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|int
name|n
init|=
name|docID
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|n
operator|>=
name|numDocs
condition|)
block|{
specifier|final
name|int
name|idx
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docID
argument_list|,
name|starts
argument_list|)
decl_stmt|;
assert|assert
name|idx
operator|>=
literal|0
operator|&&
name|idx
operator|<
name|docValuesIdx
operator|.
name|length
operator|:
literal|"idx was "
operator|+
name|idx
operator|+
literal|" for doc id: "
operator|+
name|docID
operator|+
literal|" slices : "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|starts
argument_list|)
assert|;
assert|assert
name|docValuesIdx
index|[
name|idx
index|]
operator|!=
literal|null
assert|;
try|try
block|{
name|current
operator|=
name|docValuesIdx
index|[
name|idx
index|]
operator|.
name|docValues
operator|.
name|getSource
argument_list|()
expr_stmt|;
name|missingValue
operator|.
name|copy
argument_list|(
name|current
operator|.
name|getMissing
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"load failed"
argument_list|,
name|e
argument_list|)
throw|;
comment|// TODO how should we
comment|// handle this
block|}
name|start
operator|=
name|docValuesIdx
index|[
name|idx
index|]
operator|.
name|start
expr_stmt|;
name|numDocs
operator|=
name|docValuesIdx
index|[
name|idx
index|]
operator|.
name|length
expr_stmt|;
name|n
operator|=
name|docID
operator|-
name|start
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
specifier|final
name|int
name|doc
init|=
name|ensureSource
argument_list|(
name|docID
argument_list|)
decl_stmt|;
return|return
name|current
operator|.
name|getFloat
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
specifier|final
name|int
name|doc
init|=
name|ensureSource
argument_list|(
name|docID
argument_list|)
decl_stmt|;
return|return
name|current
operator|.
name|getBytes
argument_list|(
name|doc
argument_list|,
name|bytesRef
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|DocValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|docValuesIdx
index|[
literal|0
index|]
operator|.
name|docValues
operator|.
name|type
argument_list|()
return|;
block|}
block|}
DECL|class|EmptySource
specifier|private
specifier|static
class|class
name|EmptySource
extends|extends
name|Source
block|{
DECL|field|type
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
DECL|method|EmptySource
specifier|public
name|EmptySource
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
return|return
name|this
operator|.
name|missingValue
operator|.
name|bytesValue
return|;
block|}
annotation|@
name|Override
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|missingValue
operator|.
name|doubleValue
return|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|missingValue
operator|.
name|longValue
return|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|DocValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|DocValuesEnum
operator|.
name|emptyEnum
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|docValuesIdx
index|[
literal|0
index|]
operator|.
name|docValues
operator|.
name|type
argument_list|()
return|;
block|}
block|}
end_class
end_unit
