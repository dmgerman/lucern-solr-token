begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.mockrandom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|mockrandom
package|;
end_package
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
name|Random
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
name|FieldsConsumer
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
name|FieldsProducer
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
name|PostingsFormat
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
name|PostingsReaderBase
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
name|PostingsWriterBase
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
name|TermStats
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
name|blockterms
operator|.
name|BlockTermsReader
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
name|blockterms
operator|.
name|BlockTermsWriter
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
name|blockterms
operator|.
name|FixedGapTermsIndexReader
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
name|blockterms
operator|.
name|FixedGapTermsIndexWriter
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
name|blockterms
operator|.
name|TermsIndexReaderBase
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
name|blockterms
operator|.
name|TermsIndexWriterBase
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
name|blockterms
operator|.
name|VariableGapTermsIndexReader
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
name|blockterms
operator|.
name|VariableGapTermsIndexWriter
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
name|blocktree
operator|.
name|BlockTreeTermsReader
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
name|blocktree
operator|.
name|BlockTreeTermsWriter
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
name|blocktreeords
operator|.
name|OrdsBlockTreeTermsReader
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
name|blocktreeords
operator|.
name|OrdsBlockTreeTermsWriter
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
name|lucene50
operator|.
name|Lucene50PostingsReader
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
name|lucene50
operator|.
name|Lucene50PostingsWriter
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
name|memory
operator|.
name|FSTOrdTermsReader
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
name|memory
operator|.
name|FSTOrdTermsWriter
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
name|memory
operator|.
name|FSTTermsReader
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
name|memory
operator|.
name|FSTTermsWriter
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
name|SegmentReadState
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
name|SegmentWriteState
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
name|LuceneTestCase
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * Randomly combines terms index impl w/ postings impls.  */
end_comment
begin_class
DECL|class|MockRandomPostingsFormat
specifier|public
specifier|final
class|class
name|MockRandomPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|seedRandom
specifier|private
specifier|final
name|Random
name|seedRandom
decl_stmt|;
DECL|field|SEED_EXT
specifier|private
specifier|final
name|String
name|SEED_EXT
init|=
literal|"sd"
decl_stmt|;
DECL|method|MockRandomPostingsFormat
specifier|public
name|MockRandomPostingsFormat
parameter_list|()
block|{
comment|// This ctor should *only* be used at read-time: get NPE if you use it!
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|MockRandomPostingsFormat
specifier|public
name|MockRandomPostingsFormat
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
literal|"MockRandom"
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|seedRandom
operator|=
operator|new
name|Random
argument_list|(
literal|0L
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|int
name|next
parameter_list|(
name|int
name|arg0
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Please use MockRandomPostingsFormat(Random)"
argument_list|)
throw|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|seedRandom
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|minSkipInterval
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
operator|>
literal|1000000
condition|)
block|{
comment|// Test2BPostings can OOME otherwise:
name|minSkipInterval
operator|=
literal|3
expr_stmt|;
block|}
else|else
block|{
name|minSkipInterval
operator|=
literal|2
expr_stmt|;
block|}
comment|// we pull this before the seed intentionally: because it's not consumed at runtime
comment|// (the skipInterval is written into postings header).
comment|// NOTE: Currently not passed to postings writer.
comment|//       before, it was being passed in wrongly as acceptableOverhead!
name|int
name|skipInterval
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|seedRandom
argument_list|,
name|minSkipInterval
argument_list|,
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: skipInterval="
operator|+
name|skipInterval
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|seed
init|=
name|seedRandom
operator|.
name|nextLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: writing to seg="
operator|+
name|state
operator|.
name|segmentInfo
operator|.
name|name
operator|+
literal|" formatID="
operator|+
name|state
operator|.
name|segmentSuffix
operator|+
literal|" seed="
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|seedFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|SEED_EXT
argument_list|)
decl_stmt|;
specifier|final
name|IndexOutput
name|out
init|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|seedFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
comment|// consume a random for buffersize
name|PostingsWriterBase
name|postingsWriter
init|=
operator|new
name|Lucene50PostingsWriter
argument_list|(
name|state
argument_list|)
decl_stmt|;
specifier|final
name|FieldsConsumer
name|fields
decl_stmt|;
specifier|final
name|int
name|t1
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
name|t1
operator|==
literal|0
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|FSTTermsWriter
argument_list|(
name|state
argument_list|,
name|postingsWriter
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
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|t1
operator|==
literal|1
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|FSTOrdTermsWriter
argument_list|(
name|state
argument_list|,
name|postingsWriter
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
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|t1
operator|==
literal|2
condition|)
block|{
comment|// Use BlockTree terms dict
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: writing BlockTree terms dict"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: would be nice to allow 1 but this is very
comment|// slow to write
specifier|final
name|int
name|minTermsInBlock
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxTermsInBlock
init|=
name|Math
operator|.
name|max
argument_list|(
literal|2
argument_list|,
operator|(
name|minTermsInBlock
operator|-
literal|1
operator|)
operator|*
literal|2
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|BlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|postingsWriter
argument_list|,
name|minTermsInBlock
argument_list|,
name|maxTermsInBlock
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
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|t1
operator|==
literal|3
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: writing Block terms dict"
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
specifier|final
name|TermsIndexWriterBase
name|indexWriter
decl_stmt|;
try|try
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|int
name|termIndexInterval
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: fixed-gap terms index (tii="
operator|+
name|termIndexInterval
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|=
operator|new
name|FixedGapTermsIndexWriter
argument_list|(
name|state
argument_list|,
name|termIndexInterval
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|VariableGapTermsIndexWriter
operator|.
name|IndexTermSelector
name|selector
decl_stmt|;
specifier|final
name|int
name|n2
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|n2
operator|==
literal|0
condition|)
block|{
specifier|final
name|int
name|tii
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|selector
operator|=
operator|new
name|VariableGapTermsIndexWriter
operator|.
name|EveryNTermSelector
argument_list|(
name|tii
argument_list|)
expr_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: variable-gap terms index (tii="
operator|+
name|tii
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|n2
operator|==
literal|1
condition|)
block|{
specifier|final
name|int
name|docFreqThresh
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|int
name|tii
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|selector
operator|=
operator|new
name|VariableGapTermsIndexWriter
operator|.
name|EveryNOrDocFreqTermSelector
argument_list|(
name|docFreqThresh
argument_list|,
name|tii
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|seed2
init|=
name|random
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|gap
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|40
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: random-gap terms index (max gap="
operator|+
name|gap
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|selector
operator|=
operator|new
name|VariableGapTermsIndexWriter
operator|.
name|IndexTermSelector
argument_list|()
block|{
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed2
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|isIndexTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermStats
name|stats
parameter_list|)
block|{
return|return
name|rand
operator|.
name|nextInt
argument_list|(
name|gap
argument_list|)
operator|==
name|gap
operator|/
literal|2
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|newField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{                 }
block|}
expr_stmt|;
block|}
name|indexWriter
operator|=
operator|new
name|VariableGapTermsIndexWriter
argument_list|(
name|state
argument_list|,
name|selector
argument_list|)
expr_stmt|;
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
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|BlockTermsWriter
argument_list|(
name|indexWriter
argument_list|,
name|state
argument_list|,
name|postingsWriter
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
try|try
block|{
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|t1
operator|==
literal|4
condition|)
block|{
comment|// Use OrdsBlockTree terms dict
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: writing OrdsBlockTree"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: would be nice to allow 1 but this is very
comment|// slow to write
specifier|final
name|int
name|minTermsInBlock
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxTermsInBlock
init|=
name|Math
operator|.
name|max
argument_list|(
literal|2
argument_list|,
operator|(
name|minTermsInBlock
operator|-
literal|1
operator|)
operator|*
literal|2
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|OrdsBlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|postingsWriter
argument_list|,
name|minTermsInBlock
argument_list|,
name|maxTermsInBlock
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
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// BUG!
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|seedFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|SEED_EXT
argument_list|)
decl_stmt|;
specifier|final
name|IndexInput
name|in
init|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|seedFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: reading from seg="
operator|+
name|state
operator|.
name|segmentInfo
operator|.
name|name
operator|+
literal|" formatID="
operator|+
name|state
operator|.
name|segmentSuffix
operator|+
literal|" seed="
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|int
name|readBufferSize
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|4096
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: readBufferSize="
operator|+
name|readBufferSize
argument_list|)
expr_stmt|;
block|}
name|PostingsReaderBase
name|postingsReader
init|=
operator|new
name|Lucene50PostingsReader
argument_list|(
name|state
argument_list|)
decl_stmt|;
specifier|final
name|FieldsProducer
name|fields
decl_stmt|;
specifier|final
name|int
name|t1
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
name|t1
operator|==
literal|0
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|FSTTermsReader
argument_list|(
name|state
argument_list|,
name|postingsReader
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
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|t1
operator|==
literal|1
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|FSTOrdTermsReader
argument_list|(
name|state
argument_list|,
name|postingsReader
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
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|t1
operator|==
literal|2
condition|)
block|{
comment|// Use BlockTree terms dict
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: reading BlockTree terms dict"
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|BlockTreeTermsReader
argument_list|(
name|postingsReader
argument_list|,
name|state
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
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|t1
operator|==
literal|3
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: reading Block terms dict"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|TermsIndexReaderBase
name|indexReader
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|boolean
name|doFixedGap
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
comment|// randomness diverges from writer, here:
if|if
condition|(
name|doFixedGap
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: fixed-gap terms index"
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|=
operator|new
name|FixedGapTermsIndexReader
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|n2
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|n2
operator|==
literal|1
condition|)
block|{
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n2
operator|==
literal|2
condition|)
block|{
name|random
operator|.
name|nextLong
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: variable-gap terms index"
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|=
operator|new
name|VariableGapTermsIndexReader
argument_list|(
name|state
argument_list|)
expr_stmt|;
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
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|BlockTermsReader
argument_list|(
name|indexReader
argument_list|,
name|postingsReader
argument_list|,
name|state
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
try|try
block|{
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|t1
operator|==
literal|4
condition|)
block|{
comment|// Use OrdsBlockTree terms dict
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockRandomCodec: reading OrdsBlockTree terms dict"
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fields
operator|=
operator|new
name|OrdsBlockTreeTermsReader
argument_list|(
name|postingsReader
argument_list|,
name|state
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
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// BUG!
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
return|return
name|fields
return|;
block|}
block|}
end_class
end_unit
