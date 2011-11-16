begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|query
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|Term
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
name|TermsEnum
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
name|Terms
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
name|MultiFields
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
name|BytesRef
import|;
end_import
begin_class
DECL|class|SrndTermQuery
specifier|public
class|class
name|SrndTermQuery
extends|extends
name|SimpleTerm
block|{
DECL|method|SrndTermQuery
specifier|public
name|SrndTermQuery
parameter_list|(
name|String
name|termText
parameter_list|,
name|boolean
name|quoted
parameter_list|)
block|{
name|super
argument_list|(
name|quoted
argument_list|)
expr_stmt|;
name|this
operator|.
name|termText
operator|=
name|termText
expr_stmt|;
block|}
DECL|field|termText
specifier|private
specifier|final
name|String
name|termText
decl_stmt|;
DECL|method|getTermText
specifier|public
name|String
name|getTermText
parameter_list|()
block|{
return|return
name|termText
return|;
block|}
DECL|method|getLuceneTerm
specifier|public
name|Term
name|getLuceneTerm
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|getTermText
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toStringUnquoted
specifier|public
name|String
name|toStringUnquoted
parameter_list|()
block|{
return|return
name|getTermText
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|visitMatchingTerms
specifier|public
name|void
name|visitMatchingTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|MatchingTermVisitor
name|mtv
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* check term presence in index here for symmetry with other SimpleTerm's */
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|TermsEnum
operator|.
name|SeekStatus
name|status
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|getTermText
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
condition|)
block|{
name|mtv
operator|.
name|visitMatchingTerm
argument_list|(
name|getLuceneTerm
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
