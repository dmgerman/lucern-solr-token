begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
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
name|SegmentInfoWriter
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
name|BytesRefBuilder
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
comment|/**  * writes plaintext segments files  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SimpleTextSegmentInfoWriter
specifier|public
class|class
name|SimpleTextSegmentInfoWriter
extends|extends
name|SegmentInfoWriter
block|{
DECL|field|SI_VERSION
specifier|final
specifier|static
name|BytesRef
name|SI_VERSION
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    version "
argument_list|)
decl_stmt|;
DECL|field|SI_DOCCOUNT
specifier|final
specifier|static
name|BytesRef
name|SI_DOCCOUNT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    number of documents "
argument_list|)
decl_stmt|;
DECL|field|SI_USECOMPOUND
specifier|final
specifier|static
name|BytesRef
name|SI_USECOMPOUND
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    uses compound file "
argument_list|)
decl_stmt|;
DECL|field|SI_NUM_DIAG
specifier|final
specifier|static
name|BytesRef
name|SI_NUM_DIAG
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    diagnostics "
argument_list|)
decl_stmt|;
DECL|field|SI_DIAG_KEY
specifier|final
specifier|static
name|BytesRef
name|SI_DIAG_KEY
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      key "
argument_list|)
decl_stmt|;
DECL|field|SI_DIAG_VALUE
specifier|final
specifier|static
name|BytesRef
name|SI_DIAG_VALUE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      value "
argument_list|)
decl_stmt|;
DECL|field|SI_NUM_FILES
specifier|final
specifier|static
name|BytesRef
name|SI_NUM_FILES
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    files "
argument_list|)
decl_stmt|;
DECL|field|SI_FILE
specifier|final
specifier|static
name|BytesRef
name|SI_FILE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      file "
argument_list|)
decl_stmt|;
DECL|field|SI_ID
specifier|final
specifier|static
name|BytesRef
name|SI_ID
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    id "
argument_list|)
decl_stmt|;
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
name|String
name|segFileName
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
name|SimpleTextSegmentInfoFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
name|si
operator|.
name|addFile
argument_list|(
name|segFileName
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|segFileName
argument_list|,
name|ioContext
argument_list|)
decl_stmt|;
try|try
block|{
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_VERSION
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|si
operator|.
name|getVersion
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DOCCOUNT
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|si
operator|.
name|getDocCount
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_USECOMPOUND
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|si
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
name|si
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
name|int
name|numDiagnostics
init|=
name|diagnostics
operator|==
literal|null
condition|?
literal|0
else|:
name|diagnostics
operator|.
name|size
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_NUM_DIAG
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numDiagnostics
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|numDiagnostics
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagEntry
range|:
name|diagnostics
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DIAG_KEY
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|diagEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DIAG_VALUE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|diagEntry
operator|.
name|getValue
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
name|si
operator|.
name|files
argument_list|()
decl_stmt|;
name|int
name|numFiles
init|=
name|files
operator|==
literal|null
condition|?
literal|0
else|:
name|files
operator|.
name|size
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_NUM_FILES
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numFiles
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|numFiles
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|fileName
range|:
name|files
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_FILE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|fileName
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_ID
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|si
operator|.
name|getId
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeChecksum
argument_list|(
name|output
argument_list|,
name|scratch
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
try|try
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|segFileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{         }
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
