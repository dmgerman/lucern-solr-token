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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|BufferedIndexInput
import|;
end_import
begin_class
DECL|class|MockIndexInput
specifier|public
class|class
name|MockIndexInput
extends|extends
name|BufferedIndexInput
block|{
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|pointer
specifier|private
name|int
name|pointer
init|=
literal|0
decl_stmt|;
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|method|MockIndexInput
specifier|public
name|MockIndexInput
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|buffer
operator|=
name|bytes
expr_stmt|;
name|length
operator|=
name|bytes
operator|.
name|length
expr_stmt|;
block|}
DECL|method|readInternal
specifier|protected
name|void
name|readInternal
parameter_list|(
name|byte
index|[]
name|dest
parameter_list|,
name|int
name|destOffset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|remainder
init|=
name|len
decl_stmt|;
name|int
name|start
init|=
name|pointer
decl_stmt|;
while|while
condition|(
name|remainder
operator|!=
literal|0
condition|)
block|{
comment|//          int bufferNumber = start / buffer.length;
name|int
name|bufferOffset
init|=
name|start
operator|%
name|buffer
operator|.
name|length
decl_stmt|;
name|int
name|bytesInBuffer
init|=
name|buffer
operator|.
name|length
operator|-
name|bufferOffset
decl_stmt|;
name|int
name|bytesToCopy
init|=
name|bytesInBuffer
operator|>=
name|remainder
condition|?
name|remainder
else|:
name|bytesInBuffer
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bufferOffset
argument_list|,
name|dest
argument_list|,
name|destOffset
argument_list|,
name|bytesToCopy
argument_list|)
expr_stmt|;
name|destOffset
operator|+=
name|bytesToCopy
expr_stmt|;
name|start
operator|+=
name|bytesToCopy
expr_stmt|;
name|remainder
operator|-=
name|bytesToCopy
expr_stmt|;
block|}
name|pointer
operator|+=
name|len
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// ignore
block|}
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
name|pointer
operator|=
operator|(
name|int
operator|)
name|pos
expr_stmt|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
block|}
end_class
end_unit
