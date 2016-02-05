begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Locale
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
name|stats
operator|.
name|Points
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
name|stats
operator|.
name|TaskStats
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
name|utils
operator|.
name|Config
import|;
end_import
begin_comment
comment|/**  * An abstract task to be tested for performance.<br>  * Every performance task extends this class, and provides its own  * {@link #doLogic()} method, which performs the actual task.<br>  * Tasks performing some work that should be measured for the task, can override  * {@link #setup()} and/or {@link #tearDown()} and place that work there.<br>  * Relevant properties:<code>task.max.depth.log</code>.<br>  * Also supports the following logging attributes:  *<ul>  *<li>log.step - specifies how often to log messages about the current running  * task. Default is 1000 {@link #doLogic()} invocations. Set to -1 to disable  * logging.  *<li>log.step.[class Task Name] - specifies the same as 'log.step', only for a  * particular task name. For example, log.step.AddDoc will be applied only for  * {@link AddDocTask}. It's a way to control  * per task logging settings. If you want to omit logging for any other task,  * include log.step=-1. The syntax is "log.step." together with the Task's  * 'short' name (i.e., without the 'Task' part).  *</ul>  */
end_comment
begin_class
DECL|class|PerfTask
specifier|public
specifier|abstract
class|class
name|PerfTask
implements|implements
name|Cloneable
block|{
DECL|field|DEFAULT_LOG_STEP
specifier|static
specifier|final
name|int
name|DEFAULT_LOG_STEP
init|=
literal|1000
decl_stmt|;
DECL|field|runData
specifier|private
name|PerfRunData
name|runData
decl_stmt|;
comment|// propeties that all tasks have
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|depth
specifier|private
name|int
name|depth
init|=
literal|0
decl_stmt|;
DECL|field|logStep
specifier|protected
name|int
name|logStep
decl_stmt|;
DECL|field|logStepCount
specifier|private
name|int
name|logStepCount
init|=
literal|0
decl_stmt|;
DECL|field|maxDepthLogStart
specifier|private
name|int
name|maxDepthLogStart
init|=
literal|0
decl_stmt|;
DECL|field|disableCounting
specifier|private
name|boolean
name|disableCounting
init|=
literal|false
decl_stmt|;
DECL|field|params
specifier|protected
name|String
name|params
init|=
literal|null
decl_stmt|;
DECL|field|runInBackground
specifier|private
name|boolean
name|runInBackground
decl_stmt|;
DECL|field|deltaPri
specifier|private
name|int
name|deltaPri
decl_stmt|;
comment|// The first line of this task's definition in the alg file
DECL|field|algLineNum
specifier|private
name|int
name|algLineNum
init|=
literal|0
decl_stmt|;
DECL|field|NEW_LINE
specifier|protected
specifier|static
specifier|final
name|String
name|NEW_LINE
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|/** Should not be used externally */
DECL|method|PerfTask
specifier|private
name|PerfTask
parameter_list|()
block|{
name|name
operator|=
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|"Task"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|4
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setRunInBackground
specifier|public
name|void
name|setRunInBackground
parameter_list|(
name|int
name|deltaPri
parameter_list|)
block|{
name|runInBackground
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|deltaPri
operator|=
name|deltaPri
expr_stmt|;
block|}
DECL|method|getRunInBackground
specifier|public
name|boolean
name|getRunInBackground
parameter_list|()
block|{
return|return
name|runInBackground
return|;
block|}
DECL|method|getBackgroundDeltaPriority
specifier|public
name|int
name|getBackgroundDeltaPriority
parameter_list|()
block|{
return|return
name|deltaPri
return|;
block|}
DECL|field|stopNow
specifier|protected
specifier|volatile
name|boolean
name|stopNow
decl_stmt|;
DECL|method|stopNow
specifier|public
name|void
name|stopNow
parameter_list|()
block|{
name|stopNow
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|PerfTask
specifier|public
name|PerfTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|runData
operator|=
name|runData
expr_stmt|;
name|Config
name|config
init|=
name|runData
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|this
operator|.
name|maxDepthLogStart
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"task.max.depth.log"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|String
name|logStepAtt
init|=
literal|"log.step"
decl_stmt|;
name|String
name|taskLogStepAtt
init|=
literal|"log.step."
operator|+
name|name
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|taskLogStepAtt
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|logStepAtt
operator|=
name|taskLogStepAtt
expr_stmt|;
block|}
comment|// It's important to read this from Config, to support vals-by-round.
name|logStep
operator|=
name|config
operator|.
name|get
argument_list|(
name|logStepAtt
argument_list|,
name|DEFAULT_LOG_STEP
argument_list|)
expr_stmt|;
comment|// To avoid the check 'if (logStep> 0)' in tearDown(). This effectively
comment|// turns logging off.
if|if
condition|(
name|logStep
operator|<=
literal|0
condition|)
block|{
name|logStep
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|protected
name|PerfTask
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
comment|// tasks having non primitive data structures should override this.
comment|// otherwise parallel running of a task sequence might not run correctly.
return|return
operator|(
name|PerfTask
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{   }
comment|/**    * Run the task, record statistics.    * @return number of work items done by this task.    */
DECL|method|runAndMaybeStats
specifier|public
specifier|final
name|int
name|runAndMaybeStats
parameter_list|(
name|boolean
name|reportStats
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|reportStats
operator|||
name|shouldNotRecordStats
argument_list|()
condition|)
block|{
name|setup
argument_list|()
expr_stmt|;
name|int
name|count
init|=
name|doLogic
argument_list|()
decl_stmt|;
name|count
operator|=
name|disableCounting
condition|?
literal|0
else|:
name|count
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
return|return
name|count
return|;
block|}
if|if
condition|(
name|reportStats
operator|&&
name|depth
operator|<=
name|maxDepthLogStart
operator|&&
operator|!
name|shouldNeverLogAtStart
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------------> starting task: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|setup
argument_list|()
expr_stmt|;
name|Points
name|pnts
init|=
name|runData
operator|.
name|getPoints
argument_list|()
decl_stmt|;
name|TaskStats
name|ts
init|=
name|pnts
operator|.
name|markTaskStart
argument_list|(
name|this
argument_list|,
name|runData
operator|.
name|getConfig
argument_list|()
operator|.
name|getRoundNumber
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|doLogic
argument_list|()
decl_stmt|;
name|count
operator|=
name|disableCounting
condition|?
literal|0
else|:
name|count
expr_stmt|;
name|pnts
operator|.
name|markTaskEnd
argument_list|(
name|ts
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
return|return
name|count
return|;
block|}
comment|/**    * Perform the task once (ignoring repetitions specification)    * Return number of work items done by this task.    * For indexing that can be number of docs added.    * For warming that can be number of scanned items, etc.    * @return number of work items done by this task.    */
DECL|method|doLogic
specifier|public
specifier|abstract
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * @return Returns the name.    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
return|return
name|name
return|;
block|}
return|return
operator|new
name|StringBuilder
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|params
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @param name The name to set.    */
DECL|method|setName
specifier|protected
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * @return Returns the run data.    */
DECL|method|getRunData
specifier|public
name|PerfRunData
name|getRunData
parameter_list|()
block|{
return|return
name|runData
return|;
block|}
comment|/**    * @return Returns the depth.    */
DECL|method|getDepth
specifier|public
name|int
name|getDepth
parameter_list|()
block|{
return|return
name|depth
return|;
block|}
comment|/**    * @param depth The depth to set.    */
DECL|method|setDepth
specifier|public
name|void
name|setDepth
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
comment|// compute a blank string padding for printing this task indented by its depth
DECL|method|getPadding
name|String
name|getPadding
parameter_list|()
block|{
name|char
name|c
index|[]
init|=
operator|new
name|char
index|[
literal|4
operator|*
name|getDepth
argument_list|()
index|]
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
name|c
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|c
index|[
name|i
index|]
operator|=
literal|' '
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|c
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|padd
init|=
name|getPadding
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|padd
argument_list|)
decl_stmt|;
if|if
condition|(
name|disableCounting
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getRunInBackground
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"&"
argument_list|)
expr_stmt|;
name|int
name|x
init|=
name|getBackgroundDeltaPriority
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return Returns the maxDepthLogStart.    */
DECL|method|getMaxDepthLogStart
name|int
name|getMaxDepthLogStart
parameter_list|()
block|{
return|return
name|maxDepthLogStart
return|;
block|}
DECL|method|getLogMessage
specifier|protected
name|String
name|getLogMessage
parameter_list|(
name|int
name|recsCount
parameter_list|)
block|{
return|return
literal|"processed "
operator|+
name|recsCount
operator|+
literal|" records"
return|;
block|}
comment|/**    * Tasks that should never log at start can override this.      * @return true if this task should never log when it start.    */
DECL|method|shouldNeverLogAtStart
specifier|protected
name|boolean
name|shouldNeverLogAtStart
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Tasks that should not record statistics can override this.      * @return true if this task should never record its statistics.    */
DECL|method|shouldNotRecordStats
specifier|protected
name|boolean
name|shouldNotRecordStats
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Task setup work that should not be measured for that specific task. By    * default it does nothing, but tasks can implement this, moving work from    * {@link #doLogic()} to this method. Only the work done in {@link #doLogic()}    * is measured for this task. Notice that higher level (sequence) tasks    * containing this task would then measure larger time than the sum of their    * contained tasks.    */
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{   }
comment|/**    * Task tearDown work that should not be measured for that specific task. By    * default it does nothing, but tasks can implement this, moving work from    * {@link #doLogic()} to this method. Only the work done in {@link #doLogic()}    * is measured for this task. Notice that higher level (sequence) tasks    * containing this task would then measure larger time than the sum of their    * contained tasks.    */
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|++
name|logStepCount
operator|%
name|logStep
operator|==
literal|0
condition|)
block|{
name|double
name|time
init|=
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|runData
operator|.
name|getStartTimeMillis
argument_list|()
operator|)
operator|/
literal|1000.0
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%7.2f"
argument_list|,
name|time
argument_list|)
operator|+
literal|" sec --> "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|getLogMessage
argument_list|(
name|logStepCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Sub classes that support parameters must override this method to return    * true.    *     * @return true iff this task supports command line params.    */
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Set the params of this task.    *     * @exception UnsupportedOperationException    *              for tasks supporting command line parameters.    */
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
if|if
condition|(
operator|!
name|supportsParams
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|getName
argument_list|()
operator|+
literal|" does not support command line parameters."
argument_list|)
throw|;
block|}
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
comment|/**    * @return Returns the Params.    */
DECL|method|getParams
specifier|public
name|String
name|getParams
parameter_list|()
block|{
return|return
name|params
return|;
block|}
comment|/**    * Return true if counting is disabled for this task.    */
DECL|method|isDisableCounting
specifier|public
name|boolean
name|isDisableCounting
parameter_list|()
block|{
return|return
name|disableCounting
return|;
block|}
comment|/**    * See {@link #isDisableCounting()}    */
DECL|method|setDisableCounting
specifier|public
name|void
name|setDisableCounting
parameter_list|(
name|boolean
name|disableCounting
parameter_list|)
block|{
name|this
operator|.
name|disableCounting
operator|=
name|disableCounting
expr_stmt|;
block|}
DECL|method|setAlgLineNum
specifier|public
name|void
name|setAlgLineNum
parameter_list|(
name|int
name|algLineNum
parameter_list|)
block|{
name|this
operator|.
name|algLineNum
operator|=
name|algLineNum
expr_stmt|;
block|}
DECL|method|getAlgLineNum
specifier|public
name|int
name|getAlgLineNum
parameter_list|()
block|{
return|return
name|algLineNum
return|;
block|}
block|}
end_class
end_unit
