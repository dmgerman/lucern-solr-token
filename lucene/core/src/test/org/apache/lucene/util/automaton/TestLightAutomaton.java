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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IntsRef
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
name|automaton
operator|.
name|AutomatonTestUtil
operator|.
name|RandomAcceptedStringsLight
import|;
end_import
begin_class
DECL|class|TestLightAutomaton
specifier|public
class|class
name|TestLightAutomaton
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
operator|new
name|LightAutomaton
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|x
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|y
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|a
operator|.
name|setAccept
argument_list|(
name|end
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|x
argument_list|,
literal|'a'
argument_list|,
literal|'a'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
literal|'d'
argument_list|,
literal|'d'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
literal|'b'
argument_list|,
literal|'b'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|y
argument_list|,
name|end
argument_list|,
literal|'c'
argument_list|,
literal|'c'
argument_list|)
expr_stmt|;
name|a
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
DECL|method|testReduceBasic
specifier|public
name|void
name|testReduceBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
operator|new
name|LightAutomaton
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|a
operator|.
name|setAccept
argument_list|(
name|end
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Should collapse to a-b:
name|a
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
literal|'a'
argument_list|,
literal|'a'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
literal|'b'
argument_list|,
literal|'b'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
literal|'m'
argument_list|,
literal|'m'
argument_list|)
expr_stmt|;
comment|// Should collapse to x-y:
name|a
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
literal|'x'
argument_list|,
literal|'x'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
literal|'y'
argument_list|,
literal|'y'
argument_list|)
expr_stmt|;
name|a
operator|.
name|finish
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|a
operator|.
name|getNumTransitions
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
name|LightAutomaton
operator|.
name|Transition
name|scratch
init|=
operator|new
name|LightAutomaton
operator|.
name|Transition
argument_list|()
decl_stmt|;
name|a
operator|.
name|initTransition
argument_list|(
name|start
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|a
operator|.
name|getNextTransition
argument_list|(
name|scratch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'a'
argument_list|,
name|scratch
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'b'
argument_list|,
name|scratch
operator|.
name|max
argument_list|)
expr_stmt|;
name|a
operator|.
name|getNextTransition
argument_list|(
name|scratch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'m'
argument_list|,
name|scratch
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'m'
argument_list|,
name|scratch
operator|.
name|max
argument_list|)
expr_stmt|;
name|a
operator|.
name|getNextTransition
argument_list|(
name|scratch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'x'
argument_list|,
name|scratch
operator|.
name|min
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'y'
argument_list|,
name|scratch
operator|.
name|max
argument_list|)
expr_stmt|;
block|}
DECL|method|testSameLanguage
specifier|public
name|void
name|testSameLanguage
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a1
init|=
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foobar"
argument_list|)
decl_stmt|;
name|LightAutomaton
name|a2
init|=
name|BasicOperations
operator|.
name|concatenateLight
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCommonPrefix
specifier|public
name|void
name|testCommonPrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|concatenateLight
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyStringLight
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foobar"
argument_list|,
name|SpecialOperations
operator|.
name|getCommonPrefix
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testConcatenate1
specifier|public
name|void
name|testConcatenate1
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|concatenateLight
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"m"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyStringLight
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"m"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"me"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"me too"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testConcatenate2
specifier|public
name|void
name|testConcatenate2
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|concatenateLight
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"m"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyStringLight
argument_list|()
argument_list|,
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"n"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyStringLight
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"mn"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"mone"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"m"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnion1
specifier|public
name|void
name|testUnion1
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|unionLight
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"barbaz"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"barbaz"
argument_list|)
argument_list|)
expr_stmt|;
comment|// nocommit test getFinitStrings count == 2
block|}
DECL|method|testUnion2
specifier|public
name|void
name|testUnion2
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|unionLight
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|""
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"barbaz"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"barbaz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// nocommit test getFinitStrings count == 3
block|}
DECL|method|testMinimizeSimple
specifier|public
name|void
name|testMinimizeSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foobar"
argument_list|)
decl_stmt|;
comment|//a.writeDot("a");
name|LightAutomaton
name|aMin
init|=
name|MinimizationOperationsLight
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
decl_stmt|;
comment|//aMin.writeDot("aMin");
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|a
argument_list|,
name|aMin
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinimize2
specifier|public
name|void
name|testMinimize2
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|unionLight
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"boobar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|LightAutomaton
name|aMin
init|=
name|MinimizationOperationsLight
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
argument_list|,
name|aMin
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReverse
specifier|public
name|void
name|testReverse
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foobar"
argument_list|)
decl_stmt|;
name|LightAutomaton
name|ra
init|=
name|SpecialOperations
operator|.
name|reverse
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|LightAutomaton
name|a2
init|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|SpecialOperations
operator|.
name|reverse
argument_list|(
name|ra
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
name|a2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOptional
specifier|public
name|void
name|testOptional
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"foobar"
argument_list|)
decl_stmt|;
name|LightAutomaton
name|a2
init|=
name|BasicOperations
operator|.
name|optionalLight
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|a2
operator|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRepeatAny
specifier|public
name|void
name|testRepeatAny
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"zee"
argument_list|)
decl_stmt|;
name|LightAutomaton
name|a2
init|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|BasicOperations
operator|.
name|repeatLight
argument_list|(
name|a
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezeezee"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRepeatMin
specifier|public
name|void
name|testRepeatMin
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"zee"
argument_list|)
decl_stmt|;
name|LightAutomaton
name|a2
init|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|BasicOperations
operator|.
name|repeatLight
argument_list|(
name|a
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezeezee"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRepeatMinMax1
specifier|public
name|void
name|testRepeatMinMax1
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"zee"
argument_list|)
decl_stmt|;
name|LightAutomaton
name|a2
init|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|BasicOperations
operator|.
name|repeatLight
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezeezee"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRepeatMinMax2
specifier|public
name|void
name|testRepeatMinMax2
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"zee"
argument_list|)
decl_stmt|;
name|LightAutomaton
name|a2
init|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|BasicOperations
operator|.
name|repeatLight
argument_list|(
name|a
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezeezee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezeezeezee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezeezeezeezee"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplement
specifier|public
name|void
name|testComplement
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeStringLight
argument_list|(
literal|"zee"
argument_list|)
decl_stmt|;
name|LightAutomaton
name|a2
init|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|BasicOperations
operator|.
name|complementLight
argument_list|(
name|a
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a2
argument_list|,
literal|"zeezeezee"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInterval
specifier|public
name|void
name|testInterval
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|BasicAutomata
operator|.
name|makeIntervalLight
argument_list|(
literal|17
argument_list|,
literal|100
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"017"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"100"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|"073"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCommonSuffix
specifier|public
name|void
name|testCommonSuffix
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
operator|new
name|LightAutomaton
argument_list|()
decl_stmt|;
name|int
name|init
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|fini
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|a
operator|.
name|setAccept
argument_list|(
name|init
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|a
operator|.
name|setAccept
argument_list|(
name|fini
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|init
argument_list|,
name|fini
argument_list|,
literal|'m'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|fini
argument_list|,
name|fini
argument_list|,
literal|'m'
argument_list|)
expr_stmt|;
name|a
operator|.
name|finish
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|SpecialOperations
operator|.
name|getCommonSuffixBytesRef
argument_list|(
name|a
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testReverseRandom1
specifier|public
name|void
name|testReverseRandom1
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|ITERS
init|=
name|atLeast
argument_list|(
literal|100
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
name|ITERS
condition|;
name|i
operator|++
control|)
block|{
name|LightAutomaton
name|a
init|=
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|LightAutomaton
name|ra
init|=
name|SpecialOperations
operator|.
name|reverse
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|LightAutomaton
name|rra
init|=
name|SpecialOperations
operator|.
name|reverse
argument_list|(
name|ra
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
argument_list|,
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|rra
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testReverseRandom2
specifier|public
name|void
name|testReverseRandom2
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|ITERS
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|ITERS
condition|;
name|iter
operator|++
control|)
block|{
comment|//System.out.println("TEST: iter=" + iter);
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|removeDeadTransitions
argument_list|(
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|LightAutomaton
name|ra
init|=
name|SpecialOperations
operator|.
name|reverse
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|LightAutomaton
name|rda
init|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|ra
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|rda
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|RandomAcceptedStringsLight
name|rasl
init|=
operator|new
name|RandomAcceptedStringsLight
argument_list|(
name|a
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter2
init|=
literal|0
init|;
name|iter2
operator|<
literal|20
condition|;
name|iter2
operator|++
control|)
block|{
comment|// Find string accepted by original automaton
name|int
index|[]
name|s
init|=
name|rasl
operator|.
name|getRandomAcceptedString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// Reverse it
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|s
operator|.
name|length
operator|/
literal|2
condition|;
name|j
operator|++
control|)
block|{
name|int
name|x
init|=
name|s
index|[
name|j
index|]
decl_stmt|;
name|s
index|[
name|j
index|]
operator|=
name|s
index|[
name|s
operator|.
name|length
operator|-
name|j
operator|-
literal|1
index|]
expr_stmt|;
name|s
index|[
name|s
operator|.
name|length
operator|-
name|j
operator|-
literal|1
index|]
operator|=
name|x
expr_stmt|;
block|}
comment|//System.out.println("TEST:   iter2=" + iter2 + " s=" + Arrays.toString(s));
comment|// Make sure reversed automaton accepts it
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|rda
argument_list|,
operator|new
name|IntsRef
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testAnyStringEmptyString
specifier|public
name|void
name|testAnyStringEmptyString
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|BasicAutomata
operator|.
name|makeAnyStringLight
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemoveDeadTransitionsEmpty
specifier|public
name|void
name|testRemoveDeadTransitionsEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
name|BasicAutomata
operator|.
name|makeEmptyLight
argument_list|()
decl_stmt|;
name|LightAutomaton
name|a2
init|=
name|BasicOperations
operator|.
name|removeDeadTransitions
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a2
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidAddTransition
specifier|public
name|void
name|testInvalidAddTransition
parameter_list|()
throws|throws
name|Exception
block|{
name|LightAutomaton
name|a
init|=
operator|new
name|LightAutomaton
argument_list|()
decl_stmt|;
name|int
name|s1
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|s2
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|,
literal|'a'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|s2
argument_list|,
name|s2
argument_list|,
literal|'a'
argument_list|)
expr_stmt|;
try|try
block|{
name|a
operator|.
name|addTransition
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|,
literal|'b'
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testBuilderRandom
specifier|public
name|void
name|testBuilderRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|ITERS
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|ITERS
condition|;
name|iter
operator|++
control|)
block|{
name|LightAutomaton
name|a
init|=
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// Just get all transitions, shuffle, and build a new automaton with the same transitions:
name|List
argument_list|<
name|LightAutomaton
operator|.
name|Transition
argument_list|>
name|allTrans
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numStates
init|=
name|a
operator|.
name|getNumStates
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|0
init|;
name|s
operator|<
name|numStates
condition|;
name|s
operator|++
control|)
block|{
name|int
name|count
init|=
name|a
operator|.
name|getNumTransitions
argument_list|(
name|s
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|LightAutomaton
operator|.
name|Transition
name|t
init|=
operator|new
name|LightAutomaton
operator|.
name|Transition
argument_list|()
decl_stmt|;
name|a
operator|.
name|getTransition
argument_list|(
name|s
argument_list|,
name|i
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|allTrans
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|LightAutomaton
operator|.
name|Builder
name|builder
init|=
operator|new
name|LightAutomaton
operator|.
name|Builder
argument_list|()
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
name|numStates
condition|;
name|i
operator|++
control|)
block|{
name|int
name|s
init|=
name|builder
operator|.
name|createState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setAccept
argument_list|(
name|s
argument_list|,
name|a
operator|.
name|isAccept
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|allTrans
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|LightAutomaton
operator|.
name|Transition
name|t
range|:
name|allTrans
control|)
block|{
name|builder
operator|.
name|addTransition
argument_list|(
name|t
operator|.
name|source
argument_list|,
name|t
operator|.
name|dest
argument_list|,
name|t
operator|.
name|min
argument_list|,
name|t
operator|.
name|max
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
argument_list|,
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|builder
operator|.
name|finish
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
