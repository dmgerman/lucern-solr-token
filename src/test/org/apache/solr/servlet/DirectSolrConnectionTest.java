begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
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
name|params
operator|.
name|SolrParams
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
begin_class
DECL|class|DirectSolrConnectionTest
specifier|public
class|class
name|DirectSolrConnectionTest
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
literal|"solr/crazy-path-to-schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solr/crazy-path-to-config.xml"
return|;
block|}
DECL|field|direct
name|DirectSolrConnection
name|direct
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|direct
operator|=
operator|new
name|DirectSolrConnection
argument_list|()
expr_stmt|;
block|}
comment|// Check that a request gets back the echoParams call
DECL|method|testSimpleRequest
specifier|public
name|void
name|testSimpleRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|pathAndParams
init|=
literal|"/select?wt=xml&version=2.2&echoParams=explicit&q=*:*"
decl_stmt|;
name|String
name|got
init|=
name|direct
operator|.
name|request
argument_list|(
name|pathAndParams
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|indexOf
argument_list|(
literal|"<str name=\"echoParams\">explicit</str>"
argument_list|)
operator|>
literal|5
argument_list|)
expr_stmt|;
comment|// It should throw an exception for unknown handler
try|try
block|{
name|direct
operator|.
name|request
argument_list|(
literal|"/path to nonexistang thingy!!"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should throw an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
comment|// Check that a request gets back the echoParams call
DECL|method|testInsertThenSelect
specifier|public
name|void
name|testInsertThenSelect
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|value
init|=
literal|"Kittens!!! \u20AC"
decl_stmt|;
name|String
index|[]
name|cmds
init|=
operator|new
name|String
index|[]
block|{
literal|"<delete><id>42</id></delete>"
block|,
literal|"<add><doc><field name=\"id\">42</field><field name=\"subject\">"
operator|+
name|value
operator|+
literal|"</field></doc></add>"
block|,
literal|"<commit/>"
block|}
decl_stmt|;
name|String
name|getIt
init|=
literal|"/select?wt=xml&q=id:42"
decl_stmt|;
comment|// Test using the Stream body parameter
for|for
control|(
name|String
name|cmd
range|:
name|cmds
control|)
block|{
name|direct
operator|.
name|request
argument_list|(
literal|"/update?"
operator|+
name|SolrParams
operator|.
name|STREAM_BODY
operator|+
literal|"="
operator|+
name|cmd
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|String
name|got
init|=
name|direct
operator|.
name|request
argument_list|(
name|getIt
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|indexOf
argument_list|(
name|value
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Same thing using the posted body
for|for
control|(
name|String
name|cmd
range|:
name|cmds
control|)
block|{
name|direct
operator|.
name|request
argument_list|(
literal|"/update"
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
block|}
name|got
operator|=
name|direct
operator|.
name|request
argument_list|(
name|getIt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|got
operator|.
name|indexOf
argument_list|(
name|value
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
