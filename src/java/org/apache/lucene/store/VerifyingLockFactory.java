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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
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
comment|/**  * A {@link LockFactory} that wraps another {@link  * LockFactory} and verifies that each lock obtain/release  * is "correct" (never results in two processes holding the  * lock at the same time).  It does this by contacting an  * external server ({@link LockVerifyServer}) to assert that  * at most one process holds the lock at a time.  To use  * this, you should also run {@link LockVerifyServer} on the  * host& port matching what you pass to the constructor.  *  * @see LockVerifyServer  * @see LockStressTest  */
end_comment
begin_class
DECL|class|VerifyingLockFactory
specifier|public
class|class
name|VerifyingLockFactory
extends|extends
name|LockFactory
block|{
DECL|field|lf
name|LockFactory
name|lf
decl_stmt|;
DECL|field|id
name|byte
name|id
decl_stmt|;
DECL|field|host
name|String
name|host
decl_stmt|;
DECL|field|port
name|int
name|port
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
block|{
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
block|}
DECL|method|verify
specifier|private
name|void
name|verify
parameter_list|(
name|byte
name|message
parameter_list|)
block|{
try|try
block|{
name|Socket
name|s
init|=
operator|new
name|Socket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|OutputStream
name|out
init|=
name|s
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
name|s
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|int
name|result
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|0
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"lock was double acquired"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|obtain
specifier|public
specifier|synchronized
name|boolean
name|obtain
parameter_list|(
name|long
name|lockWaitTimeout
parameter_list|)
throws|throws
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|boolean
name|obtained
init|=
name|lock
operator|.
name|obtain
argument_list|(
name|lockWaitTimeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|obtained
condition|)
name|verify
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
return|return
name|obtained
return|;
block|}
DECL|method|obtain
specifier|public
specifier|synchronized
name|boolean
name|obtain
parameter_list|()
throws|throws
name|LockObtainFailedException
throws|,
name|IOException
block|{
return|return
name|lock
operator|.
name|obtain
argument_list|()
return|;
block|}
DECL|method|isLocked
specifier|public
specifier|synchronized
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
name|lock
operator|.
name|isLocked
argument_list|()
return|;
block|}
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isLocked
argument_list|()
condition|)
block|{
name|verify
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @param id should be a unique id across all clients    * @param lf the LockFactory that we are testing    * @param host host or IP where {@link LockVerifyServer}             is running    * @param port the port {@link LockVerifyServer} is             listening on   */
DECL|method|VerifyingLockFactory
specifier|public
name|VerifyingLockFactory
parameter_list|(
name|byte
name|id
parameter_list|,
name|LockFactory
name|lf
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|lf
operator|=
name|lf
expr_stmt|;
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
DECL|method|makeLock
specifier|public
specifier|synchronized
name|Lock
name|makeLock
parameter_list|(
name|String
name|lockName
parameter_list|)
block|{
return|return
operator|new
name|CheckedLock
argument_list|(
name|lf
operator|.
name|makeLock
argument_list|(
name|lockName
argument_list|)
argument_list|)
return|;
block|}
DECL|method|clearLock
specifier|public
specifier|synchronized
name|void
name|clearLock
parameter_list|(
name|String
name|lockName
parameter_list|)
throws|throws
name|IOException
block|{
name|lf
operator|.
name|clearLock
argument_list|(
name|lockName
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
