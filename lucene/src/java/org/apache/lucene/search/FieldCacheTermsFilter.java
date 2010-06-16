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
name|util
operator|.
name|OpenBitSet
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
comment|/**  * A {@link Filter} that only accepts documents whose single  * term value in the specified field is contained in the  * provided set of allowed terms.  *   *<p/>  *   * This is the same functionality as TermsFilter (from  * contrib/queries), except this filter requires that the  * field contains only a single term for all documents.  * Because of drastically different implementations, they  * also have different performance characteristics, as  * described below.  *   *<p/>  *   * The first invocation of this filter on a given field will  * be slower, since a {@link FieldCache.StringIndex} must be  * created.  Subsequent invocations using the same field  * will re-use this cache.  However, as with all  * functionality based on {@link FieldCache}, persistent RAM  * is consumed to hold the cache, and is not freed until the  * {@link IndexReader} is closed.  In contrast, TermsFilter  * has no persistent RAM consumption.  *   *   *<p/>  *   * With each search, this filter translates the specified  * set of Terms into a private {@link OpenBitSet} keyed by  * term number per unique {@link IndexReader} (normally one  * reader per segment).  Then, during matching, the term  * number for each docID is retrieved from the cache and  * then checked for inclusion using the {@link OpenBitSet}.  * Since all testing is done using RAM resident data  * structures, performance should be very fast, most likely  * fast enough to not require further caching of the  * DocIdSet for each possible combination of terms.  * However, because docIDs are simply scanned linearly, an  * index with a great many small documents may find this  * linear scan too costly.  *   *<p/>  *   * In contrast, TermsFilter builds up an {@link OpenBitSet},  * keyed by docID, every time it's created, by enumerating  * through all matching docs using {@link DocsEnum} to seek  * and scan through each term's docID list.  While there is  * no linear scan of all docIDs, besides the allocation of  * the underlying array in the {@link OpenBitSet}, this  * approach requires a number of "disk seeks" in proportion  * to the number of terms, which can be exceptionally costly  * when there are cache misses in the OS's IO cache.  *   *<p/>  *   * Generally, this filter will be slower on the first  * invocation for a given field, but subsequent invocations,  * even if you change the allowed set of Terms, should be  * faster than TermsFilter, especially as the number of  * Terms being matched increases.  If you are matching only  * a very small number of terms, and those terms in turn  * match a very small number of documents, TermsFilter may  * perform faster.  *  *<p/>  *  * Which filter is best is very application dependent.  */
end_comment
begin_class
DECL|class|FieldCacheTermsFilter
specifier|public
class|class
name|FieldCacheTermsFilter
extends|extends
name|Filter
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|terms
specifier|private
name|String
index|[]
name|terms
decl_stmt|;
DECL|method|FieldCacheTermsFilter
specifier|public
name|FieldCacheTermsFilter
parameter_list|(
name|String
name|field
parameter_list|,
name|String
modifier|...
name|terms
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
block|}
DECL|method|getFieldCache
specifier|public
name|FieldCache
name|getFieldCache
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|DEFAULT
return|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldCacheTermsFilterDocIdSet
argument_list|(
name|getFieldCache
argument_list|()
operator|.
name|getTermsIndex
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
argument_list|)
return|;
block|}
DECL|class|FieldCacheTermsFilterDocIdSet
specifier|protected
class|class
name|FieldCacheTermsFilterDocIdSet
extends|extends
name|DocIdSet
block|{
DECL|field|fcsi
specifier|private
name|FieldCache
operator|.
name|DocTermsIndex
name|fcsi
decl_stmt|;
DECL|field|openBitSet
specifier|private
name|OpenBitSet
name|openBitSet
decl_stmt|;
DECL|method|FieldCacheTermsFilterDocIdSet
specifier|public
name|FieldCacheTermsFilterDocIdSet
parameter_list|(
name|FieldCache
operator|.
name|DocTermsIndex
name|fcsi
parameter_list|)
block|{
name|this
operator|.
name|fcsi
operator|=
name|fcsi
expr_stmt|;
name|openBitSet
operator|=
operator|new
name|OpenBitSet
argument_list|(
name|this
operator|.
name|fcsi
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|termNumber
init|=
name|this
operator|.
name|fcsi
operator|.
name|binarySearchLookup
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
argument_list|,
name|spare
argument_list|)
decl_stmt|;
if|if
condition|(
name|termNumber
operator|>
literal|0
condition|)
block|{
name|openBitSet
operator|.
name|fastSet
argument_list|(
name|termNumber
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|FieldCacheTermsFilterDocIdSetIterator
argument_list|()
return|;
block|}
comment|/** This DocIdSet implementation is cacheable. */
annotation|@
name|Override
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|class|FieldCacheTermsFilterDocIdSetIterator
specifier|protected
class|class
name|FieldCacheTermsFilterDocIdSetIterator
extends|extends
name|DocIdSetIterator
block|{
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
operator|!
name|openBitSet
operator|.
name|fastGet
argument_list|(
name|fcsi
operator|.
name|getOrd
argument_list|(
operator|++
name|doc
argument_list|)
argument_list|)
condition|)
block|{}
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
name|doc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
try|try
block|{
name|doc
operator|=
name|target
expr_stmt|;
while|while
condition|(
operator|!
name|openBitSet
operator|.
name|fastGet
argument_list|(
name|fcsi
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
argument_list|)
condition|)
block|{
name|doc
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
name|doc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
