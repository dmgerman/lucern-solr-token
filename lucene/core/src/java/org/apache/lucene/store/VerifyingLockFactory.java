begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import
begin_comment
comment|/**  * A {@link LockFactory} that wraps another {@link  * LockFactory} and verifies that each lock obtain/release  * is "correct" (never results in two processes holding the  * lock at the same time).  It does this by contacting an  * external server ({@link LockVerifyServer}) to assert that  * at most one process holds the lock at a time.  To use  * this, you should also run {@link LockVerifyServer} on the  * host and port matching what you pass to the constructor.  *  * @see LockVerifyServer  * @see LockStressTest  */
end_comment
begin_class
DECL|class|VerifyingLockFactory
specifier|public
specifier|final
class|class
name|VerifyingLockFactory
extends|extends
name|LockFactory
block|{
DECL|field|lf
specifier|final
name|LockFactory
name|lf
decl_stmt|;
DECL|field|in
specifier|final
name|InputStream
name|in
decl_stmt|;
DECL|field|out
specifier|final
name|OutputStream
name|out
decl_stmt|;
DECL|class|CheckedLock
specifier|private
class|class
name|CheckedLock
extends|extends
name|Lock
block|{
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
DECL|method|CheckedLock
specifier|public
name|CheckedLock
parameter_list|(
name|Lock
name|lock
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
name|verify
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ensureValid
specifier|public
name|void
name|ensureValid
parameter_list|()
throws|throws
name|IOException
block|{
name|lock
operator|.
name|ensureValid
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
try|try
init|(
name|Lock
name|l
init|=
name|lock
init|)
block|{
name|l
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|verify
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verify
specifier|private
name|void
name|verify
parameter_list|(
name|byte
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
specifier|final
name|int
name|ret
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|ret
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Lock server died because of locking error."
argument_list|)
throw|;
block|}
if|if
condition|(
name|ret
operator|!=
name|message
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Protocol violation."
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * @param lf the LockFactory that we are testing    * @param in the socket's input to {@link LockVerifyServer}    * @param out the socket's output to {@link LockVerifyServer}   */
DECL|method|VerifyingLockFactory
specifier|public
name|VerifyingLockFactory
parameter_list|(
name|LockFactory
name|lf
parameter_list|,
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|lf
operator|=
name|lf
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|obtainLock
specifier|public
name|Lock
name|obtainLock
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|lockName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CheckedLock
argument_list|(
name|lf
operator|.
name|obtainLock
argument_list|(
name|dir
argument_list|,
name|lockName
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
