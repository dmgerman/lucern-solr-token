begin_unit
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|Analyzer
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
name|codecs
operator|.
name|DocValuesFormat
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|SortedSetDocValues
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
name|SortedSetDocValues
operator|.
name|OrdIterator
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
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * A very simple demo used in the API documentation (src/java/overview.html).  *  * Please try to keep src/java/overview.html up-to-date when making changes  * to this class.  */
end_comment
begin_class
DECL|class|TestDemoDocValue
specifier|public
class|class
name|TestDemoDocValue
extends|extends
name|LuceneTestCase
block|{
comment|// nocommit: only Lucene42/Asserting implemented right now
DECL|field|saved
specifier|private
name|Codec
name|saved
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|saved
operator|=
name|Codec
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|Codec
operator|.
name|setDefault
argument_list|(
name|_TestUtil
operator|.
name|alwaysDocValuesFormat
argument_list|(
name|DocValuesFormat
operator|.
name|forName
argument_list|(
literal|"Asserting"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Codec
operator|.
name|setDefault
argument_list|(
name|saved
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testOneValue
specifier|public
name|void
name|testOneValue
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// Store the index in memory:
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// To store an index on disk, use this instead:
comment|// Directory directory = FSDirectory.open(new File("/tmp/testindex"));
name|RandomIndexWriter
name|iwriter
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|analyzer
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
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now search the index:
name|DirectoryReader
name|ireader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// read-only=true
name|SortedSetDocValues
name|dv
init|=
name|getOnlySegmentReader
argument_list|(
name|ireader
argument_list|)
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|OrdIterator
name|oi
init|=
name|dv
operator|.
name|getOrds
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrdIterator
operator|.
name|NO_MORE_ORDS
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|dv
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|ireader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testTwoDocumentsMerged
specifier|public
name|void
name|testTwoDocumentsMerged
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// Store the index in memory:
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// To store an index on disk, use this instead:
comment|// Directory directory = FSDirectory.open(new File("/tmp/testindex"));
name|IndexWriterConfig
name|iwconfig
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|iwconfig
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iwriter
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|iwconfig
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
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"world"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now search the index:
name|DirectoryReader
name|ireader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// read-only=true
name|SortedSetDocValues
name|dv
init|=
name|getOnlySegmentReader
argument_list|(
name|ireader
argument_list|)
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|OrdIterator
name|oi
init|=
name|dv
operator|.
name|getOrds
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrdIterator
operator|.
name|NO_MORE_ORDS
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|dv
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|oi
operator|=
name|dv
operator|.
name|getOrds
argument_list|(
literal|1
argument_list|,
name|oi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrdIterator
operator|.
name|NO_MORE_ORDS
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|dv
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"world"
argument_list|)
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dv
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|ireader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testTwoValues
specifier|public
name|void
name|testTwoValues
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// Store the index in memory:
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// To store an index on disk, use this instead:
comment|// Directory directory = FSDirectory.open(new File("/tmp/testindex"));
name|RandomIndexWriter
name|iwriter
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|analyzer
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
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"world"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now search the index:
name|DirectoryReader
name|ireader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// read-only=true
name|SortedSetDocValues
name|dv
init|=
name|getOnlySegmentReader
argument_list|(
name|ireader
argument_list|)
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|OrdIterator
name|oi
init|=
name|dv
operator|.
name|getOrds
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrdIterator
operator|.
name|NO_MORE_ORDS
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|dv
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|dv
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"world"
argument_list|)
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|ireader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testThreeValuesTwoDocs
specifier|public
name|void
name|testThreeValuesTwoDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// Store the index in memory:
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// To store an index on disk, use this instead:
comment|// Directory directory = FSDirectory.open(new File("/tmp/testindex"));
name|IndexWriterConfig
name|iwconfig
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|iwconfig
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iwriter
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|iwconfig
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
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"world"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"beer"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now search the index:
name|DirectoryReader
name|ireader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// read-only=true
name|SortedSetDocValues
name|dv
init|=
name|getOnlySegmentReader
argument_list|(
name|ireader
argument_list|)
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dv
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|OrdIterator
name|oi
init|=
name|dv
operator|.
name|getOrds
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrdIterator
operator|.
name|NO_MORE_ORDS
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|oi
operator|=
name|dv
operator|.
name|getOrds
argument_list|(
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrdIterator
operator|.
name|NO_MORE_ORDS
argument_list|,
name|oi
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|dv
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"beer"
argument_list|)
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|dv
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|dv
operator|.
name|lookupOrd
argument_list|(
literal|2
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"world"
argument_list|)
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|ireader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
