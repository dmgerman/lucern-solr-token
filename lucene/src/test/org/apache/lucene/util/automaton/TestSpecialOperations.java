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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestSpecialOperations
specifier|public
class|class
name|TestSpecialOperations
extends|extends
name|LuceneTestCase
block|{
comment|/**    * tests against the original brics implementation.    */
DECL|method|testIsFinite
specifier|public
name|void
name|testIsFinite
parameter_list|()
block|{
name|int
name|num
init|=
literal|2000
operator|*
name|RANDOM_MULTIPLIER
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
name|assertEquals
argument_list|(
name|AutomatonTestUtil
operator|.
name|isFiniteSlow
argument_list|(
name|a
argument_list|)
argument_list|,
name|SpecialOperations
operator|.
name|isFinite
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
