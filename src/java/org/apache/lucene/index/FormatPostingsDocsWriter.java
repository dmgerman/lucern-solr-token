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
begin_comment
comment|/** Consumes doc& freq, writing them using the current  *  index file format */
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
name|util
operator|.
name|UnicodeUtil
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
begin_class
DECL|class|FormatPostingsDocsWriter
specifier|final
class|class
name|FormatPostingsDocsWriter
extends|extends
name|FormatPostingsDocsConsumer
block|{
DECL|field|out
specifier|final
name|IndexOutput
name|out
decl_stmt|;
DECL|field|parent
specifier|final
name|FormatPostingsTermsWriter
name|parent
decl_stmt|;
DECL|field|posWriter
specifier|final
name|FormatPostingsPositionsWriter
name|posWriter
decl_stmt|;
DECL|field|skipListWriter
specifier|final
name|DefaultSkipListWriter
name|skipListWriter
decl_stmt|;
DECL|field|skipInterval
specifier|final
name|int
name|skipInterval
decl_stmt|;
DECL|field|totalNumDocs
specifier|final
name|int
name|totalNumDocs
decl_stmt|;
DECL|field|omitTF
name|boolean
name|omitTF
decl_stmt|;
DECL|field|storePayloads
name|boolean
name|storePayloads
decl_stmt|;
DECL|field|freqStart
name|long
name|freqStart
decl_stmt|;
DECL|field|fieldInfo
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|method|FormatPostingsDocsWriter
name|FormatPostingsDocsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|FormatPostingsTermsWriter
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|parent
operator|.
name|parent
operator|.
name|segment
argument_list|,
name|IndexFileNames
operator|.
name|FREQ_EXTENSION
argument_list|)
decl_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|out
operator|=
name|parent
operator|.
name|parent
operator|.
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|totalNumDocs
operator|=
name|parent
operator|.
name|parent
operator|.
name|totalNumDocs
expr_stmt|;
comment|// TODO: abstraction violation
name|skipInterval
operator|=
name|parent
operator|.
name|parent
operator|.
name|termsOut
operator|.
name|skipInterval
expr_stmt|;
name|skipListWriter
operator|=
name|parent
operator|.
name|parent
operator|.
name|skipListWriter
expr_stmt|;
name|skipListWriter
operator|.
name|setFreqOutput
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|posWriter
operator|=
operator|new
name|FormatPostingsPositionsWriter
argument_list|(
name|state
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|setField
name|void
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|omitTF
operator|=
name|fieldInfo
operator|.
name|omitTf
expr_stmt|;
name|storePayloads
operator|=
name|fieldInfo
operator|.
name|storePayloads
expr_stmt|;
name|posWriter
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
DECL|field|lastDocID
name|int
name|lastDocID
decl_stmt|;
DECL|field|df
name|int
name|df
decl_stmt|;
comment|/** Adds a new doc in this term.  If this returns null    *  then we just skip consuming positions/payloads. */
DECL|method|addDoc
name|FormatPostingsPositionsConsumer
name|addDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|termDocFreq
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|delta
init|=
name|docID
operator|-
name|lastDocID
decl_stmt|;
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
operator|(
name|df
operator|>
literal|0
operator|&&
name|delta
operator|<=
literal|0
operator|)
condition|)
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"docs out of order ("
operator|+
name|docID
operator|+
literal|"<= "
operator|+
name|lastDocID
operator|+
literal|" )"
argument_list|)
throw|;
if|if
condition|(
operator|(
operator|++
name|df
operator|%
name|skipInterval
operator|)
operator|==
literal|0
condition|)
block|{
comment|// TODO: abstraction violation
name|skipListWriter
operator|.
name|setSkipData
argument_list|(
name|lastDocID
argument_list|,
name|storePayloads
argument_list|,
name|posWriter
operator|.
name|lastPayloadLength
argument_list|)
expr_stmt|;
name|skipListWriter
operator|.
name|bufferSkip
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
assert|assert
name|docID
operator|<
name|totalNumDocs
operator|:
literal|"docID="
operator|+
name|docID
operator|+
literal|" totalNumDocs="
operator|+
name|totalNumDocs
assert|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
if|if
condition|(
name|omitTF
condition|)
name|out
operator|.
name|writeVInt
argument_list|(
name|delta
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|1
operator|==
name|termDocFreq
condition|)
name|out
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|termDocFreq
argument_list|)
expr_stmt|;
block|}
return|return
name|posWriter
return|;
block|}
DECL|field|termInfo
specifier|private
specifier|final
name|TermInfo
name|termInfo
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
comment|// minimize consing
DECL|field|utf8
specifier|final
name|UnicodeUtil
operator|.
name|UTF8Result
name|utf8
init|=
operator|new
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|()
decl_stmt|;
comment|/** Called when we are done adding docs to this term */
DECL|method|finish
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|skipPointer
init|=
name|skipListWriter
operator|.
name|writeSkip
argument_list|(
name|out
argument_list|)
decl_stmt|;
comment|// TODO: this is abstraction violation -- we should not
comment|// peek up into parents terms encoding format
name|termInfo
operator|.
name|set
argument_list|(
name|df
argument_list|,
name|parent
operator|.
name|freqStart
argument_list|,
name|parent
operator|.
name|proxStart
argument_list|,
call|(
name|int
call|)
argument_list|(
name|skipPointer
operator|-
name|parent
operator|.
name|freqStart
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: we could do this incrementally
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|parent
operator|.
name|currentTerm
argument_list|,
name|parent
operator|.
name|currentTermStart
argument_list|,
name|utf8
argument_list|)
expr_stmt|;
if|if
condition|(
name|df
operator|>
literal|0
condition|)
block|{
name|parent
operator|.
name|termsOut
operator|.
name|add
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|,
name|utf8
operator|.
name|result
argument_list|,
name|utf8
operator|.
name|length
argument_list|,
name|termInfo
argument_list|)
expr_stmt|;
block|}
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|df
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|posWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
