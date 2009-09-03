begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.stats
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
name|stats
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
name|tasks
operator|.
name|PerfTask
import|;
end_import
begin_comment
comment|/**  * Statistics for a task run.   *<br>The same task can run more than once, but, if that task records statistics,   * each run would create its own TaskStats.  */
end_comment
begin_class
DECL|class|TaskStats
specifier|public
class|class
name|TaskStats
implements|implements
name|Cloneable
block|{
comment|/** task for which data was collected */
DECL|field|task
specifier|private
name|PerfTask
name|task
decl_stmt|;
comment|/** round in which task run started */
DECL|field|round
specifier|private
name|int
name|round
decl_stmt|;
comment|/** task start time */
DECL|field|start
specifier|private
name|long
name|start
decl_stmt|;
comment|/** task elapsed time.  elapsed>= 0 indicates run completion! */
DECL|field|elapsed
specifier|private
name|long
name|elapsed
init|=
operator|-
literal|1
decl_stmt|;
comment|/** max tot mem during task */
DECL|field|maxTotMem
specifier|private
name|long
name|maxTotMem
decl_stmt|;
comment|/** max used mem during task */
DECL|field|maxUsedMem
specifier|private
name|long
name|maxUsedMem
decl_stmt|;
comment|/** serial run number of this task run in the perf run */
DECL|field|taskRunNum
specifier|private
name|int
name|taskRunNum
decl_stmt|;
comment|/** number of other tasks that started to run while this task was still running */
DECL|field|numParallelTasks
specifier|private
name|int
name|numParallelTasks
decl_stmt|;
comment|/** number of work items done by this task.    * For indexing that can be number of docs added.    * For warming that can be number of scanned items, etc.     * For repeating tasks, this is a sum over repetitions.    */
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
comment|/** Number of similar tasks aggregated into this record.       * Used when summing up on few runs/instances of similar tasks.    */
DECL|field|numRuns
specifier|private
name|int
name|numRuns
init|=
literal|1
decl_stmt|;
comment|/**    * Create a run data for a task that is starting now.    * To be called from Points.    */
DECL|method|TaskStats
name|TaskStats
parameter_list|(
name|PerfTask
name|task
parameter_list|,
name|int
name|taskRunNum
parameter_list|,
name|int
name|round
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|taskRunNum
operator|=
name|taskRunNum
expr_stmt|;
name|this
operator|.
name|round
operator|=
name|round
expr_stmt|;
name|maxTotMem
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
expr_stmt|;
name|maxUsedMem
operator|=
name|maxTotMem
operator|-
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|freeMemory
argument_list|()
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
comment|/**    * mark the end of a task    */
DECL|method|markEnd
name|void
name|markEnd
parameter_list|(
name|int
name|numParallelTasks
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|elapsed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
expr_stmt|;
name|long
name|totMem
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
decl_stmt|;
if|if
condition|(
name|totMem
operator|>
name|maxTotMem
condition|)
block|{
name|maxTotMem
operator|=
name|totMem
expr_stmt|;
block|}
name|long
name|usedMem
init|=
name|totMem
operator|-
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|freeMemory
argument_list|()
decl_stmt|;
if|if
condition|(
name|usedMem
operator|>
name|maxUsedMem
condition|)
block|{
name|maxUsedMem
operator|=
name|usedMem
expr_stmt|;
block|}
name|this
operator|.
name|numParallelTasks
operator|=
name|numParallelTasks
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
comment|/**    * @return the taskRunNum.    */
DECL|method|getTaskRunNum
specifier|public
name|int
name|getTaskRunNum
parameter_list|()
block|{
return|return
name|taskRunNum
return|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#toString()    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|res
init|=
operator|new
name|StringBuffer
argument_list|(
name|task
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
name|elapsed
argument_list|)
expr_stmt|;
return|return
name|res
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return Returns the count.    */
DECL|method|getCount
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/**    * @return elapsed time.    */
DECL|method|getElapsed
specifier|public
name|long
name|getElapsed
parameter_list|()
block|{
return|return
name|elapsed
return|;
block|}
comment|/**    * @return Returns the maxTotMem.    */
DECL|method|getMaxTotMem
specifier|public
name|long
name|getMaxTotMem
parameter_list|()
block|{
return|return
name|maxTotMem
return|;
block|}
comment|/**    * @return Returns the maxUsedMem.    */
DECL|method|getMaxUsedMem
specifier|public
name|long
name|getMaxUsedMem
parameter_list|()
block|{
return|return
name|maxUsedMem
return|;
block|}
comment|/**    * @return Returns the numParallelTasks.    */
DECL|method|getNumParallelTasks
specifier|public
name|int
name|getNumParallelTasks
parameter_list|()
block|{
return|return
name|numParallelTasks
return|;
block|}
comment|/**    * @return Returns the task.    */
DECL|method|getTask
specifier|public
name|PerfTask
name|getTask
parameter_list|()
block|{
return|return
name|task
return|;
block|}
comment|/**    * @return Returns the numRuns.    */
DECL|method|getNumRuns
specifier|public
name|int
name|getNumRuns
parameter_list|()
block|{
return|return
name|numRuns
return|;
block|}
comment|/**    * Add data from another stat, for aggregation    * @param stat2 the added stat data.    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|TaskStats
name|stat2
parameter_list|)
block|{
name|numRuns
operator|+=
name|stat2
operator|.
name|getNumRuns
argument_list|()
expr_stmt|;
name|elapsed
operator|+=
name|stat2
operator|.
name|getElapsed
argument_list|()
expr_stmt|;
name|maxTotMem
operator|+=
name|stat2
operator|.
name|getMaxTotMem
argument_list|()
expr_stmt|;
name|maxUsedMem
operator|+=
name|stat2
operator|.
name|getMaxUsedMem
argument_list|()
expr_stmt|;
name|count
operator|+=
name|stat2
operator|.
name|getCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|round
operator|!=
name|stat2
operator|.
name|round
condition|)
block|{
name|round
operator|=
operator|-
literal|1
expr_stmt|;
comment|// no meaning if aggregating tasks of different round.
block|}
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#clone()    */
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/**    * @return the round number.    */
DECL|method|getRound
specifier|public
name|int
name|getRound
parameter_list|()
block|{
return|return
name|round
return|;
block|}
block|}
end_class
end_unit
