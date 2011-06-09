begin_unit
begin_package
DECL|package|org.apache.lucene.util.automaton
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**   * This test builds some randomish NFA/DFA and minimizes them.  */
end_comment
begin_class
DECL|class|TestMinimize
specifier|public
class|class
name|TestMinimize
extends|extends
name|LuceneTestCase
block|{
comment|/** the minimal and non-minimal are compared to ensure they are the same. */
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|200
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|Automaton
name|a
init|=
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Automaton
name|b
init|=
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** compare minimized against minimized with a slower, simple impl.    * we check not only that they are the same, but that #states/#transitions    * are the same. */
DECL|method|testAgainstBrzozowski
specifier|public
name|void
name|testAgainstBrzozowski
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|200
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|Automaton
name|a
init|=
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|AutomatonTestUtil
operator|.
name|minimizeSimple
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|Automaton
name|b
init|=
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|getNumberOfStates
argument_list|()
argument_list|,
name|b
operator|.
name|getNumberOfStates
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|getNumberOfTransitions
argument_list|()
argument_list|,
name|b
operator|.
name|getNumberOfTransitions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** n^2 space usage in Hopcroft minimization? */
DECL|method|testMinimizeHuge
specifier|public
name|void
name|testMinimizeHuge
parameter_list|()
block|{
operator|new
name|RegExp
argument_list|(
literal|"+-*(A|.....|BC)*]"
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
operator|.
name|toAutomaton
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
