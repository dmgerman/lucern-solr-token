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
name|SolrQuery
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
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|embedded
operator|.
name|JettySolrRunner
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrServer
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
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
name|SolrInputDocument
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
name|cloud
operator|.
name|CompositeIdRouter
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
name|cloud
operator|.
name|ZkNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|ModifiableSolrParams
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
name|params
operator|.
name|ShardParams
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
name|StrUtils
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
name|servlet
operator|.
name|SolrDispatchFilter
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
name|update
operator|.
name|DirectUpdateHandler2
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
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|List
import|;
end_import
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
name|Set
import|;
end_import
begin_class
DECL|class|ShardRoutingTest
specifier|public
class|class
name|ShardRoutingTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|bucket1
name|String
name|bucket1
init|=
literal|"shard1"
decl_stmt|;
comment|// shard1: top bits:10  80000000:bfffffff
DECL|field|bucket2
name|String
name|bucket2
init|=
literal|"shard2"
decl_stmt|;
comment|// shard2: top bits:11  c0000000:ffffffff
DECL|field|bucket3
name|String
name|bucket3
init|=
literal|"shard3"
decl_stmt|;
comment|// shard3: top bits:00  00000000:3fffffff
DECL|field|bucket4
name|String
name|bucket4
init|=
literal|"shard4"
decl_stmt|;
comment|// shard4: top bits:01  40000000:7fffffff
annotation|@
name|BeforeClass
DECL|method|beforeShardHashingTest
specifier|public
specifier|static
name|void
name|beforeShardHashingTest
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
DECL|method|ShardRoutingTest
specifier|public
name|ShardRoutingTest
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
literal|4
expr_stmt|;
name|super
operator|.
name|shardCount
operator|=
literal|8
expr_stmt|;
name|super
operator|.
name|fixShardCount
operator|=
literal|true
expr_stmt|;
comment|// we only want to test with exactly 4 slices.
comment|// from negative to positive, the upper bits of the hash ranges should be
comment|// shard1: top bits:10  80000000:bfffffff
comment|// shard2: top bits:11  c0000000:ffffffff
comment|// shard3: top bits:00  00000000:3fffffff
comment|// shard4: top bits:01  40000000:7fffffff
comment|/***      hash of a is 3c2569b2 high bits=0 shard=shard3      hash of b is 95de7e03 high bits=2 shard=shard1      hash of c is e132d65f high bits=3 shard=shard2      hash of d is 27191473 high bits=0 shard=shard3      hash of e is 656c4367 high bits=1 shard=shard4      hash of f is 2b64883b high bits=0 shard=shard3      hash of g is f18ae416 high bits=3 shard=shard2      hash of h is d482b2d3 high bits=3 shard=shard2      hash of i is 811a702b high bits=2 shard=shard1      hash of j is ca745a39 high bits=3 shard=shard2      hash of k is cfbda5d1 high bits=3 shard=shard2      hash of l is 1d5d6a2c high bits=0 shard=shard3      hash of m is 5ae4385c high bits=1 shard=shard4      hash of n is c651d8ac high bits=3 shard=shard2      hash of o is 68348473 high bits=1 shard=shard4      hash of p is 986fdf9a high bits=2 shard=shard1      hash of q is ff8209e8 high bits=3 shard=shard2      hash of r is 5c9373f1 high bits=1 shard=shard4      hash of s is ff4acaf1 high bits=3 shard=shard2      hash of t is ca87df4d high bits=3 shard=shard2      hash of u is 62203ae0 high bits=1 shard=shard4      hash of v is bdafcc55 high bits=2 shard=shard1      hash of w is ff439d1f high bits=3 shard=shard2      hash of x is 3e9a9b1b high bits=0 shard=shard3      hash of y is 477d9216 high bits=1 shard=shard4      hash of z is c1f69a17 high bits=3 shard=shard2       hash of f1 is 313bf6b1      hash of f2 is ff143f8       ***/
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
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
comment|// todo: do I have to do this here?
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|doHashingTest
argument_list|()
expr_stmt|;
name|doTestNumRequests
argument_list|()
expr_stmt|;
name|doAtomicUpdate
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
DECL|method|doHashingTest
specifier|private
name|void
name|doHashingTest
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"### STARTING doHashingTest"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|DEFAULT_COLLECTION
argument_list|)
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|shardKeys
init|=
name|ShardParams
operator|.
name|SHARD_KEYS
decl_stmt|;
comment|// for now,  we know how ranges will be distributed to shards.
comment|// may have to look it up in clusterstate if that assumption changes.
name|doAddDoc
argument_list|(
literal|"b!doc1"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"c!doc2"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"d!doc3"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"e!doc4"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"f1!f2!doc5"
argument_list|)
expr_stmt|;
comment|// Check successful addition of a document with a '/' in the id part.
name|doAddDoc
argument_list|(
literal|"f1!f2!doc5/5"
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc1"
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"c!doc2"
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"d!doc3"
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"e!doc4"
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"f1!f2!doc5"
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"f1!f2!doc5/5"
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc1,c!doc2"
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"d!doc3,e!doc4"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,c!doc2,d!doc3,e!doc4,f1!f2!doc5,f1!f2!doc5/5"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,c!doc2,d!doc3,e!doc4,f1!f2!doc5,f1!f2!doc5/5"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
literal|"shard1,shard2,shard3,shard4"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,c!doc2,d!doc3,e!doc4,f1!f2!doc5,f1!f2!doc5/5"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"b!,c!,d!,e!,f1!f2!"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"b!"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"c!doc2"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"c!"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"d!doc3,f1!f2!doc5,f1!f2!doc5/5"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"d!"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"e!doc4"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"e!"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"f1!f2!doc5,d!doc3,f1!f2!doc5/5"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"f1/8!"
argument_list|)
expr_stmt|;
comment|// try using shards parameter
name|doQuery
argument_list|(
literal|"b!doc1"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
name|bucket1
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"c!doc2"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
name|bucket2
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"d!doc3,f1!f2!doc5,f1!f2!doc5/5"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
name|bucket3
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"e!doc4"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
name|bucket4
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,c!doc2"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"b!,c!"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,e!doc4"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"b!,e!"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,c!doc2"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"b,c"
argument_list|)
expr_stmt|;
comment|// query shards that would contain *documents* "b" and "c" (i.e. not prefixes).  The upper bits are the same, so the shards should be the same.
name|doQuery
argument_list|(
literal|"b!doc1,c!doc2"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"b/1!"
argument_list|)
expr_stmt|;
comment|// top bit of hash(b)==1, so shard1 and shard2
name|doQuery
argument_list|(
literal|"d!doc3,e!doc4,f1!f2!doc5,f1!f2!doc5/5"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"d/1!"
argument_list|)
expr_stmt|;
comment|// top bit of hash(b)==0, so shard3 and shard4
name|doQuery
argument_list|(
literal|"b!doc1,c!doc2"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"b!,c!"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,f1!f2!doc5,c!doc2,d!doc3,e!doc4,f1!f2!doc5/5"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"foo/0!"
argument_list|)
expr_stmt|;
comment|// test targeting deleteByQuery at only certain shards
name|doDBQ
argument_list|(
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"b!"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"c!doc2,d!doc3,e!doc4,f1!f2!doc5,f1!f2!doc5/5"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"b!doc1"
argument_list|)
expr_stmt|;
name|doDBQ
argument_list|(
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"f1!"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,c!doc2,e!doc4"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"f1!f2!doc5"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"d!doc3"
argument_list|)
expr_stmt|;
name|doDBQ
argument_list|(
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"c!"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,f1!f2!doc5,d!doc3,e!doc4"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"c!doc2"
argument_list|)
expr_stmt|;
name|doDBQ
argument_list|(
literal|"*:*"
argument_list|,
name|shardKeys
argument_list|,
literal|"d!,e!"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,c!doc2"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"d!doc3"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"e!doc4"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"f1!f2!doc5"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doDBQ
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"b!"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"c!doc1"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!,c!doc1"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|deleteById
argument_list|(
literal|"b!"
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"c!doc1"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doDBQ
argument_list|(
literal|"id:b!"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"c!doc1"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doDBQ
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"a!b!"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"b!doc1"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"c!doc2"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"d!doc3"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"e!doc4"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"f1!f2!doc5"
argument_list|)
expr_stmt|;
name|doAddDoc
argument_list|(
literal|"f1!f2!doc5/5"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"a!b!,b!doc1,c!doc2,d!doc3,e!doc4,f1!f2!doc5,f1!f2!doc5/5"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestNumRequests
specifier|public
name|void
name|doTestNumRequests
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"### STARTING doTestNumRequests"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|runners
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
name|bucket1
argument_list|)
decl_stmt|;
name|CloudJettyRunner
name|leader
init|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
name|bucket1
argument_list|)
decl_stmt|;
name|CloudJettyRunner
name|replica
init|=
literal|null
decl_stmt|;
for|for
control|(
name|CloudJettyRunner
name|r
range|:
name|runners
control|)
block|{
if|if
condition|(
name|r
operator|!=
name|leader
condition|)
name|replica
operator|=
name|r
expr_stmt|;
block|}
name|long
name|nStart
init|=
name|getNumRequests
argument_list|()
decl_stmt|;
name|leader
operator|.
name|client
operator|.
name|solrClient
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"b!doc1"
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|nEnd
init|=
name|getNumRequests
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nEnd
operator|-
name|nStart
argument_list|)
expr_stmt|;
comment|// one request to leader, which makes another to a replica
name|nStart
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|replica
operator|.
name|client
operator|.
name|solrClient
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"b!doc1"
argument_list|)
argument_list|)
expr_stmt|;
name|nEnd
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|nEnd
operator|-
name|nStart
argument_list|)
expr_stmt|;
comment|// orig request + replica forwards to leader, which forward back to replica.
name|nStart
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|replica
operator|.
name|client
operator|.
name|solrClient
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"b!doc1"
argument_list|)
argument_list|)
expr_stmt|;
name|nEnd
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|nEnd
operator|-
name|nStart
argument_list|)
expr_stmt|;
comment|// orig request + replica forwards to leader, which forward back to replica.
name|CloudJettyRunner
name|leader2
init|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
name|bucket2
argument_list|)
decl_stmt|;
name|nStart
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|replica
operator|.
name|client
operator|.
name|solrClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
name|bucket1
argument_list|)
argument_list|)
expr_stmt|;
name|nEnd
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nEnd
operator|-
name|nStart
argument_list|)
expr_stmt|;
comment|// short circuit should prevent distrib search
name|nStart
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|replica
operator|.
name|client
operator|.
name|solrClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shard.keys"
argument_list|,
literal|"b!"
argument_list|)
argument_list|)
expr_stmt|;
name|nEnd
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nEnd
operator|-
name|nStart
argument_list|)
expr_stmt|;
comment|// short circuit should prevent distrib search
name|nStart
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|leader2
operator|.
name|client
operator|.
name|solrClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shard.keys"
argument_list|,
literal|"b!"
argument_list|)
argument_list|)
expr_stmt|;
name|nEnd
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|nEnd
operator|-
name|nStart
argument_list|)
expr_stmt|;
comment|// original + 2 phase distrib search.  we could improve this!
name|nStart
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|leader2
operator|.
name|client
operator|.
name|solrClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|nEnd
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|nEnd
operator|-
name|nStart
argument_list|)
expr_stmt|;
comment|// original + 2 phase distrib search * 4 shards.
name|nStart
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|leader2
operator|.
name|client
operator|.
name|solrClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shard.keys"
argument_list|,
literal|"b!,d!"
argument_list|)
argument_list|)
expr_stmt|;
name|nEnd
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|nEnd
operator|-
name|nStart
argument_list|)
expr_stmt|;
comment|// original + 2 phase distrib search * 2 shards.
name|nStart
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|leader2
operator|.
name|client
operator|.
name|solrClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shard.keys"
argument_list|,
literal|"b!,f1!f2!"
argument_list|)
argument_list|)
expr_stmt|;
name|nEnd
operator|=
name|getNumRequests
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|nEnd
operator|-
name|nStart
argument_list|)
expr_stmt|;
block|}
DECL|method|doAtomicUpdate
specifier|public
name|void
name|doAtomicUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"### STARTING doAtomicUpdate"
argument_list|)
expr_stmt|;
name|int
name|nClients
init|=
name|clients
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|nClients
argument_list|)
expr_stmt|;
name|int
name|expectedVal
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SolrServer
name|client
range|:
name|clients
control|)
block|{
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"b!doc"
argument_list|,
literal|"foo_i"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|expectedVal
operator|++
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|client
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"b!doc"
argument_list|)
argument_list|)
decl_stmt|;
name|Object
name|val
init|=
operator|(
operator|(
name|Map
operator|)
name|rsp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"foo_i"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|Integer
operator|)
name|expectedVal
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getNumRequests
name|long
name|getNumRequests
parameter_list|()
block|{
name|long
name|n
init|=
name|controlJetty
operator|.
name|getDebugFilter
argument_list|()
operator|.
name|getTotalRequests
argument_list|()
decl_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|n
operator|+=
name|jetty
operator|.
name|getDebugFilter
argument_list|()
operator|.
name|getTotalRequests
argument_list|()
expr_stmt|;
block|}
return|return
name|n
return|;
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
DECL|method|doRTG
name|void
name|doRTG
parameter_list|(
name|String
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|doQuery
argument_list|(
name|ids
argument_list|,
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
comment|// TODO: refactor some of this stuff into the SolrJ client... it should be easier to use
DECL|method|doDBQ
name|void
name|doDBQ
parameter_list|(
name|String
name|q
parameter_list|,
name|String
modifier|...
name|reqParams
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|deleteByQuery
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|(
name|reqParams
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
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
