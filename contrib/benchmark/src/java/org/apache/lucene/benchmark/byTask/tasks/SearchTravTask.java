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
comment|/**  * Search and Traverse task.  *   *<p>Note: This task reuses the reader if it is already open.   * Otherwise a reader is opened at start and closed at the end.  *<p/>  *   *<p>Takes optional param: traversal size (otherwise all results are traversed).</p>  *   *<p>Other side effects: counts additional 1 (record) for each traversed hit.</p>  */
end_comment
begin_class
DECL|class|SearchTravTask
specifier|public
class|class
name|SearchTravTask
extends|extends
name|ReadTask
block|{
DECL|field|traversalSize
specifier|protected
name|int
name|traversalSize
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|method|SearchTravTask
specifier|public
name|SearchTravTask
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
literal|true
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
name|getQueryMaker
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|traversalSize
specifier|public
name|int
name|traversalSize
parameter_list|()
block|{
return|return
name|traversalSize
return|;
block|}
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|traversalSize
operator|=
operator|(
name|int
operator|)
name|Float
operator|.
name|parseFloat
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#supportsParams()    */
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
