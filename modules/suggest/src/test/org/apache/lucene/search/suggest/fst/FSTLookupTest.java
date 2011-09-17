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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|List
import|;
end_import
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
name|fst
operator|.
name|FSTLookup
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
name|search
operator|.
name|suggest
operator|.
name|LookupBenchmarkTest
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
name|TermFreq
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
name|TermFreqArrayIterator
import|;
end_import
begin_comment
comment|/**  * Unit tests for {@link FSTLookup}.  */
end_comment
begin_class
DECL|class|FSTLookupTest
specifier|public
class|class
name|FSTLookupTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|tf
specifier|public
specifier|static
name|TermFreq
name|tf
parameter_list|(
name|String
name|t
parameter_list|,
name|float
name|v
parameter_list|)
block|{
return|return
operator|new
name|TermFreq
argument_list|(
name|t
argument_list|,
name|v
argument_list|)
return|;
block|}
DECL|field|lookup
specifier|private
name|FSTLookup
name|lookup
decl_stmt|;
DECL|method|setUp
specifier|public
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
name|lookup
operator|=
operator|new
name|FSTLookup
argument_list|()
expr_stmt|;
name|lookup
operator|.
name|build
argument_list|(
operator|new
name|TermFreqArrayIterator
argument_list|(
name|evalKeys
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|evalKeys
specifier|private
name|TermFreq
index|[]
name|evalKeys
parameter_list|()
block|{
specifier|final
name|TermFreq
index|[]
name|keys
init|=
operator|new
name|TermFreq
index|[]
block|{
name|tf
argument_list|(
literal|"one"
argument_list|,
literal|0.5f
argument_list|)
block|,
name|tf
argument_list|(
literal|"oneness"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"onerous"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"onesimus"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"two"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"twofold"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"twonk"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"thrive"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"through"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"threat"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"three"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"foundation"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"fourblah"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"fourteen"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"four"
argument_list|,
literal|0.5f
argument_list|)
block|,
name|tf
argument_list|(
literal|"fourier"
argument_list|,
literal|0.5f
argument_list|)
block|,
name|tf
argument_list|(
literal|"fourty"
argument_list|,
literal|0.5f
argument_list|)
block|,
name|tf
argument_list|(
literal|"xo"
argument_list|,
literal|1
argument_list|)
block|,       }
decl_stmt|;
return|return
name|keys
return|;
block|}
DECL|method|testExactMatchHighPriority
specifier|public
name|void
name|testExactMatchHighPriority
parameter_list|()
throws|throws
name|Exception
block|{
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|"two"
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"two/1.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExactMatchLowPriority
specifier|public
name|void
name|testExactMatchLowPriority
parameter_list|()
throws|throws
name|Exception
block|{
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|"one"
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"one/0.0"
argument_list|,
literal|"oneness/1.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRequestedCount
specifier|public
name|void
name|testRequestedCount
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 'one' is promoted after collecting two higher ranking results.
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|"one"
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"one/0.0"
argument_list|,
literal|"oneness/1.0"
argument_list|)
expr_stmt|;
comment|// 'one' is at the top after collecting all alphabetical results.
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|"one"
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"one/0.0"
argument_list|,
literal|"oneness/1.0"
argument_list|)
expr_stmt|;
comment|// 'four' is collected in a bucket and then again as an exact match.
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|"four"
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"four/0.0"
argument_list|,
literal|"fourblah/1.0"
argument_list|)
expr_stmt|;
comment|// Check reordering of exact matches.
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|"four"
argument_list|,
literal|true
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|"four/0.0"
argument_list|,
literal|"fourblah/1.0"
argument_list|,
literal|"fourteen/1.0"
argument_list|,
literal|"fourier/0.0"
argument_list|)
expr_stmt|;
name|lookup
operator|=
operator|new
name|FSTLookup
argument_list|(
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|lookup
operator|.
name|build
argument_list|(
operator|new
name|TermFreqArrayIterator
argument_list|(
name|evalKeys
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'one' is not promoted after collecting two higher ranking results.
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|"one"
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"oneness/1.0"
argument_list|,
literal|"onerous/1.0"
argument_list|)
expr_stmt|;
comment|// 'one' is at the top after collecting all alphabetical results.
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|"one"
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"one/0.0"
argument_list|,
literal|"oneness/1.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMiss
specifier|public
name|void
name|testMiss
parameter_list|()
throws|throws
name|Exception
block|{
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|"xyz"
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAlphabeticWithWeights
specifier|public
name|void
name|testAlphabeticWithWeights
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|lookup
operator|.
name|lookup
argument_list|(
literal|"xyz"
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFullMatchList
specifier|public
name|void
name|testFullMatchList
parameter_list|()
throws|throws
name|Exception
block|{
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|"one"
argument_list|,
literal|true
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
literal|"oneness/1.0"
argument_list|,
literal|"onerous/1.0"
argument_list|,
literal|"onesimus/1.0"
argument_list|,
literal|"one/0.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultilingualInput
specifier|public
name|void
name|testMultilingualInput
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|TermFreq
argument_list|>
name|input
init|=
name|LookupBenchmarkTest
operator|.
name|readTop50KWiki
argument_list|()
decl_stmt|;
name|lookup
operator|=
operator|new
name|FSTLookup
argument_list|()
expr_stmt|;
name|lookup
operator|.
name|build
argument_list|(
operator|new
name|TermFreqArrayIterator
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|TermFreq
name|tf
range|:
name|input
control|)
block|{
name|assertTrue
argument_list|(
literal|"Not found: "
operator|+
name|tf
operator|.
name|term
argument_list|,
name|lookup
operator|.
name|get
argument_list|(
name|tf
operator|.
name|term
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tf
operator|.
name|term
argument_list|,
name|lookup
operator|.
name|lookup
argument_list|(
name|tf
operator|.
name|term
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyInput
specifier|public
name|void
name|testEmptyInput
parameter_list|()
throws|throws
name|Exception
block|{
name|lookup
operator|=
operator|new
name|FSTLookup
argument_list|()
expr_stmt|;
name|lookup
operator|.
name|build
argument_list|(
operator|new
name|TermFreqArrayIterator
argument_list|(
operator|new
name|TermFreq
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertMatchEquals
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
literal|""
argument_list|,
literal|true
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|TermFreq
argument_list|>
name|freqs
init|=
operator|new
name|ArrayList
argument_list|<
name|TermFreq
argument_list|>
argument_list|()
decl_stmt|;
name|Random
name|rnd
init|=
name|random
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
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|freqs
operator|.
name|add
argument_list|(
operator|new
name|TermFreq
argument_list|(
literal|""
operator|+
name|rnd
operator|.
name|nextLong
argument_list|()
argument_list|,
name|rnd
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lookup
operator|=
operator|new
name|FSTLookup
argument_list|()
expr_stmt|;
name|lookup
operator|.
name|build
argument_list|(
operator|new
name|TermFreqArrayIterator
argument_list|(
name|freqs
operator|.
name|toArray
argument_list|(
operator|new
name|TermFreq
index|[
name|freqs
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|TermFreq
name|tf
range|:
name|freqs
control|)
block|{
specifier|final
name|String
name|term
init|=
name|tf
operator|.
name|term
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|term
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|prefix
init|=
name|term
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|LookupResult
name|lr
range|:
name|lookup
operator|.
name|lookup
argument_list|(
name|prefix
argument_list|,
literal|true
argument_list|,
literal|10
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|lr
operator|.
name|key
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|assertMatchEquals
specifier|private
name|void
name|assertMatchEquals
parameter_list|(
name|List
argument_list|<
name|LookupResult
argument_list|>
name|res
parameter_list|,
name|String
modifier|...
name|expected
parameter_list|)
block|{
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|res
operator|.
name|size
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
name|res
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|result
index|[
name|i
index|]
operator|=
name|res
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
condition|)
block|{
name|int
name|colLen
init|=
name|Math
operator|.
name|max
argument_list|(
name|maxLen
argument_list|(
name|expected
argument_list|)
argument_list|,
name|maxLen
argument_list|(
name|result
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|format
init|=
literal|"%"
operator|+
name|colLen
operator|+
literal|"s  "
operator|+
literal|"%"
operator|+
name|colLen
operator|+
literal|"s\n"
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
name|format
argument_list|,
literal|"Expected"
argument_list|,
literal|"Result"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|Math
operator|.
name|max
argument_list|(
name|result
operator|.
name|length
argument_list|,
name|expected
operator|.
name|length
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
name|format
argument_list|,
name|i
operator|<
name|expected
operator|.
name|length
condition|?
name|expected
index|[
name|i
index|]
else|:
literal|"--"
argument_list|,
name|i
operator|<
name|result
operator|.
name|length
condition|?
name|result
index|[
name|i
index|]
else|:
literal|"--"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected different output:\n"
operator|+
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|maxLen
specifier|private
name|int
name|maxLen
parameter_list|(
name|String
index|[]
name|result
parameter_list|)
block|{
name|int
name|len
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|result
control|)
name|len
operator|=
name|Math
operator|.
name|max
argument_list|(
name|len
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
block|}
end_class
end_unit
