begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|QueryMaker
import|;
end_import
begin_comment
comment|/**  * Search task.  *   *<p>Note: This task reuses the reader if it is already open.   * Otherwise a reader is opened at start and closed at the end.  */
end_comment
begin_class
DECL|class|SearchTask
specifier|public
class|class
name|SearchTask
extends|extends
name|ReadTask
block|{
DECL|method|SearchTask
specifier|public
name|SearchTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|method|withRetrieve
specifier|public
name|boolean
name|withRetrieve
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|withSearch
specifier|public
name|boolean
name|withSearch
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|withTraverse
specifier|public
name|boolean
name|withTraverse
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|withWarm
specifier|public
name|boolean
name|withWarm
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|getQueryMaker
specifier|public
name|QueryMaker
name|getQueryMaker
parameter_list|()
block|{
return|return
name|getRunData
argument_list|()
operator|.
name|getSearchQueryMaker
argument_list|()
return|;
block|}
block|}
end_class
end_unit
