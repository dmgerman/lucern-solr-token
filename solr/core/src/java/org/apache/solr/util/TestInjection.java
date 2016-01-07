begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashSet
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|Pair
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|CoreContainer
import|;
end_import
begin_comment
comment|/**  * Allows random faults to be injected in running code during test runs.  */
end_comment
begin_class
DECL|class|TestInjection
specifier|public
class|class
name|TestInjection
block|{
DECL|class|TestShutdownFailError
specifier|public
specifier|static
class|class
name|TestShutdownFailError
extends|extends
name|OutOfMemoryError
block|{
DECL|method|TestShutdownFailError
specifier|public
name|TestShutdownFailError
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|ENABLED_PERCENT
specifier|private
specifier|static
specifier|final
name|Pattern
name|ENABLED_PERCENT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(true|false)(?:\\:(\\d+))?$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
decl_stmt|;
static|static
block|{
comment|// We try to make things reproducible in the context of our tests by initializing the random instance
comment|// based on the current seed
name|String
name|seed
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.seed"
argument_list|)
decl_stmt|;
if|if
condition|(
name|seed
operator|==
literal|null
condition|)
block|{
name|RANDOM
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|RANDOM
operator|=
operator|new
name|Random
argument_list|(
name|seed
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|nonGracefullClose
specifier|public
specifier|static
name|String
name|nonGracefullClose
init|=
literal|null
decl_stmt|;
DECL|field|failReplicaRequests
specifier|public
specifier|static
name|String
name|failReplicaRequests
init|=
literal|null
decl_stmt|;
DECL|field|failUpdateRequests
specifier|public
specifier|static
name|String
name|failUpdateRequests
init|=
literal|null
decl_stmt|;
DECL|field|timers
specifier|private
specifier|static
name|Set
argument_list|<
name|Timer
argument_list|>
name|timers
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|Timer
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|reset
specifier|public
specifier|static
name|void
name|reset
parameter_list|()
block|{
name|nonGracefullClose
operator|=
literal|null
expr_stmt|;
name|failReplicaRequests
operator|=
literal|null
expr_stmt|;
name|failUpdateRequests
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|Timer
name|timer
range|:
name|timers
control|)
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|injectNonGracefullClose
specifier|public
specifier|static
name|boolean
name|injectNonGracefullClose
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
if|if
condition|(
name|cc
operator|.
name|isShutDown
argument_list|()
operator|&&
name|nonGracefullClose
operator|!=
literal|null
condition|)
block|{
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|nonGracefullClose
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
if|if
condition|(
name|RANDOM
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TestShutdownFailError
argument_list|(
literal|"Test exception for non graceful close"
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|Thread
name|cthread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|TimerTask
name|task
init|=
operator|new
name|TimerTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// as long as places that catch interruptedexception reset that
comment|// interrupted status,
comment|// we should only need to do it once
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                              }
name|cthread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|timers
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
name|timers
operator|.
name|add
argument_list|(
name|timer
argument_list|)
expr_stmt|;
name|timer
operator|.
name|schedule
argument_list|(
name|task
argument_list|,
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|injectFailReplicaRequests
specifier|public
specifier|static
name|boolean
name|injectFailReplicaRequests
parameter_list|()
block|{
if|if
condition|(
name|failReplicaRequests
operator|!=
literal|null
condition|)
block|{
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|failReplicaRequests
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Random test update fail"
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|injectFailUpdateRequests
specifier|public
specifier|static
name|boolean
name|injectFailUpdateRequests
parameter_list|()
block|{
if|if
condition|(
name|failUpdateRequests
operator|!=
literal|null
condition|)
block|{
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|failUpdateRequests
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Random test update fail"
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|parseValue
specifier|private
specifier|static
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|parseValue
parameter_list|(
name|String
name|raw
parameter_list|)
block|{
name|Matcher
name|m
init|=
name|ENABLED_PERCENT
operator|.
name|matcher
argument_list|(
name|raw
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|matches
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No match, probably bad syntax: "
operator|+
name|raw
argument_list|)
throw|;
name|String
name|val
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|percent
init|=
literal|"100"
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|groupCount
argument_list|()
operator|==
literal|2
condition|)
block|{
name|percent
operator|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Pair
argument_list|<>
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|val
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|percent
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
