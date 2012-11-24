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
name|util
operator|.
name|BytesRef
import|;
end_import
begin_comment
comment|// nocommit need marker interface?
end_comment
begin_class
DECL|class|BinaryDocValues
specifier|public
specifier|abstract
class|class
name|BinaryDocValues
block|{
comment|// nocommit throws IOE or not?
DECL|method|get
specifier|public
specifier|abstract
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
function_decl|;
DECL|field|MISSING
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|MISSING
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
DECL|method|size
specifier|public
specifier|abstract
name|int
name|size
parameter_list|()
function_decl|;
DECL|method|isFixedLength
specifier|public
specifier|abstract
name|boolean
name|isFixedLength
parameter_list|()
function_decl|;
DECL|method|maxLength
specifier|public
specifier|abstract
name|int
name|maxLength
parameter_list|()
function_decl|;
comment|// nocommit: rethink this api? alternative is boolean on atomicreader...?
comment|// doc that the thing returned here must be thread safe...
DECL|method|newRAMInstance
specifier|public
name|BinaryDocValues
name|newRAMInstance
parameter_list|()
block|{
comment|// TODO: optimize this default impl with e.g. isFixedLength/maxLength and so on
comment|// nocommit used packed ints/pagedbytes and so on
specifier|final
name|int
name|maxDoc
init|=
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxLength
init|=
name|maxLength
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|fixedLength
init|=
name|isFixedLength
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
index|[]
name|values
init|=
operator|new
name|byte
index|[
name|maxDoc
index|]
index|[]
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
name|get
argument_list|(
name|docID
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|values
index|[
name|docID
index|]
operator|=
operator|new
name|byte
index|[
name|scratch
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|,
name|scratch
operator|.
name|offset
argument_list|,
name|values
index|[
name|docID
index|]
argument_list|,
literal|0
argument_list|,
name|scratch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|result
operator|.
name|bytes
operator|=
name|values
index|[
name|docID
index|]
expr_stmt|;
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|result
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFixedLength
parameter_list|()
block|{
return|return
name|fixedLength
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|maxLength
parameter_list|()
block|{
return|return
name|maxLength
return|;
block|}
annotation|@
name|Override
specifier|public
name|BinaryDocValues
name|newRAMInstance
parameter_list|()
block|{
comment|// nocommit: ugly, maybe throw exception instead?
return|return
name|this
return|;
block|}
block|}
return|;
block|}
DECL|class|EMPTY
specifier|public
specifier|static
class|class
name|EMPTY
extends|extends
name|BinaryDocValues
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|EMPTY
specifier|public
name|EMPTY
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|result
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|isFixedLength
specifier|public
name|boolean
name|isFixedLength
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|maxLength
specifier|public
name|int
name|maxLength
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
empty_stmt|;
block|}
end_class
end_unit
