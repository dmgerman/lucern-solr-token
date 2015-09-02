begin_unit
begin_package
DECL|package|org.apache.solr.velocity
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|velocity
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
name|core
operator|.
name|SolrCore
import|;
end_import
begin_class
DECL|class|MockTool
specifier|public
class|class
name|MockTool
block|{
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|method|MockTool
specifier|public
name|MockTool
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
block|}
DECL|method|star
specifier|public
name|String
name|star
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
literal|"** "
operator|+
name|str
operator|+
literal|" **"
return|;
block|}
DECL|method|getCore
specifier|public
name|SolrCore
name|getCore
parameter_list|()
block|{
return|return
name|core
return|;
block|}
block|}
end_class
end_unit
