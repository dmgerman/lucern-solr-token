begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.idversion
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|idversion
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
name|CannedTokenStream
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
name|analysis
operator|.
name|Token
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
name|index
operator|.
name|BasePostingsFormatTestCase
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
name|MultiFields
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
comment|/**  * Basic tests for IDVersionPostingsFormat  */
end_comment
begin_class
DECL|class|TestIDVersionPostingsFormat
specifier|public
class|class
name|TestIDVersionPostingsFormat
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
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
name|iwc
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|IDVersionPostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
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
name|makeIDField
argument_list|(
literal|"id0"
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IDVersionSegmentTermsEnum
name|termsEnum
init|=
operator|(
name|IDVersionSegmentTermsEnum
operator|)
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"id"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"id0"
argument_list|)
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"id0"
argument_list|)
argument_list|,
literal|101
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
comment|// nocommit need testRandom
DECL|method|makeIDField
specifier|private
specifier|static
name|Field
name|makeIDField
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|)
block|{
name|Field
name|field
init|=
name|newTextField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|Token
name|token
init|=
operator|new
name|Token
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|payload
init|=
operator|new
name|BytesRef
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|IDVersionPostingsFormat
operator|.
name|longToBytes
argument_list|(
literal|100
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|token
operator|.
name|setPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|field
operator|.
name|setTokenStream
argument_list|(
operator|new
name|CannedTokenStream
argument_list|(
name|token
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|field
return|;
block|}
block|}
end_class
end_unit
