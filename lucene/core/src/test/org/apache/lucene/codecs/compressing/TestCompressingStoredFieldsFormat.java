begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
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
name|FieldType
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
name|IntField
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
name|BaseStoredFieldsFormatTestCase
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
name|IndexWriterConfig
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
name|store
operator|.
name|Directory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Repeat
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
import|;
end_import
begin_class
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
literal|5
argument_list|)
comment|// give it a chance to test various compression modes with different chunk sizes
DECL|class|TestCompressingStoredFieldsFormat
specifier|public
class|class
name|TestCompressingStoredFieldsFormat
extends|extends
name|BaseStoredFieldsFormatTestCase
block|{
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|CompressingCodec
operator|.
name|randomInstance
argument_list|(
name|random
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testDeletePartiallyWrittenFilesIfAbort
specifier|public
name|void
name|testDeletePartiallyWrittenFilesIfAbort
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwConf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwConf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|iwConf
operator|.
name|setCodec
argument_list|(
name|CompressingCodec
operator|.
name|randomInstance
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// disable CFS because this test checks file names
name|iwConf
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|iwConf
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwConf
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|validDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|validDoc
operator|.
name|add
argument_list|(
operator|new
name|IntField
argument_list|(
literal|"id"
argument_list|,
literal|0
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|validDoc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// make sure that #writeField will fail to trigger an abort
specifier|final
name|Document
name|invalidDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|fieldType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|fieldType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|invalidDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"invalid"
argument_list|,
name|fieldType
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|invalidDoc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|fileName
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|fileName
operator|.
name|endsWith
argument_list|(
literal|".fdt"
argument_list|)
operator|||
name|fileName
operator|.
name|endsWith
argument_list|(
literal|".fdx"
argument_list|)
condition|)
block|{
name|counter
operator|++
expr_stmt|;
block|}
block|}
comment|// Only one .fdt and one .fdx files must have been found
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|iw
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
block|}
block|}
end_class
end_unit
