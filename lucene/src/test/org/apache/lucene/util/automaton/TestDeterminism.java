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
name|java
operator|.
name|util
operator|.
name|Random
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
name|util
operator|.
name|LuceneTestCase
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
name|util
operator|.
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * Not thorough, but tries to test determinism correctness  * somewhat randomly.  */
end_comment
begin_class
DECL|class|TestDeterminism
specifier|public
class|class
name|TestDeterminism
extends|extends
name|LuceneTestCase
block|{
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|random
operator|=
name|newRandom
argument_list|()
expr_stmt|;
block|}
comment|/** test a bunch of random regular expressions */
DECL|method|testRegexps
specifier|public
name|void
name|testRegexps
parameter_list|()
throws|throws
name|Exception
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
literal|500
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
condition|;
name|i
operator|++
control|)
name|assertAutomaton
argument_list|(
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAutomaton
specifier|private
specifier|static
name|void
name|assertAutomaton
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
name|Automaton
name|clone
init|=
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// complement(complement(a)) = a
name|Automaton
name|equivalent
init|=
name|BasicOperations
operator|.
name|complement
argument_list|(
name|BasicOperations
operator|.
name|complement
argument_list|(
name|a
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|a
argument_list|,
name|equivalent
argument_list|)
argument_list|)
expr_stmt|;
comment|// a union a = a
name|equivalent
operator|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|a
argument_list|,
name|clone
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
name|equivalent
argument_list|)
argument_list|)
expr_stmt|;
comment|// a intersect a = a
name|equivalent
operator|=
name|BasicOperations
operator|.
name|intersection
argument_list|(
name|a
argument_list|,
name|clone
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
name|equivalent
argument_list|)
argument_list|)
expr_stmt|;
comment|// a minus a = empty
name|Automaton
name|empty
init|=
name|BasicOperations
operator|.
name|minus
argument_list|(
name|a
argument_list|,
name|clone
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|isEmpty
argument_list|(
name|empty
argument_list|)
argument_list|)
expr_stmt|;
comment|// as long as don't accept the empty string
comment|// then optional(a) - empty = a
if|if
condition|(
operator|!
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|""
argument_list|)
condition|)
block|{
comment|//System.out.println("test " + a);
name|Automaton
name|optional
init|=
name|BasicOperations
operator|.
name|optional
argument_list|(
name|a
argument_list|)
decl_stmt|;
comment|//System.out.println("optional " + optional);
name|equivalent
operator|=
name|BasicOperations
operator|.
name|minus
argument_list|(
name|optional
argument_list|,
name|BasicAutomata
operator|.
name|makeEmptyString
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.out.println("equiv " + equivalent);
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|a
argument_list|,
name|equivalent
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
