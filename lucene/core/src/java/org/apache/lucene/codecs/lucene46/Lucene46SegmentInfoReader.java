begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene46
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene46
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
name|Map
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|codecs
operator|.
name|SegmentInfoReader
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
name|util
operator|.
name|Version
import|;
end_import
begin_comment
comment|/**  * Lucene 4.6 implementation of {@link SegmentInfoReader}.  *   * @see Lucene46SegmentInfoFormat  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Lucene46SegmentInfoReader
specifier|public
class|class
name|Lucene46SegmentInfoReader
extends|extends
name|SegmentInfoReader
block|{
comment|/** Sole constructor. */
DECL|method|Lucene46SegmentInfoReader
specifier|public
name|Lucene46SegmentInfoReader
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|read
specifier|public
name|SegmentInfo
name|read
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segment
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|Lucene46SegmentInfoFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|ChecksumIndexInput
name|input
init|=
name|dir
operator|.
name|openChecksumInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
init|)
block|{
name|int
name|codecVersion
init|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|Lucene46SegmentInfoFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene46SegmentInfoFormat
operator|.
name|VERSION_START
argument_list|,
name|Lucene46SegmentInfoFormat
operator|.
name|VERSION_CURRENT
argument_list|)
decl_stmt|;
specifier|final
name|Version
name|version
init|=
name|Version
operator|.
name|parse
argument_list|(
name|input
operator|.
name|readString
argument_list|()
argument_list|)
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
if|if
condition|(
name|docCount
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid docCount: "
operator|+
name|docCount
argument_list|,
name|input
argument_list|)
throw|;
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
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
name|input
operator|.
name|readStringSet
argument_list|()
decl_stmt|;
name|String
name|id
decl_stmt|;
if|if
condition|(
name|codecVersion
operator|>=
name|Lucene46SegmentInfoFormat
operator|.
name|VERSION_ID
condition|)
block|{
name|id
operator|=
name|input
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|id
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|codecVersion
operator|>=
name|Lucene46SegmentInfoFormat
operator|.
name|VERSION_CHECKSUM
condition|)
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CodecUtil
operator|.
name|checkEOF
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SegmentInfo
name|si
init|=
operator|new
name|SegmentInfo
argument_list|(
name|dir
argument_list|,
name|version
argument_list|,
name|segment
argument_list|,
name|docCount
argument_list|,
name|isCompoundFile
argument_list|,
literal|null
argument_list|,
name|diagnostics
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|si
operator|.
name|setFiles
argument_list|(
name|files
argument_list|)
expr_stmt|;
return|return
name|si
return|;
block|}
block|}
block|}
end_class
end_unit
