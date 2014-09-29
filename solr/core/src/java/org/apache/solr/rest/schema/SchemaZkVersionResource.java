begin_unit
begin_package
DECL|package|org.apache.solr.rest.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
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
name|cloud
operator|.
name|ZkSolrResourceLoader
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
name|rest
operator|.
name|BaseSolrResource
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
name|rest
operator|.
name|GETable
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
name|schema
operator|.
name|ManagedIndexSchema
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
name|ZkIndexSchemaReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|representation
operator|.
name|Representation
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|resource
operator|.
name|ResourceException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  * This class responds to requests at /solr/(corename)/schema/zkversion  */
end_comment
begin_class
DECL|class|SchemaZkVersionResource
specifier|public
class|class
name|SchemaZkVersionResource
extends|extends
name|BaseSolrResource
implements|implements
name|GETable
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SchemaZkVersionResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|refreshIfBelowVersion
specifier|protected
name|int
name|refreshIfBelowVersion
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|SchemaZkVersionResource
specifier|public
name|SchemaZkVersionResource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doInit
specifier|public
name|void
name|doInit
parameter_list|()
throws|throws
name|ResourceException
block|{
name|super
operator|.
name|doInit
argument_list|()
expr_stmt|;
comment|// sometimes the client knows which version it expects
name|Object
name|refreshParam
init|=
name|getSolrRequest
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"refreshIfBelowVersion"
argument_list|)
decl_stmt|;
if|if
condition|(
name|refreshParam
operator|!=
literal|null
condition|)
name|refreshIfBelowVersion
operator|=
operator|(
name|refreshParam
operator|instanceof
name|Number
operator|)
condition|?
operator|(
operator|(
name|Number
operator|)
name|refreshParam
operator|)
operator|.
name|intValue
argument_list|()
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|refreshParam
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Representation
name|get
parameter_list|()
block|{
try|try
block|{
name|int
name|zkVersion
init|=
operator|-
literal|1
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|getSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|schema
operator|instanceof
name|ManagedIndexSchema
condition|)
block|{
name|ManagedIndexSchema
name|managed
init|=
operator|(
name|ManagedIndexSchema
operator|)
name|schema
decl_stmt|;
name|zkVersion
operator|=
name|managed
operator|.
name|getSchemaZkVersion
argument_list|()
expr_stmt|;
if|if
condition|(
name|refreshIfBelowVersion
operator|!=
operator|-
literal|1
operator|&&
name|zkVersion
operator|<
name|refreshIfBelowVersion
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"\n\n\n REFRESHING SCHEMA (refreshIfBelowVersion="
operator|+
name|refreshIfBelowVersion
operator|+
literal|") before returning version! \n\n\n"
argument_list|)
expr_stmt|;
name|ZkSolrResourceLoader
name|zkSolrResourceLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|getSolrCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|ZkIndexSchemaReader
name|zkIndexSchemaReader
init|=
name|zkSolrResourceLoader
operator|.
name|getZkIndexSchemaReader
argument_list|()
decl_stmt|;
name|managed
operator|=
name|zkIndexSchemaReader
operator|.
name|refreshSchemaFromZk
argument_list|(
name|refreshIfBelowVersion
argument_list|)
expr_stmt|;
name|zkVersion
operator|=
name|managed
operator|.
name|getSchemaZkVersion
argument_list|()
expr_stmt|;
block|}
block|}
name|getSolrResponse
argument_list|()
operator|.
name|add
argument_list|(
literal|"zkversion"
argument_list|,
name|zkVersion
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getSolrResponse
argument_list|()
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|handlePostExecution
argument_list|(
name|log
argument_list|)
expr_stmt|;
return|return
operator|new
name|SolrOutputRepresentation
argument_list|()
return|;
block|}
block|}
end_class
end_unit
