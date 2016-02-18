begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.request
package|package
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
package|;
end_package
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|CollectionAdminRequest
operator|.
name|Create
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
name|CollectionAdminRequest
operator|.
name|CreateAlias
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
name|CollectionAdminRequest
operator|.
name|CreateShard
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/**  * Unit tests for {@link CollectionAdminRequest}.  */
end_comment
begin_class
DECL|class|TestCollectionAdminRequest
specifier|public
class|class
name|TestCollectionAdminRequest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testInvalidCollectionNameRejectedWhenCreatingCollection
specifier|public
name|void
name|testInvalidCollectionNameRejectedWhenCreatingCollection
parameter_list|()
block|{
specifier|final
name|Create
name|createRequest
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
try|try
block|{
name|createRequest
operator|.
name|setCollectionName
argument_list|(
literal|"invalid$collection@name"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid collection"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$collection@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidShardNamesRejectedWhenCreatingCollection
specifier|public
name|void
name|testInvalidShardNamesRejectedWhenCreatingCollection
parameter_list|()
block|{
specifier|final
name|Create
name|createRequest
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
try|try
block|{
name|createRequest
operator|.
name|setShards
argument_list|(
literal|"invalid$shard@name"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid shard"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$shard@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidAliasNameRejectedWhenCreatingAlias
specifier|public
name|void
name|testInvalidAliasNameRejectedWhenCreatingAlias
parameter_list|()
block|{
specifier|final
name|CreateAlias
name|createAliasRequest
init|=
operator|new
name|CreateAlias
argument_list|()
decl_stmt|;
try|try
block|{
name|createAliasRequest
operator|.
name|setAliasName
argument_list|(
literal|"invalid$alias@name"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid collection"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$alias@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidShardNameRejectedWhenCreatingShard
specifier|public
name|void
name|testInvalidShardNameRejectedWhenCreatingShard
parameter_list|()
block|{
specifier|final
name|CreateShard
name|createShardRequest
init|=
operator|new
name|CreateShard
argument_list|()
decl_stmt|;
try|try
block|{
name|createShardRequest
operator|.
name|setShardName
argument_list|(
literal|"invalid$shard@name"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid shard"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$shard@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
