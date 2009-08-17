begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|request
operator|.
name|SolrQueryRequest
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
name|schema
operator|.
name|IndexSchema
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
name|util
operator|.
name|AbstractSolrTestCase
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/** Bypass the normal Solr pipeline and just text indexing performance  * starting at the update handler.  The same document is indexed repeatedly.  *   * $ ant test -Dtestcase=TestIndexingPerformance -Dargs="-server -Diter=100000"; grep throughput build/test-results/*TestIndexingPerformance.xml  */
end_comment
begin_class
DECL|class|TestIndexingPerformance
specifier|public
class|class
name|TestIndexingPerformance
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema12.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig_perf.xml"
return|;
block|}
DECL|method|testIndexingPerf
specifier|public
name|void
name|testIndexingPerf
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|iter
init|=
literal|1000
decl_stmt|;
name|String
name|iterS
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"iter"
argument_list|)
decl_stmt|;
if|if
condition|(
name|iterS
operator|!=
literal|null
condition|)
name|iter
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|iterS
argument_list|)
expr_stmt|;
name|boolean
name|includeDoc
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"includeDoc"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
comment|// include the time to create the document
name|boolean
name|overwrite
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"overwrite"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|doc
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|doc
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|SolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updateHandler
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|String
index|[]
name|fields
init|=
block|{
literal|"text"
block|,
literal|"simple"
block|,
literal|"text"
block|,
literal|"test"
block|,
literal|"text"
block|,
literal|"how now brown cow"
block|,
literal|"text"
block|,
literal|"what's that?"
block|,
literal|"text"
block|,
literal|"radical!"
block|,
literal|"text"
block|,
literal|"what's all this about, anyway?"
block|,
literal|"text"
block|,
literal|"just how fast is this text indexing?"
block|}
decl_stmt|;
comment|/***     String[] fields = {             "a_i","1"             ,"b_i","2"             ,"c_i","3"             ,"d_i","4"             ,"e_i","5"             ,"f_i","6"             ,"g_i","7"             ,"h_i","8"             ,"i_i","9"             ,"j_i","0"             ,"k_i","0"     };    ***/
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|AddUpdateCommand
name|add
init|=
operator|new
name|AddUpdateCommand
argument_list|()
decl_stmt|;
name|add
operator|.
name|allowDups
operator|=
operator|!
name|overwrite
expr_stmt|;
name|add
operator|.
name|overwriteCommitted
operator|=
name|overwrite
expr_stmt|;
name|add
operator|.
name|overwritePending
operator|=
name|overwrite
expr_stmt|;
name|Field
name|idField
init|=
literal|null
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
if|if
condition|(
name|includeDoc
operator|||
name|add
operator|.
name|doc
operator|==
literal|null
condition|)
block|{
name|add
operator|.
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|idField
operator|=
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
expr_stmt|;
name|add
operator|.
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
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
name|fields
operator|.
name|length
condition|;
name|j
operator|+=
literal|2
control|)
block|{
name|String
name|field
init|=
name|fields
index|[
name|j
index|]
decl_stmt|;
name|String
name|val
init|=
name|fields
index|[
name|j
operator|+
literal|1
index|]
decl_stmt|;
name|Field
name|f
init|=
name|schema
operator|.
name|getField
argument_list|(
name|field
argument_list|)
operator|.
name|createField
argument_list|(
name|val
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
name|add
operator|.
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
name|idField
operator|.
name|setValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|updateHandler
operator|.
name|addDoc
argument_list|(
name|add
argument_list|)
expr_stmt|;
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
literal|"includeDoc="
operator|+
name|includeDoc
operator|+
literal|" doc="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|fields
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"iter="
operator|+
name|iter
operator|+
literal|" time="
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|" throughput="
operator|+
operator|(
operator|(
name|long
operator|)
name|iter
operator|*
literal|1000
operator|)
operator|/
operator|(
name|end
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
comment|//discard all the changes
name|updateHandler
operator|.
name|rollback
argument_list|(
operator|new
name|RollbackUpdateCommand
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
