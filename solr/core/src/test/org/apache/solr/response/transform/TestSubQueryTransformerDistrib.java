begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
package|;
end_package
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
name|ArrayList
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
name|Iterator
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
name|Random
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
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
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
name|request
operator|.
name|ContentStreamUpdateRequest
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
name|QueryRequest
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|SolrDocumentList
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
name|ContentStreamBase
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
begin_class
annotation|@
name|SuppressSSL
DECL|class|TestSubQueryTransformerDistrib
specifier|public
class|class
name|TestSubQueryTransformerDistrib
extends|extends
name|AbstractFullDistribZkTestBase
block|{
annotation|@
name|Override
DECL|method|getCloudSchemaFile
specifier|protected
name|String
name|getCloudSchemaFile
parameter_list|()
block|{
return|return
literal|"schema-docValuesJoin.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig-basic.xml"
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|int
name|peopleMultiplier
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|deptMultiplier
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|people
init|=
literal|"people"
decl_stmt|;
name|int
name|numPeopleShards
decl_stmt|;
name|createCollection
argument_list|(
name|people
argument_list|,
name|atLeast
argument_list|(
literal|1
argument_list|)
argument_list|,
name|numPeopleShards
operator|=
name|atLeast
argument_list|(
literal|2
argument_list|)
argument_list|,
name|numPeopleShards
argument_list|)
expr_stmt|;
name|String
name|depts
init|=
literal|"departments"
decl_stmt|;
name|int
name|numDeptsShards
decl_stmt|;
name|createCollection
argument_list|(
name|depts
argument_list|,
name|atLeast
argument_list|(
literal|1
argument_list|)
argument_list|,
name|numDeptsShards
operator|=
name|atLeast
argument_list|(
literal|2
argument_list|)
argument_list|,
name|numDeptsShards
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
name|people
argument_list|,
name|peopleMultiplier
argument_list|,
name|depts
argument_list|,
name|deptMultiplier
argument_list|)
expr_stmt|;
name|Random
name|random1
init|=
name|random
argument_list|()
decl_stmt|;
block|{
specifier|final
name|QueryRequest
name|qr
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"q"
block|,
literal|"name_s:dave"
block|,
literal|"indent"
block|,
literal|"true"
block|,
literal|"fl"
block|,
literal|"*,depts:[subquery "
operator|+
operator|(
operator|(
name|random1
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|""
else|:
literal|"separator=,"
operator|)
operator|)
operator|+
literal|"]"
block|,
literal|"rows"
block|,
literal|""
operator|+
name|peopleMultiplier
block|,
literal|"depts.q"
block|,
literal|"{!terms f=dept_id_s v=$row.dept_ss_dv "
operator|+
operator|(
operator|(
name|random1
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|""
else|:
literal|"separator=,"
operator|)
operator|)
operator|+
literal|"}"
block|,
literal|"depts.fl"
block|,
literal|"text_t"
block|,
literal|"depts.indent"
block|,
literal|"true"
block|,
literal|"depts.collection"
block|,
literal|"departments"
block|,
literal|"depts.rows"
block|,
literal|""
operator|+
operator|(
name|deptMultiplier
operator|*
literal|2
operator|)
block|,
literal|"depts.logParamsList"
block|,
literal|"q,fl,rows,row.dept_ss_dv"
block|}
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|QueryResponse
name|rsp
init|=
operator|new
name|QueryResponse
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|setResponse
argument_list|(
name|cloudClient
operator|.
name|request
argument_list|(
name|qr
argument_list|,
name|people
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SolrDocumentList
name|hits
init|=
name|rsp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|peopleMultiplier
argument_list|,
name|hits
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|engText
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"text_t"
argument_list|,
literal|"These guys develop stuff"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|suppText
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"text_t"
argument_list|,
literal|"These guys help customers"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|int
name|engineer
init|=
literal|0
decl_stmt|;
name|int
name|support
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|res
range|:
operator|new
name|int
index|[]
block|{
literal|0
block|,
operator|(
name|peopleMultiplier
operator|-
literal|1
operator|)
operator|/
literal|2
block|,
name|peopleMultiplier
operator|-
literal|1
block|}
control|)
block|{
name|SolrDocument
name|doc
init|=
name|hits
operator|.
name|get
argument_list|(
name|res
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dave"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"name_s_dv"
argument_list|)
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|relDepts
init|=
operator|(
name|SolrDocumentList
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"depts"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dave works in both depts "
operator|+
name|rsp
argument_list|,
name|deptMultiplier
operator|*
literal|2
argument_list|,
name|relDepts
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|deptN
init|=
literal|0
init|;
name|deptN
operator|<
name|relDepts
operator|.
name|getNumFound
argument_list|()
condition|;
name|deptN
operator|++
control|)
block|{
name|SolrDocument
name|deptDoc
init|=
name|relDepts
operator|.
name|get
argument_list|(
name|deptN
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|deptDoc
operator|+
literal|"should be either "
operator|+
name|engText
operator|+
literal|" or "
operator|+
name|suppText
argument_list|,
operator|(
name|engText
operator|.
name|equals
argument_list|(
name|deptDoc
argument_list|)
operator|&&
operator|++
name|engineer
operator|>
literal|0
operator|)
operator|||
operator|(
name|suppText
operator|.
name|equals
argument_list|(
name|deptDoc
argument_list|)
operator|&&
operator|++
name|support
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|hits
operator|.
name|toString
argument_list|()
argument_list|,
name|engineer
argument_list|,
name|support
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createIndex
specifier|private
name|void
name|createIndex
parameter_list|(
name|String
name|people
parameter_list|,
name|int
name|peopleMultiplier
parameter_list|,
name|String
name|depts
parameter_list|,
name|int
name|deptMultiplier
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|int
name|id
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|peopleDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|p
init|=
literal|0
init|;
name|p
operator|<
name|peopleMultiplier
condition|;
name|p
operator|++
control|)
block|{
name|peopleDocs
operator|.
name|add
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
operator|++
argument_list|,
literal|"name_s"
argument_list|,
literal|"john"
argument_list|,
literal|"title_s"
argument_list|,
literal|"Director"
argument_list|,
literal|"dept_ss_dv"
argument_list|,
literal|"Engineering"
argument_list|,
literal|"dept_i"
argument_list|,
literal|"0"
argument_list|,
literal|"dept_is"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|peopleDocs
operator|.
name|add
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
operator|++
argument_list|,
literal|"name_s"
argument_list|,
literal|"mark"
argument_list|,
literal|"title_s"
argument_list|,
literal|"VP"
argument_list|,
literal|"dept_ss_dv"
argument_list|,
literal|"Marketing"
argument_list|,
literal|"dept_i"
argument_list|,
literal|"1"
argument_list|,
literal|"dept_is"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|peopleDocs
operator|.
name|add
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
operator|++
argument_list|,
literal|"name_s"
argument_list|,
literal|"nancy"
argument_list|,
literal|"title_s"
argument_list|,
literal|"MTS"
argument_list|,
literal|"dept_ss_dv"
argument_list|,
literal|"Sales"
argument_list|,
literal|"dept_i"
argument_list|,
literal|"2"
argument_list|,
literal|"dept_is"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|peopleDocs
operator|.
name|add
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
operator|++
argument_list|,
literal|"name_s"
argument_list|,
literal|"dave"
argument_list|,
literal|"title_s"
argument_list|,
literal|"MTS"
argument_list|,
literal|"dept_ss_dv"
argument_list|,
literal|"Support"
argument_list|,
literal|"dept_ss_dv"
argument_list|,
literal|"Engineering"
argument_list|,
literal|"dept_i"
argument_list|,
literal|"3"
argument_list|,
literal|"dept_is"
argument_list|,
literal|"3"
argument_list|,
literal|"dept_is"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|peopleDocs
operator|.
name|add
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
operator|++
argument_list|,
literal|"name_s"
argument_list|,
literal|"tina"
argument_list|,
literal|"title_s"
argument_list|,
literal|"VP"
argument_list|,
literal|"dept_ss_dv"
argument_list|,
literal|"Engineering"
argument_list|,
literal|"dept_i"
argument_list|,
literal|"0"
argument_list|,
literal|"dept_is"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|addDocs
argument_list|(
name|people
argument_list|,
name|peopleDocs
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|deptsDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|deptMultiplier
condition|;
name|d
operator|++
control|)
block|{
name|deptsDocs
operator|.
name|add
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
operator|++
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Engineering"
argument_list|,
literal|"text_t"
argument_list|,
literal|"These guys develop stuff"
argument_list|,
literal|"salary_i_dv"
argument_list|,
literal|"1000"
argument_list|,
literal|"dept_id_i"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|deptsDocs
operator|.
name|add
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
operator|++
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Marketing"
argument_list|,
literal|"text_t"
argument_list|,
literal|"These guys make you look good"
argument_list|,
literal|"salary_i_dv"
argument_list|,
literal|"1500"
argument_list|,
literal|"dept_id_i"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|deptsDocs
operator|.
name|add
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
operator|++
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Sales"
argument_list|,
literal|"text_t"
argument_list|,
literal|"These guys sell stuff"
argument_list|,
literal|"salary_i_dv"
argument_list|,
literal|"1600"
argument_list|,
literal|"dept_id_i"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|deptsDocs
operator|.
name|add
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|id
operator|++
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Support"
argument_list|,
literal|"text_t"
argument_list|,
literal|"These guys help customers"
argument_list|,
literal|"salary_i_dv"
argument_list|,
literal|"800"
argument_list|,
literal|"dept_id_i"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|addDocs
argument_list|(
name|depts
argument_list|,
name|deptsDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|addDocs
specifier|private
name|void
name|addDocs
parameter_list|(
name|String
name|collection
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|docs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|StringBuilder
name|upd
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<update>"
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|docs
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|add
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|upd
operator|.
name|append
argument_list|(
name|add
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|upd
operator|.
name|append
argument_list|(
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|rarely
argument_list|()
operator|||
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|upd
operator|.
name|append
argument_list|(
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|upd
operator|.
name|append
argument_list|(
literal|"</update>"
argument_list|)
expr_stmt|;
name|ContentStreamUpdateRequest
name|req
init|=
operator|new
name|ContentStreamUpdateRequest
argument_list|(
literal|"/update"
argument_list|)
decl_stmt|;
name|req
operator|.
name|addContentStream
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|upd
operator|.
name|toString
argument_list|()
argument_list|,
literal|"text/xml"
argument_list|)
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|req
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|upd
operator|.
name|setLength
argument_list|(
literal|"<update>"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
