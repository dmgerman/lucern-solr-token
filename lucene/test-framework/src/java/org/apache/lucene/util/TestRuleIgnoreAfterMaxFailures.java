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
name|org
operator|.
name|junit
operator|.
name|Assert
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
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Repeat
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * This rule keeps a count of failed tests (suites) and will result in an  * {@link AssumptionViolatedException} after a given number of failures for all  * tests following this condition.  *   *<p>  * Aborting quickly on failed tests can be useful when used in combination with  * test repeats (via the {@link Repeat} annotation or system property).  */
end_comment
begin_class
DECL|class|TestRuleIgnoreAfterMaxFailures
specifier|public
specifier|final
class|class
name|TestRuleIgnoreAfterMaxFailures
implements|implements
name|TestRule
block|{
comment|/**    * Maximum failures. Package scope for tests.    */
DECL|field|maxFailures
name|int
name|maxFailures
decl_stmt|;
comment|/**    * Current count of failures. Package scope for tests.    */
DECL|field|failuresSoFar
name|int
name|failuresSoFar
decl_stmt|;
comment|/**    * @param maxFailures    *          The number of failures after which all tests are ignored. Must be    *          greater or equal 1.    */
DECL|method|TestRuleIgnoreAfterMaxFailures
specifier|public
name|TestRuleIgnoreAfterMaxFailures
parameter_list|(
name|int
name|maxFailures
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"maxFailures must be>= 1: "
operator|+
name|maxFailures
argument_list|,
name|maxFailures
operator|>=
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxFailures
operator|=
name|maxFailures
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
specifier|final
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
if|if
condition|(
name|failuresSoFar
operator|>=
name|maxFailures
condition|)
block|{
name|RandomizedTest
operator|.
name|assumeTrue
argument_list|(
literal|"Ignored, failures limit reached ("
operator|+
name|failuresSoFar
operator|+
literal|">= "
operator|+
name|maxFailures
operator|+
literal|")."
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
operator|!
name|TestRuleMarkFailure
operator|.
name|isAssumption
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|failuresSoFar
operator|++
expr_stmt|;
block|}
throw|throw
name|t
throw|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
