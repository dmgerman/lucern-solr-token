begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|FastOutputStream
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
name|LinkedList
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
comment|/** @lucene.internal */
end_comment
begin_class
DECL|class|MemOutputStream
specifier|public
class|class
name|MemOutputStream
extends|extends
name|FastOutputStream
block|{
DECL|field|buffers
specifier|public
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|buffers
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|MemOutputStream
specifier|public
name|MemOutputStream
parameter_list|(
name|byte
index|[]
name|tempBuffer
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|tempBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|byte
index|[]
name|arr
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|arr
operator|==
name|buf
operator|&&
name|offset
operator|==
literal|0
operator|&&
name|len
operator|==
name|buf
operator|.
name|length
condition|)
block|{
name|buffers
operator|.
name|add
argument_list|(
name|buf
argument_list|)
expr_stmt|;
comment|// steal the buffer
name|buf
operator|=
operator|new
name|byte
index|[
literal|8192
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|newBuf
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
name|offset
argument_list|,
name|newBuf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|buffers
operator|.
name|add
argument_list|(
name|newBuf
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeAll
specifier|public
name|void
name|writeAll
parameter_list|(
name|FastOutputStream
name|fos
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|byte
index|[]
name|buffer
range|:
name|buffers
control|)
block|{
name|fos
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|fos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
