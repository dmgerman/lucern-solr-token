begin_unit
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|SolrDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Set
import|;
end_import
begin_class
DECL|class|TriLevelCompositeIdRoutingTest
specifier|public
class|class
name|TriLevelCompositeIdRoutingTest
extends|extends
name|ShardRoutingTest
block|{
DECL|field|NUM_APPS
name|int
name|NUM_APPS
init|=
literal|5
decl_stmt|;
DECL|field|NUM_USERS
name|int
name|NUM_USERS
init|=
literal|10
decl_stmt|;
DECL|field|NUM_DOCS
name|int
name|NUM_DOCS
init|=
literal|100
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeTriLevelCompositeIdRoutingTest
specifier|public
specifier|static
name|void
name|beforeTriLevelCompositeIdRoutingTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: we use an fs based dir because something
comment|// like a ram dir will not recover correctly right now
comment|// because tran log will still exist on restart and ram
comment|// dir will not persist - perhaps translog can empty on
comment|// start if using an EphemeralDirectoryFactory
name|useFactory
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|TriLevelCompositeIdRoutingTest
specifier|public
name|TriLevelCompositeIdRoutingTest
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
name|super
operator|.
name|sliceCount
operator|=
literal|12
expr_stmt|;
comment|// a lot of slices for more ranges and buckets
name|super
operator|.
name|shardCount
operator|=
literal|24
expr_stmt|;
name|super
operator|.
name|fixShardCount
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|testFinished
init|=
literal|false
decl_stmt|;
try|try
block|{
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
comment|// todo: do I have to do this here?
name|waitForRecoveriesToFinish
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doTriLevelHashingTest
argument_list|()
expr_stmt|;
name|doTriLevelHashingTestWithBitMask
argument_list|()
expr_stmt|;
name|testFinished
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|testFinished
condition|)
block|{
name|printLayoutOnTearDown
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
DECL|method|doTriLevelHashingTest
specifier|private
name|void
name|doTriLevelHashingTest
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"### STARTING doTriLevelHashingTest"
argument_list|)
expr_stmt|;
comment|// for now,  we know how ranges will be distributed to shards.
comment|// may have to look it up in clusterstate if that assumption changes.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|int
name|appId
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|NUM_APPS
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|userId
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|NUM_USERS
argument_list|)
operator|+
literal|1
decl_stmt|;
name|String
name|id
init|=
literal|"app"
operator|+
name|appId
operator|+
literal|"!"
operator|+
literal|"user"
operator|+
name|userId
operator|+
literal|"!"
operator|+
literal|"doc"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|doAddDoc
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|idMap
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
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|sliceCount
condition|;
name|i
operator|++
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|doQueryGetUniqueIdKeys
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
literal|"shard"
operator|+
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|assertFalse
argument_list|(
literal|"Found the same route key ["
operator|+
name|id
operator|+
literal|"] in 2 shards."
argument_list|,
name|idMap
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|idMap
operator|.
name|put
argument_list|(
name|getKey
argument_list|(
name|id
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doTriLevelHashingTestWithBitMask
specifier|private
name|void
name|doTriLevelHashingTestWithBitMask
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"### STARTING doTriLevelHashingTestWithBitMask"
argument_list|)
expr_stmt|;
comment|// for now,  we know how ranges will be distributed to shards.
comment|// may have to look it up in clusterstate if that assumption changes.
name|del
argument_list|(
literal|"*:*"
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|int
name|appId
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|NUM_APPS
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|userId
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|NUM_USERS
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|bitMask
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|16
argument_list|)
operator|+
literal|1
decl_stmt|;
name|String
name|id
init|=
literal|"app"
operator|+
name|appId
operator|+
literal|"/"
operator|+
name|bitMask
operator|+
literal|"!"
operator|+
literal|"user"
operator|+
name|userId
operator|+
literal|"!"
operator|+
literal|"doc"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|doAddDoc
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|idMap
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
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|sliceCount
condition|;
name|i
operator|++
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|doQueryGetUniqueIdKeys
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
literal|"shard"
operator|+
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|assertFalse
argument_list|(
literal|"Found the same route key ["
operator|+
name|id
operator|+
literal|"] in 2 shards."
argument_list|,
name|idMap
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|idMap
operator|.
name|put
argument_list|(
name|getKey
argument_list|(
name|id
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doAddDoc
name|void
name|doAddDoc
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|index
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
comment|// todo - target diff servers and use cloud clients as well as non-cloud clients
block|}
DECL|method|doQueryGetUniqueIdKeys
name|Set
argument_list|<
name|String
argument_list|>
name|doQueryGetUniqueIdKeys
parameter_list|(
name|String
modifier|...
name|queryParams
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryResponse
name|rsp
init|=
name|cloudClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
name|queryParams
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|obtainedIdKeys
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|obtainedIdKeys2
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|rsp
operator|.
name|getResults
argument_list|()
control|)
block|{
name|obtainedIdKeys
operator|.
name|add
argument_list|(
name|getKey
argument_list|(
operator|(
name|String
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|obtainedIdKeys
return|;
block|}
DECL|method|getKey
specifier|private
name|String
name|getKey
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|id
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|id
operator|.
name|lastIndexOf
argument_list|(
literal|'!'
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
