begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
begin_class
DECL|class|TestStringIntern
specifier|public
class|class
name|TestStringIntern
extends|extends
name|LuceneTestCase
block|{
DECL|field|testStrings
name|String
index|[]
name|testStrings
decl_stmt|;
DECL|field|internedStrings
name|String
index|[]
name|internedStrings
decl_stmt|;
DECL|field|r
name|Random
name|r
init|=
name|newRandom
argument_list|()
decl_stmt|;
DECL|method|randStr
specifier|private
name|String
name|randStr
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|len
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|arr
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|26
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|arr
argument_list|)
return|;
block|}
DECL|method|makeStrings
specifier|private
name|void
name|makeStrings
parameter_list|(
name|int
name|sz
parameter_list|)
block|{
name|testStrings
operator|=
operator|new
name|String
index|[
name|sz
index|]
expr_stmt|;
name|internedStrings
operator|=
operator|new
name|String
index|[
name|sz
index|]
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|testStrings
index|[
name|i
index|]
operator|=
name|randStr
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
operator|+
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testStringIntern
specifier|public
name|void
name|testStringIntern
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|makeStrings
argument_list|(
literal|1024
operator|*
literal|10
argument_list|)
expr_stmt|;
comment|// something greater than the capacity of the default cache size
comment|// makeStrings(100);  // realistic for perf testing
name|int
name|nThreads
init|=
literal|20
decl_stmt|;
comment|// final int iter=100000;
specifier|final
name|int
name|iter
init|=
literal|1000000
decl_stmt|;
specifier|final
name|boolean
name|newStrings
init|=
literal|true
decl_stmt|;
comment|// try native intern
comment|// StringHelper.interner = new StringInterner();
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|nThreads
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
name|nThreads
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|seed
init|=
name|i
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|String
index|[]
name|myInterned
init|=
operator|new
name|String
index|[
name|testStrings
operator|.
name|length
index|]
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
name|iter
condition|;
name|j
operator|++
control|)
block|{
name|int
name|idx
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|testStrings
operator|.
name|length
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|testStrings
index|[
name|idx
index|]
decl_stmt|;
if|if
condition|(
name|newStrings
operator|==
literal|true
operator|&&
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
name|s
operator|=
operator|new
name|String
argument_list|(
name|s
argument_list|)
expr_stmt|;
comment|// make a copy half of the time
name|String
name|interned
init|=
name|StringHelper
operator|.
name|intern
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|String
name|prevInterned
init|=
name|myInterned
index|[
name|idx
index|]
decl_stmt|;
name|String
name|otherInterned
init|=
name|internedStrings
index|[
name|idx
index|]
decl_stmt|;
comment|// test against other threads
if|if
condition|(
name|otherInterned
operator|!=
literal|null
operator|&&
name|otherInterned
operator|!=
name|interned
condition|)
block|{
name|TestCase
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
name|internedStrings
index|[
name|idx
index|]
operator|=
name|interned
expr_stmt|;
comment|// test against local copy
if|if
condition|(
name|prevInterned
operator|!=
literal|null
operator|&&
name|prevInterned
operator|!=
name|interned
condition|)
block|{
name|TestCase
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
name|myInterned
index|[
name|idx
index|]
operator|=
name|interned
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nThreads
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
