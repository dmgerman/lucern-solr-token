begin_unit
begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
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
name|index
operator|.
name|*
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
operator|.
name|AtomicReaderContext
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
name|DocIdSet
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
name|Filter
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
name|Bits
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
name|FixedBitSet
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import
begin_comment
comment|/**  * Constructs a filter for docs matching any of the terms added to this class.  * Unlike a RangeFilter this can be used for filtering on multiple terms that are not necessarily in  * a sequence. An example might be a collection of primary keys from a database query result or perhaps  * a choice of "category" labels picked by the end user. As a filter, this is much faster than the  * equivalent query (a BooleanQuery with many "should" TermQueries)  */
end_comment
begin_class
DECL|class|TermsFilter
specifier|public
class|class
name|TermsFilter
extends|extends
name|Filter
block|{
DECL|field|terms
specifier|private
specifier|final
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|TreeSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Adds a term to the list of acceptable terms    *    * @param term    */
DECL|method|addTerm
specifier|public
name|void
name|addTerm
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.search.Filter#getDocIdSet(org.apache.lucene.index.IndexReader)    */
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|context
operator|.
name|reader
decl_stmt|;
name|FixedBitSet
name|result
init|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|String
name|lastField
init|=
literal|null
decl_stmt|;
name|Terms
name|termsC
init|=
literal|null
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
if|if
condition|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|lastField
argument_list|)
condition|)
block|{
name|termsC
operator|=
name|fields
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|termsC
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|termsEnum
operator|=
name|termsC
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|lastField
operator|=
name|term
operator|.
name|field
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
comment|// TODO this check doesn't make sense, decide which variable its supposed to be for
name|br
operator|.
name|copy
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|br
argument_list|)
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
condition|)
block|{
name|docs
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|docs
argument_list|)
expr_stmt|;
while|while
condition|(
name|docs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|result
operator|.
name|set
argument_list|(
name|docs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|result
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
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|(
name|obj
operator|==
literal|null
operator|)
operator|||
operator|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TermsFilter
name|test
init|=
operator|(
name|TermsFilter
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|terms
operator|==
name|test
operator|.
name|terms
operator|||
operator|(
name|terms
operator|!=
literal|null
operator|&&
name|terms
operator|.
name|equals
argument_list|(
name|test
operator|.
name|terms
argument_list|)
operator|)
operator|)
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
name|int
name|hash
init|=
literal|9
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|term
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
block|}
end_class
end_unit
