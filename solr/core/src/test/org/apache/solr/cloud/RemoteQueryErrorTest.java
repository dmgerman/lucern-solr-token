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
name|List
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
operator|.
name|Slow
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
begin_comment
comment|/**  * Verify that remote (proxied) queries return proper error messages  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|RemoteQueryErrorTest
specifier|public
class|class
name|RemoteQueryErrorTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|method|RemoteQueryErrorTest
specifier|public
name|RemoteQueryErrorTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
name|shardCount
operator|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|3
else|:
literal|4
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
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
literal|"collection2"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|numShardsNumReplicaList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|numShardsNumReplicaList
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|numShardsNumReplicaList
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|checkForCollection
argument_list|(
literal|"collection2"
argument_list|,
name|numShardsNumReplicaList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|"collection2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrServer
name|solrServer
range|:
name|clients
control|)
block|{
try|try
block|{
name|SolrInputDocument
name|emptyDoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|solrServer
operator|.
name|add
argument_list|(
name|emptyDoc
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected unique key exceptoin"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Document is missing mandatory uniqueKey field: id"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Expected a SolrException to occur, instead received: "
operator|+
name|ex
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|solrServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
