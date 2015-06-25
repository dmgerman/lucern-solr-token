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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
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
name|java
operator|.
name|util
operator|.
name|List
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
begin_comment
comment|/** Utility class to do efficient primary-key (only 1 doc contains the  *  given term) lookups by segment, re-using the enums.  This class is  *  not thread safe, so it is the caller's job to create and use one  *  instance of this per thread.  Do not use this if a term may appear  *  in more than one document!  It will only return the first one it  *  finds. */
end_comment
begin_class
DECL|class|PerThreadPKLookup
specifier|public
class|class
name|PerThreadPKLookup
block|{
DECL|field|termsEnums
specifier|protected
specifier|final
name|TermsEnum
index|[]
name|termsEnums
decl_stmt|;
DECL|field|postingsEnums
specifier|protected
specifier|final
name|PostingsEnum
index|[]
name|postingsEnums
decl_stmt|;
DECL|field|liveDocs
specifier|protected
specifier|final
name|Bits
index|[]
name|liveDocs
decl_stmt|;
DECL|field|docBases
specifier|protected
specifier|final
name|int
index|[]
name|docBases
decl_stmt|;
DECL|field|numSegs
specifier|protected
specifier|final
name|int
name|numSegs
decl_stmt|;
DECL|field|hasDeletions
specifier|protected
specifier|final
name|boolean
name|hasDeletions
decl_stmt|;
DECL|method|PerThreadPKLookup
specifier|public
name|PerThreadPKLookup
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|idFieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|r
operator|.
name|leaves
argument_list|()
argument_list|)
decl_stmt|;
comment|// Larger segments are more likely to have the id, so we sort largest to smallest by numDocs:
name|Collections
operator|.
name|sort
argument_list|(
name|leaves
argument_list|,
operator|new
name|Comparator
argument_list|<
name|LeafReaderContext
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|LeafReaderContext
name|c1
parameter_list|,
name|LeafReaderContext
name|c2
parameter_list|)
block|{
return|return
name|c2
operator|.
name|reader
argument_list|()
operator|.
name|numDocs
argument_list|()
operator|-
name|c1
operator|.
name|reader
argument_list|()
operator|.
name|numDocs
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|termsEnums
operator|=
operator|new
name|TermsEnum
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|postingsEnums
operator|=
operator|new
name|PostingsEnum
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|liveDocs
operator|=
operator|new
name|Bits
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|docBases
operator|=
operator|new
name|int
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|numSegs
init|=
literal|0
decl_stmt|;
name|boolean
name|hasDeletions
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|leaves
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Terms
name|terms
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|idFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|termsEnums
index|[
name|numSegs
index|]
operator|=
name|terms
operator|.
name|iterator
argument_list|()
expr_stmt|;
assert|assert
name|termsEnums
index|[
name|numSegs
index|]
operator|!=
literal|null
assert|;
name|docBases
index|[
name|numSegs
index|]
operator|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|docBase
expr_stmt|;
name|liveDocs
index|[
name|numSegs
index|]
operator|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
name|hasDeletions
operator||=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|hasDeletions
argument_list|()
expr_stmt|;
name|numSegs
operator|++
expr_stmt|;
block|}
block|}
name|this
operator|.
name|numSegs
operator|=
name|numSegs
expr_stmt|;
name|this
operator|.
name|hasDeletions
operator|=
name|hasDeletions
expr_stmt|;
block|}
comment|/** Returns docID if found, else -1. */
DECL|method|lookup
specifier|public
name|int
name|lookup
parameter_list|(
name|BytesRef
name|id
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|seg
init|=
literal|0
init|;
name|seg
operator|<
name|numSegs
condition|;
name|seg
operator|++
control|)
block|{
if|if
condition|(
name|termsEnums
index|[
name|seg
index|]
operator|.
name|seekExact
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|postingsEnums
index|[
name|seg
index|]
operator|=
name|termsEnums
index|[
name|seg
index|]
operator|.
name|postings
argument_list|(
name|postingsEnums
index|[
name|seg
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|docID
init|=
name|postingsEnums
index|[
name|seg
index|]
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|!=
name|PostingsEnum
operator|.
name|NO_MORE_DOCS
operator|&&
operator|(
name|liveDocs
index|[
name|seg
index|]
operator|==
literal|null
operator|||
name|liveDocs
index|[
name|seg
index|]
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|)
condition|)
block|{
return|return
name|docBases
index|[
name|seg
index|]
operator|+
name|docID
return|;
block|}
assert|assert
name|hasDeletions
assert|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|// TODO: add reopen method to carry over re-used enums...?
block|}
end_class
end_unit
