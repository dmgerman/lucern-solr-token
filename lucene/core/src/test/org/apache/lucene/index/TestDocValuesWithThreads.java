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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|search
operator|.
name|FieldCache
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
begin_class
DECL|class|TestDocValuesWithThreads
specifier|public
class|class
name|TestDocValuesWithThreads
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
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
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
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
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|numbers
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|binary
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|sorted
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|long
name|number
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"number"
argument_list|,
name|number
argument_list|)
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"bytes"
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|binary
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|bytes
operator|=
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"sorted"
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|sorted
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|numbers
operator|.
name|add
argument_list|(
name|number
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
specifier|final
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReader
name|ar
init|=
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
name|int
name|numThreads
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|startingGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|numThreads
condition|;
name|t
operator|++
control|)
block|{
specifier|final
name|Random
name|threadRandom
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|//NumericDocValues ndv = ar.getNumericDocValues("number");
name|FieldCache
operator|.
name|Longs
name|ndv
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getLongs
argument_list|(
name|ar
argument_list|,
literal|"number"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//BinaryDocValues bdv = ar.getBinaryDocValues("bytes");
name|BinaryDocValues
name|bdv
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTerms
argument_list|(
name|ar
argument_list|,
literal|"bytes"
argument_list|)
decl_stmt|;
name|SortedDocValues
name|sdv
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|ar
argument_list|,
literal|"sorted"
argument_list|)
decl_stmt|;
name|startingGun
operator|.
name|await
argument_list|()
expr_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch2
init|=
operator|new
name|BytesRef
argument_list|()
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
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|docID
init|=
name|threadRandom
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|threadRandom
operator|.
name|nextInt
argument_list|(
literal|6
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|numbers
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getBytes
argument_list|(
name|ar
argument_list|,
literal|"number"
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
name|numbers
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getShorts
argument_list|(
name|ar
argument_list|,
literal|"number"
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
name|numbers
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getInts
argument_list|(
name|ar
argument_list|,
literal|"number"
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|assertEquals
argument_list|(
name|numbers
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getLongs
argument_list|(
name|ar
argument_list|,
literal|"number"
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|assertEquals
argument_list|(
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|numbers
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|,
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getFloats
argument_list|(
name|ar
argument_list|,
literal|"number"
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|assertEquals
argument_list|(
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|numbers
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|,
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDoubles
argument_list|(
name|ar
argument_list|,
literal|"number"
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
break|break;
block|}
name|bdv
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|binary
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
comment|// Cannot share a single scratch against two "sources":
name|sdv
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|scratch2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sorted
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|,
name|scratch2
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|r
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
DECL|method|test2
specifier|public
name|void
name|test2
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
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
specifier|final
name|boolean
name|allowDups
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
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
literal|"TEST: NUM_DOCS="
operator|+
name|NUM_DOCS
operator|+
literal|" allowDups="
operator|+
name|allowDups
argument_list|)
expr_stmt|;
block|}
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|docValues
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
comment|// TODO: deletions
while|while
condition|(
name|numDocs
operator|<
name|NUM_DOCS
condition|)
block|{
specifier|final
name|String
name|s
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|s
operator|=
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|allowDups
condition|)
block|{
if|if
condition|(
name|seen
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|seen
operator|.
name|add
argument_list|(
name|s
argument_list|)
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
literal|"  "
operator|+
name|numDocs
operator|+
literal|": s="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
specifier|final
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
literal|"stringdv"
argument_list|,
name|br
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
literal|"id"
argument_list|,
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|docValues
operator|.
name|add
argument_list|(
name|br
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|numDocs
operator|++
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|40
argument_list|)
operator|==
literal|17
condition|)
block|{
comment|// force flush
name|writer
operator|.
name|getReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|DirectoryReader
name|r
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|AtomicReader
name|sr
init|=
name|getOnlySegmentReader
argument_list|(
name|r
argument_list|)
decl_stmt|;
specifier|final
name|long
name|END_TIME
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
operator|(
name|TEST_NIGHTLY
condition|?
literal|30
else|:
literal|1
operator|)
decl_stmt|;
specifier|final
name|int
name|NUM_THREADS
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|NUM_THREADS
index|]
decl_stmt|;
for|for
control|(
name|int
name|thread
init|=
literal|0
init|;
name|thread
operator|<
name|NUM_THREADS
condition|;
name|thread
operator|++
control|)
block|{
name|threads
index|[
name|thread
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|SortedDocValues
name|stringDVDirect
decl_stmt|;
specifier|final
name|NumericDocValues
name|docIDToID
decl_stmt|;
try|try
block|{
name|stringDVDirect
operator|=
name|sr
operator|.
name|getSortedDocValues
argument_list|(
literal|"stringdv"
argument_list|)
expr_stmt|;
name|docIDToID
operator|=
name|sr
operator|.
name|getNumericDocValues
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|stringDVDirect
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|END_TIME
condition|)
block|{
specifier|final
name|SortedDocValues
name|source
decl_stmt|;
name|source
operator|=
name|stringDVDirect
expr_stmt|;
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
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
literal|100
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|docID
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|sr
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|source
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docValues
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|docIDToID
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|thread
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|r
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
