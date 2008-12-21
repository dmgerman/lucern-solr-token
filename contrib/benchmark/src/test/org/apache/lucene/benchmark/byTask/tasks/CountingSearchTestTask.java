begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_comment
comment|/**  * Test Search task which counts number of searches.  */
end_comment
begin_class
DECL|class|CountingSearchTestTask
specifier|public
class|class
name|CountingSearchTestTask
extends|extends
name|SearchTask
block|{
DECL|field|numSearches
specifier|public
specifier|static
name|int
name|numSearches
init|=
literal|0
decl_stmt|;
DECL|field|startMillis
specifier|public
specifier|static
name|long
name|startMillis
decl_stmt|;
DECL|field|lastMillis
specifier|public
specifier|static
name|long
name|lastMillis
decl_stmt|;
DECL|method|CountingSearchTestTask
specifier|public
name|CountingSearchTestTask
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
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|res
init|=
name|super
operator|.
name|doLogic
argument_list|()
decl_stmt|;
name|incrNumSearches
argument_list|()
expr_stmt|;
return|return
name|res
return|;
block|}
DECL|method|incrNumSearches
specifier|private
specifier|static
specifier|synchronized
name|void
name|incrNumSearches
parameter_list|()
block|{
name|lastMillis
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|numSearches
condition|)
name|startMillis
operator|=
name|lastMillis
expr_stmt|;
name|numSearches
operator|++
expr_stmt|;
block|}
DECL|method|getElapsedMillis
specifier|public
name|long
name|getElapsedMillis
parameter_list|()
block|{
return|return
name|lastMillis
operator|-
name|startMillis
return|;
block|}
block|}
end_class
end_unit
