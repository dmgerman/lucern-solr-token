begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene3x
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene3x
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
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|SegmentInfosReader
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
name|IndexFormatTooNewException
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
name|IndexFormatTooOldException
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
name|SegmentInfos
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
name|ChecksumIndexInput
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
name|CompoundFileDirectory
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
name|IOContext
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
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * Lucene 3x implementation of {@link SegmentInfosReader}.  * @lucene.experimental  * @deprecated  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|Lucene3xSegmentInfosReader
specifier|public
class|class
name|Lucene3xSegmentInfosReader
extends|extends
name|SegmentInfosReader
block|{
DECL|method|readLegacyInfos
specifier|public
specifier|static
name|void
name|readLegacyInfos
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|IndexInput
name|input
parameter_list|,
name|int
name|format
parameter_list|)
throws|throws
name|IOException
block|{
name|infos
operator|.
name|version
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
comment|// read version
name|infos
operator|.
name|counter
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// read counter
name|Lucene3xSegmentInfosReader
name|reader
init|=
operator|new
name|Lucene3xSegmentInfosReader
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|input
operator|.
name|readInt
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
comment|// read segmentInfos
name|SegmentInfo
name|si
init|=
name|reader
operator|.
name|readSegmentInfo
argument_list|(
name|directory
argument_list|,
name|format
argument_list|,
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|si
operator|.
name|getVersion
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// Could be a 3.0 - try to open the doc stores - if it fails, it's a
comment|// 2.x segment, and an IndexFormatTooOldException will be thrown,
comment|// which is what we want.
name|Directory
name|dir
init|=
name|directory
decl_stmt|;
if|if
condition|(
name|si
operator|.
name|getDocStoreOffset
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|si
operator|.
name|getDocStoreIsCompoundFile
argument_list|()
condition|)
block|{
name|dir
operator|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|dir
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|Lucene3xCodec
operator|.
name|COMPOUND_FILE_STORE_EXTENSION
argument_list|)
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|si
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
name|dir
operator|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|dir
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Lucene3xStoredFieldsReader
operator|.
name|checkCodeVersion
argument_list|(
name|dir
argument_list|,
name|si
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// If we opened the directory, close it
if|if
condition|(
name|dir
operator|!=
name|directory
condition|)
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Above call succeeded, so it's a 3.0 segment. Upgrade it so the next
comment|// time the segment is read, its version won't be null and we won't
comment|// need to open FieldsReader every time for each such segment.
name|si
operator|.
name|setVersion
argument_list|(
literal|"3.0"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|si
operator|.
name|getVersion
argument_list|()
operator|.
name|equals
argument_list|(
literal|"2.x"
argument_list|)
condition|)
block|{
comment|// If it's a 3x index touched by 3.1+ code, then segments record their
comment|// version, whether they are 2.x ones or not. We detect that and throw
comment|// appropriate exception.
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
literal|"segment "
operator|+
name|si
operator|.
name|name
operator|+
literal|" in resource "
operator|+
name|input
argument_list|,
name|si
operator|.
name|getVersion
argument_list|()
argument_list|)
throw|;
block|}
name|infos
operator|.
name|add
argument_list|(
name|si
argument_list|)
expr_stmt|;
block|}
name|infos
operator|.
name|userData
operator|=
name|input
operator|.
name|readStringStringMap
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|SegmentInfo
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|read
argument_list|(
name|directory
argument_list|,
name|segmentName
argument_list|,
name|Lucene3xSegmentInfosFormat
operator|.
name|FORMAT_4_0
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|SegmentInfo
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|int
name|format
parameter_list|)
throws|throws
name|IOException
block|{
comment|// NOTE: this is NOT how 3.x is really written...
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
literal|""
argument_list|,
name|Lucene3xSegmentInfosFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
comment|// nocommit what IOCtx
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
decl_stmt|;
try|try
block|{
name|SegmentInfo
name|si
init|=
name|readSegmentInfo
argument_list|(
name|directory
argument_list|,
name|format
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|si
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|readSegmentInfo
specifier|private
name|SegmentInfo
name|readSegmentInfo
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|format
parameter_list|,
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
comment|// check that it is a format we can understand
if|if
condition|(
name|format
operator|>
name|Lucene3xSegmentInfosFormat
operator|.
name|FORMAT_MINIMUM
condition|)
block|{
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
name|input
argument_list|,
name|format
argument_list|,
name|Lucene3xSegmentInfosFormat
operator|.
name|FORMAT_MINIMUM
argument_list|,
name|Lucene3xSegmentInfosFormat
operator|.
name|FORMAT_CURRENT
argument_list|)
throw|;
block|}
if|if
condition|(
name|format
operator|<
name|Lucene3xSegmentInfosFormat
operator|.
name|FORMAT_CURRENT
condition|)
block|{
throw|throw
operator|new
name|IndexFormatTooNewException
argument_list|(
name|input
argument_list|,
name|format
argument_list|,
name|Lucene3xSegmentInfosFormat
operator|.
name|FORMAT_MINIMUM
argument_list|,
name|Lucene3xSegmentInfosFormat
operator|.
name|FORMAT_CURRENT
argument_list|)
throw|;
block|}
specifier|final
name|String
name|version
decl_stmt|;
if|if
condition|(
name|format
operator|<=
name|Lucene3xSegmentInfosFormat
operator|.
name|FORMAT_3_1
condition|)
block|{
name|version
operator|=
name|input
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|version
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|String
name|name
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docCount
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
specifier|final
name|long
name|delGen
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docStoreOffset
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
specifier|final
name|String
name|docStoreSegment
decl_stmt|;
specifier|final
name|boolean
name|docStoreIsCompoundFile
decl_stmt|;
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|docStoreSegment
operator|=
name|input
operator|.
name|readString
argument_list|()
expr_stmt|;
name|docStoreIsCompoundFile
operator|=
name|input
operator|.
name|readByte
argument_list|()
operator|==
name|SegmentInfo
operator|.
name|YES
expr_stmt|;
block|}
else|else
block|{
name|docStoreSegment
operator|=
name|name
expr_stmt|;
name|docStoreIsCompoundFile
operator|=
literal|false
expr_stmt|;
block|}
comment|// pre-4.0 indexes write a byte if there is a single norms file
name|byte
name|b
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"version="
operator|+
name|version
operator|+
literal|" name="
operator|+
name|name
operator|+
literal|" docCount="
operator|+
name|docCount
operator|+
literal|" delGen="
operator|+
name|delGen
operator|+
literal|" dso="
operator|+
name|docStoreOffset
operator|+
literal|" dss="
operator|+
name|docStoreSegment
operator|+
literal|" dssCFs="
operator|+
name|docStoreIsCompoundFile
operator|+
literal|" b="
operator|+
name|b
operator|+
literal|" format="
operator|+
name|format
argument_list|)
expr_stmt|;
assert|assert
literal|1
operator|==
name|b
operator|:
literal|"expected 1 but was: "
operator|+
name|b
operator|+
literal|" format: "
operator|+
name|format
assert|;
specifier|final
name|int
name|numNormGen
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|normGen
decl_stmt|;
if|if
condition|(
name|numNormGen
operator|==
name|SegmentInfo
operator|.
name|NO
condition|)
block|{
name|normGen
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|normGen
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numNormGen
condition|;
name|j
operator|++
control|)
block|{
name|normGen
operator|.
name|put
argument_list|(
name|j
argument_list|,
name|input
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|boolean
name|isCompoundFile
init|=
name|input
operator|.
name|readByte
argument_list|()
operator|==
name|SegmentInfo
operator|.
name|YES
decl_stmt|;
specifier|final
name|int
name|delCount
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
assert|assert
name|delCount
operator|<=
name|docCount
assert|;
specifier|final
name|boolean
name|hasProx
init|=
name|input
operator|.
name|readByte
argument_list|()
operator|==
literal|1
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
name|input
operator|.
name|readStringStringMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|format
operator|<=
name|Lucene3xSegmentInfosFormat
operator|.
name|FORMAT_HAS_VECTORS
condition|)
block|{
name|input
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
comment|// nocommit we can use hasProx/hasVectors from the 3.x
comment|// si... if we can pass this to the other components...?
name|SegmentInfo
name|info
init|=
operator|new
name|SegmentInfo
argument_list|(
name|dir
argument_list|,
name|version
argument_list|,
name|name
argument_list|,
name|docCount
argument_list|,
name|docStoreOffset
argument_list|,
name|docStoreSegment
argument_list|,
name|docStoreIsCompoundFile
argument_list|,
name|normGen
argument_list|,
name|isCompoundFile
argument_list|,
name|delCount
argument_list|,
literal|null
argument_list|,
name|diagnostics
argument_list|)
decl_stmt|;
name|info
operator|.
name|setDelGen
argument_list|(
name|delGen
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
block|}
end_class
end_unit
