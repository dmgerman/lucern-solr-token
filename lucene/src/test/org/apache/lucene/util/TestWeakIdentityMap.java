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
name|java
operator|.
name|util
operator|.
name|Map
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReferenceArray
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import
begin_class
DECL|class|TestWeakIdentityMap
specifier|public
class|class
name|TestWeakIdentityMap
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimpleHashMap
specifier|public
name|void
name|testSimpleHashMap
parameter_list|()
block|{
specifier|final
name|WeakIdentityMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|WeakIdentityMap
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|// we keep strong references to the keys,
comment|// so WeakIdentityMap will not forget about them:
name|String
name|key1
init|=
operator|new
name|String
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|String
name|key2
init|=
operator|new
name|String
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|String
name|key3
init|=
operator|new
name|String
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|key1
argument_list|,
name|key2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|key1
argument_list|,
name|key2
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|key1
argument_list|,
name|key3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|key1
argument_list|,
name|key3
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|key2
argument_list|,
name|key3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|key2
argument_list|,
name|key3
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key1
argument_list|,
literal|"bar1"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key2
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|null
argument_list|,
literal|"null"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar1"
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar2"
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"null"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|key1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|key2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|key3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// repeat and check that we have no double entries
name|map
operator|.
name|put
argument_list|(
name|key1
argument_list|,
literal|"bar1"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key2
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|null
argument_list|,
literal|"null"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar1"
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar2"
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"null"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|key1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|key2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|key3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|key1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key1
argument_list|,
literal|"bar1"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key2
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key3
argument_list|,
literal|"bar3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// clear strong refs
name|key1
operator|=
name|key2
operator|=
name|key3
operator|=
literal|null
expr_stmt|;
comment|// check that GC does not cause problems in reap() method, wait 1 second and let GC work:
name|int
name|size
init|=
name|map
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|size
operator|>
literal|0
operator|&&
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
try|try
block|{
name|System
operator|.
name|runFinalization
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|size
operator|>=
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|size
operator|=
name|map
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{}
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|key1
operator|=
operator|new
name|String
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|key2
operator|=
operator|new
name|String
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key1
argument_list|,
literal|"bar1"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key2
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testConcurrentHashMap
specifier|public
name|void
name|testConcurrentHashMap
parameter_list|()
throws|throws
name|Exception
block|{
comment|// don't make threadCount and keyCount random, otherwise easily OOMs or fails otherwise:
specifier|final
name|int
name|threadCount
init|=
literal|8
decl_stmt|,
name|keyCount
init|=
literal|1024
decl_stmt|;
specifier|final
name|ExecutorService
name|exec
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|threadCount
argument_list|)
decl_stmt|;
specifier|final
name|WeakIdentityMap
argument_list|<
name|Object
argument_list|,
name|Integer
argument_list|>
name|map
init|=
name|WeakIdentityMap
operator|.
name|newConcurrentHashMap
argument_list|()
decl_stmt|;
comment|// we keep strong references to the keys,
comment|// so WeakIdentityMap will not forget about them:
specifier|final
name|AtomicReferenceArray
argument_list|<
name|Object
argument_list|>
name|keys
init|=
operator|new
name|AtomicReferenceArray
argument_list|<
name|Object
argument_list|>
argument_list|(
name|keyCount
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
name|keyCount
condition|;
name|j
operator|++
control|)
block|{
name|keys
operator|.
name|set
argument_list|(
name|j
argument_list|,
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|threadCount
condition|;
name|t
operator|++
control|)
block|{
specifier|final
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|exec
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|int
name|count
init|=
name|atLeast
argument_list|(
name|rnd
argument_list|,
literal|10000
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
specifier|final
name|int
name|j
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
name|keyCount
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|rnd
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|map
operator|.
name|put
argument_list|(
name|keys
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
specifier|final
name|Integer
name|v
init|=
name|map
operator|.
name|get
argument_list|(
name|keys
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|j
argument_list|,
name|v
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|2
case|:
name|map
operator|.
name|remove
argument_list|(
name|keys
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
comment|// renew key, the old one will be GCed at some time:
name|keys
operator|.
name|set
argument_list|(
name|j
argument_list|,
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
literal|"Should not get here."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|exec
operator|.
name|shutdown
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|exec
operator|.
name|awaitTermination
argument_list|(
literal|1000L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
empty_stmt|;
block|}
comment|// clear strong refs
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|keyCount
condition|;
name|j
operator|++
control|)
block|{
name|keys
operator|.
name|set
argument_list|(
name|j
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// check that GC does not cause problems in reap() method:
name|int
name|size
init|=
name|map
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|size
operator|>
literal|0
operator|&&
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
try|try
block|{
name|System
operator|.
name|runFinalization
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|size
operator|>=
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|size
operator|=
name|map
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{}
block|}
block|}
end_class
end_unit
