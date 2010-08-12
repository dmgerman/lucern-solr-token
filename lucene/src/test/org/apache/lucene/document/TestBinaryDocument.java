begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
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
name|MockRAMDirectory
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Tests {@link Document} class.  */
end_comment
begin_class
DECL|class|TestBinaryDocument
specifier|public
class|class
name|TestBinaryDocument
extends|extends
name|LuceneTestCase
block|{
DECL|field|binaryValStored
name|String
name|binaryValStored
init|=
literal|"this text will be stored as a byte array in the index"
decl_stmt|;
DECL|field|binaryValCompressed
name|String
name|binaryValCompressed
init|=
literal|"this text will be also stored and compressed as a byte array in the index"
decl_stmt|;
DECL|method|testBinaryFieldInIndex
specifier|public
name|void
name|testBinaryFieldInIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|Fieldable
name|binaryFldStored
init|=
operator|new
name|Field
argument_list|(
literal|"binaryStored"
argument_list|,
name|binaryValStored
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|Fieldable
name|stringFldStored
init|=
operator|new
name|Field
argument_list|(
literal|"stringStored"
argument_list|,
name|binaryValStored
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
decl_stmt|;
try|try
block|{
comment|// binary fields with store off are not allowed
operator|new
name|Field
argument_list|(
literal|"fail"
argument_list|,
name|binaryValStored
operator|.
name|getBytes
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{     }
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
name|binaryFldStored
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|stringFldStored
argument_list|)
expr_stmt|;
comment|/** test for field count */
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc
operator|.
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|/** add the doc to a ram index */
name|Random
name|random
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|MockRAMDirectory
name|dir
init|=
name|newDirectory
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|/** open a reader and fetch the document */
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Document
name|docFromReader
init|=
name|reader
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|docFromReader
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|/** fetch the binary stored field and compare it's content with the original one */
name|String
name|binaryFldStoredTest
init|=
operator|new
name|String
argument_list|(
name|docFromReader
operator|.
name|getBinaryValue
argument_list|(
literal|"binaryStored"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|binaryFldStoredTest
operator|.
name|equals
argument_list|(
name|binaryValStored
argument_list|)
argument_list|)
expr_stmt|;
comment|/** fetch the string field and compare it's content with the original one */
name|String
name|stringFldStoredTest
init|=
name|docFromReader
operator|.
name|get
argument_list|(
literal|"stringStored"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stringFldStoredTest
operator|.
name|equals
argument_list|(
name|binaryValStored
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|/** delete the document from index */
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
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
DECL|method|testCompressionTools
specifier|public
name|void
name|testCompressionTools
parameter_list|()
throws|throws
name|Exception
block|{
name|Fieldable
name|binaryFldCompressed
init|=
operator|new
name|Field
argument_list|(
literal|"binaryCompressed"
argument_list|,
name|CompressionTools
operator|.
name|compress
argument_list|(
name|binaryValCompressed
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Fieldable
name|stringFldCompressed
init|=
operator|new
name|Field
argument_list|(
literal|"stringCompressed"
argument_list|,
name|CompressionTools
operator|.
name|compressString
argument_list|(
name|binaryValCompressed
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
name|binaryFldCompressed
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|stringFldCompressed
argument_list|)
expr_stmt|;
comment|/** add the doc to a ram index */
name|Random
name|random
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|MockRAMDirectory
name|dir
init|=
name|newDirectory
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|/** open a reader and fetch the document */
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Document
name|docFromReader
init|=
name|reader
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|docFromReader
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|/** fetch the binary compressed field and compare it's content with the original one */
name|String
name|binaryFldCompressedTest
init|=
operator|new
name|String
argument_list|(
name|CompressionTools
operator|.
name|decompress
argument_list|(
name|docFromReader
operator|.
name|getBinaryValue
argument_list|(
literal|"binaryCompressed"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|binaryFldCompressedTest
operator|.
name|equals
argument_list|(
name|binaryValCompressed
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CompressionTools
operator|.
name|decompressString
argument_list|(
name|docFromReader
operator|.
name|getBinaryValue
argument_list|(
literal|"stringCompressed"
argument_list|)
argument_list|)
operator|.
name|equals
argument_list|(
name|binaryValCompressed
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
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
end_class
end_unit
