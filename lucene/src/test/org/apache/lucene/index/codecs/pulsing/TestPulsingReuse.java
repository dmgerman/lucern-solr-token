begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.pulsing
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
name|pulsing
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
name|IdentityHashMap
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
name|analysis
operator|.
name|MockAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|TextField
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
name|CheckIndex
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
name|index
operator|.
name|PerDocWriteState
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
name|RandomIndexWriter
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
name|index
operator|.
name|codecs
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
name|index
operator|.
name|codecs
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
name|index
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
name|index
operator|.
name|codecs
operator|.
name|CodecProvider
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
name|DefaultDocValuesConsumer
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
name|DefaultDocValuesProducer
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
name|index
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
name|index
operator|.
name|codecs
operator|.
name|PerDocConsumer
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
name|PerDocValues
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
name|index
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
name|index
operator|.
name|codecs
operator|.
name|standard
operator|.
name|StandardCodec
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
name|StandardPostingsWriter
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
name|MockDirectoryWrapper
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
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * Tests that pulsing codec reuses its enums and wrapped enums  */
end_comment
begin_class
DECL|class|TestPulsingReuse
specifier|public
class|class
name|TestPulsingReuse
extends|extends
name|LuceneTestCase
block|{
comment|// TODO: this is a basic test. this thing is complicated, add more
DECL|method|testSophisticatedReuse
specifier|public
name|void
name|testSophisticatedReuse
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we always run this test with pulsing codec.
name|CodecProvider
name|cp
init|=
name|_TestUtil
operator|.
name|alwaysCodec
argument_list|(
operator|new
name|PulsingCodec
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setCodecProvider
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
literal|"a b b c c c d e f g g h i i j j k"
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|segment
init|=
name|ir
operator|.
name|getSequentialSubReaders
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|DocsEnum
name|reuse
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
name|allEnums
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|TermsEnum
name|te
init|=
name|segment
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|reuse
operator|=
name|te
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|reuse
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|put
argument_list|(
name|reuse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allEnums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|clear
argument_list|()
expr_stmt|;
name|DocsAndPositionsEnum
name|posReuse
init|=
literal|null
decl_stmt|;
name|te
operator|=
name|segment
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|posReuse
operator|=
name|te
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|posReuse
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|put
argument_list|(
name|posReuse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allEnums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** tests reuse with Pulsing1(Pulsing2(Standard)) */
DECL|method|testNestedPulsing
specifier|public
name|void
name|testNestedPulsing
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we always run this test with pulsing codec.
name|CodecProvider
name|cp
init|=
name|_TestUtil
operator|.
name|alwaysCodec
argument_list|(
operator|new
name|NestedPulsing
argument_list|()
argument_list|)
decl_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// will do this ourselves, custom codec
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setCodecProvider
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
literal|"a b b c c c d e f g g g h i i j j k l l m m m"
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
comment|// note: the reuse is imperfect, here we would have 4 enums (lost reuse when we get an enum for 'm')
comment|// this is because we only track the 'last' enum we reused (not all).
comment|// but this seems 'good enough' for now.
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|segment
init|=
name|ir
operator|.
name|getSequentialSubReaders
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|DocsEnum
name|reuse
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
name|allEnums
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|TermsEnum
name|te
init|=
name|segment
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|reuse
operator|=
name|te
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|reuse
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|put
argument_list|(
name|reuse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|allEnums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|clear
argument_list|()
expr_stmt|;
name|DocsAndPositionsEnum
name|posReuse
init|=
literal|null
decl_stmt|;
name|te
operator|=
name|segment
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|posReuse
operator|=
name|te
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|posReuse
argument_list|)
expr_stmt|;
name|allEnums
operator|.
name|put
argument_list|(
name|posReuse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|allEnums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|CheckIndex
name|ci
init|=
operator|new
name|CheckIndex
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|ci
operator|.
name|checkIndex
argument_list|(
literal|null
argument_list|,
name|cp
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|NestedPulsing
specifier|static
class|class
name|NestedPulsing
extends|extends
name|Codec
block|{
DECL|method|NestedPulsing
specifier|public
name|NestedPulsing
parameter_list|()
block|{
name|super
argument_list|(
literal|"NestedPulsing"
argument_list|)
expr_stmt|;
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
name|PostingsWriterBase
name|docsWriter
init|=
operator|new
name|StandardPostingsWriter
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|PostingsWriterBase
name|pulsingWriterInner
init|=
operator|new
name|PulsingPostingsWriter
argument_list|(
literal|2
argument_list|,
name|docsWriter
argument_list|)
decl_stmt|;
name|PostingsWriterBase
name|pulsingWriter
init|=
operator|new
name|PulsingPostingsWriter
argument_list|(
literal|1
argument_list|,
name|pulsingWriterInner
argument_list|)
decl_stmt|;
comment|// Terms dict
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FieldsConsumer
name|ret
init|=
operator|new
name|BlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|pulsingWriter
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MIN_BLOCK_SIZE
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MAX_BLOCK_SIZE
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
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
name|pulsingWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
name|PostingsReaderBase
name|docsReader
init|=
operator|new
name|StandardPostingsReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|codecId
argument_list|)
decl_stmt|;
name|PostingsReaderBase
name|pulsingReaderInner
init|=
operator|new
name|PulsingPostingsReader
argument_list|(
name|docsReader
argument_list|)
decl_stmt|;
name|PostingsReaderBase
name|pulsingReader
init|=
operator|new
name|PulsingPostingsReader
argument_list|(
name|pulsingReaderInner
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FieldsProducer
name|ret
init|=
operator|new
name|BlockTreeTermsReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|pulsingReader
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|codecId
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
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
name|pulsingReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|docsConsumer
specifier|public
name|PerDocConsumer
name|docsConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DefaultDocValuesConsumer
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsProducer
specifier|public
name|PerDocValues
name|docsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DefaultDocValuesProducer
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|int
name|id
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|StandardPostingsReader
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|id
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|BlockTreeTermsReader
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|id
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|DefaultDocValuesConsumer
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|id
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExtensions
specifier|public
name|void
name|getExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|StandardCodec
operator|.
name|getStandardExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
name|DefaultDocValuesConsumer
operator|.
name|getExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
