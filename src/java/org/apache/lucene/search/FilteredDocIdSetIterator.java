begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
begin_comment
comment|/**  * Abstract decorator class of a DocIdSetIterator  * implementation that provides on-demand filter/validation  * mechanism on an underlying DocIdSetIterator.  See {@link  * FilteredDocIdSet}.  */
end_comment
begin_class
DECL|class|FilteredDocIdSetIterator
specifier|public
specifier|abstract
class|class
name|FilteredDocIdSetIterator
extends|extends
name|DocIdSetIterator
block|{
DECL|field|_innerIter
specifier|protected
name|DocIdSetIterator
name|_innerIter
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
decl_stmt|;
comment|/**    * Constructor.    * @param innerIter Underlying DocIdSetIterator.    */
DECL|method|FilteredDocIdSetIterator
specifier|public
name|FilteredDocIdSetIterator
parameter_list|(
name|DocIdSetIterator
name|innerIter
parameter_list|)
block|{
if|if
condition|(
name|innerIter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null iterator"
argument_list|)
throw|;
block|}
name|_innerIter
operator|=
name|innerIter
expr_stmt|;
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**    * Validation method to determine whether a docid should be in the result set.    * @param doc docid to be tested    * @return true if input docid should be in the result set, false otherwise.    * @see #FilteredDocIdSetIterator(DocIdSetIterator).    */
DECL|method|match
specifier|abstract
specifier|protected
name|boolean
name|match
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** @deprecated use {@link #docID()} instead. */
DECL|method|doc
specifier|public
specifier|final
name|int
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
comment|/** @deprecated use {@link #nextDoc()} instead. */
DECL|method|next
specifier|public
specifier|final
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
return|;
block|}
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
operator|(
name|doc
operator|=
name|_innerIter
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|match
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
name|doc
return|;
block|}
block|}
return|return
name|doc
return|;
block|}
comment|/** @deprecated use {@link #advance(int)} instead. */
DECL|method|skipTo
specifier|public
specifier|final
name|boolean
name|skipTo
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
name|n
argument_list|)
operator|!=
name|NO_MORE_DOCS
return|;
block|}
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|=
name|_innerIter
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|match
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
name|doc
return|;
block|}
else|else
block|{
while|while
condition|(
operator|(
name|doc
operator|=
name|_innerIter
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|match
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
name|doc
return|;
block|}
block|}
return|return
name|doc
return|;
block|}
block|}
return|return
name|doc
return|;
block|}
block|}
end_class
end_unit
