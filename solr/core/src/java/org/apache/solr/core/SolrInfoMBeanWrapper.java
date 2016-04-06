begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
begin_comment
comment|/**  * Wraps a {@link SolrInfoMBean}.  */
end_comment
begin_class
DECL|class|SolrInfoMBeanWrapper
specifier|public
class|class
name|SolrInfoMBeanWrapper
implements|implements
name|SolrInfoMBean
block|{
DECL|field|mbean
specifier|private
specifier|final
name|SolrInfoMBean
name|mbean
decl_stmt|;
DECL|method|SolrInfoMBeanWrapper
specifier|public
name|SolrInfoMBeanWrapper
parameter_list|(
name|SolrInfoMBean
name|mbean
parameter_list|)
block|{
name|this
operator|.
name|mbean
operator|=
name|mbean
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|mbean
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|mbean
operator|.
name|getVersion
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|mbean
operator|.
name|getDescription
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|mbean
operator|.
name|getCategory
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
name|mbean
operator|.
name|getSource
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
name|mbean
operator|.
name|getDocs
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
return|return
name|mbean
operator|.
name|getStatistics
argument_list|()
return|;
block|}
block|}
end_class
end_unit
