begin_unit
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Semaphore
import|;
end_import
begin_class
DECL|class|AdjustableSemaphore
specifier|final
specifier|public
class|class
name|AdjustableSemaphore
block|{
DECL|field|semaphore
specifier|private
specifier|final
name|ResizeableSemaphore
name|semaphore
decl_stmt|;
DECL|field|maxPermits
specifier|private
name|int
name|maxPermits
init|=
literal|0
decl_stmt|;
DECL|method|AdjustableSemaphore
specifier|public
name|AdjustableSemaphore
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|semaphore
operator|=
operator|new
name|ResizeableSemaphore
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|setMaxPermits
specifier|public
specifier|synchronized
name|void
name|setMaxPermits
parameter_list|(
name|int
name|newMax
parameter_list|)
block|{
if|if
condition|(
name|newMax
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Semaphore size must be at least 1,"
operator|+
literal|" was "
operator|+
name|newMax
argument_list|)
throw|;
block|}
name|int
name|delta
init|=
name|newMax
operator|-
name|this
operator|.
name|maxPermits
decl_stmt|;
if|if
condition|(
name|delta
operator|==
literal|0
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|delta
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|semaphore
operator|.
name|release
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|delta
operator|*=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|semaphore
operator|.
name|reducePermits
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|maxPermits
operator|=
name|newMax
expr_stmt|;
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{
name|this
operator|.
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|(
name|int
name|numPermits
parameter_list|)
block|{
name|this
operator|.
name|semaphore
operator|.
name|release
argument_list|(
name|numPermits
argument_list|)
expr_stmt|;
block|}
DECL|method|acquire
specifier|public
name|void
name|acquire
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|this
operator|.
name|semaphore
operator|.
name|acquire
argument_list|()
expr_stmt|;
block|}
DECL|method|getMaxPermits
specifier|public
name|int
name|getMaxPermits
parameter_list|()
block|{
return|return
name|maxPermits
return|;
block|}
DECL|class|ResizeableSemaphore
specifier|private
specifier|static
specifier|final
class|class
name|ResizeableSemaphore
extends|extends
name|Semaphore
block|{
DECL|method|ResizeableSemaphore
name|ResizeableSemaphore
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reducePermits
specifier|protected
name|void
name|reducePermits
parameter_list|(
name|int
name|reduction
parameter_list|)
block|{
name|super
operator|.
name|reducePermits
argument_list|(
name|reduction
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
