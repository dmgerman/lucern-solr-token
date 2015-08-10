begin_unit
begin_package
DECL|package|org.apache.solr.query
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|query
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import
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
name|ConstantScoreQuery
import|;
end_import
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
name|IndexSearcher
import|;
end_import
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
name|Query
import|;
end_import
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
name|Weight
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ToStringUtils
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
name|search
operator|.
name|DocSet
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
name|search
operator|.
name|ExtendedQueryBase
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import
begin_class
DECL|class|FilterQuery
specifier|public
class|class
name|FilterQuery
extends|extends
name|ExtendedQueryBase
block|{
DECL|field|q
specifier|protected
specifier|final
name|Query
name|q
decl_stmt|;
DECL|method|FilterQuery
specifier|public
name|FilterQuery
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
name|setBoost
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// default boost is 0;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|q
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|q
operator|.
name|hashCode
argument_list|()
operator|+
literal|0xc0e65615
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|FilterQuery
operator|)
condition|)
return|return
literal|false
return|;
name|FilterQuery
name|fq
init|=
operator|(
name|FilterQuery
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|q
operator|.
name|equals
argument_list|(
name|fq
operator|.
name|q
argument_list|)
operator|&&
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|fq
operator|.
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"field("
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|q
operator|.
name|toString
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|newQ
init|=
name|q
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|newQ
operator|!=
name|q
condition|)
block|{
name|FilterQuery
name|fq
init|=
operator|new
name|FilterQuery
argument_list|(
name|newQ
argument_list|)
decl_stmt|;
name|fq
operator|.
name|setBoost
argument_list|(
name|this
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|fq
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needScores
parameter_list|)
throws|throws
name|IOException
block|{
comment|// SolrRequestInfo reqInfo = SolrRequestInfo.getRequestInfo();
if|if
condition|(
operator|!
operator|(
name|searcher
operator|instanceof
name|SolrIndexSearcher
operator|)
condition|)
block|{
comment|// delete-by-query won't have SolrIndexSearcher
name|ConstantScoreQuery
name|csq
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|csq
operator|.
name|setBoost
argument_list|(
name|this
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|csq
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needScores
argument_list|)
return|;
block|}
name|SolrIndexSearcher
name|solrSearcher
init|=
operator|(
name|SolrIndexSearcher
operator|)
name|searcher
decl_stmt|;
name|DocSet
name|docs
init|=
name|solrSearcher
operator|.
name|getDocSet
argument_list|(
name|q
argument_list|)
decl_stmt|;
comment|// reqInfo.addCloseHook(docs);  // needed for off-heap refcounting
name|ConstantScoreQuery
name|csq
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|docs
operator|.
name|getTopFilter
argument_list|()
argument_list|)
decl_stmt|;
name|csq
operator|.
name|setBoost
argument_list|(
name|this
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|csq
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needScores
argument_list|)
return|;
block|}
block|}
end_class
end_unit