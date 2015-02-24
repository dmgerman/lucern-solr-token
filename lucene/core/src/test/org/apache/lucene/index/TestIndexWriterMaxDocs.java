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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Sort
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
name|search
operator|.
name|SortField
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
name|search
operator|.
name|TermQuery
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
name|search
operator|.
name|TopDocs
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
name|LuceneTestCase
operator|.
name|SuppressCodecs
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
name|TimeUnits
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
name|TimeoutSuite
import|;
end_import
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"SimpleText"
block|,
literal|"Memory"
block|,
literal|"Direct"
block|}
argument_list|)
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
literal|8
operator|*
name|TimeUnits
operator|.
name|HOUR
argument_list|)
DECL|class|TestIndexWriterMaxDocs
specifier|public
class|class
name|TestIndexWriterMaxDocs
extends|extends
name|LuceneTestCase
block|{
comment|// The two hour time was achieved on a Linux 3.13 system with these specs:
comment|// 3-core AMD at 2.5Ghz, 12 GB RAM, 5GB test heap, 2 test JVMs, 2TB SATA.
annotation|@
name|Monster
argument_list|(
literal|"takes over two hours"
argument_list|)
DECL|method|testExactlyAtTrueLimit
specifier|public
name|void
name|testExactlyAtTrueLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"2BDocs3"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
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
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|"text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|IndexWriter
operator|.
name|MAX_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|/*       if (i%1000000 == 0) {         System.out.println((i/1000000) + " M docs...");       }       */
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// First unoptimized, then optimized:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|DirectoryReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|IndexWriter
operator|.
name|MAX_DOCS
argument_list|,
name|ir
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriter
operator|.
name|MAX_DOCS
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|IndexWriter
operator|.
name|MAX_DOCS
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// Sort by docID reversed:
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOC
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriter
operator|.
name|MAX_DOCS
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|hits
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriter
operator|.
name|MAX_DOCS
operator|-
literal|1
argument_list|,
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
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
DECL|method|testAddDocument
specifier|public
name|void
name|testAddDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|setIndexWriterMaxDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// 11th document should fail:
try|try
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
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
finally|finally
block|{
name|restoreIndexWriterMaxDocs
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testAddDocuments
specifier|public
name|void
name|testAddDocuments
parameter_list|()
throws|throws
name|Exception
block|{
name|setIndexWriterMaxDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// 11th document should fail:
try|try
block|{
name|w
operator|.
name|addDocuments
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
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
finally|finally
block|{
name|restoreIndexWriterMaxDocs
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testUpdateDocument
specifier|public
name|void
name|testUpdateDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|setIndexWriterMaxDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// 11th document should fail:
try|try
block|{
name|w
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
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
finally|finally
block|{
name|restoreIndexWriterMaxDocs
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testUpdateDocuments
specifier|public
name|void
name|testUpdateDocuments
parameter_list|()
throws|throws
name|Exception
block|{
name|setIndexWriterMaxDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// 11th document should fail:
try|try
block|{
name|w
operator|.
name|updateDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
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
finally|finally
block|{
name|restoreIndexWriterMaxDocs
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testReclaimedDeletes
specifier|public
name|void
name|testReclaimedDeletes
parameter_list|()
throws|throws
name|Exception
block|{
name|setIndexWriterMaxDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
block|}
comment|// Delete 5 of them:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|w
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add 5 more docs
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// 11th document should fail:
try|try
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
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
finally|finally
block|{
name|restoreIndexWriterMaxDocs
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Tests that 100% deleted segments (which IW "specializes" by dropping entirely) are not mis-counted
DECL|method|testReclaimedDeletesWholeSegments
specifier|public
name|void
name|testReclaimedDeletesWholeSegments
parameter_list|()
throws|throws
name|Exception
block|{
name|setIndexWriterMaxDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
try|try
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
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
comment|// Make a new segment every 2 docs:
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Delete 5 of them:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|w
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add 5 more docs
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// 11th document should fail:
try|try
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
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
finally|finally
block|{
name|restoreIndexWriterMaxDocs
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testAddIndexes
specifier|public
name|void
name|testAddIndexes
parameter_list|()
throws|throws
name|Exception
block|{
name|setIndexWriterMaxDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|w2
operator|.
name|addIndexes
argument_list|(
operator|new
name|Directory
index|[]
block|{
name|dir
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|w2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
try|try
block|{
name|TestUtil
operator|.
name|addIndexesSlowly
argument_list|(
name|w2
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
name|w2
operator|.
name|close
argument_list|()
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
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|restoreIndexWriterMaxDocs
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Make sure MultiReader lets you search exactly the limit number of docs:
DECL|method|testMultiReaderExactLimit
specifier|public
name|void
name|testMultiReaderExactLimit
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|remainder
init|=
name|IndexWriter
operator|.
name|MAX_DOCS
operator|%
literal|100000
decl_stmt|;
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|remainder
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|copies
init|=
name|IndexWriter
operator|.
name|MAX_DOCS
operator|/
literal|100000
decl_stmt|;
name|DirectoryReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|DirectoryReader
name|ir2
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
decl_stmt|;
name|IndexReader
name|subReaders
index|[]
init|=
operator|new
name|IndexReader
index|[
name|copies
operator|+
literal|1
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|subReaders
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|subReaders
index|[
name|subReaders
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|ir2
expr_stmt|;
name|MultiReader
name|mr
init|=
operator|new
name|MultiReader
argument_list|(
name|subReaders
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|IndexWriter
operator|.
name|MAX_DOCS
argument_list|,
name|mr
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriter
operator|.
name|MAX_DOCS
argument_list|,
name|mr
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Make sure MultiReader is upset if you exceed the limit
DECL|method|testMultiReaderBeyondLimit
specifier|public
name|void
name|testMultiReaderBeyondLimit
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|remainder
init|=
name|IndexWriter
operator|.
name|MAX_DOCS
operator|%
literal|100000
decl_stmt|;
comment|// One too many:
name|remainder
operator|++
expr_stmt|;
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|w
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|remainder
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|copies
init|=
name|IndexWriter
operator|.
name|MAX_DOCS
operator|/
literal|100000
decl_stmt|;
name|DirectoryReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|DirectoryReader
name|ir2
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
decl_stmt|;
name|IndexReader
name|subReaders
index|[]
init|=
operator|new
name|IndexReader
index|[
name|copies
operator|+
literal|1
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|subReaders
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|subReaders
index|[
name|subReaders
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|ir2
expr_stmt|;
try|try
block|{
operator|new
name|MultiReader
argument_list|(
name|subReaders
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testTooLargeMaxDocs
specifier|public
name|void
name|testTooLargeMaxDocs
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|IndexWriter
operator|.
name|setMaxDocs
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class
end_unit
