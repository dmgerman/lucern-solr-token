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
import|;
end_import
begin_comment
comment|/** Test that creates way, way, way too many fields */
end_comment
begin_class
DECL|class|TestManyFields
specifier|public
class|class
name|TestManyFields
extends|extends
name|LuceneTestCase
block|{
DECL|field|storedTextType
specifier|private
specifier|static
specifier|final
name|FieldType
name|storedTextType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
DECL|method|testManyFields
specifier|public
name|void
name|testManyFields
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
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
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
name|newField
argument_list|(
literal|"a"
operator|+
name|j
argument_list|,
literal|"aaa"
operator|+
name|j
argument_list|,
name|storedTextType
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"b"
operator|+
name|j
argument_list|,
literal|"aaa"
operator|+
name|j
argument_list|,
name|storedTextType
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"c"
operator|+
name|j
argument_list|,
literal|"aaa"
operator|+
name|j
argument_list|,
name|storedTextType
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"d"
operator|+
name|j
argument_list|,
literal|"aaa"
argument_list|,
name|storedTextType
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"e"
operator|+
name|j
argument_list|,
literal|"aaa"
argument_list|,
name|storedTextType
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f"
operator|+
name|j
argument_list|,
literal|"aaa"
argument_list|,
name|storedTextType
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
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
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
literal|100
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
operator|+
name|j
argument_list|,
literal|"aaa"
operator|+
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"b"
operator|+
name|j
argument_list|,
literal|"aaa"
operator|+
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"c"
operator|+
name|j
argument_list|,
literal|"aaa"
operator|+
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"d"
operator|+
name|j
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"e"
operator|+
name|j
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
operator|+
name|j
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
DECL|method|testDiverseDocs
specifier|public
name|void
name|testDiverseDocs
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
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|0.5
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|atLeast
argument_list|(
literal|1
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
name|n
condition|;
name|i
operator|++
control|)
block|{
comment|// First, docs where every term is unique (heavy on
comment|// Posting instances)
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
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
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
name|k
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|,
name|storedTextType
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// Next, many single term docs where only one term
comment|// occurs (heavy on byte blocks)
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
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
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"aaa aaa aaa aaa aaa aaa aaa aaa aaa aaa"
argument_list|,
name|storedTextType
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
block|}
comment|// Next, many single term docs where only one term
comment|// occurs but the terms are very long (heavy on
comment|// char[] arrays)
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|x
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
operator|+
literal|"."
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|1000
condition|;
name|k
operator|++
control|)
name|b
operator|.
name|append
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|String
name|longTerm
init|=
name|b
operator|.
name|toString
argument_list|()
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
name|newField
argument_list|(
literal|"field"
argument_list|,
name|longTerm
argument_list|,
name|storedTextType
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
block|}
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|int
name|totalHits
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
literal|"aaa"
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|assertEquals
argument_list|(
name|n
operator|*
literal|100
argument_list|,
name|totalHits
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
comment|// LUCENE-4398
DECL|method|testRotatingFieldNames
specifier|public
name|void
name|testRotatingFieldNames
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
literal|"TestIndexWriter.testChangingFields"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
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
name|setRAMBufferSizeMB
argument_list|(
literal|0.2
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
operator|-
literal|1
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
name|int
name|upto
init|=
literal|0
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|firstDocCount
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|startFlushCount
init|=
name|w
operator|.
name|getFlushCount
argument_list|()
decl_stmt|;
name|int
name|docCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|w
operator|.
name|getFlushCount
argument_list|()
operator|==
name|startFlushCount
condition|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field"
operator|+
operator|(
name|upto
operator|++
operator|)
argument_list|,
literal|"content"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: iter="
operator|+
name|iter
operator|+
literal|" flushed after docCount="
operator|+
name|docCount
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|iter
operator|==
literal|0
condition|)
block|{
name|firstDocCount
operator|=
name|docCount
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"flushed after too few docs: first segment flushed at docCount="
operator|+
name|firstDocCount
operator|+
literal|", but current segment flushed after docCount="
operator|+
name|docCount
operator|+
literal|"; iter="
operator|+
name|iter
argument_list|,
operator|(
operator|(
name|float
operator|)
name|docCount
operator|)
operator|/
name|firstDocCount
operator|>
literal|0.9
argument_list|)
expr_stmt|;
if|if
condition|(
name|upto
operator|>
literal|5000
condition|)
block|{
comment|// Start re-using field names after a while
comment|// ... important because otherwise we can OOME due
comment|// to too many FieldInfo instances.
name|upto
operator|=
literal|0
expr_stmt|;
block|}
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
block|}
end_class
end_unit
