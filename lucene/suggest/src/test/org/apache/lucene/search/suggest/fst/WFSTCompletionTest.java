begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
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
name|search
operator|.
name|suggest
operator|.
name|Lookup
operator|.
name|LookupResult
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
name|search
operator|.
name|suggest
operator|.
name|Input
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
name|search
operator|.
name|suggest
operator|.
name|InputArrayIterator
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
name|BytesRef
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
begin_class
DECL|class|WFSTCompletionTest
specifier|public
class|class
name|WFSTCompletionTest
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
name|Input
name|keys
index|[]
init|=
operator|new
name|Input
index|[]
block|{
operator|new
name|Input
argument_list|(
literal|"foo"
argument_list|,
literal|50
argument_list|)
block|,
operator|new
name|Input
argument_list|(
literal|"bar"
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|Input
argument_list|(
literal|"barbar"
argument_list|,
literal|12
argument_list|)
block|,
operator|new
name|Input
argument_list|(
literal|"barbara"
argument_list|,
literal|6
argument_list|)
block|}
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|WFSTCompletionLookup
name|suggester
init|=
operator|new
name|WFSTCompletionLookup
argument_list|()
decl_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
comment|// top N of 2, but only foo is available
name|List
argument_list|<
name|LookupResult
argument_list|>
name|results
init|=
name|suggester
operator|.
name|lookup
argument_list|(
name|_TestUtil
operator|.
name|stringToCharSequence
argument_list|(
literal|"f"
argument_list|,
name|random
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|,
literal|0.01F
argument_list|)
expr_stmt|;
comment|// make sure we don't get a dup exact suggestion:
name|results
operator|=
name|suggester
operator|.
name|lookup
argument_list|(
name|_TestUtil
operator|.
name|stringToCharSequence
argument_list|(
literal|"foo"
argument_list|,
name|random
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|,
literal|0.01F
argument_list|)
expr_stmt|;
comment|// top N of 1 for 'bar': we return this even though barbar is higher
name|results
operator|=
name|suggester
operator|.
name|lookup
argument_list|(
name|_TestUtil
operator|.
name|stringToCharSequence
argument_list|(
literal|"bar"
argument_list|,
name|random
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|,
literal|0.01F
argument_list|)
expr_stmt|;
comment|// top N Of 2 for 'b'
name|results
operator|=
name|suggester
operator|.
name|lookup
argument_list|(
name|_TestUtil
operator|.
name|stringToCharSequence
argument_list|(
literal|"b"
argument_list|,
name|random
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"barbar"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|,
literal|0.01F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|value
argument_list|,
literal|0.01F
argument_list|)
expr_stmt|;
comment|// top N of 3 for 'ba'
name|results
operator|=
name|suggester
operator|.
name|lookup
argument_list|(
name|_TestUtil
operator|.
name|stringToCharSequence
argument_list|(
literal|"ba"
argument_list|,
name|random
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"barbar"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|,
literal|0.01F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|value
argument_list|,
literal|0.01F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"barbara"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|value
argument_list|,
literal|0.01F
argument_list|)
expr_stmt|;
block|}
DECL|method|testExactFirst
specifier|public
name|void
name|testExactFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|WFSTCompletionLookup
name|suggester
init|=
operator|new
name|WFSTCompletionLookup
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
operator|new
name|Input
index|[]
block|{
operator|new
name|Input
argument_list|(
literal|"x y"
argument_list|,
literal|20
argument_list|)
block|,
operator|new
name|Input
argument_list|(
literal|"x"
argument_list|,
literal|2
argument_list|)
block|,         }
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|topN
init|=
literal|1
init|;
name|topN
operator|<
literal|4
condition|;
name|topN
operator|++
control|)
block|{
name|List
argument_list|<
name|LookupResult
argument_list|>
name|results
init|=
name|suggester
operator|.
name|lookup
argument_list|(
literal|"x"
argument_list|,
literal|false
argument_list|,
name|topN
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|topN
argument_list|,
literal|2
argument_list|)
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|topN
operator|>
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
literal|"x y"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testNonExactFirst
specifier|public
name|void
name|testNonExactFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|WFSTCompletionLookup
name|suggester
init|=
operator|new
name|WFSTCompletionLookup
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
operator|new
name|Input
index|[]
block|{
operator|new
name|Input
argument_list|(
literal|"x y"
argument_list|,
literal|20
argument_list|)
block|,
operator|new
name|Input
argument_list|(
literal|"x"
argument_list|,
literal|2
argument_list|)
block|,         }
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|topN
init|=
literal|1
init|;
name|topN
operator|<
literal|4
condition|;
name|topN
operator|++
control|)
block|{
name|List
argument_list|<
name|LookupResult
argument_list|>
name|results
init|=
name|suggester
operator|.
name|lookup
argument_list|(
literal|"x"
argument_list|,
literal|false
argument_list|,
name|topN
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|topN
argument_list|,
literal|2
argument_list|)
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x y"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|topN
operator|>
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numWords
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|slowCompletor
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|TreeSet
argument_list|<
name|String
argument_list|>
name|allPrefixes
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Input
index|[]
name|keys
init|=
operator|new
name|Input
index|[
name|numWords
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
name|numWords
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// TODO: would be nice to fix this slowCompletor/comparator to
comment|// use full range, but we might lose some coverage too...
name|s
operator|=
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|slowCompletor
operator|.
name|containsKey
argument_list|(
name|s
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|allPrefixes
operator|.
name|add
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// we can probably do Integer.MAX_VALUE here, but why worry.
name|int
name|weight
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1
operator|<<
literal|24
argument_list|)
decl_stmt|;
name|slowCompletor
operator|.
name|put
argument_list|(
name|s
argument_list|,
operator|(
name|long
operator|)
name|weight
argument_list|)
expr_stmt|;
name|keys
index|[
name|i
index|]
operator|=
operator|new
name|Input
argument_list|(
name|s
argument_list|,
name|weight
argument_list|)
expr_stmt|;
block|}
name|WFSTCompletionLookup
name|suggester
init|=
operator|new
name|WFSTCompletionLookup
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numWords
argument_list|,
name|suggester
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|prefix
range|:
name|allPrefixes
control|)
block|{
specifier|final
name|int
name|topN
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LookupResult
argument_list|>
name|r
init|=
name|suggester
operator|.
name|lookup
argument_list|(
name|_TestUtil
operator|.
name|stringToCharSequence
argument_list|(
name|prefix
argument_list|,
name|random
argument_list|)
argument_list|,
literal|false
argument_list|,
name|topN
argument_list|)
decl_stmt|;
comment|// 2. go thru whole treemap (slowCompletor) and check its actually the best suggestion
specifier|final
name|List
argument_list|<
name|LookupResult
argument_list|>
name|matches
init|=
operator|new
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
argument_list|()
decl_stmt|;
comment|// TODO: could be faster... but its slowCompletor for a reason
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|e
range|:
name|slowCompletor
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|matches
operator|.
name|add
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|matches
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|matches
argument_list|,
operator|new
name|Comparator
argument_list|<
name|LookupResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|LookupResult
name|left
parameter_list|,
name|LookupResult
name|right
parameter_list|)
block|{
name|int
name|cmp
init|=
name|Float
operator|.
name|compare
argument_list|(
name|right
operator|.
name|value
argument_list|,
name|left
operator|.
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
return|return
name|left
operator|.
name|compareTo
argument_list|(
name|right
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|cmp
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|matches
operator|.
name|size
argument_list|()
operator|>
name|topN
condition|)
block|{
name|matches
operator|.
name|subList
argument_list|(
name|topN
argument_list|,
name|matches
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|matches
operator|.
name|size
argument_list|()
argument_list|,
name|r
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|hit
init|=
literal|0
init|;
name|hit
operator|<
name|r
operator|.
name|size
argument_list|()
condition|;
name|hit
operator|++
control|)
block|{
comment|//System.out.println("  check hit " + hit);
name|assertEquals
argument_list|(
name|matches
operator|.
name|get
argument_list|(
name|hit
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|,
name|r
operator|.
name|get
argument_list|(
name|hit
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|matches
operator|.
name|get
argument_list|(
name|hit
argument_list|)
operator|.
name|value
argument_list|,
name|r
operator|.
name|get
argument_list|(
name|hit
argument_list|)
operator|.
name|value
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|test0ByteKeys
specifier|public
name|void
name|test0ByteKeys
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesRef
name|key1
init|=
operator|new
name|BytesRef
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|key1
operator|.
name|length
operator|=
literal|4
expr_stmt|;
name|BytesRef
name|key2
init|=
operator|new
name|BytesRef
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|key1
operator|.
name|length
operator|=
literal|3
expr_stmt|;
name|WFSTCompletionLookup
name|suggester
init|=
operator|new
name|WFSTCompletionLookup
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
operator|new
name|Input
index|[]
block|{
operator|new
name|Input
argument_list|(
name|key1
argument_list|,
literal|50
argument_list|)
block|,
operator|new
name|Input
argument_list|(
name|key2
argument_list|,
literal|50
argument_list|)
block|,         }
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|WFSTCompletionLookup
name|suggester
init|=
operator|new
name|WFSTCompletionLookup
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
operator|new
name|Input
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|suggester
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LookupResult
argument_list|>
name|result
init|=
name|suggester
operator|.
name|lookup
argument_list|(
literal|"a"
argument_list|,
literal|false
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
