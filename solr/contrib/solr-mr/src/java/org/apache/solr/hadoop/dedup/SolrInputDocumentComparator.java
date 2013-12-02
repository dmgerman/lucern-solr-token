begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.hadoop.dedup
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
operator|.
name|dedup
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|common
operator|.
name|SolrInputField
import|;
end_import
begin_comment
comment|/**  * Default mechanism of determining which of two Solr documents with the same  * key is the more recent version.  */
end_comment
begin_class
DECL|class|SolrInputDocumentComparator
specifier|public
specifier|final
class|class
name|SolrInputDocumentComparator
implements|implements
name|Comparator
argument_list|<
name|SolrInputDocument
argument_list|>
block|{
DECL|field|child
specifier|private
name|Comparator
name|child
decl_stmt|;
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|method|SolrInputDocumentComparator
name|SolrInputDocumentComparator
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Comparator
name|child
parameter_list|)
block|{
name|this
operator|.
name|child
operator|=
name|child
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|SolrInputDocument
name|doc1
parameter_list|,
name|SolrInputDocument
name|doc2
parameter_list|)
block|{
name|SolrInputField
name|f1
init|=
name|doc1
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|SolrInputField
name|f2
init|=
name|doc2
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|f1
operator|==
name|f2
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|f1
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|f2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
name|Object
name|v1
init|=
name|f1
operator|.
name|getFirstValue
argument_list|()
decl_stmt|;
name|Object
name|v2
init|=
name|f2
operator|.
name|getFirstValue
argument_list|()
decl_stmt|;
return|return
name|child
operator|.
name|compare
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
DECL|class|TimeStampComparator
specifier|public
specifier|static
specifier|final
class|class
name|TimeStampComparator
implements|implements
name|Comparator
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|v1
parameter_list|,
name|Object
name|v2
parameter_list|)
block|{
if|if
condition|(
name|v1
operator|==
name|v2
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|v1
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|v2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
name|long
name|t1
init|=
name|getLong
argument_list|(
name|v1
argument_list|)
decl_stmt|;
name|long
name|t2
init|=
name|getLong
argument_list|(
name|v2
argument_list|)
decl_stmt|;
return|return
operator|(
name|t1
operator|<
name|t2
condition|?
operator|-
literal|1
else|:
operator|(
name|t1
operator|==
name|t2
condition|?
literal|0
else|:
literal|1
operator|)
operator|)
return|;
block|}
DECL|method|getLong
specifier|private
name|long
name|getLong
parameter_list|(
name|Object
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|instanceof
name|Long
condition|)
block|{
return|return
operator|(
operator|(
name|Long
operator|)
name|v
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|v
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
