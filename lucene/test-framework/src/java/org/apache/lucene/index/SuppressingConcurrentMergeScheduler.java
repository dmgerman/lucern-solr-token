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
name|Directory
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/** A {@link ConcurrentMergeScheduler} that ignores AlreadyClosedException. */
end_comment
begin_class
DECL|class|SuppressingConcurrentMergeScheduler
specifier|public
specifier|abstract
class|class
name|SuppressingConcurrentMergeScheduler
extends|extends
name|ConcurrentMergeScheduler
block|{
annotation|@
name|Override
DECL|method|handleMergeException
specifier|protected
name|void
name|handleMergeException
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Throwable
name|exc
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|isOK
argument_list|(
name|exc
argument_list|)
condition|)
block|{
return|return;
block|}
name|exc
operator|=
name|exc
operator|.
name|getCause
argument_list|()
expr_stmt|;
if|if
condition|(
name|exc
operator|==
literal|null
condition|)
block|{
name|super
operator|.
name|handleMergeException
argument_list|(
name|dir
argument_list|,
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isOK
specifier|protected
specifier|abstract
name|boolean
name|isOK
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
end_class
end_unit
