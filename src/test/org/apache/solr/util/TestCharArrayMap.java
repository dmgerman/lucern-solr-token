begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|analysis
operator|.
name|StopAnalyzer
import|;
end_import
begin_class
DECL|class|TestCharArrayMap
specifier|public
class|class
name|TestCharArrayMap
extends|extends
name|TestCase
block|{
DECL|field|r
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|doRandom
specifier|public
name|void
name|doRandom
parameter_list|(
name|int
name|iter
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|CharArrayMap
name|map
init|=
operator|new
name|CharArrayMap
argument_list|(
literal|1
argument_list|,
name|ignoreCase
argument_list|)
decl_stmt|;
name|HashMap
name|hmap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|char
index|[]
name|key
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|len
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|key
operator|=
operator|new
name|char
index|[
name|len
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|key
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|key
index|[
name|j
index|]
operator|=
operator|(
name|char
operator|)
name|r
operator|.
name|nextInt
argument_list|(
literal|127
argument_list|)
expr_stmt|;
block|}
name|String
name|keyStr
init|=
operator|new
name|String
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|String
name|hmapKey
init|=
name|ignoreCase
condition|?
name|keyStr
operator|.
name|toLowerCase
argument_list|()
else|:
name|keyStr
decl_stmt|;
name|int
name|val
init|=
name|r
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|Object
name|o1
init|=
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
decl_stmt|;
name|Object
name|o2
init|=
name|hmap
operator|.
name|put
argument_list|(
name|hmapKey
argument_list|,
name|val
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|o1
argument_list|,
name|o2
argument_list|)
expr_stmt|;
comment|// add it again with the string method
name|assertEquals
argument_list|(
name|val
argument_list|,
name|map
operator|.
name|put
argument_list|(
name|keyStr
argument_list|,
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|val
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|,
literal|0
argument_list|,
name|key
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|val
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|val
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|keyStr
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hmap
operator|.
name|size
argument_list|()
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|map
argument_list|,
name|hmap
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hmap
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
DECL|method|testCharArrayMap
specifier|public
name|void
name|testCharArrayMap
parameter_list|()
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
comment|// pump this up for more random testing
name|doRandom
argument_list|(
literal|1000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|doRandom
argument_list|(
literal|1000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMethods
specifier|public
name|void
name|testMethods
parameter_list|()
block|{
name|CharArrayMap
argument_list|<
name|Integer
argument_list|>
name|cm
init|=
operator|new
name|CharArrayMap
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|hm
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|hm
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|hm
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|cm
operator|.
name|putAll
argument_list|(
name|hm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hm
argument_list|,
name|cm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cm
argument_list|,
name|hm
argument_list|)
expr_stmt|;
name|hm
operator|.
name|put
argument_list|(
literal|"baz"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hm
operator|.
name|equals
argument_list|(
name|cm
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cm
operator|.
name|equals
argument_list|(
name|hm
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|equals
argument_list|(
name|cm
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|putAll
argument_list|(
name|hm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hm
argument_list|,
name|cm
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|iter1
init|=
name|cm
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iter1
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
init|=
name|iter1
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Integer
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|hm
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setValue
argument_list|(
name|val
operator|*
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|val
operator|*
literal|100
argument_list|,
operator|(
name|int
operator|)
name|cm
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|n
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|hm
operator|.
name|size
argument_list|()
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|cm
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cm
operator|.
name|putAll
argument_list|(
name|hm
argument_list|)
expr_stmt|;
name|CharArrayMap
argument_list|<
name|Integer
argument_list|>
operator|.
name|EntryIterator
name|iter2
init|=
name|cm
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|n
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|iter2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|char
index|[]
name|keyc
init|=
name|iter2
operator|.
name|nextKey
argument_list|()
decl_stmt|;
name|Integer
name|val
init|=
name|iter2
operator|.
name|currentValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|hm
operator|.
name|get
argument_list|(
operator|new
name|String
argument_list|(
name|keyc
argument_list|)
argument_list|)
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|iter2
operator|.
name|setValue
argument_list|(
name|val
operator|*
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|val
operator|*
literal|100
argument_list|,
operator|(
name|int
operator|)
name|cm
operator|.
name|get
argument_list|(
name|keyc
argument_list|)
argument_list|)
expr_stmt|;
name|n
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|hm
operator|.
name|size
argument_list|()
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|cm
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// performance test vs HashMap<String,Object>
comment|// HashMap will have an edge because we are testing with
comment|// non-dynamically created keys and String caches hashCode
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|int
name|a
init|=
literal|0
decl_stmt|;
name|String
name|impl
init|=
name|args
index|[
name|a
operator|++
index|]
operator|.
name|intern
argument_list|()
decl_stmt|;
comment|// hash OR chars OR char
name|int
name|iter1
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|a
operator|++
index|]
argument_list|)
decl_stmt|;
comment|// iterations of put()
name|int
name|iter2
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|a
operator|++
index|]
argument_list|)
decl_stmt|;
comment|// iterations of get()
name|int
name|ret
init|=
literal|0
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|stopwords
init|=
operator|(
name|Set
argument_list|<
name|String
argument_list|>
operator|)
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
decl_stmt|;
comment|// words = "this is a different test to see what is really going on here... I hope it works well but I'm not sure it will".split(" ");
name|char
index|[]
index|[]
name|stopwordschars
init|=
operator|new
name|char
index|[
name|stopwords
operator|.
name|size
argument_list|()
index|]
index|[]
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|stopwords
operator|.
name|iterator
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
name|stopwords
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|stopwordschars
index|[
name|i
index|]
operator|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|testwords
init|=
literal|"now is the time for all good men to come to the aid of their country"
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
comment|// testwords = "this is a different test to see what is really going on here... I hope it works well but I'm not sure it will".split(" ");
name|char
index|[]
index|[]
name|testwordchars
init|=
operator|new
name|char
index|[
name|testwords
operator|.
name|length
index|]
index|[]
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
name|testwordchars
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|testwordchars
index|[
name|i
index|]
operator|=
name|testwords
index|[
name|i
index|]
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|hm
init|=
literal|null
decl_stmt|;
name|CharArrayMap
argument_list|<
name|Integer
argument_list|>
name|cm
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|impl
operator|==
literal|"hash"
condition|)
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
name|iter1
condition|;
name|i
operator|++
control|)
block|{
name|hm
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|int
name|v
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|word
range|:
name|stopwords
control|)
block|{
name|hm
operator|.
name|put
argument_list|(
name|word
argument_list|,
operator|++
name|v
argument_list|)
expr_stmt|;
block|}
name|ret
operator|+=
name|hm
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|impl
operator|==
literal|"chars"
condition|)
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
name|iter1
condition|;
name|i
operator|++
control|)
block|{
name|cm
operator|=
operator|new
name|CharArrayMap
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|v
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|stopwords
control|)
block|{
name|cm
operator|.
name|put
argument_list|(
name|s
argument_list|,
operator|++
name|v
argument_list|)
expr_stmt|;
block|}
name|ret
operator|+=
name|cm
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|impl
operator|==
literal|"char"
condition|)
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
name|iter1
condition|;
name|i
operator|++
control|)
block|{
name|cm
operator|=
operator|new
name|CharArrayMap
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|v
init|=
literal|0
decl_stmt|;
for|for
control|(
name|char
index|[]
name|s
range|:
name|stopwordschars
control|)
block|{
name|cm
operator|.
name|put
argument_list|(
name|s
argument_list|,
operator|++
name|v
argument_list|)
expr_stmt|;
block|}
name|ret
operator|+=
name|cm
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|impl
operator|==
literal|"hash"
condition|)
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
name|iter2
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|String
name|word
range|:
name|testwords
control|)
block|{
name|Integer
name|v
init|=
name|hm
operator|.
name|get
argument_list|(
name|word
argument_list|)
decl_stmt|;
name|ret
operator|+=
name|v
operator|==
literal|null
condition|?
literal|0
else|:
name|v
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|impl
operator|==
literal|"chars"
condition|)
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
name|iter2
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|String
name|word
range|:
name|testwords
control|)
block|{
name|Integer
name|v
init|=
name|cm
operator|.
name|get
argument_list|(
name|word
argument_list|)
decl_stmt|;
name|ret
operator|+=
name|v
operator|==
literal|null
condition|?
literal|0
else|:
name|v
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|impl
operator|==
literal|"char"
condition|)
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
name|iter2
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|char
index|[]
name|word
range|:
name|testwordchars
control|)
block|{
name|Integer
name|v
init|=
name|cm
operator|.
name|get
argument_list|(
name|word
argument_list|)
decl_stmt|;
name|ret
operator|+=
name|v
operator|==
literal|null
condition|?
literal|0
else|:
name|v
expr_stmt|;
block|}
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"result="
operator|+
name|ret
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"time="
operator|+
operator|(
name|end
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
