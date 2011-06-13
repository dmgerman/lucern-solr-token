begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A utility for executing 2-phase commit on several objects.  *   * @see TwoPhaseCommit  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TwoPhaseCommitTool
specifier|public
specifier|final
class|class
name|TwoPhaseCommitTool
block|{
comment|/**    * A wrapper of a {@link TwoPhaseCommit}, which delegates all calls to the    * wrapped object, passing the specified commitData. This object is useful for    * use with {@link TwoPhaseCommitTool#execute(TwoPhaseCommit...)} if one would    * like to store commitData as part of the commit.    */
DECL|class|TwoPhaseCommitWrapper
specifier|public
specifier|static
specifier|final
class|class
name|TwoPhaseCommitWrapper
implements|implements
name|TwoPhaseCommit
block|{
DECL|field|tpc
specifier|private
specifier|final
name|TwoPhaseCommit
name|tpc
decl_stmt|;
DECL|field|commitData
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
decl_stmt|;
DECL|method|TwoPhaseCommitWrapper
specifier|public
name|TwoPhaseCommitWrapper
parameter_list|(
name|TwoPhaseCommit
name|tpc
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
parameter_list|)
block|{
name|this
operator|.
name|tpc
operator|=
name|tpc
expr_stmt|;
name|this
operator|.
name|commitData
operator|=
name|commitData
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareCommit
specifier|public
name|void
name|prepareCommit
parameter_list|()
throws|throws
name|IOException
block|{
name|prepareCommit
argument_list|(
name|commitData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareCommit
specifier|public
name|void
name|prepareCommit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
parameter_list|)
throws|throws
name|IOException
block|{
name|tpc
operator|.
name|prepareCommit
argument_list|(
name|this
operator|.
name|commitData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
name|commit
argument_list|(
name|commitData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
parameter_list|)
throws|throws
name|IOException
block|{
name|tpc
operator|.
name|commit
argument_list|(
name|this
operator|.
name|commitData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IOException
block|{
name|tpc
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Thrown by {@link TwoPhaseCommitTool#execute(TwoPhaseCommit...)} when an    * object fails to prepareCommit().    */
DECL|class|PrepareCommitFailException
specifier|public
specifier|static
class|class
name|PrepareCommitFailException
extends|extends
name|IOException
block|{
DECL|method|PrepareCommitFailException
specifier|public
name|PrepareCommitFailException
parameter_list|(
name|Throwable
name|cause
parameter_list|,
name|TwoPhaseCommit
name|obj
parameter_list|)
block|{
name|super
argument_list|(
literal|"prepareCommit() failed on "
operator|+
name|obj
argument_list|)
expr_stmt|;
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Thrown by {@link TwoPhaseCommitTool#execute(TwoPhaseCommit...)} when an    * object fails to commit().    */
DECL|class|CommitFailException
specifier|public
specifier|static
class|class
name|CommitFailException
extends|extends
name|IOException
block|{
DECL|method|CommitFailException
specifier|public
name|CommitFailException
parameter_list|(
name|Throwable
name|cause
parameter_list|,
name|TwoPhaseCommit
name|obj
parameter_list|)
block|{
name|super
argument_list|(
literal|"commit() failed on "
operator|+
name|obj
argument_list|)
expr_stmt|;
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** rollback all objects, discarding any exceptions that occur. */
DECL|method|rollback
specifier|private
specifier|static
name|void
name|rollback
parameter_list|(
name|TwoPhaseCommit
modifier|...
name|objects
parameter_list|)
block|{
for|for
control|(
name|TwoPhaseCommit
name|tpc
range|:
name|objects
control|)
block|{
comment|// ignore any exception that occurs during rollback - we want to ensure
comment|// all objects are rolled-back.
if|if
condition|(
name|tpc
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|tpc
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{}
block|}
block|}
block|}
comment|/**    * Executes a 2-phase commit algorithm by first    * {@link TwoPhaseCommit#prepareCommit()} all objects and only if all succeed,    * it proceeds with {@link TwoPhaseCommit#commit()}. If any of the objects    * fail on either the preparation or actual commit, it terminates and    * {@link TwoPhaseCommit#rollback()} all of them.    *<p>    *<b>NOTE:</b> it may happen that an object fails to commit, after few have    * already successfully committed. This tool will still issue a rollback    * instruction on them as well, but depending on the implementation, it may    * not have any effect.    *<p>    *<b>NOTE:</b> if any of the objects are {@code null}, this method simply    * skips over them.    *     * @throws PrepareCommitFailException    *           if any of the objects fail to    *           {@link TwoPhaseCommit#prepareCommit()}    * @throws CommitFailException    *           if any of the objects fail to {@link TwoPhaseCommit#commit()}    */
DECL|method|execute
specifier|public
specifier|static
name|void
name|execute
parameter_list|(
name|TwoPhaseCommit
modifier|...
name|objects
parameter_list|)
throws|throws
name|PrepareCommitFailException
throws|,
name|CommitFailException
block|{
name|TwoPhaseCommit
name|tpc
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// first, all should successfully prepareCommit()
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|objects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|tpc
operator|=
name|objects
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|tpc
operator|!=
literal|null
condition|)
block|{
name|tpc
operator|.
name|prepareCommit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// first object that fails results in rollback all of them and
comment|// throwing an exception.
name|rollback
argument_list|(
name|objects
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PrepareCommitFailException
argument_list|(
name|t
argument_list|,
name|tpc
argument_list|)
throw|;
block|}
comment|// If all successfully prepareCommit(), attempt the actual commit()
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|objects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|tpc
operator|=
name|objects
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|tpc
operator|!=
literal|null
condition|)
block|{
name|tpc
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// first object that fails results in rollback all of them and
comment|// throwing an exception.
name|rollback
argument_list|(
name|objects
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|CommitFailException
argument_list|(
name|t
argument_list|,
name|tpc
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
