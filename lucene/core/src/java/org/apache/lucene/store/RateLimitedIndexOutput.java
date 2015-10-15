begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
begin_comment
comment|/**  * A {@link RateLimiter rate limiting} {@link IndexOutput}  *   * @lucene.internal  */
end_comment
begin_class
DECL|class|RateLimitedIndexOutput
specifier|public
specifier|final
class|class
name|RateLimitedIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|delegate
specifier|private
specifier|final
name|IndexOutput
name|delegate
decl_stmt|;
DECL|field|rateLimiter
specifier|private
specifier|final
name|RateLimiter
name|rateLimiter
decl_stmt|;
comment|/** How many bytes we've written since we last called rateLimiter.pause. */
DECL|field|bytesSinceLastPause
specifier|private
name|long
name|bytesSinceLastPause
decl_stmt|;
comment|/** Cached here not not always have to call RateLimiter#getMinPauseCheckBytes()    * which does volatile read. */
DECL|field|currentMinPauseCheckBytes
specifier|private
name|long
name|currentMinPauseCheckBytes
decl_stmt|;
DECL|method|RateLimitedIndexOutput
specifier|public
name|RateLimitedIndexOutput
parameter_list|(
specifier|final
name|RateLimiter
name|rateLimiter
parameter_list|,
specifier|final
name|IndexOutput
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
literal|"RateLimitedIndexOutput("
operator|+
name|delegate
operator|+
literal|")"
argument_list|,
name|delegate
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|rateLimiter
operator|=
name|rateLimiter
expr_stmt|;
name|this
operator|.
name|currentMinPauseCheckBytes
operator|=
name|rateLimiter
operator|.
name|getMinPauseCheckBytes
argument_list|()
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
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChecksum
specifier|public
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getChecksum
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|bytesSinceLastPause
operator|++
expr_stmt|;
name|checkRate
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|bytesSinceLastPause
operator|+=
name|length
expr_stmt|;
name|checkRate
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|checkRate
specifier|private
name|void
name|checkRate
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytesSinceLastPause
operator|>
name|currentMinPauseCheckBytes
condition|)
block|{
name|rateLimiter
operator|.
name|pause
argument_list|(
name|bytesSinceLastPause
argument_list|)
expr_stmt|;
name|bytesSinceLastPause
operator|=
literal|0
expr_stmt|;
name|currentMinPauseCheckBytes
operator|=
name|rateLimiter
operator|.
name|getMinPauseCheckBytes
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
