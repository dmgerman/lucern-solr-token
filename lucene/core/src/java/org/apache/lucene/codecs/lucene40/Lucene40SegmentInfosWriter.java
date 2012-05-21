begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|SegmentInfosWriter
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
name|ChecksumIndexOutput
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
name|FlushInfo
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
name|IndexOutput
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
comment|/**  * Lucene 4.0 implementation of {@link SegmentInfosWriter}.  *   * @see Lucene40SegmentInfosFormat  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Lucene40SegmentInfosWriter
specifier|public
class|class
name|Lucene40SegmentInfosWriter
extends|extends
name|SegmentInfosWriter
block|{
comment|/** Save a single segment's info. */
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|IOContext
name|ioContext
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|si
operator|.
name|getDelCount
argument_list|()
operator|<=
name|si
operator|.
name|docCount
operator|:
literal|"delCount="
operator|+
name|si
operator|.
name|getDelCount
argument_list|()
operator|+
literal|" docCount="
operator|+
name|si
operator|.
name|docCount
operator|+
literal|" segment="
operator|+
name|si
operator|.
name|name
assert|;
specifier|final
name|String
name|fileName
init|=
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
name|Lucene40SegmentInfosFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
assert|assert
name|si
operator|.
name|getFiles
argument_list|()
operator|!=
literal|null
assert|;
name|si
operator|.
name|getFiles
argument_list|()
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
specifier|final
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|ioContext
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// Write the Lucene version that created this segment, since 3.1
name|output
operator|.
name|writeString
argument_list|(
name|si
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|si
operator|.
name|docCount
argument_list|)
expr_stmt|;
assert|assert
name|si
operator|.
name|getDocStoreOffset
argument_list|()
operator|==
operator|-
literal|1
assert|;
assert|assert
name|si
operator|.
name|getNormGen
argument_list|()
operator|==
literal|null
assert|;
name|output
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|si
operator|.
name|getUseCompoundFile
argument_list|()
condition|?
name|SegmentInfo
operator|.
name|YES
else|:
name|SegmentInfo
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeStringStringMap
argument_list|(
name|si
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeStringSet
argument_list|(
name|si
operator|.
name|getFiles
argument_list|()
argument_list|)
expr_stmt|;
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|si
operator|.
name|dir
operator|.
name|deleteFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
