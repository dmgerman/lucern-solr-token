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
name|SortedDocValuesField
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
name|SortedNumericDocValuesField
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
name|SortedSetDocValuesField
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
comment|/** Tests helper methods in DocValues */
end_comment
begin_class
DECL|class|TestDocValues
specifier|public
class|class
name|TestDocValues
extends|extends
name|LuceneTestCase
block|{
comment|/**     * If the field doesn't exist, we return empty instances:    * it can easily happen that a segment just doesn't have any docs with the field.    */
DECL|method|testEmptyIndex
specifier|public
name|void
name|testEmptyIndex
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
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|dr
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LeafReader
name|r
init|=
name|getOnlySegmentReader
argument_list|(
name|dr
argument_list|)
decl_stmt|;
comment|// ok
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getBinary
argument_list|(
name|r
argument_list|,
literal|"bogus"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|r
argument_list|,
literal|"bogus"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getSorted
argument_list|(
name|r
argument_list|,
literal|"bogus"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|r
argument_list|,
literal|"bogus"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|r
argument_list|,
literal|"bogus"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|r
argument_list|,
literal|"bogus"
argument_list|)
argument_list|)
expr_stmt|;
name|dr
operator|.
name|close
argument_list|()
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
comment|/**     * field just doesnt have any docvalues at all: exception    */
DECL|method|testMisconfiguredField
specifier|public
name|void
name|testMisconfiguredField
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
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
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
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|DirectoryReader
name|dr
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LeafReader
name|r
init|=
name|getOnlySegmentReader
argument_list|(
name|dr
argument_list|)
decl_stmt|;
comment|// errors
try|try
block|{
name|DocValues
operator|.
name|getBinary
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSorted
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
name|dr
operator|.
name|close
argument_list|()
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
comment|/**     * field with numeric docvalues    */
DECL|method|testNumericField
specifier|public
name|void
name|testNumericField
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
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
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
name|NumericDocValuesField
argument_list|(
literal|"foo"
argument_list|,
literal|3
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
name|DirectoryReader
name|dr
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LeafReader
name|r
init|=
name|getOnlySegmentReader
argument_list|(
name|dr
argument_list|)
decl_stmt|;
comment|// ok
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
comment|// errors
try|try
block|{
name|DocValues
operator|.
name|getBinary
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSorted
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
name|dr
operator|.
name|close
argument_list|()
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
comment|/**     * field with binary docvalues    */
DECL|method|testBinaryField
specifier|public
name|void
name|testBinaryField
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
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
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
name|BinaryDocValuesField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
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
name|DirectoryReader
name|dr
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LeafReader
name|r
init|=
name|getOnlySegmentReader
argument_list|(
name|dr
argument_list|)
decl_stmt|;
comment|// ok
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getBinary
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
comment|// errors
try|try
block|{
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSorted
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
name|dr
operator|.
name|close
argument_list|()
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
comment|/**     * field with sorted docvalues    */
DECL|method|testSortedField
specifier|public
name|void
name|testSortedField
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
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
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
name|SortedDocValuesField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
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
name|DirectoryReader
name|dr
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LeafReader
name|r
init|=
name|getOnlySegmentReader
argument_list|(
name|dr
argument_list|)
decl_stmt|;
comment|// ok
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getBinary
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getSorted
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
comment|// errors
try|try
block|{
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
name|dr
operator|.
name|close
argument_list|()
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
comment|/**     * field with sortedset docvalues    */
DECL|method|testSortedSetField
specifier|public
name|void
name|testSortedSetField
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
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
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
name|SortedSetDocValuesField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
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
name|DirectoryReader
name|dr
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LeafReader
name|r
init|=
name|getOnlySegmentReader
argument_list|(
name|dr
argument_list|)
decl_stmt|;
comment|// ok
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
comment|// errors
try|try
block|{
name|DocValues
operator|.
name|getBinary
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSorted
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
name|dr
operator|.
name|close
argument_list|()
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
comment|/**     * field with sortednumeric docvalues    */
DECL|method|testSortedNumericField
specifier|public
name|void
name|testSortedNumericField
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
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
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
name|SortedNumericDocValuesField
argument_list|(
literal|"foo"
argument_list|,
literal|3
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
name|DirectoryReader
name|dr
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LeafReader
name|r
init|=
name|getOnlySegmentReader
argument_list|(
name|dr
argument_list|)
decl_stmt|;
comment|// ok
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
comment|// errors
try|try
block|{
name|DocValues
operator|.
name|getBinary
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSorted
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{}
name|dr
operator|.
name|close
argument_list|()
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
end_class
end_unit