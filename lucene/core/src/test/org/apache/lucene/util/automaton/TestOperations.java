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
name|*
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
name|*
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
name|fst
operator|.
name|Util
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
name|generators
operator|.
name|RandomInts
import|;
end_import
begin_class
DECL|class|TestOperations
specifier|public
class|class
name|TestOperations
extends|extends
name|LuceneTestCase
block|{
comment|/** Test string union. */
DECL|method|testStringUnion
specifier|public
name|void
name|testStringUnion
parameter_list|()
block|{
name|List
argument_list|<
name|BytesRef
argument_list|>
name|strings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|)
init|;
operator|--
name|i
operator|>=
literal|0
condition|;
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|strings
argument_list|)
expr_stmt|;
name|Automaton
name|union
init|=
name|Automata
operator|.
name|makeStringUnion
argument_list|(
name|strings
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|union
operator|.
name|isDeterministic
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Operations
operator|.
name|hasDeadStatesFromInitial
argument_list|(
name|union
argument_list|)
argument_list|)
expr_stmt|;
name|Automaton
name|naiveUnion
init|=
name|naiveUnion
argument_list|(
name|strings
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|naiveUnion
operator|.
name|isDeterministic
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Operations
operator|.
name|hasDeadStatesFromInitial
argument_list|(
name|naiveUnion
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|union
argument_list|,
name|naiveUnion
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|naiveUnion
specifier|private
specifier|static
name|Automaton
name|naiveUnion
parameter_list|(
name|List
argument_list|<
name|BytesRef
argument_list|>
name|strings
parameter_list|)
block|{
name|Automaton
index|[]
name|eachIndividual
init|=
operator|new
name|Automaton
index|[
name|strings
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|bref
range|:
name|strings
control|)
block|{
name|eachIndividual
index|[
name|i
operator|++
index|]
operator|=
name|Automata
operator|.
name|makeString
argument_list|(
name|bref
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Operations
operator|.
name|determinize
argument_list|(
name|Operations
operator|.
name|union
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|eachIndividual
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/** Test concatenation with empty language returns empty */
DECL|method|testEmptyLanguageConcatenate
specifier|public
name|void
name|testEmptyLanguageConcatenate
parameter_list|()
block|{
name|Automaton
name|a
init|=
name|Automata
operator|.
name|makeString
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|Automaton
name|concat
init|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|a
argument_list|,
name|Automata
operator|.
name|makeEmpty
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|isEmpty
argument_list|(
name|concat
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test optimization to concatenate() with empty String to an NFA */
DECL|method|testEmptySingletonNFAConcatenate
specifier|public
name|void
name|testEmptySingletonNFAConcatenate
parameter_list|()
block|{
name|Automaton
name|singleton
init|=
name|Automata
operator|.
name|makeString
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|Automaton
name|expandedSingleton
init|=
name|singleton
decl_stmt|;
comment|// an NFA (two transitions for 't' from initial state)
name|Automaton
name|nfa
init|=
name|Operations
operator|.
name|union
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
literal|"this"
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeString
argument_list|(
literal|"three"
argument_list|)
argument_list|)
decl_stmt|;
name|Automaton
name|concat1
init|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|expandedSingleton
argument_list|,
name|nfa
argument_list|)
decl_stmt|;
name|Automaton
name|concat2
init|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|singleton
argument_list|,
name|nfa
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|concat2
operator|.
name|isDeterministic
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|Operations
operator|.
name|determinize
argument_list|(
name|concat1
argument_list|)
argument_list|,
name|Operations
operator|.
name|determinize
argument_list|(
name|concat2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|Operations
operator|.
name|determinize
argument_list|(
name|nfa
argument_list|)
argument_list|,
name|Operations
operator|.
name|determinize
argument_list|(
name|concat1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|Operations
operator|.
name|determinize
argument_list|(
name|nfa
argument_list|)
argument_list|,
name|Operations
operator|.
name|determinize
argument_list|(
name|concat2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetRandomAcceptedString
specifier|public
name|void
name|testGetRandomAcceptedString
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|int
name|ITER1
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ITER2
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
name|ITER1
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RegExp
name|re
init|=
operator|new
name|RegExp
argument_list|(
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
comment|//System.out.println("TEST i=" + i + " re=" + re);
specifier|final
name|Automaton
name|a
init|=
name|Operations
operator|.
name|determinize
argument_list|(
name|re
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|Operations
operator|.
name|isEmpty
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AutomatonTestUtil
operator|.
name|RandomAcceptedStrings
name|rx
init|=
operator|new
name|AutomatonTestUtil
operator|.
name|RandomAcceptedStrings
argument_list|(
name|a
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ITER2
condition|;
name|j
operator|++
control|)
block|{
comment|//System.out.println("TEST: j=" + j);
name|int
index|[]
name|acc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|acc
operator|=
name|rx
operator|.
name|getRandomAcceptedString
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|s
init|=
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|acc
argument_list|,
literal|0
argument_list|,
name|acc
operator|.
name|length
argument_list|)
decl_stmt|;
comment|//a.writeDot("adot");
name|assertTrue
argument_list|(
name|Operations
operator|.
name|run
argument_list|(
name|a
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"regexp: "
operator|+
name|re
argument_list|)
expr_stmt|;
if|if
condition|(
name|acc
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"fail acc re="
operator|+
name|re
operator|+
literal|" count="
operator|+
name|acc
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|acc
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|acc
index|[
name|k
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
name|t
throw|;
block|}
block|}
block|}
block|}
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
argument_list|()
argument_list|)
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
name|Operations
operator|.
name|isFinite
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Pass false for testRecursive if the expected strings    *  may be too long */
DECL|method|getFiniteStrings
specifier|private
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|getFiniteStrings
parameter_list|(
name|Automaton
name|a
parameter_list|,
name|int
name|limit
parameter_list|,
name|boolean
name|testRecursive
parameter_list|)
block|{
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|result
init|=
name|Operations
operator|.
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
name|limit
argument_list|)
decl_stmt|;
if|if
condition|(
name|testRecursive
condition|)
block|{
name|assertEquals
argument_list|(
name|AutomatonTestUtil
operator|.
name|getFiniteStringsRecursive
argument_list|(
name|a
argument_list|,
name|limit
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Basic test for getFiniteStrings    */
DECL|method|testFiniteStringsBasic
specifier|public
name|void
name|testFiniteStringsBasic
parameter_list|()
block|{
name|Automaton
name|a
init|=
name|Operations
operator|.
name|union
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
literal|"dog"
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeString
argument_list|(
literal|"duck"
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|strings
init|=
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|strings
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRef
name|dog
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"dog"
argument_list|)
argument_list|,
name|dog
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strings
operator|.
name|contains
argument_list|(
name|dog
argument_list|)
argument_list|)
expr_stmt|;
name|IntsRef
name|duck
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"duck"
argument_list|)
argument_list|,
name|duck
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strings
operator|.
name|contains
argument_list|(
name|duck
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFiniteStringsEatsStack
specifier|public
name|void
name|testFiniteStringsEatsStack
parameter_list|()
block|{
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
literal|50000
index|]
decl_stmt|;
name|TestUtil
operator|.
name|randomFixedLengthUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|bigString1
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|randomFixedLengthUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|bigString2
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
decl_stmt|;
name|Automaton
name|a
init|=
name|Operations
operator|.
name|union
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
name|bigString1
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeString
argument_list|(
name|bigString2
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|strings
init|=
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|strings
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toUTF32
argument_list|(
name|bigString1
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bigString1
operator|.
name|length
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strings
operator|.
name|contains
argument_list|(
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
name|Util
operator|.
name|toUTF32
argument_list|(
name|bigString2
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bigString2
operator|.
name|length
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strings
operator|.
name|contains
argument_list|(
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomFiniteStrings1
specifier|public
name|void
name|testRandomFiniteStrings1
parameter_list|()
block|{
name|int
name|numStrings
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: numStrings="
operator|+
name|numStrings
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|strings
init|=
operator|new
name|HashSet
argument_list|<
name|IntsRef
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Automaton
argument_list|>
name|automata
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|numStrings
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|automata
operator|.
name|add
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toUTF32
argument_list|(
name|s
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|strings
operator|.
name|add
argument_list|(
name|scratch
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  add string="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO: we could sometimes use
comment|// DaciukMihovAutomatonBuilder here
comment|// TODO: what other random things can we do here...
name|Automaton
name|a
init|=
name|Operations
operator|.
name|union
argument_list|(
name|automata
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|a
operator|=
name|MinimizationOperations
operator|.
name|minimize
argument_list|(
name|a
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: a.minimize numStates="
operator|+
name|a
operator|.
name|getNumStates
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: a.determinize"
argument_list|)
expr_stmt|;
block|}
name|a
operator|=
name|Operations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: a.removeDeadStates"
argument_list|)
expr_stmt|;
block|}
name|a
operator|=
name|Operations
operator|.
name|removeDeadStates
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|actual
init|=
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|strings
operator|.
name|equals
argument_list|(
name|actual
argument_list|)
operator|==
literal|false
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"strings.size()="
operator|+
name|strings
operator|.
name|size
argument_list|()
operator|+
literal|" actual.size="
operator|+
name|actual
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IntsRef
argument_list|>
name|x
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|strings
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IntsRef
argument_list|>
name|y
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|actual
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|y
argument_list|)
expr_stmt|;
name|int
name|end
init|=
name|Math
operator|.
name|min
argument_list|(
name|x
operator|.
name|size
argument_list|()
argument_list|,
name|y
operator|.
name|size
argument_list|()
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
name|end
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  i="
operator|+
name|i
operator|+
literal|" string="
operator|+
name|toString
argument_list|(
name|x
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|+
literal|" actual="
operator|+
name|toString
argument_list|(
name|y
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"wrong strings found"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ascii only!
DECL|method|toString
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|IntsRef
name|ints
parameter_list|)
block|{
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
argument_list|(
name|ints
operator|.
name|length
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
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|br
operator|.
name|bytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|ints
operator|.
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
name|br
operator|.
name|length
operator|=
name|ints
operator|.
name|length
expr_stmt|;
return|return
name|br
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
DECL|method|testWithCycle
specifier|public
name|void
name|testWithCycle
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Operations
operator|.
name|getFiniteStrings
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"abc.*"
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testRandomFiniteStrings2
specifier|public
name|void
name|testRandomFiniteStrings2
parameter_list|()
block|{
comment|// Just makes sure we can run on any random finite
comment|// automaton:
name|int
name|iters
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
name|iters
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
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Must pass a limit because the random automaton
comment|// can accept MANY strings:
name|Operations
operator|.
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
comment|// NOTE: cannot do this, because the method is not
comment|// guaranteed to detect cycles when you have a limit
comment|//assertTrue(Operations.isFinite(a));
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|Operations
operator|.
name|isFinite
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testInvalidLimit
specifier|public
name|void
name|testInvalidLimit
parameter_list|()
block|{
name|Automaton
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
try|try
block|{
name|Operations
operator|.
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
operator|-
literal|7
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testInvalidLimit2
specifier|public
name|void
name|testInvalidLimit2
parameter_list|()
block|{
name|Automaton
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
try|try
block|{
name|Operations
operator|.
name|getFiniteStrings
argument_list|(
name|a
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testSingletonNoLimit
specifier|public
name|void
name|testSingletonNoLimit
parameter_list|()
block|{
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|result
init|=
name|Operations
operator|.
name|getFiniteStrings
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toUTF32
argument_list|(
literal|"foobar"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingletonLimit1
specifier|public
name|void
name|testSingletonLimit1
parameter_list|()
block|{
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|result
init|=
name|Operations
operator|.
name|getFiniteStrings
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toUTF32
argument_list|(
literal|"foobar"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|contains
argument_list|(
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
