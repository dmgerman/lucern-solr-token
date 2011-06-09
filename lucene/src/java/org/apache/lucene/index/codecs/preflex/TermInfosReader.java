begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.preflex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|preflex
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
name|CorruptIndexException
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
name|FieldInfos
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
name|IndexFileNames
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
name|store
operator|.
name|Directory
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
name|CloseableThreadLocal
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
name|DoubleBarrelLRUCache
import|;
end_import
begin_comment
comment|/** This stores a monotonically increasing set of<Term, TermInfo> pairs in a  * Directory.  Pairs are accessed either by Term or by ordinal position the  * set  * @deprecated (4.0) This class has been replaced by  * FormatPostingsTermsDictReader, except for reading old segments.   * @lucene.experimental  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|TermInfosReader
specifier|public
specifier|final
class|class
name|TermInfosReader
block|{
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|segment
specifier|private
specifier|final
name|String
name|segment
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|threadResources
specifier|private
specifier|final
name|CloseableThreadLocal
argument_list|<
name|ThreadResources
argument_list|>
name|threadResources
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|ThreadResources
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|origEnum
specifier|private
specifier|final
name|SegmentTermEnum
name|origEnum
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|long
name|size
decl_stmt|;
DECL|field|indexTerms
specifier|private
specifier|final
name|Term
index|[]
name|indexTerms
decl_stmt|;
DECL|field|indexInfos
specifier|private
specifier|final
name|TermInfo
index|[]
name|indexInfos
decl_stmt|;
DECL|field|indexPointers
specifier|private
specifier|final
name|long
index|[]
name|indexPointers
decl_stmt|;
DECL|field|totalIndexInterval
specifier|private
specifier|final
name|int
name|totalIndexInterval
decl_stmt|;
DECL|field|DEFAULT_CACHE_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|DEFAULT_CACHE_SIZE
init|=
literal|1024
decl_stmt|;
comment|// Just adds term's ord to TermInfo
DECL|class|TermInfoAndOrd
specifier|private
specifier|final
specifier|static
class|class
name|TermInfoAndOrd
extends|extends
name|TermInfo
block|{
DECL|field|termOrd
specifier|final
name|long
name|termOrd
decl_stmt|;
DECL|method|TermInfoAndOrd
specifier|public
name|TermInfoAndOrd
parameter_list|(
name|TermInfo
name|ti
parameter_list|,
name|long
name|termOrd
parameter_list|)
block|{
name|super
argument_list|(
name|ti
argument_list|)
expr_stmt|;
assert|assert
name|termOrd
operator|>=
literal|0
assert|;
name|this
operator|.
name|termOrd
operator|=
name|termOrd
expr_stmt|;
block|}
block|}
DECL|class|CloneableTerm
specifier|private
specifier|static
class|class
name|CloneableTerm
extends|extends
name|DoubleBarrelLRUCache
operator|.
name|CloneableKey
block|{
DECL|field|term
name|Term
name|term
decl_stmt|;
DECL|method|CloneableTerm
specifier|public
name|CloneableTerm
parameter_list|(
name|Term
name|t
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|t
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
name|CloneableTerm
name|t
init|=
operator|(
name|CloneableTerm
operator|)
name|other
decl_stmt|;
return|return
name|this
operator|.
name|term
operator|.
name|equals
argument_list|(
name|t
operator|.
name|term
argument_list|)
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
name|term
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|CloneableTerm
argument_list|(
name|term
argument_list|)
return|;
block|}
block|}
DECL|field|termsCache
specifier|private
specifier|final
name|DoubleBarrelLRUCache
argument_list|<
name|CloneableTerm
argument_list|,
name|TermInfoAndOrd
argument_list|>
name|termsCache
init|=
operator|new
name|DoubleBarrelLRUCache
argument_list|<
name|CloneableTerm
argument_list|,
name|TermInfoAndOrd
argument_list|>
argument_list|(
name|DEFAULT_CACHE_SIZE
argument_list|)
decl_stmt|;
comment|/**    * Per-thread resources managed by ThreadLocal    */
DECL|class|ThreadResources
specifier|private
specifier|static
specifier|final
class|class
name|ThreadResources
block|{
DECL|field|termEnum
name|SegmentTermEnum
name|termEnum
decl_stmt|;
block|}
DECL|method|TermInfosReader
name|TermInfosReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|seg
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|int
name|readBufferSize
parameter_list|,
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|indexDivisor
operator|<
literal|1
operator|&&
name|indexDivisor
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"indexDivisor must be -1 (don't load terms index) or greater than 0: got "
operator|+
name|indexDivisor
argument_list|)
throw|;
block|}
try|try
block|{
name|directory
operator|=
name|dir
expr_stmt|;
name|segment
operator|=
name|seg
expr_stmt|;
name|fieldInfos
operator|=
name|fis
expr_stmt|;
name|origEnum
operator|=
operator|new
name|SegmentTermEnum
argument_list|(
name|directory
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|PreFlexCodec
operator|.
name|TERMS_EXTENSION
argument_list|)
argument_list|,
name|readBufferSize
argument_list|)
argument_list|,
name|fieldInfos
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|size
operator|=
name|origEnum
operator|.
name|size
expr_stmt|;
if|if
condition|(
name|indexDivisor
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Load terms index
name|totalIndexInterval
operator|=
name|origEnum
operator|.
name|indexInterval
operator|*
name|indexDivisor
expr_stmt|;
specifier|final
name|SegmentTermEnum
name|indexEnum
init|=
operator|new
name|SegmentTermEnum
argument_list|(
name|directory
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|PreFlexCodec
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
argument_list|,
name|readBufferSize
argument_list|)
argument_list|,
name|fieldInfos
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|indexSize
init|=
literal|1
operator|+
operator|(
operator|(
name|int
operator|)
name|indexEnum
operator|.
name|size
operator|-
literal|1
operator|)
operator|/
name|indexDivisor
decl_stmt|;
comment|// otherwise read index
name|indexTerms
operator|=
operator|new
name|Term
index|[
name|indexSize
index|]
expr_stmt|;
name|indexInfos
operator|=
operator|new
name|TermInfo
index|[
name|indexSize
index|]
expr_stmt|;
name|indexPointers
operator|=
operator|new
name|long
index|[
name|indexSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|indexEnum
operator|.
name|next
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|indexTerms
index|[
name|i
index|]
operator|=
name|indexEnum
operator|.
name|term
argument_list|()
expr_stmt|;
assert|assert
name|indexTerms
index|[
name|i
index|]
operator|!=
literal|null
assert|;
assert|assert
name|indexTerms
index|[
name|i
index|]
operator|.
name|text
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|indexTerms
index|[
name|i
index|]
operator|.
name|field
argument_list|()
operator|!=
literal|null
assert|;
name|indexInfos
index|[
name|i
index|]
operator|=
name|indexEnum
operator|.
name|termInfo
argument_list|()
expr_stmt|;
name|indexPointers
index|[
name|i
index|]
operator|=
name|indexEnum
operator|.
name|indexPointer
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|indexDivisor
condition|;
name|j
operator|++
control|)
if|if
condition|(
operator|!
name|indexEnum
operator|.
name|next
argument_list|()
condition|)
break|break;
block|}
block|}
finally|finally
block|{
name|indexEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Do not load terms index:
name|totalIndexInterval
operator|=
operator|-
literal|1
expr_stmt|;
name|indexTerms
operator|=
literal|null
expr_stmt|;
name|indexInfos
operator|=
literal|null
expr_stmt|;
name|indexPointers
operator|=
literal|null
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
comment|// With lock-less commits, it's entirely possible (and
comment|// fine) to hit a FileNotFound exception above. In
comment|// this case, we want to explicitly close any subset
comment|// of things that were opened so that we don't have to
comment|// wait for a GC to do so.
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getSkipInterval
specifier|public
name|int
name|getSkipInterval
parameter_list|()
block|{
return|return
name|origEnum
operator|.
name|skipInterval
return|;
block|}
DECL|method|getMaxSkipLevels
specifier|public
name|int
name|getMaxSkipLevels
parameter_list|()
block|{
return|return
name|origEnum
operator|.
name|maxSkipLevels
return|;
block|}
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|origEnum
operator|!=
literal|null
condition|)
name|origEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|threadResources
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the number of term/value pairs in the set. */
DECL|method|size
name|long
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|getThreadResources
specifier|private
name|ThreadResources
name|getThreadResources
parameter_list|()
block|{
name|ThreadResources
name|resources
init|=
name|threadResources
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|resources
operator|==
literal|null
condition|)
block|{
name|resources
operator|=
operator|new
name|ThreadResources
argument_list|()
expr_stmt|;
name|resources
operator|.
name|termEnum
operator|=
name|terms
argument_list|()
expr_stmt|;
name|threadResources
operator|.
name|set
argument_list|(
name|resources
argument_list|)
expr_stmt|;
block|}
return|return
name|resources
return|;
block|}
comment|/** Returns the offset of the greatest index entry which is less than or equal to term.*/
DECL|method|getIndexOffset
specifier|private
name|int
name|getIndexOffset
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// binary search indexTerms[]
name|int
name|hi
init|=
name|indexTerms
operator|.
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
assert|assert
name|indexTerms
index|[
name|mid
index|]
operator|!=
literal|null
operator|:
literal|"indexTerms = "
operator|+
name|indexTerms
operator|.
name|length
operator|+
literal|" mid="
operator|+
name|mid
assert|;
name|int
name|delta
init|=
name|term
operator|.
name|compareToUTF16
argument_list|(
name|indexTerms
index|[
name|mid
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|delta
operator|<
literal|0
condition|)
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|delta
operator|>
literal|0
condition|)
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
else|else
return|return
name|mid
return|;
block|}
return|return
name|hi
return|;
block|}
DECL|method|seekEnum
specifier|private
name|void
name|seekEnum
parameter_list|(
name|SegmentTermEnum
name|enumerator
parameter_list|,
name|int
name|indexOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|enumerator
operator|.
name|seek
argument_list|(
name|indexPointers
index|[
name|indexOffset
index|]
argument_list|,
operator|(
operator|(
name|long
operator|)
name|indexOffset
operator|*
name|totalIndexInterval
operator|)
operator|-
literal|1
argument_list|,
name|indexTerms
index|[
name|indexOffset
index|]
argument_list|,
name|indexInfos
index|[
name|indexOffset
index|]
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the TermInfo for a Term in the set, or null. */
DECL|method|get
name|TermInfo
name|get
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|get
argument_list|(
name|term
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Returns the TermInfo for a Term in the set, or null. */
DECL|method|get
specifier|private
name|TermInfo
name|get
parameter_list|(
name|Term
name|term
parameter_list|,
name|boolean
name|mustSeekEnum
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|ensureIndexIsRead
argument_list|()
expr_stmt|;
name|TermInfoAndOrd
name|tiOrd
init|=
name|termsCache
operator|.
name|get
argument_list|(
operator|new
name|CloneableTerm
argument_list|(
name|term
argument_list|)
argument_list|)
decl_stmt|;
name|ThreadResources
name|resources
init|=
name|getThreadResources
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|mustSeekEnum
operator|&&
name|tiOrd
operator|!=
literal|null
condition|)
block|{
return|return
name|tiOrd
return|;
block|}
return|return
name|seekEnum
argument_list|(
name|resources
operator|.
name|termEnum
argument_list|,
name|term
argument_list|,
name|tiOrd
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|cacheCurrentTerm
specifier|public
name|void
name|cacheCurrentTerm
parameter_list|(
name|SegmentTermEnum
name|enumerator
parameter_list|)
block|{
name|termsCache
operator|.
name|put
argument_list|(
operator|new
name|CloneableTerm
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
argument_list|,
operator|new
name|TermInfoAndOrd
argument_list|(
name|enumerator
operator|.
name|termInfo
argument_list|,
name|enumerator
operator|.
name|position
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|seekEnum
name|TermInfo
name|seekEnum
parameter_list|(
name|SegmentTermEnum
name|enumerator
parameter_list|,
name|Term
name|term
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|useCache
condition|)
block|{
return|return
name|seekEnum
argument_list|(
name|enumerator
argument_list|,
name|term
argument_list|,
name|termsCache
operator|.
name|get
argument_list|(
operator|new
name|CloneableTerm
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|,
name|useCache
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|seekEnum
argument_list|(
name|enumerator
argument_list|,
name|term
argument_list|,
literal|null
argument_list|,
name|useCache
argument_list|)
return|;
block|}
block|}
DECL|method|seekEnum
name|TermInfo
name|seekEnum
parameter_list|(
name|SegmentTermEnum
name|enumerator
parameter_list|,
name|Term
name|term
parameter_list|,
name|TermInfoAndOrd
name|tiOrd
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// optimize sequential access: first try scanning cached enum w/o seeking
if|if
condition|(
name|enumerator
operator|.
name|term
argument_list|()
operator|!=
literal|null
comment|// term is at or past current
operator|&&
operator|(
operator|(
name|enumerator
operator|.
name|prev
argument_list|()
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareToUTF16
argument_list|(
name|enumerator
operator|.
name|prev
argument_list|()
argument_list|)
operator|>
literal|0
operator|)
operator|||
name|term
operator|.
name|compareToUTF16
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
operator|>=
literal|0
operator|)
condition|)
block|{
name|int
name|enumOffset
init|=
call|(
name|int
call|)
argument_list|(
name|enumerator
operator|.
name|position
operator|/
name|totalIndexInterval
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|indexTerms
operator|.
name|length
operator|==
name|enumOffset
comment|// but before end of block
operator|||
name|term
operator|.
name|compareToUTF16
argument_list|(
name|indexTerms
index|[
name|enumOffset
index|]
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// no need to seek
specifier|final
name|TermInfo
name|ti
decl_stmt|;
name|int
name|numScans
init|=
name|enumerator
operator|.
name|scanTo
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|enumerator
operator|.
name|term
argument_list|()
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareToUTF16
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
block|{
name|ti
operator|=
name|enumerator
operator|.
name|termInfo
expr_stmt|;
if|if
condition|(
name|numScans
operator|>
literal|1
condition|)
block|{
comment|// we only  want to put this TermInfo into the cache if
comment|// scanEnum skipped more than one dictionary entry.
comment|// This prevents RangeQueries or WildcardQueries to
comment|// wipe out the cache when they iterate over a large numbers
comment|// of terms in order
if|if
condition|(
name|tiOrd
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|useCache
condition|)
block|{
name|termsCache
operator|.
name|put
argument_list|(
operator|new
name|CloneableTerm
argument_list|(
name|term
argument_list|)
argument_list|,
operator|new
name|TermInfoAndOrd
argument_list|(
name|ti
argument_list|,
name|enumerator
operator|.
name|position
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
name|sameTermInfo
argument_list|(
name|ti
argument_list|,
name|tiOrd
argument_list|,
name|enumerator
argument_list|)
assert|;
assert|assert
operator|(
name|int
operator|)
name|enumerator
operator|.
name|position
operator|==
name|tiOrd
operator|.
name|termOrd
assert|;
block|}
block|}
block|}
else|else
block|{
name|ti
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|ti
return|;
block|}
block|}
comment|// random-access: must seek
specifier|final
name|int
name|indexPos
decl_stmt|;
if|if
condition|(
name|tiOrd
operator|!=
literal|null
condition|)
block|{
name|indexPos
operator|=
call|(
name|int
call|)
argument_list|(
name|tiOrd
operator|.
name|termOrd
operator|/
name|totalIndexInterval
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Must do binary search:
name|indexPos
operator|=
name|getIndexOffset
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|seekEnum
argument_list|(
name|enumerator
argument_list|,
name|indexPos
argument_list|)
expr_stmt|;
name|enumerator
operator|.
name|scanTo
argument_list|(
name|term
argument_list|)
expr_stmt|;
specifier|final
name|TermInfo
name|ti
decl_stmt|;
if|if
condition|(
name|enumerator
operator|.
name|term
argument_list|()
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareToUTF16
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
block|{
name|ti
operator|=
name|enumerator
operator|.
name|termInfo
expr_stmt|;
if|if
condition|(
name|tiOrd
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|useCache
condition|)
block|{
comment|// LUCENE-3183: it's possible, if term is Term("",
comment|// ""), for the STE to be incorrectly un-positioned
comment|// after scan-to; work around this by not caching in
comment|// this case:
if|if
condition|(
name|enumerator
operator|.
name|position
operator|>=
literal|0
condition|)
block|{
name|termsCache
operator|.
name|put
argument_list|(
operator|new
name|CloneableTerm
argument_list|(
name|term
argument_list|)
argument_list|,
operator|new
name|TermInfoAndOrd
argument_list|(
name|ti
argument_list|,
name|enumerator
operator|.
name|position
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
assert|assert
name|sameTermInfo
argument_list|(
name|ti
argument_list|,
name|tiOrd
argument_list|,
name|enumerator
argument_list|)
assert|;
assert|assert
name|enumerator
operator|.
name|position
operator|==
name|tiOrd
operator|.
name|termOrd
assert|;
block|}
block|}
else|else
block|{
name|ti
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|ti
return|;
block|}
comment|// called only from asserts
DECL|method|sameTermInfo
specifier|private
name|boolean
name|sameTermInfo
parameter_list|(
name|TermInfo
name|ti1
parameter_list|,
name|TermInfo
name|ti2
parameter_list|,
name|SegmentTermEnum
name|enumerator
parameter_list|)
block|{
if|if
condition|(
name|ti1
operator|.
name|docFreq
operator|!=
name|ti2
operator|.
name|docFreq
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|ti1
operator|.
name|freqPointer
operator|!=
name|ti2
operator|.
name|freqPointer
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|ti1
operator|.
name|proxPointer
operator|!=
name|ti2
operator|.
name|proxPointer
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// skipOffset is only valid when docFreq>= skipInterval:
if|if
condition|(
name|ti1
operator|.
name|docFreq
operator|>=
name|enumerator
operator|.
name|skipInterval
operator|&&
name|ti1
operator|.
name|skipOffset
operator|!=
name|ti2
operator|.
name|skipOffset
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|ensureIndexIsRead
specifier|private
name|void
name|ensureIndexIsRead
parameter_list|()
block|{
if|if
condition|(
name|indexTerms
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"terms index was not loaded when this reader was created"
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the position of a Term in the set or -1. */
DECL|method|getPosition
name|long
name|getPosition
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
return|return
operator|-
literal|1
return|;
name|ensureIndexIsRead
argument_list|()
expr_stmt|;
name|int
name|indexOffset
init|=
name|getIndexOffset
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|SegmentTermEnum
name|enumerator
init|=
name|getThreadResources
argument_list|()
operator|.
name|termEnum
decl_stmt|;
name|seekEnum
argument_list|(
name|enumerator
argument_list|,
name|indexOffset
argument_list|)
expr_stmt|;
while|while
condition|(
name|term
operator|.
name|compareToUTF16
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
operator|>
literal|0
operator|&&
name|enumerator
operator|.
name|next
argument_list|()
condition|)
block|{}
if|if
condition|(
name|term
operator|.
name|compareToUTF16
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
return|return
name|enumerator
operator|.
name|position
return|;
else|else
return|return
operator|-
literal|1
return|;
block|}
comment|/** Returns an enumeration of all the Terms and TermInfos in the set. */
DECL|method|terms
specifier|public
name|SegmentTermEnum
name|terms
parameter_list|()
block|{
return|return
operator|(
name|SegmentTermEnum
operator|)
name|origEnum
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/** Returns an enumeration of terms starting at or after the named term. */
DECL|method|terms
specifier|public
name|SegmentTermEnum
name|terms
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|get
argument_list|(
name|term
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|SegmentTermEnum
operator|)
name|getThreadResources
argument_list|()
operator|.
name|termEnum
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
end_class
end_unit
