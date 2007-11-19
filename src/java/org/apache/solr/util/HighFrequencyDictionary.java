begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|TermEnum
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
name|spell
operator|.
name|Dictionary
import|;
end_import
begin_comment
comment|/**  * HighFrequencyDictionary: terms taken from the given field  * of a Lucene index, which appear in a number of documents  * above a given threshold.  *  * When using IndexReader.terms(Term) the code must not call next() on TermEnum  * as the first call to TermEnum, see: http://issues.apache.org/jira/browse/LUCENE-6  *  * Threshold is a value in [0..1] representing the minimum  * number of documents (of the total) where a term should appear.  *   * Based on LuceneDictionary.  */
end_comment
begin_class
DECL|class|HighFrequencyDictionary
specifier|public
class|class
name|HighFrequencyDictionary
implements|implements
name|Dictionary
block|{
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|thresh
specifier|private
name|float
name|thresh
decl_stmt|;
DECL|method|HighFrequencyDictionary
specifier|public
name|HighFrequencyDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|float
name|thresh
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|thresh
operator|=
name|thresh
expr_stmt|;
block|}
DECL|method|getWordsIterator
specifier|public
specifier|final
name|Iterator
name|getWordsIterator
parameter_list|()
block|{
return|return
operator|new
name|HighFrequencyIterator
argument_list|()
return|;
block|}
DECL|class|HighFrequencyIterator
specifier|final
class|class
name|HighFrequencyIterator
implements|implements
name|Iterator
block|{
DECL|field|termEnum
specifier|private
name|TermEnum
name|termEnum
decl_stmt|;
DECL|field|actualTerm
specifier|private
name|Term
name|actualTerm
decl_stmt|;
DECL|field|hasNextCalled
specifier|private
name|boolean
name|hasNextCalled
decl_stmt|;
DECL|field|minNumDocs
specifier|private
name|int
name|minNumDocs
decl_stmt|;
DECL|method|HighFrequencyIterator
name|HighFrequencyIterator
parameter_list|()
block|{
try|try
block|{
name|termEnum
operator|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|minNumDocs
operator|=
call|(
name|int
call|)
argument_list|(
name|thresh
operator|*
operator|(
name|float
operator|)
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|isFrequent
specifier|private
name|boolean
name|isFrequent
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
try|try
block|{
return|return
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
operator|>=
name|minNumDocs
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|next
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNextCalled
condition|)
block|{
name|hasNext
argument_list|()
expr_stmt|;
block|}
name|hasNextCalled
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|termEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
operator|(
name|actualTerm
operator|!=
literal|null
operator|)
condition|?
name|actualTerm
operator|.
name|text
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|hasNextCalled
condition|)
block|{
return|return
name|actualTerm
operator|!=
literal|null
return|;
block|}
name|hasNextCalled
operator|=
literal|true
expr_stmt|;
do|do
block|{
name|actualTerm
operator|=
name|termEnum
operator|.
name|term
argument_list|()
expr_stmt|;
comment|// if there are no words return false
if|if
condition|(
name|actualTerm
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|currentField
init|=
name|actualTerm
operator|.
name|field
argument_list|()
decl_stmt|;
comment|// if the next word doesn't have the same field return false
if|if
condition|(
name|currentField
operator|!=
name|field
condition|)
block|{
name|actualTerm
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// got a valid term, does it pass the threshold?
if|if
condition|(
name|isFrequent
argument_list|(
name|actualTerm
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// term not up to threshold
try|try
block|{
name|termEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class
end_unit
