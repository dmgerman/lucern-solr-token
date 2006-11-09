begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
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
name|analysis
operator|.
name|TokenFilter
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
name|analysis
operator|.
name|TokenStream
import|;
end_import
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
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * A filter that stemms french words. It supports a table of words that should  * not be stemmed at all. The used stemmer can be changed at runtime after the  * filter object is created (as long as it is a FrenchStemmer).  *  * @author    Patrick Talbot (based on Gerhard Schwarz work for German)  */
end_comment
begin_class
DECL|class|FrenchStemFilter
specifier|public
specifier|final
class|class
name|FrenchStemFilter
extends|extends
name|TokenFilter
block|{
comment|/** 	 * The actual token in the input stream. 	 */
DECL|field|token
specifier|private
name|Token
name|token
init|=
literal|null
decl_stmt|;
DECL|field|stemmer
specifier|private
name|FrenchStemmer
name|stemmer
init|=
literal|null
decl_stmt|;
DECL|field|exclusions
specifier|private
name|Set
name|exclusions
init|=
literal|null
decl_stmt|;
DECL|method|FrenchStemFilter
specifier|public
name|FrenchStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|stemmer
operator|=
operator|new
name|FrenchStemmer
argument_list|()
expr_stmt|;
block|}
DECL|method|FrenchStemFilter
specifier|public
name|FrenchStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Set
name|exclusiontable
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|exclusions
operator|=
name|exclusiontable
expr_stmt|;
block|}
comment|/** 	 * @return  Returns the next token in the stream, or null at EOS 	 */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|token
operator|=
name|input
operator|.
name|next
argument_list|()
operator|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Check the exclusiontable
elseif|else
if|if
condition|(
name|exclusions
operator|!=
literal|null
operator|&&
name|exclusions
operator|.
name|contains
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|token
return|;
block|}
else|else
block|{
name|String
name|s
init|=
name|stemmer
operator|.
name|stem
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
decl_stmt|;
comment|// If not stemmed, dont waste the time creating a new token
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|Token
argument_list|(
name|s
argument_list|,
name|token
operator|.
name|startOffset
argument_list|()
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
argument_list|,
name|token
operator|.
name|type
argument_list|()
argument_list|)
return|;
block|}
return|return
name|token
return|;
block|}
block|}
comment|/** 	 * Set a alternative/custom FrenchStemmer for this filter. 	 */
DECL|method|setStemmer
specifier|public
name|void
name|setStemmer
parameter_list|(
name|FrenchStemmer
name|stemmer
parameter_list|)
block|{
if|if
condition|(
name|stemmer
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|stemmer
operator|=
name|stemmer
expr_stmt|;
block|}
block|}
comment|/** 	 * Set an alternative exclusion list for this filter. 	 */
DECL|method|setExclusionTable
specifier|public
name|void
name|setExclusionTable
parameter_list|(
name|Hashtable
name|exclusiontable
parameter_list|)
block|{
name|exclusions
operator|=
operator|new
name|HashSet
argument_list|(
name|exclusiontable
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
