begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
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
name|search
operator|.
name|vectorhighlight
operator|.
name|BoundaryScanner
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
name|params
operator|.
name|HighlightParams
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
name|params
operator|.
name|SolrParams
import|;
end_import
begin_class
DECL|class|SimpleBoundaryScanner
specifier|public
class|class
name|SimpleBoundaryScanner
extends|extends
name|SolrBoundaryScanner
block|{
annotation|@
name|Override
DECL|method|get
specifier|protected
name|BoundaryScanner
name|get
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|int
name|maxScan
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|BS_MAX_SCAN
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|BS_CHARS
argument_list|,
literal|".,!? \t\n"
argument_list|)
decl_stmt|;
name|Character
index|[]
name|chars
init|=
operator|new
name|Character
index|[
name|str
operator|.
name|length
argument_list|()
index|]
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
name|str
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|chars
index|[
name|i
index|]
operator|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
operator|.
name|SimpleBoundaryScanner
argument_list|(
name|maxScan
argument_list|,
name|chars
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////
comment|//////////////////////// SolrInfoMBeans methods ///////////////////////
comment|///////////////////////////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"SimpleBoundaryScanner"
return|;
block|}
block|}
end_class
end_unit
