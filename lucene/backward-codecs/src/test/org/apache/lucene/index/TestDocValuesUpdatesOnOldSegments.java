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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|lucene40
operator|.
name|Lucene40RWCodec
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
name|lucene41
operator|.
name|Lucene41RWCodec
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
name|lucene42
operator|.
name|Lucene42RWCodec
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
name|lucene45
operator|.
name|Lucene45RWCodec
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
name|BinaryDocValuesField
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
operator|.
name|Store
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
name|NumericDocValuesField
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
name|StringField
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
begin_comment
comment|/**   * Tests performing docvalues updates against versions of lucene  * that did not support it.  */
end_comment
begin_class
DECL|class|TestDocValuesUpdatesOnOldSegments
specifier|public
class|class
name|TestDocValuesUpdatesOnOldSegments
extends|extends
name|LuceneTestCase
block|{
DECL|method|getValue
specifier|static
name|long
name|getValue
parameter_list|(
name|BinaryDocValues
name|bdv
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
name|BytesRef
name|term
init|=
name|bdv
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|idx
operator|=
name|term
operator|.
name|offset
expr_stmt|;
name|byte
name|b
init|=
name|term
operator|.
name|bytes
index|[
name|idx
operator|++
index|]
decl_stmt|;
name|long
name|value
init|=
name|b
operator|&
literal|0x7FL
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0x80L
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
name|b
operator|=
name|term
operator|.
name|bytes
index|[
name|idx
operator|++
index|]
expr_stmt|;
name|value
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
name|shift
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
comment|// encodes a long into a BytesRef as VLong so that we get varying number of bytes when we update
DECL|method|toBytes
specifier|static
name|BytesRef
name|toBytes
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|// negative longs may take 10 bytes
while|while
condition|(
operator|(
name|value
operator|&
operator|~
literal|0x7FL
operator|)
operator|!=
literal|0L
condition|)
block|{
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|value
operator|&
literal|0x7FL
operator|)
operator||
literal|0x80L
argument_list|)
expr_stmt|;
name|value
operator|>>>=
literal|7
expr_stmt|;
block|}
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
return|return
name|bytes
return|;
block|}
DECL|method|testBinaryUpdates
specifier|public
name|void
name|testBinaryUpdates
parameter_list|()
throws|throws
name|Exception
block|{
name|Codec
index|[]
name|oldCodecs
init|=
operator|new
name|Codec
index|[]
block|{
operator|new
name|Lucene40RWCodec
argument_list|()
block|,
operator|new
name|Lucene41RWCodec
argument_list|()
block|,
operator|new
name|Lucene42RWCodec
argument_list|()
block|,
operator|new
name|Lucene45RWCodec
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|Codec
name|codec
range|:
name|oldCodecs
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// create a segment with an old Codec
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
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
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|toBytes
argument_list|(
literal|5L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateBinaryDocValue
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
argument_list|)
argument_list|,
literal|"f"
argument_list|,
name|toBytes
argument_list|(
literal|4L
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should not have succeeded to update a segment written with an old Codec"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|writer
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testNumericUpdates
specifier|public
name|void
name|testNumericUpdates
parameter_list|()
throws|throws
name|Exception
block|{
name|Codec
index|[]
name|oldCodecs
init|=
operator|new
name|Codec
index|[]
block|{
operator|new
name|Lucene40RWCodec
argument_list|()
block|,
operator|new
name|Lucene41RWCodec
argument_list|()
block|,
operator|new
name|Lucene42RWCodec
argument_list|()
block|,
operator|new
name|Lucene45RWCodec
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|Codec
name|codec
range|:
name|oldCodecs
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// create a segment with an old Codec
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
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
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"f"
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateNumericDocValue
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
argument_list|)
argument_list|,
literal|"f"
argument_list|,
literal|4L
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should not have succeeded to update a segment written with an old Codec"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|writer
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
