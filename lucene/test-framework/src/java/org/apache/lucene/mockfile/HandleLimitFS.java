begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
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
name|nio
operator|.
name|file
operator|.
name|FileSystem
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|FileSystemException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_comment
comment|/**   * FileSystem that throws exception if file handles   * in use exceeds a specified limit   */
end_comment
begin_class
DECL|class|HandleLimitFS
specifier|public
class|class
name|HandleLimitFS
extends|extends
name|HandleTrackingFS
block|{
DECL|field|limit
specifier|final
name|int
name|limit
decl_stmt|;
DECL|field|count
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/**    * Create a new instance, limiting the maximum number    * of open files to {@code limit}    * @param delegate delegate filesystem to wrap.    * @param limit maximum number of open files.    */
DECL|method|HandleLimitFS
specifier|public
name|HandleLimitFS
parameter_list|(
name|FileSystem
name|delegate
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|super
argument_list|(
literal|"handlelimit://"
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onOpen
specifier|protected
name|void
name|onOpen
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|.
name|incrementAndGet
argument_list|()
operator|>
name|limit
condition|)
block|{
name|count
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|FileSystemException
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"Too many open files"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|protected
name|void
name|onClose
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|count
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
