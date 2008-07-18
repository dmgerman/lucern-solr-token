begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
begin_class
DECL|class|TermsHashPerThread
specifier|final
class|class
name|TermsHashPerThread
extends|extends
name|InvertedDocConsumerPerThread
block|{
DECL|field|termsHash
specifier|final
name|TermsHash
name|termsHash
decl_stmt|;
DECL|field|consumer
specifier|final
name|TermsHashConsumerPerThread
name|consumer
decl_stmt|;
DECL|field|nextPerThread
specifier|final
name|TermsHashPerThread
name|nextPerThread
decl_stmt|;
DECL|field|charPool
specifier|final
name|CharBlockPool
name|charPool
decl_stmt|;
DECL|field|intPool
specifier|final
name|IntBlockPool
name|intPool
decl_stmt|;
DECL|field|bytePool
specifier|final
name|ByteBlockPool
name|bytePool
decl_stmt|;
DECL|field|primary
specifier|final
name|boolean
name|primary
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|freePostings
specifier|final
name|RawPostingList
name|freePostings
index|[]
init|=
operator|new
name|RawPostingList
index|[
literal|256
index|]
decl_stmt|;
DECL|field|freePostingsCount
name|int
name|freePostingsCount
decl_stmt|;
DECL|method|TermsHashPerThread
specifier|public
name|TermsHashPerThread
parameter_list|(
name|DocInverterPerThread
name|docInverterPerThread
parameter_list|,
specifier|final
name|TermsHash
name|termsHash
parameter_list|,
specifier|final
name|TermsHash
name|nextTermsHash
parameter_list|,
specifier|final
name|TermsHashPerThread
name|primaryPerThread
parameter_list|)
block|{
name|docState
operator|=
name|docInverterPerThread
operator|.
name|docState
expr_stmt|;
name|this
operator|.
name|termsHash
operator|=
name|termsHash
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|termsHash
operator|.
name|consumer
operator|.
name|addThread
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
comment|// We are primary
name|charPool
operator|=
operator|new
name|CharBlockPool
argument_list|(
name|termsHash
operator|.
name|docWriter
argument_list|)
expr_stmt|;
name|primary
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|charPool
operator|=
name|primaryPerThread
operator|.
name|charPool
expr_stmt|;
name|primary
operator|=
literal|false
expr_stmt|;
block|}
name|intPool
operator|=
operator|new
name|IntBlockPool
argument_list|(
name|termsHash
operator|.
name|docWriter
argument_list|,
name|termsHash
operator|.
name|trackAllocations
argument_list|)
expr_stmt|;
name|bytePool
operator|=
operator|new
name|ByteBlockPool
argument_list|(
name|termsHash
operator|.
name|docWriter
operator|.
name|byteBlockAllocator
argument_list|,
name|termsHash
operator|.
name|trackAllocations
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
name|nextPerThread
operator|=
name|nextTermsHash
operator|.
name|addThread
argument_list|(
name|docInverterPerThread
argument_list|,
name|this
argument_list|)
expr_stmt|;
else|else
name|nextPerThread
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|addField
name|InvertedDocConsumerPerField
name|addField
parameter_list|(
name|DocInverterPerField
name|docInverterPerField
parameter_list|,
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
operator|new
name|TermsHashPerField
argument_list|(
name|docInverterPerField
argument_list|,
name|this
argument_list|,
name|nextPerThread
argument_list|,
name|fieldInfo
argument_list|)
return|;
block|}
DECL|method|abort
specifier|synchronized
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|reset
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextPerThread
operator|!=
literal|null
condition|)
name|nextPerThread
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
comment|// perField calls this when it needs more postings:
DECL|method|morePostings
name|void
name|morePostings
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|freePostingsCount
operator|==
literal|0
assert|;
name|termsHash
operator|.
name|getPostings
argument_list|(
name|freePostings
argument_list|)
expr_stmt|;
name|freePostingsCount
operator|=
name|freePostings
operator|.
name|length
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|freePostingsCount
condition|;
name|i
operator|++
control|)
assert|assert
name|freePostings
index|[
name|i
index|]
operator|!=
literal|null
assert|;
block|}
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextPerThread
operator|!=
literal|null
condition|)
name|nextPerThread
operator|.
name|consumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
DECL|method|finishDocument
specifier|public
name|DocumentsWriter
operator|.
name|DocWriter
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|DocumentsWriter
operator|.
name|DocWriter
name|doc
init|=
name|consumer
operator|.
name|finishDocument
argument_list|()
decl_stmt|;
specifier|final
name|DocumentsWriter
operator|.
name|DocWriter
name|doc2
decl_stmt|;
if|if
condition|(
name|nextPerThread
operator|!=
literal|null
condition|)
name|doc2
operator|=
name|nextPerThread
operator|.
name|consumer
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
else|else
name|doc2
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
return|return
name|doc2
return|;
else|else
block|{
name|doc
operator|.
name|setNext
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
comment|// Clear all state
DECL|method|reset
name|void
name|reset
parameter_list|(
name|boolean
name|recyclePostings
parameter_list|)
block|{
name|intPool
operator|.
name|reset
argument_list|()
expr_stmt|;
name|bytePool
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|primary
condition|)
name|charPool
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|recyclePostings
condition|)
block|{
name|termsHash
operator|.
name|recyclePostings
argument_list|(
name|freePostings
argument_list|,
name|freePostingsCount
argument_list|)
expr_stmt|;
name|freePostingsCount
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
