begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
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
name|handler
operator|.
name|dataimport
operator|.
name|config
operator|.
name|ConfigNameConstants
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
name|handler
operator|.
name|dataimport
operator|.
name|config
operator|.
name|DIHConfiguration
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
name|handler
operator|.
name|dataimport
operator|.
name|config
operator|.
name|Entity
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  *<p>  * Test for DocBuilder  *</p>  *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestDocBuilder
specifier|public
class|class
name|TestDocBuilder
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|loadClass
specifier|public
name|void
name|loadClass
parameter_list|()
throws|throws
name|Exception
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Class
argument_list|<
name|Transformer
argument_list|>
name|clz
init|=
name|DocBuilder
operator|.
name|loadClass
argument_list|(
literal|"RegexTransformer"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|clz
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|singleEntityNoRows
specifier|public
name|void
name|singleEntityNoRows
parameter_list|()
block|{
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|dc_singleEntity
argument_list|)
expr_stmt|;
name|DIHConfiguration
name|cfg
init|=
name|di
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|Entity
name|ent
init|=
name|cfg
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|deleteAllCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|commitCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|finishCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|queryCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|docCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|rowsCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeltaImportNoRows_MustNotCommit
specifier|public
name|void
name|testDeltaImportNoRows_MustNotCommit
parameter_list|()
block|{
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|dc_deltaConfig
argument_list|)
expr_stmt|;
name|DIHConfiguration
name|cfg
init|=
name|di
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|Entity
name|ent
init|=
name|cfg
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select id from x"
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"delta-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|,
name|swi
operator|.
name|deleteAllCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|,
name|swi
operator|.
name|commitCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|finishCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|queryCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|docCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|rowsCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|singleEntityOneRow
specifier|public
name|void
name|singleEntityOneRow
parameter_list|()
block|{
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|dc_singleEntity
argument_list|)
expr_stmt|;
name|DIHConfiguration
name|cfg
init|=
name|di
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|Entity
name|ent
init|=
name|cfg
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|l
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|deleteAllCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|commitCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|finishCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|queryCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|docCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|rowsCount
operator|.
name|get
argument_list|()
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
name|l
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|l
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|swi
operator|.
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testImportCommand
specifier|public
name|void
name|testImportCommand
parameter_list|()
block|{
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|dc_singleEntity
argument_list|)
expr_stmt|;
name|DIHConfiguration
name|cfg
init|=
name|di
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|Entity
name|ent
init|=
name|cfg
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|l
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|,
name|swi
operator|.
name|deleteAllCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|commitCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|finishCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|queryCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|docCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|rowsCount
operator|.
name|get
argument_list|()
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
name|l
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|l
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|swi
operator|.
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|singleEntityMultipleRows
specifier|public
name|void
name|singleEntityMultipleRows
parameter_list|()
block|{
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|dc_singleEntity
argument_list|)
expr_stmt|;
name|DIHConfiguration
name|cfg
init|=
name|di
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|Entity
name|ent
init|=
name|cfg
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|,
literal|"desc"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|,
literal|"desc"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|l
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|deleteAllCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|commitCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|swi
operator|.
name|finishCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
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
name|l
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|l
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|swi
operator|.
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"desc"
argument_list|)
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"desc_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|queryCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|docCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|di
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|importStatistics
operator|.
name|rowsCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|SolrWriterImpl
specifier|static
class|class
name|SolrWriterImpl
extends|extends
name|SolrWriter
block|{
DECL|field|docs
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|deleteAllCalled
name|Boolean
name|deleteAllCalled
init|=
name|Boolean
operator|.
name|FALSE
decl_stmt|;
DECL|field|commitCalled
name|Boolean
name|commitCalled
init|=
name|Boolean
operator|.
name|FALSE
decl_stmt|;
DECL|field|finishCalled
name|Boolean
name|finishCalled
init|=
name|Boolean
operator|.
name|FALSE
decl_stmt|;
DECL|method|SolrWriterImpl
specifier|public
name|SolrWriterImpl
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|upload
specifier|public
name|boolean
name|upload
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
block|{
return|return
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doDeleteAll
specifier|public
name|void
name|doDeleteAll
parameter_list|()
block|{
name|deleteAllCalled
operator|=
name|Boolean
operator|.
name|TRUE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
name|commitCalled
operator|=
name|Boolean
operator|.
name|TRUE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|finishCalled
operator|=
name|Boolean
operator|.
name|TRUE
expr_stmt|;
block|}
block|}
DECL|field|dc_singleEntity
specifier|public
specifier|static
specifier|final
name|String
name|dc_singleEntity
init|=
literal|"<dataConfig>\n"
operator|+
literal|"<dataSource  type=\"MockDataSource\"/>\n"
operator|+
literal|"<document name=\"X\">\n"
operator|+
literal|"<entity name=\"x\" query=\"select * from x\">\n"
operator|+
literal|"<field column=\"id\"/>\n"
operator|+
literal|"<field column=\"desc\"/>\n"
operator|+
literal|"<field column=\"desc\" name=\"desc_s\" />"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|dc_deltaConfig
specifier|public
specifier|static
specifier|final
name|String
name|dc_deltaConfig
init|=
literal|"<dataConfig>\n"
operator|+
literal|"<dataSource  type=\"MockDataSource\"/>\n"
operator|+
literal|"<document name=\"X\">\n"
operator|+
literal|"<entity name=\"x\" query=\"select * from x\" deltaQuery=\"select id from x\">\n"
operator|+
literal|"<field column=\"id\"/>\n"
operator|+
literal|"<field column=\"desc\"/>\n"
operator|+
literal|"<field column=\"desc\" name=\"desc_s\" />"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
block|}
end_class
end_unit
