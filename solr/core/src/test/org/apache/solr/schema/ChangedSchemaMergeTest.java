begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|Charsets
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|NamedList
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
name|SolrCore
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
name|LocalSolrQueryRequest
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
name|update
operator|.
name|AddUpdateCommand
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
name|CommitUpdateCommand
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
name|UpdateHandler
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
name|Test
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
begin_class
DECL|class|ChangedSchemaMergeTest
specifier|public
class|class
name|ChangedSchemaMergeTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|()
expr_stmt|;
block|}
DECL|field|solrHomeDirectory
specifier|private
specifier|final
name|File
name|solrHomeDirectory
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|getSimpleClassName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|schemaFile
specifier|private
name|File
name|schemaFile
init|=
literal|null
decl_stmt|;
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
modifier|...
name|fieldValues
parameter_list|)
throws|throws
name|IOException
block|{
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|solrDoc
operator|=
name|sdoc
argument_list|(
operator|(
name|Object
index|[]
operator|)
name|fieldValues
argument_list|)
expr_stmt|;
name|updater
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|CoreContainer
name|init
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|solrHomeDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Failed to mkdirs workDir"
argument_list|,
name|solrHomeDirectory
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|changed
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"changed"
argument_list|)
decl_stmt|;
name|copyMinConf
argument_list|(
name|changed
argument_list|,
literal|"name=changed"
argument_list|)
expr_stmt|;
comment|// Overlay with my local schema
name|schemaFile
operator|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|changed
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
name|schemaFile
argument_list|,
name|withWhich
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|discoveryXml
init|=
literal|"<solr></solr>"
decl_stmt|;
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"solr.xml"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
name|solrXml
argument_list|,
name|discoveryXml
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
operator|new
name|CoreContainer
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|cores
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|cores
return|;
block|}
annotation|@
name|Test
DECL|method|testOptimizeDiffSchemas
specifier|public
name|void
name|testOptimizeDiffSchemas
parameter_list|()
throws|throws
name|Exception
block|{
comment|// load up a core (why not put it on disk?)
name|CoreContainer
name|cc
init|=
name|init
argument_list|()
decl_stmt|;
name|SolrCore
name|changed
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"changed"
argument_list|)
decl_stmt|;
try|try
block|{
comment|// add some documents
name|addDoc
argument_list|(
name|changed
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"which"
argument_list|,
literal|"15"
argument_list|,
literal|"text"
argument_list|,
literal|"some stuff with which"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|changed
argument_list|,
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"which"
argument_list|,
literal|"15"
argument_list|,
literal|"text"
argument_list|,
literal|"some stuff with which"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|changed
argument_list|,
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"which"
argument_list|,
literal|"15"
argument_list|,
literal|"text"
argument_list|,
literal|"some stuff with which"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|changed
argument_list|,
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"which"
argument_list|,
literal|"15"
argument_list|,
literal|"text"
argument_list|,
literal|"some stuff with which"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|changed
argument_list|,
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|changed
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|commit
argument_list|(
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// write the new schema out and make it current
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
name|schemaFile
argument_list|,
name|withoutWhich
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSchema
name|iSchema
init|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
literal|"schema.xml"
argument_list|,
name|changed
operator|.
name|getSolrConfig
argument_list|()
argument_list|)
decl_stmt|;
name|changed
operator|.
name|setLatestSchema
argument_list|(
name|iSchema
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|changed
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"some stuff without which"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|changed
argument_list|,
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"text"
argument_list|,
literal|"some stuff without which"
argument_list|)
expr_stmt|;
name|changed
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|commit
argument_list|(
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|changed
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|commit
argument_list|(
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|changed
operator|!=
literal|null
condition|)
name|changed
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|withWhich
specifier|private
specifier|static
name|String
name|withWhich
init|=
literal|"<schema name=\"tiny\" version=\"1.1\">\n"
operator|+
literal|"<fields>\n"
operator|+
literal|"<field name=\"id\" type=\"string\" indexed=\"true\" stored=\"true\" required=\"true\"/>\n"
operator|+
literal|"<field name=\"text\" type=\"text\" indexed=\"true\" stored=\"true\"/>\n"
operator|+
literal|"<field name=\"which\" type=\"int\" indexed=\"true\" stored=\"true\"/>\n"
operator|+
literal|"</fields>\n"
operator|+
literal|"<uniqueKey>id</uniqueKey>\n"
operator|+
literal|"\n"
operator|+
literal|"<types>\n"
operator|+
literal|"<fieldtype name=\"text\" class=\"solr.TextField\">\n"
operator|+
literal|"<analyzer>\n"
operator|+
literal|"<tokenizer class=\"solr.WhitespaceTokenizerFactory\"/>\n"
operator|+
literal|"<filter class=\"solr.LowerCaseFilterFactory\"/>\n"
operator|+
literal|"</analyzer>\n"
operator|+
literal|"</fieldtype>\n"
operator|+
literal|"<fieldType name=\"string\" class=\"solr.StrField\"/>\n"
operator|+
literal|"<fieldType name=\"int\" class=\"solr.TrieIntField\" precisionStep=\"0\" positionIncrementGap=\"0\"/>"
operator|+
literal|"</types>\n"
operator|+
literal|"</schema>"
decl_stmt|;
DECL|field|withoutWhich
specifier|private
specifier|static
name|String
name|withoutWhich
init|=
literal|"<schema name=\"tiny\" version=\"1.1\">\n"
operator|+
literal|"<fields>\n"
operator|+
literal|"<field name=\"id\" type=\"string\" indexed=\"true\" stored=\"true\" required=\"true\"/>\n"
operator|+
literal|"<field name=\"text\" type=\"text\" indexed=\"true\" stored=\"true\"/>\n"
operator|+
literal|"</fields>\n"
operator|+
literal|"<uniqueKey>id</uniqueKey>\n"
operator|+
literal|"\n"
operator|+
literal|"<types>\n"
operator|+
literal|"<fieldtype name=\"text\" class=\"solr.TextField\">\n"
operator|+
literal|"<analyzer>\n"
operator|+
literal|"<tokenizer class=\"solr.WhitespaceTokenizerFactory\"/>\n"
operator|+
literal|"<filter class=\"solr.LowerCaseFilterFactory\"/>\n"
operator|+
literal|"</analyzer>\n"
operator|+
literal|"</fieldtype>\n"
operator|+
literal|"<fieldType name=\"string\" class=\"solr.StrField\"/>\n"
operator|+
literal|"<fieldType name=\"int\" class=\"solr.TrieIntField\" precisionStep=\"0\" positionIncrementGap=\"0\"/>"
operator|+
literal|"</types>\n"
operator|+
literal|"</schema>"
decl_stmt|;
block|}
end_class
end_unit
