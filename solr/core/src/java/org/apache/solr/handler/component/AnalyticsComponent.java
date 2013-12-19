begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|plugin
operator|.
name|AnalyticsStatisticsCollector
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
name|analytics
operator|.
name|request
operator|.
name|AnalyticsStats
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
name|analytics
operator|.
name|util
operator|.
name|AnalyticsParams
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
begin_class
DECL|class|AnalyticsComponent
specifier|public
class|class
name|AnalyticsComponent
extends|extends
name|SearchComponent
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"analytics"
decl_stmt|;
DECL|field|analyticsCollector
specifier|private
specifier|final
name|AnalyticsStatisticsCollector
name|analyticsCollector
init|=
operator|new
name|AnalyticsStatisticsCollector
argument_list|()
decl_stmt|;
empty_stmt|;
annotation|@
name|Override
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|AnalyticsParams
operator|.
name|ANALYTICS
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|rb
operator|.
name|setNeedDocSet
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|AnalyticsParams
operator|.
name|ANALYTICS
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|AnalyticsStats
name|s
init|=
operator|new
name|AnalyticsStats
argument_list|(
name|rb
operator|.
name|req
argument_list|,
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docSet
argument_list|,
name|params
argument_list|,
name|analyticsCollector
argument_list|)
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"stats"
argument_list|,
name|s
operator|.
name|execute
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*   @Override   public int distributedProcess(ResponseBuilder rb) throws IOException {     return ResponseBuilder.STAGE_DONE;   }      @Override   public void modifyRequest(ResponseBuilder rb, SearchComponent who, ShardRequest sreq) {     // TODO Auto-generated method stub     super.modifyRequest(rb, who, sreq);   }      @Override   public void handleResponses(ResponseBuilder rb, ShardRequest sreq) {     // TODO Auto-generated method stub     super.handleResponses(rb, sreq);   }     @Override   public void finishStage(ResponseBuilder rb) {     // TODO Auto-generated method stub     super.finishStage(rb);   }   */
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|COMPONENT_NAME
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Perform analytics"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getSpecificationVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
return|return
name|analyticsCollector
operator|.
name|getStatistics
argument_list|()
return|;
block|}
block|}
end_class
end_unit
