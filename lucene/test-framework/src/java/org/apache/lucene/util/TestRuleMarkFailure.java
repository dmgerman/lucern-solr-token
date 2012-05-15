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
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|internal
operator|.
name|AssumptionViolatedException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A rule for marking failed tests and suites.  */
end_comment
begin_class
DECL|class|TestRuleMarkFailure
specifier|public
specifier|final
class|class
name|TestRuleMarkFailure
implements|implements
name|TestRule
block|{
DECL|field|chained
specifier|private
specifier|final
name|TestRuleMarkFailure
index|[]
name|chained
decl_stmt|;
DECL|field|failures
specifier|private
specifier|volatile
name|boolean
name|failures
decl_stmt|;
DECL|method|TestRuleMarkFailure
specifier|public
name|TestRuleMarkFailure
parameter_list|(
name|TestRuleMarkFailure
modifier|...
name|chained
parameter_list|)
block|{
name|this
operator|.
name|chained
operator|=
name|chained
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|s
parameter_list|,
name|Description
name|d
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// Clear status at start.
name|failures
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|s
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
for|for
control|(
name|Throwable
name|t2
range|:
name|expandFromMultiple
argument_list|(
name|t
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|t2
operator|instanceof
name|AssumptionViolatedException
operator|)
condition|)
block|{
name|markFailed
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
throw|throw
name|t
throw|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Expand from multi-exception wrappers.    */
DECL|method|expandFromMultiple
specifier|private
specifier|static
name|List
argument_list|<
name|Throwable
argument_list|>
name|expandFromMultiple
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
name|expandFromMultiple
argument_list|(
name|t
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
comment|/** Internal recursive routine. */
DECL|method|expandFromMultiple
specifier|private
specifier|static
name|List
argument_list|<
name|Throwable
argument_list|>
name|expandFromMultiple
parameter_list|(
name|Throwable
name|t
parameter_list|,
name|List
argument_list|<
name|Throwable
argument_list|>
name|list
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|MultipleFailureException
condition|)
block|{
for|for
control|(
name|Throwable
name|sub
range|:
operator|(
operator|(
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|MultipleFailureException
operator|)
name|t
operator|)
operator|.
name|getFailures
argument_list|()
control|)
block|{
name|expandFromMultiple
argument_list|(
name|sub
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|list
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|/**    * Taints this object and any chained as having failures.    */
DECL|method|markFailed
specifier|public
name|void
name|markFailed
parameter_list|()
block|{
name|failures
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|TestRuleMarkFailure
name|next
range|:
name|chained
control|)
block|{
name|next
operator|.
name|markFailed
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Check if this object had any marked failures.    */
DECL|method|hadFailures
specifier|public
name|boolean
name|hadFailures
parameter_list|()
block|{
return|return
name|failures
return|;
block|}
comment|/**    * Check if this object was successful (the opposite of {@link #hadFailures()}).     */
DECL|method|wasSuccessful
specifier|public
name|boolean
name|wasSuccessful
parameter_list|()
block|{
return|return
operator|!
name|hadFailures
argument_list|()
return|;
block|}
block|}
end_class
end_unit
