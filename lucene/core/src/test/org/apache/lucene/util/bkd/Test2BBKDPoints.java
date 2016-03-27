begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util.bkd
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|bkd
package|;
end_package
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
name|FSDirectory
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
name|IOContext
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
name|IndexInput
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
name|IndexOutput
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
name|Monster
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
name|NumericUtils
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
begin_comment
comment|// e.g. run like this: ant test -Dtestcase=Test2BBKDPoints -Dtests.nightly=true -Dtests.verbose=true -Dtests.monster=true
end_comment
begin_comment
comment|//
end_comment
begin_comment
comment|//   or: python -u /l/util/src/python/repeatLuceneTest.py -heap 4g -once -nolog -tmpDir /b/tmp -logDir /l/logs Test2BBKDPoints.test2D -verbose
end_comment
begin_class
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
literal|365
operator|*
literal|24
operator|*
name|TimeUnits
operator|.
name|HOUR
argument_list|)
comment|// hopefully ~1 year is long enough ;)
annotation|@
name|Monster
argument_list|(
literal|"takes at least 4 hours and consumes many GB of temp disk space"
argument_list|)
DECL|class|Test2BBKDPoints
specifier|public
class|class
name|Test2BBKDPoints
extends|extends
name|LuceneTestCase
block|{
DECL|method|test1D
specifier|public
name|void
name|test1D
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|createTempDir
argument_list|(
literal|"2BBKDPoints1D"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|26
operator|)
operator|+
literal|100
decl_stmt|;
name|BKDWriter
name|w
init|=
operator|new
name|BKDWriter
argument_list|(
name|numDocs
argument_list|,
name|dir
argument_list|,
literal|"_0"
argument_list|,
literal|1
argument_list|,
name|Long
operator|.
name|BYTES
argument_list|,
name|BKDWriter
operator|.
name|DEFAULT_MAX_POINTS_IN_LEAF_NODE
argument_list|,
name|BKDWriter
operator|.
name|DEFAULT_MAX_MB_SORT_IN_HEAP
argument_list|,
literal|26L
operator|*
name|numDocs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|packedBytes
init|=
operator|new
name|byte
index|[
name|Long
operator|.
name|BYTES
index|]
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|numDocs
condition|;
name|docID
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|26
condition|;
name|j
operator|++
control|)
block|{
comment|// first a random int:
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|,
name|packedBytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// then our counter, which will overflow a bit in the end:
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|counter
argument_list|,
name|packedBytes
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|w
operator|.
name|add
argument_list|(
name|packedBytes
argument_list|,
name|docID
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
operator|&&
name|docID
operator|%
literal|100000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|docID
operator|+
literal|" of "
operator|+
name|numDocs
operator|+
literal|"..."
argument_list|)
expr_stmt|;
block|}
block|}
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"1d.bkd"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|long
name|indexFP
init|=
name|w
operator|.
name|finish
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"1d.bkd"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|indexFP
argument_list|)
expr_stmt|;
name|BKDReader
name|r
init|=
operator|new
name|BKDReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|r
operator|.
name|verify
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|in
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
DECL|method|test2D
specifier|public
name|void
name|test2D
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|createTempDir
argument_list|(
literal|"2BBKDPoints2D"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|26
operator|)
operator|+
literal|100
decl_stmt|;
name|BKDWriter
name|w
init|=
operator|new
name|BKDWriter
argument_list|(
name|numDocs
argument_list|,
name|dir
argument_list|,
literal|"_0"
argument_list|,
literal|2
argument_list|,
name|Long
operator|.
name|BYTES
argument_list|,
name|BKDWriter
operator|.
name|DEFAULT_MAX_POINTS_IN_LEAF_NODE
argument_list|,
name|BKDWriter
operator|.
name|DEFAULT_MAX_MB_SORT_IN_HEAP
argument_list|,
literal|26L
operator|*
name|numDocs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|packedBytes
init|=
operator|new
name|byte
index|[
literal|2
operator|*
name|Long
operator|.
name|BYTES
index|]
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|numDocs
condition|;
name|docID
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|26
condition|;
name|j
operator|++
control|)
block|{
comment|// first a random int:
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|,
name|packedBytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// then our counter, which will overflow a bit in the end:
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|counter
argument_list|,
name|packedBytes
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
comment|// then two random ints for the 2nd dimension:
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|,
name|packedBytes
argument_list|,
name|Long
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|,
name|packedBytes
argument_list|,
name|Long
operator|.
name|BYTES
operator|+
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|w
operator|.
name|add
argument_list|(
name|packedBytes
argument_list|,
name|docID
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
operator|&&
name|docID
operator|%
literal|100000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|docID
operator|+
literal|" of "
operator|+
name|numDocs
operator|+
literal|"..."
argument_list|)
expr_stmt|;
block|}
block|}
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"2d.bkd"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|long
name|indexFP
init|=
name|w
operator|.
name|finish
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"2d.bkd"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|indexFP
argument_list|)
expr_stmt|;
name|BKDReader
name|r
init|=
operator|new
name|BKDReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|r
operator|.
name|verify
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|in
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
