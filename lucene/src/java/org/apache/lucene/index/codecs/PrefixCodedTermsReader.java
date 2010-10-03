begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs
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
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DocsEnum
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
name|DocsAndPositionsEnum
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
name|FieldInfo
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
name|FieldsEnum
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
name|index
operator|.
name|SegmentInfo
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
name|store
operator|.
name|IndexInput
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
name|DoubleBarrelLRUCache
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
name|CodecUtil
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
name|codecs
operator|.
name|standard
operator|.
name|StandardPostingsReader
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_comment
comment|/** Handles a terms dict, but decouples all details of  *  doc/freqs/positions reading to an instance of {@link  *  StandardPostingsReader}.  This class is reusable for  *  codecs that use a different format for  *  docs/freqs/positions (though codecs are also free to  *  make their own terms dict impl).  *  *<p>This class also interacts with an instance of {@link  * TermsIndexReaderBase}, to abstract away the specific  * implementation of the terms dict index.   * @lucene.experimental */
end_comment
begin_class
DECL|class|PrefixCodedTermsReader
specifier|public
class|class
name|PrefixCodedTermsReader
extends|extends
name|FieldsProducer
block|{
comment|// Open input to the main terms dict file (_X.tis)
DECL|field|in
specifier|private
specifier|final
name|IndexInput
name|in
decl_stmt|;
comment|// Reads the terms dict entries, to gather state to
comment|// produce DocsEnum on demand
DECL|field|postingsReader
specifier|private
specifier|final
name|PostingsReaderBase
name|postingsReader
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|FieldReader
argument_list|>
name|fields
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|FieldReader
argument_list|>
argument_list|()
decl_stmt|;
comment|// Comparator that orders our terms
DECL|field|termComp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
decl_stmt|;
comment|// Caches the most recently looked-up field + terms:
DECL|field|termsCache
specifier|private
specifier|final
name|DoubleBarrelLRUCache
argument_list|<
name|FieldAndTerm
argument_list|,
name|TermState
argument_list|>
name|termsCache
decl_stmt|;
comment|// Reads the terms index
DECL|field|indexReader
specifier|private
name|TermsIndexReaderBase
name|indexReader
decl_stmt|;
comment|// keeps the dirStart offset
DECL|field|dirOffset
specifier|protected
name|long
name|dirOffset
decl_stmt|;
comment|// Used as key for the terms cache
DECL|class|FieldAndTerm
specifier|private
specifier|static
class|class
name|FieldAndTerm
extends|extends
name|DoubleBarrelLRUCache
operator|.
name|CloneableKey
block|{
DECL|field|field
name|String
name|field
decl_stmt|;
DECL|field|term
name|BytesRef
name|term
decl_stmt|;
DECL|method|FieldAndTerm
specifier|public
name|FieldAndTerm
parameter_list|()
block|{     }
DECL|method|FieldAndTerm
specifier|public
name|FieldAndTerm
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|term
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
name|term
operator|=
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldAndTerm
specifier|public
name|FieldAndTerm
parameter_list|(
name|FieldAndTerm
name|other
parameter_list|)
block|{
name|field
operator|=
name|other
operator|.
name|field
expr_stmt|;
name|term
operator|=
operator|new
name|BytesRef
argument_list|(
name|other
operator|.
name|term
argument_list|)
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
name|_other
parameter_list|)
block|{
name|FieldAndTerm
name|other
init|=
operator|(
name|FieldAndTerm
operator|)
name|_other
decl_stmt|;
return|return
name|other
operator|.
name|field
operator|==
name|field
operator|&&
name|term
operator|.
name|bytesEquals
argument_list|(
name|other
operator|.
name|term
argument_list|)
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
name|FieldAndTerm
argument_list|(
name|this
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
name|field
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
name|term
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
DECL|method|PrefixCodedTermsReader
specifier|public
name|PrefixCodedTermsReader
parameter_list|(
name|TermsIndexReaderBase
name|indexReader
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|String
name|segment
parameter_list|,
name|PostingsReaderBase
name|postingsReader
parameter_list|,
name|int
name|readBufferSize
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
parameter_list|,
name|int
name|termsCacheSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|postingsReader
operator|=
name|postingsReader
expr_stmt|;
name|termsCache
operator|=
operator|new
name|DoubleBarrelLRUCache
argument_list|<
name|FieldAndTerm
argument_list|,
name|TermState
argument_list|>
argument_list|(
name|termsCacheSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|termComp
operator|=
name|termComp
expr_stmt|;
name|in
operator|=
name|dir
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
name|PrefixCodedTermsWriter
operator|.
name|TERMS_EXTENSION
argument_list|)
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|readHeader
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// Have PostingsReader init itself
name|postingsReader
operator|.
name|init
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// Read per-field details
name|seekDir
argument_list|(
name|in
argument_list|,
name|dirOffset
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numFields
init|=
name|in
operator|.
name|readInt
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|field
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
specifier|final
name|long
name|numTerms
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
assert|assert
name|numTerms
operator|>=
literal|0
assert|;
specifier|final
name|long
name|termsStartPointer
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
specifier|final
name|TermsIndexReaderBase
operator|.
name|FieldReader
name|fieldIndexReader
decl_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|fieldIndexReader
operator|=
name|indexReader
operator|.
name|getField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|numTerms
operator|>
literal|0
condition|)
block|{
assert|assert
operator|!
name|fields
operator|.
name|containsKey
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
assert|;
name|fields
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|FieldReader
argument_list|(
name|fieldIndexReader
argument_list|,
name|fieldInfo
argument_list|,
name|numTerms
argument_list|,
name|termsStartPointer
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|this
operator|.
name|indexReader
operator|=
name|indexReader
expr_stmt|;
block|}
DECL|method|readHeader
specifier|protected
name|void
name|readHeader
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|PrefixCodedTermsWriter
operator|.
name|CODEC_NAME
argument_list|,
name|PrefixCodedTermsWriter
operator|.
name|VERSION_START
argument_list|,
name|PrefixCodedTermsWriter
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|dirOffset
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
DECL|method|seekDir
specifier|protected
name|void
name|seekDir
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|long
name|dirOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|input
operator|.
name|seek
argument_list|(
name|dirOffset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|loadTermsIndex
specifier|public
name|void
name|loadTermsIndex
parameter_list|(
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
name|indexReader
operator|.
name|loadTermsIndex
argument_list|(
name|indexDivisor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
try|try
block|{
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// null so if an app hangs on to us (ie, we are not
comment|// GCable, despite being closed) we still free most
comment|// ram
name|indexReader
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|postingsReader
operator|!=
literal|null
condition|)
block|{
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
for|for
control|(
name|FieldReader
name|field
range|:
name|fields
operator|.
name|values
argument_list|()
control|)
block|{
name|field
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|files
specifier|public
specifier|static
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|PrefixCodedTermsWriter
operator|.
name|TERMS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getExtensions
specifier|public
specifier|static
name|void
name|getExtensions
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|extensions
operator|.
name|add
argument_list|(
name|PrefixCodedTermsWriter
operator|.
name|TERMS_EXTENSION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|FieldsEnum
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|TermFieldsEnum
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|// Iterates through all fields
DECL|class|TermFieldsEnum
specifier|private
class|class
name|TermFieldsEnum
extends|extends
name|FieldsEnum
block|{
DECL|field|it
specifier|final
name|Iterator
argument_list|<
name|FieldReader
argument_list|>
name|it
decl_stmt|;
DECL|field|current
name|FieldReader
name|current
decl_stmt|;
DECL|method|TermFieldsEnum
name|TermFieldsEnum
parameter_list|()
block|{
name|it
operator|=
name|fields
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
block|{
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|current
operator|.
name|fieldInfo
operator|.
name|name
return|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|TermsEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
DECL|class|FieldReader
specifier|private
class|class
name|FieldReader
extends|extends
name|Terms
implements|implements
name|Closeable
block|{
DECL|field|numTerms
specifier|final
name|long
name|numTerms
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|termsStartPointer
specifier|final
name|long
name|termsStartPointer
decl_stmt|;
DECL|field|fieldIndexReader
specifier|final
name|TermsIndexReaderBase
operator|.
name|FieldReader
name|fieldIndexReader
decl_stmt|;
DECL|method|FieldReader
name|FieldReader
parameter_list|(
name|TermsIndexReaderBase
operator|.
name|FieldReader
name|fieldIndexReader
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|numTerms
parameter_list|,
name|long
name|termsStartPointer
parameter_list|)
block|{
assert|assert
name|numTerms
operator|>
literal|0
assert|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|numTerms
operator|=
name|numTerms
expr_stmt|;
name|this
operator|.
name|termsStartPointer
operator|=
name|termsStartPointer
expr_stmt|;
name|this
operator|.
name|fieldIndexReader
operator|=
name|fieldIndexReader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|termComp
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentTermsEnum
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUniqueTermCount
specifier|public
name|long
name|getUniqueTermCount
parameter_list|()
block|{
return|return
name|numTerms
return|;
block|}
comment|// Iterates through terms in this field
DECL|class|SegmentTermsEnum
specifier|private
class|class
name|SegmentTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|in
specifier|private
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|bytesReader
specifier|private
specifier|final
name|DeltaBytesReader
name|bytesReader
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|TermState
name|state
decl_stmt|;
DECL|field|seekPending
specifier|private
name|boolean
name|seekPending
decl_stmt|;
DECL|field|indexResult
specifier|private
specifier|final
name|TermsIndexReaderBase
operator|.
name|TermsIndexResult
name|indexResult
init|=
operator|new
name|TermsIndexReaderBase
operator|.
name|TermsIndexResult
argument_list|()
decl_stmt|;
DECL|field|fieldTerm
specifier|private
specifier|final
name|FieldAndTerm
name|fieldTerm
init|=
operator|new
name|FieldAndTerm
argument_list|()
decl_stmt|;
DECL|method|SegmentTermsEnum
name|SegmentTermsEnum
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|=
operator|(
name|IndexInput
operator|)
name|PrefixCodedTermsReader
operator|.
name|this
operator|.
name|in
operator|.
name|clone
argument_list|()
expr_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|termsStartPointer
argument_list|)
expr_stmt|;
name|bytesReader
operator|=
operator|new
name|DeltaBytesReader
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|fieldTerm
operator|.
name|field
operator|=
name|fieldInfo
operator|.
name|name
expr_stmt|;
name|state
operator|=
name|postingsReader
operator|.
name|newTermState
argument_list|()
expr_stmt|;
name|state
operator|.
name|ord
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|termComp
return|;
block|}
annotation|@
name|Override
DECL|method|cacheCurrentTerm
specifier|public
name|void
name|cacheCurrentTerm
parameter_list|()
block|{
name|TermState
name|stateCopy
init|=
operator|(
name|TermState
operator|)
name|state
operator|.
name|clone
argument_list|()
decl_stmt|;
name|stateCopy
operator|.
name|filePointer
operator|=
name|in
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|termsCache
operator|.
name|put
argument_list|(
operator|new
name|FieldAndTerm
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|bytesReader
operator|.
name|term
argument_list|)
argument_list|,
name|stateCopy
argument_list|)
expr_stmt|;
block|}
comment|/** Seeks until the first term that's>= the provided        *  text; returns SeekStatus.FOUND if the exact term        *  is found, SeekStatus.NOT_FOUND if a different term        *  was found, SeekStatus.END if we hit EOF */
annotation|@
name|Override
DECL|method|seek
specifier|public
name|SeekStatus
name|seek
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Check cache
name|fieldTerm
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|TermState
name|cachedState
decl_stmt|;
if|if
condition|(
name|useCache
condition|)
block|{
name|cachedState
operator|=
name|termsCache
operator|.
name|get
argument_list|(
name|fieldTerm
argument_list|)
expr_stmt|;
if|if
condition|(
name|cachedState
operator|!=
literal|null
condition|)
block|{
name|state
operator|.
name|copy
argument_list|(
name|cachedState
argument_list|)
expr_stmt|;
name|seekPending
operator|=
literal|true
expr_stmt|;
name|bytesReader
operator|.
name|term
operator|.
name|copy
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
block|}
else|else
block|{
name|cachedState
operator|=
literal|null
expr_stmt|;
block|}
name|boolean
name|doSeek
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|ord
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// we are positioned
specifier|final
name|int
name|cmp
init|=
name|termComp
operator|.
name|compare
argument_list|(
name|bytesReader
operator|.
name|term
argument_list|,
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
comment|// already at the requested term
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
if|if
condition|(
name|cmp
operator|<
literal|0
operator|&&
name|fieldIndexReader
operator|.
name|nextIndexTerm
argument_list|(
name|state
operator|.
name|ord
argument_list|,
name|indexResult
argument_list|)
operator|&&
name|termComp
operator|.
name|compare
argument_list|(
name|indexResult
operator|.
name|term
argument_list|,
name|term
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// Optimization: requested term is within the
comment|// same index block we are now in; skip seeking
comment|// (but do scanning):
name|doSeek
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|// Used only for assert:
specifier|final
name|long
name|startOrd
decl_stmt|;
if|if
condition|(
name|doSeek
condition|)
block|{
comment|// As index to find biggest index term that's<=
comment|// our text:
name|fieldIndexReader
operator|.
name|getIndexOffset
argument_list|(
name|term
argument_list|,
name|indexResult
argument_list|)
expr_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|indexResult
operator|.
name|offset
argument_list|)
expr_stmt|;
name|seekPending
operator|=
literal|false
expr_stmt|;
comment|// NOTE: the first next() after an index seek is
comment|// wasteful, since it redundantly reads the same
comment|// bytes into the buffer.  We could avoid storing
comment|// those bytes in the primary file, but then when
comment|// scanning over an index term we'd have to
comment|// special case it:
name|bytesReader
operator|.
name|reset
argument_list|(
name|indexResult
operator|.
name|term
argument_list|)
expr_stmt|;
name|state
operator|.
name|ord
operator|=
name|indexResult
operator|.
name|position
operator|-
literal|1
expr_stmt|;
assert|assert
name|state
operator|.
name|ord
operator|>=
operator|-
literal|1
operator|:
literal|"ord="
operator|+
name|state
operator|.
name|ord
operator|+
literal|" pos="
operator|+
name|indexResult
operator|.
name|position
assert|;
name|startOrd
operator|=
name|indexResult
operator|.
name|position
expr_stmt|;
block|}
else|else
block|{
name|startOrd
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|// Now scan:
while|while
condition|(
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|cmp
init|=
name|termComp
operator|.
name|compare
argument_list|(
name|bytesReader
operator|.
name|term
argument_list|,
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|doSeek
operator|&&
name|useCache
condition|)
block|{
comment|// Store in cache
name|FieldAndTerm
name|entryKey
init|=
operator|new
name|FieldAndTerm
argument_list|(
name|fieldTerm
argument_list|)
decl_stmt|;
name|cachedState
operator|=
operator|(
name|TermState
operator|)
name|state
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// this is fp after current term
name|cachedState
operator|.
name|filePointer
operator|=
name|in
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|termsCache
operator|.
name|put
argument_list|(
name|entryKey
argument_list|,
name|cachedState
argument_list|)
expr_stmt|;
block|}
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|NOT_FOUND
return|;
block|}
comment|// The purpose of the terms dict index is to seek
comment|// the enum to the closest index term before the
comment|// term we are looking for.  So, we should never
comment|// cross another index term (besides the first
comment|// one) while we are scanning:
assert|assert
name|state
operator|.
name|ord
operator|==
name|startOrd
operator|||
operator|!
name|fieldIndexReader
operator|.
name|isIndexTerm
argument_list|(
name|state
operator|.
name|ord
argument_list|,
name|state
operator|.
name|docFreq
argument_list|,
literal|true
argument_list|)
operator|:
literal|"state.ord="
operator|+
name|state
operator|.
name|ord
operator|+
literal|" startOrd="
operator|+
name|startOrd
operator|+
literal|" ir.isIndexTerm="
operator|+
name|fieldIndexReader
operator|.
name|isIndexTerm
argument_list|(
name|state
operator|.
name|ord
argument_list|,
name|state
operator|.
name|docFreq
argument_list|,
literal|true
argument_list|)
operator|+
literal|" state.docFreq="
operator|+
name|state
operator|.
name|docFreq
assert|;
block|}
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|SeekStatus
name|seek
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: should we cache term lookup by ord as well...?
if|if
condition|(
name|ord
operator|>=
name|numTerms
condition|)
block|{
name|state
operator|.
name|ord
operator|=
name|numTerms
operator|-
literal|1
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
name|fieldIndexReader
operator|.
name|getIndexOffset
argument_list|(
name|ord
argument_list|,
name|indexResult
argument_list|)
expr_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|indexResult
operator|.
name|offset
argument_list|)
expr_stmt|;
name|seekPending
operator|=
literal|false
expr_stmt|;
comment|// NOTE: the first next() after an index seek is
comment|// wasteful, since it redundantly reads the same
comment|// bytes into the buffer
name|bytesReader
operator|.
name|reset
argument_list|(
name|indexResult
operator|.
name|term
argument_list|)
expr_stmt|;
name|state
operator|.
name|ord
operator|=
name|indexResult
operator|.
name|position
operator|-
literal|1
expr_stmt|;
assert|assert
name|state
operator|.
name|ord
operator|>=
operator|-
literal|1
operator|:
literal|"ord="
operator|+
name|state
operator|.
name|ord
assert|;
comment|// Now, scan:
name|int
name|left
init|=
call|(
name|int
call|)
argument_list|(
name|ord
operator|-
name|state
operator|.
name|ord
argument_list|)
decl_stmt|;
while|while
condition|(
name|left
operator|>
literal|0
condition|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|next
argument_list|()
decl_stmt|;
assert|assert
name|term
operator|!=
literal|null
assert|;
name|left
operator|--
expr_stmt|;
block|}
comment|// always found
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
block|{
return|return
name|bytesReader
operator|.
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
return|return
name|state
operator|.
name|ord
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|seekPending
condition|)
block|{
name|seekPending
operator|=
literal|false
expr_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|state
operator|.
name|filePointer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|.
name|ord
operator|>=
name|numTerms
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
name|bytesReader
operator|.
name|read
argument_list|()
expr_stmt|;
name|state
operator|.
name|docFreq
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// TODO: would be cleaner, but space-wasting, to
comment|// simply record a bit into each index entry as to
comment|// whether it's an index entry or not, rather than
comment|// re-compute that information... or, possibly store
comment|// a "how many terms until next index entry" in each
comment|// index entry, but that'd require some tricky
comment|// lookahead work when writing the index
name|postingsReader
operator|.
name|readTerm
argument_list|(
name|in
argument_list|,
name|fieldInfo
argument_list|,
name|state
argument_list|,
name|fieldIndexReader
operator|.
name|isIndexTerm
argument_list|(
literal|1
operator|+
name|state
operator|.
name|ord
argument_list|,
name|state
operator|.
name|docFreq
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|state
operator|.
name|ord
operator|++
expr_stmt|;
return|return
name|bytesReader
operator|.
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|state
operator|.
name|docFreq
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
name|DocsEnum
name|docsEnum
init|=
name|postingsReader
operator|.
name|docs
argument_list|(
name|fieldInfo
argument_list|,
name|state
argument_list|,
name|skipDocs
argument_list|,
name|reuse
argument_list|)
decl_stmt|;
assert|assert
name|docsEnum
operator|!=
literal|null
assert|;
return|return
name|docsEnum
return|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|postingsReader
operator|.
name|docsAndPositions
argument_list|(
name|fieldInfo
argument_list|,
name|state
argument_list|,
name|skipDocs
argument_list|,
name|reuse
argument_list|)
return|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
