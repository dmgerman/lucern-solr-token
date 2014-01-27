begin_unit
begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
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
name|ArrayBlockingQueue
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
name|BlockingQueue
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
name|ConcurrentHashMap
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
name|ConcurrentMap
import|;
end_import
begin_class
DECL|class|BufferStore
specifier|public
class|class
name|BufferStore
implements|implements
name|Store
block|{
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|Store
name|EMPTY
init|=
operator|new
name|Store
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|takeBuffer
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
return|return
operator|new
name|byte
index|[
name|bufferSize
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|putBuffer
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
block|{     }
block|}
decl_stmt|;
DECL|field|bufferStores
specifier|private
specifier|final
specifier|static
name|ConcurrentMap
argument_list|<
name|Integer
argument_list|,
name|BufferStore
argument_list|>
name|bufferStores
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|BufferStore
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|buffers
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|byte
index|[]
argument_list|>
name|buffers
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
DECL|method|initNewBuffer
specifier|public
specifier|synchronized
specifier|static
name|void
name|initNewBuffer
parameter_list|(
name|int
name|bufferSize
parameter_list|,
name|long
name|totalAmount
parameter_list|)
block|{
if|if
condition|(
name|totalAmount
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|BufferStore
name|bufferStore
init|=
name|bufferStores
operator|.
name|get
argument_list|(
name|bufferSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|bufferStore
operator|==
literal|null
condition|)
block|{
name|long
name|count
init|=
name|totalAmount
operator|/
name|bufferSize
decl_stmt|;
if|if
condition|(
name|count
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|count
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
name|BufferStore
name|store
init|=
operator|new
name|BufferStore
argument_list|(
name|bufferSize
argument_list|,
operator|(
name|int
operator|)
name|count
argument_list|)
decl_stmt|;
name|bufferStores
operator|.
name|put
argument_list|(
name|bufferSize
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|BufferStore
specifier|private
name|BufferStore
parameter_list|(
name|int
name|bufferSize
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|buffers
operator|=
name|setupBuffers
argument_list|(
name|bufferSize
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
DECL|method|setupBuffers
specifier|private
specifier|static
name|BlockingQueue
argument_list|<
name|byte
index|[]
argument_list|>
name|setupBuffers
parameter_list|(
name|int
name|bufferSize
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|BlockingQueue
argument_list|<
name|byte
index|[]
argument_list|>
name|queue
init|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|(
name|count
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|queue
operator|.
name|add
argument_list|(
operator|new
name|byte
index|[
name|bufferSize
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|queue
return|;
block|}
DECL|method|instance
specifier|public
specifier|static
name|Store
name|instance
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
name|BufferStore
name|bufferStore
init|=
name|bufferStores
operator|.
name|get
argument_list|(
name|bufferSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|bufferStore
operator|==
literal|null
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
return|return
name|bufferStore
return|;
block|}
annotation|@
name|Override
DECL|method|takeBuffer
specifier|public
name|byte
index|[]
name|takeBuffer
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|bufferSize
operator|!=
name|bufferSize
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Buffer with length ["
operator|+
name|bufferSize
operator|+
literal|"] does not match buffer size of ["
operator|+
name|bufferSize
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|newBuffer
argument_list|(
name|buffers
operator|.
name|poll
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|putBuffer
specifier|public
name|void
name|putBuffer
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|buffer
operator|.
name|length
operator|!=
name|bufferSize
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Buffer with length ["
operator|+
name|buffer
operator|.
name|length
operator|+
literal|"] does not match buffer size of ["
operator|+
name|bufferSize
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|checkReturn
argument_list|(
name|buffers
operator|.
name|offer
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkReturn
specifier|private
name|void
name|checkReturn
parameter_list|(
name|boolean
name|offer
parameter_list|)
block|{    }
DECL|method|newBuffer
specifier|private
name|byte
index|[]
name|newBuffer
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|)
block|{
if|if
condition|(
name|buf
operator|!=
literal|null
condition|)
block|{
return|return
name|buf
return|;
block|}
return|return
operator|new
name|byte
index|[
name|bufferSize
index|]
return|;
block|}
block|}
end_class
end_unit
