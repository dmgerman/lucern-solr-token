begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
package|;
end_package
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
name|util
operator|.
name|CharArrayMap
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
name|util
operator|.
name|CharArraySet
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
begin_class
DECL|class|TestCharArrayMap
specifier|public
class|class
name|TestCharArrayMap
extends|extends
name|LuceneTestCase
block|{
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
argument_list|<
name|Integer
argument_list|>
name|map
init|=
operator|new
name|CharArrayMap
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|1
argument_list|,
name|ignoreCase
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|hmap
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
name|random
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
name|random
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
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
else|:
name|keyStr
decl_stmt|;
name|int
name|val
init|=
name|random
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
operator|.
name|intValue
argument_list|()
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
operator|.
name|intValue
argument_list|()
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
operator|.
name|intValue
argument_list|()
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
operator|.
name|intValue
argument_list|()
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
block|}
DECL|method|testCharArrayMap
specifier|public
name|void
name|testCharArrayMap
parameter_list|()
block|{
name|int
name|num
init|=
literal|5
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
name|TEST_VERSION_CURRENT
argument_list|,
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
operator|.
name|size
argument_list|()
argument_list|,
name|cm
operator|.
name|size
argument_list|()
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
operator|.
name|size
argument_list|()
argument_list|,
name|cm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|CharArraySet
name|cs
init|=
name|cm
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|cs
control|)
block|{
name|assertTrue
argument_list|(
name|cm
operator|.
name|containsKey
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
name|char
index|[]
name|co
init|=
operator|(
name|char
index|[]
operator|)
name|o
decl_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|containsKey
argument_list|(
name|co
argument_list|,
literal|0
argument_list|,
name|co
operator|.
name|length
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
name|assertEquals
argument_list|(
name|hm
operator|.
name|size
argument_list|()
argument_list|,
name|cs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cm
operator|.
name|size
argument_list|()
argument_list|,
name|cs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cs
operator|.
name|size
argument_list|()
argument_list|)
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
try|try
block|{
name|cs
operator|.
name|add
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"keySet() allows adding new keys"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ue
parameter_list|)
block|{
comment|// pass
block|}
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
operator|.
name|size
argument_list|()
argument_list|,
name|cs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cm
operator|.
name|size
argument_list|()
argument_list|,
name|cs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Object
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
name|n
operator|=
literal|0
expr_stmt|;
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
name|Object
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
name|Object
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
name|cm
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
name|assertEquals
argument_list|(
name|cm
operator|.
name|size
argument_list|()
argument_list|,
name|n
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
name|entrySet
argument_list|()
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
name|entrySet
argument_list|()
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cm
operator|.
name|entrySet
argument_list|()
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
DECL|method|testModifyOnUnmodifiable
specifier|public
name|void
name|testModifyOnUnmodifiable
parameter_list|()
block|{
name|CharArrayMap
argument_list|<
name|Integer
argument_list|>
name|map
init|=
operator|new
name|CharArrayMap
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|map
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|=
name|CharArrayMap
operator|.
name|unmodifiableMap
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Map size changed due to unmodifiableMap call"
argument_list|,
name|size
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|NOT_IN_MAP
init|=
literal|"SirGallahad"
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Test String already exists in map"
argument_list|,
name|map
operator|.
name|containsKey
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Test String already exists in map"
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|map
operator|.
name|put
argument_list|(
name|NOT_IN_MAP
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable map"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Test String has been added to unmodifiable map"
argument_list|,
name|map
operator|.
name|containsKey
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Test String has been added to unmodifiable map"
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of unmodifiable map has changed"
argument_list|,
name|size
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|map
operator|.
name|put
argument_list|(
name|NOT_IN_MAP
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable map"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Test String has been added to unmodifiable map"
argument_list|,
name|map
operator|.
name|containsKey
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Test String has been added to unmodifiable map"
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of unmodifiable map has changed"
argument_list|,
name|size
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|map
operator|.
name|put
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable map"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Test String has been added to unmodifiable map"
argument_list|,
name|map
operator|.
name|containsKey
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Test String has been added to unmodifiable map"
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of unmodifiable map has changed"
argument_list|,
name|size
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable map"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertEquals
argument_list|(
literal|"Size of unmodifiable map has changed"
argument_list|,
name|size
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable map"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertEquals
argument_list|(
literal|"Size of unmodifiable map has changed"
argument_list|,
name|size
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable map"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertEquals
argument_list|(
literal|"Size of unmodifiable map has changed"
argument_list|,
name|size
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|map
operator|.
name|put
argument_list|(
operator|(
name|Object
operator|)
name|NOT_IN_MAP
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable map"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Test String has been added to unmodifiable map"
argument_list|,
name|map
operator|.
name|containsKey
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Test String has been added to unmodifiable map"
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of unmodifiable map has changed"
argument_list|,
name|size
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|map
operator|.
name|putAll
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
name|NOT_IN_MAP
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Modified unmodifiable map"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Test String has been added to unmodifiable map"
argument_list|,
name|map
operator|.
name|containsKey
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Test String has been added to unmodifiable map"
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|NOT_IN_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of unmodifiable map has changed"
argument_list|,
name|size
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
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
name|TEST_VERSION_CURRENT
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[test]"
argument_list|,
name|cm
operator|.
name|keySet
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1]"
argument_list|,
name|cm
operator|.
name|values
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[test=1]"
argument_list|,
name|cm
operator|.
name|entrySet
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{test=1}"
argument_list|,
name|cm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|put
argument_list|(
literal|"test2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|keySet
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|", "
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|values
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|", "
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|entrySet
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|", "
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|", "
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
