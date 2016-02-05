begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.request.schema
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
operator|.
name|schema
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_class
DECL|class|FieldTypeDefinition
specifier|public
class|class
name|FieldTypeDefinition
block|{
DECL|field|attributes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
decl_stmt|;
DECL|field|analyzer
specifier|private
name|AnalyzerDefinition
name|analyzer
decl_stmt|;
DECL|field|indexAnalyzer
specifier|private
name|AnalyzerDefinition
name|indexAnalyzer
decl_stmt|;
DECL|field|queryAnalyzer
specifier|private
name|AnalyzerDefinition
name|queryAnalyzer
decl_stmt|;
DECL|field|multiTermAnalyzer
specifier|private
name|AnalyzerDefinition
name|multiTermAnalyzer
decl_stmt|;
DECL|field|similarity
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|similarity
decl_stmt|;
DECL|method|FieldTypeDefinition
specifier|public
name|FieldTypeDefinition
parameter_list|()
block|{   }
DECL|method|getAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getAttributes
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
DECL|method|setAttributes
specifier|public
name|void
name|setAttributes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
parameter_list|)
block|{
name|this
operator|.
name|attributes
operator|=
name|attributes
expr_stmt|;
block|}
DECL|method|getSimilarity
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getSimilarity
parameter_list|()
block|{
return|return
name|similarity
return|;
block|}
DECL|method|setSimilarity
specifier|public
name|void
name|setSimilarity
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
block|}
DECL|method|getAnalyzer
specifier|public
name|AnalyzerDefinition
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|AnalyzerDefinition
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
DECL|method|getIndexAnalyzer
specifier|public
name|AnalyzerDefinition
name|getIndexAnalyzer
parameter_list|()
block|{
return|return
name|indexAnalyzer
return|;
block|}
DECL|method|setIndexAnalyzer
specifier|public
name|void
name|setIndexAnalyzer
parameter_list|(
name|AnalyzerDefinition
name|indexAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|indexAnalyzer
operator|=
name|indexAnalyzer
expr_stmt|;
block|}
DECL|method|getQueryAnalyzer
specifier|public
name|AnalyzerDefinition
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|queryAnalyzer
return|;
block|}
DECL|method|setQueryAnalyzer
specifier|public
name|void
name|setQueryAnalyzer
parameter_list|(
name|AnalyzerDefinition
name|queryAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|queryAnalyzer
operator|=
name|queryAnalyzer
expr_stmt|;
block|}
DECL|method|getMultiTermAnalyzer
specifier|public
name|AnalyzerDefinition
name|getMultiTermAnalyzer
parameter_list|()
block|{
return|return
name|multiTermAnalyzer
return|;
block|}
DECL|method|setMultiTermAnalyzer
specifier|public
name|void
name|setMultiTermAnalyzer
parameter_list|(
name|AnalyzerDefinition
name|multiTermAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|multiTermAnalyzer
operator|=
name|multiTermAnalyzer
expr_stmt|;
block|}
block|}
end_class
end_unit
