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
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|*
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
name|*
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
name|*
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_class
DECL|class|TestStressIndexing2
specifier|public
class|class
name|TestStressIndexing2
extends|extends
name|LuceneTestCase
block|{
DECL|field|maxFields
specifier|static
name|int
name|maxFields
init|=
literal|4
decl_stmt|;
DECL|field|bigFieldSize
specifier|static
name|int
name|bigFieldSize
init|=
literal|10
decl_stmt|;
DECL|field|sameFieldOrder
specifier|static
name|boolean
name|sameFieldOrder
init|=
literal|false
decl_stmt|;
DECL|field|autoCommit
specifier|static
name|boolean
name|autoCommit
init|=
literal|false
decl_stmt|;
DECL|field|mergeFactor
specifier|static
name|int
name|mergeFactor
init|=
literal|3
decl_stmt|;
DECL|field|maxBufferedDocs
specifier|static
name|int
name|maxBufferedDocs
init|=
literal|3
decl_stmt|;
DECL|field|r
specifier|static
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir1
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// dir1 = FSDirectory.getDirectory("foofoofoo");
name|Directory
name|dir2
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// mergeFactor=2; maxBufferedDocs=2; Map docs = indexRandom(1, 3, 2, dir1);
name|Map
name|docs
init|=
name|indexRandom
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
name|dir1
argument_list|)
decl_stmt|;
name|indexSerial
argument_list|(
name|docs
argument_list|,
name|dir2
argument_list|)
expr_stmt|;
comment|// verifying verify
comment|// verifyEquals(dir1, dir1, "id");
comment|// verifyEquals(dir2, dir2, "id");
name|verifyEquals
argument_list|(
name|dir1
argument_list|,
name|dir2
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiConfig
specifier|public
name|void
name|testMultiConfig
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test lots of smaller different params together
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
comment|// increase iterations for better testing
name|sameFieldOrder
operator|=
name|r
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|autoCommit
operator|=
name|r
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|mergeFactor
operator|=
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|+
literal|2
expr_stmt|;
name|maxBufferedDocs
operator|=
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|+
literal|2
expr_stmt|;
name|int
name|nThreads
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|iter
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|range
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|+
literal|1
decl_stmt|;
name|Directory
name|dir1
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|Directory
name|dir2
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|Map
name|docs
init|=
name|indexRandom
argument_list|(
name|nThreads
argument_list|,
name|iter
argument_list|,
name|range
argument_list|,
name|dir1
argument_list|)
decl_stmt|;
name|indexSerial
argument_list|(
name|docs
argument_list|,
name|dir2
argument_list|)
expr_stmt|;
name|verifyEquals
argument_list|(
name|dir1
argument_list|,
name|dir2
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|idTerm
specifier|static
name|Term
name|idTerm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
DECL|field|threads
name|IndexingThread
index|[]
name|threads
decl_stmt|;
DECL|field|fieldNameComparator
specifier|static
name|Comparator
name|fieldNameComparator
init|=
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Fieldable
operator|)
name|o1
operator|)
operator|.
name|name
argument_list|()
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|Fieldable
operator|)
name|o2
operator|)
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// This test avoids using any extra synchronization in the multiple
comment|// indexing threads to test that IndexWriter does correctly synchronize
comment|// everything.
DECL|method|indexRandom
specifier|public
name|Map
name|indexRandom
parameter_list|(
name|int
name|nThreads
parameter_list|,
name|int
name|iterations
parameter_list|,
name|int
name|range
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|w
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|/***     w.setMaxMergeDocs(Integer.MAX_VALUE);     w.setMaxFieldLength(10000);     w.setRAMBufferSizeMB(1);     w.setMergeFactor(10);     ***/
comment|// force many merges
name|w
operator|.
name|setMergeFactor
argument_list|(
name|mergeFactor
argument_list|)
expr_stmt|;
name|w
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|.1
argument_list|)
expr_stmt|;
name|w
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBufferedDocs
argument_list|)
expr_stmt|;
name|threads
operator|=
operator|new
name|IndexingThread
index|[
name|nThreads
index|]
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexingThread
name|th
init|=
operator|new
name|IndexingThread
argument_list|()
decl_stmt|;
name|th
operator|.
name|w
operator|=
name|w
expr_stmt|;
name|th
operator|.
name|base
operator|=
literal|1000000
operator|*
name|i
expr_stmt|;
name|th
operator|.
name|range
operator|=
name|range
expr_stmt|;
name|th
operator|.
name|iterations
operator|=
name|iterations
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|=
name|th
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|// w.optimize();
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|Map
name|docs
init|=
operator|new
name|HashMap
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexingThread
name|th
init|=
name|threads
index|[
name|i
index|]
decl_stmt|;
synchronized|synchronized
init|(
name|th
init|)
block|{
name|docs
operator|.
name|putAll
argument_list|(
name|th
operator|.
name|docs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|docs
return|;
block|}
DECL|method|indexSerial
specifier|public
specifier|static
name|void
name|indexSerial
parameter_list|(
name|Map
name|docs
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
comment|// index all docs in a single thread
name|Iterator
name|iter
init|=
name|docs
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Document
name|d
init|=
operator|(
name|Document
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|ArrayList
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|addAll
argument_list|(
name|d
operator|.
name|getFields
argument_list|()
argument_list|)
expr_stmt|;
comment|// put fields in same order each time
name|Collections
operator|.
name|sort
argument_list|(
name|fields
argument_list|,
name|fieldNameComparator
argument_list|)
expr_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|setBoost
argument_list|(
name|d
operator|.
name|getBoost
argument_list|()
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
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|d1
operator|.
name|add
argument_list|(
operator|(
name|Fieldable
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
comment|// System.out.println("indexing "+d1);
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyEquals
specifier|public
specifier|static
name|void
name|verifyEquals
parameter_list|(
name|Directory
name|dir1
parameter_list|,
name|Directory
name|dir2
parameter_list|,
name|String
name|idField
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|r1
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
decl_stmt|;
name|IndexReader
name|r2
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
decl_stmt|;
name|verifyEquals
argument_list|(
name|r1
argument_list|,
name|r2
argument_list|,
name|idField
argument_list|)
expr_stmt|;
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyEquals
specifier|public
specifier|static
name|void
name|verifyEquals
parameter_list|(
name|IndexReader
name|r1
parameter_list|,
name|IndexReader
name|r2
parameter_list|,
name|String
name|idField
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|r1
operator|.
name|numDocs
argument_list|()
argument_list|,
name|r2
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|hasDeletes
init|=
operator|!
operator|(
name|r1
operator|.
name|maxDoc
argument_list|()
operator|==
name|r2
operator|.
name|maxDoc
argument_list|()
operator|&&
name|r1
operator|.
name|numDocs
argument_list|()
operator|==
name|r1
operator|.
name|maxDoc
argument_list|()
operator|)
decl_stmt|;
name|int
index|[]
name|r2r1
init|=
operator|new
name|int
index|[
name|r2
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
comment|// r2 id to r1 id mapping
name|TermDocs
name|termDocs1
init|=
name|r1
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermDocs
name|termDocs2
init|=
name|r2
operator|.
name|termDocs
argument_list|()
decl_stmt|;
comment|// create mapping from id2 space to id2 based on idField
name|idField
operator|=
name|idField
operator|.
name|intern
argument_list|()
expr_stmt|;
name|TermEnum
name|termEnum
init|=
name|r1
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|idField
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|||
name|term
operator|.
name|field
argument_list|()
operator|!=
name|idField
condition|)
break|break;
name|termDocs1
operator|.
name|seek
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termDocs1
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|id1
init|=
name|termDocs1
operator|.
name|doc
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|termDocs1
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|termDocs2
operator|.
name|seek
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termDocs2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|id2
init|=
name|termDocs2
operator|.
name|doc
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|termDocs2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|r2r1
index|[
name|id2
index|]
operator|=
name|id1
expr_stmt|;
comment|// verify stored fields are equivalent
name|verifyEquals
argument_list|(
name|r1
operator|.
name|document
argument_list|(
name|id1
argument_list|)
argument_list|,
name|r2
operator|.
name|document
argument_list|(
name|id2
argument_list|)
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Verify postings
name|TermEnum
name|termEnum1
init|=
name|r1
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|TermEnum
name|termEnum2
init|=
name|r2
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
comment|// pack both doc and freq into single element for easy sorting
name|long
index|[]
name|info1
init|=
operator|new
name|long
index|[
name|r1
operator|.
name|numDocs
argument_list|()
index|]
decl_stmt|;
name|long
index|[]
name|info2
init|=
operator|new
name|long
index|[
name|r2
operator|.
name|numDocs
argument_list|()
index|]
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|Term
name|term1
decl_stmt|,
name|term2
decl_stmt|;
comment|// iterate until we get some docs
name|int
name|len1
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|len1
operator|=
literal|0
expr_stmt|;
name|term1
operator|=
name|termEnum1
operator|.
name|term
argument_list|()
expr_stmt|;
if|if
condition|(
name|term1
operator|==
literal|null
condition|)
break|break;
name|termDocs1
operator|.
name|seek
argument_list|(
name|termEnum1
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs1
operator|.
name|next
argument_list|()
condition|)
block|{
name|int
name|d1
init|=
name|termDocs1
operator|.
name|doc
argument_list|()
decl_stmt|;
name|int
name|f1
init|=
name|termDocs1
operator|.
name|freq
argument_list|()
decl_stmt|;
name|info1
index|[
name|len1
index|]
operator|=
operator|(
operator|(
operator|(
name|long
operator|)
name|d1
operator|)
operator|<<
literal|32
operator|)
operator||
name|f1
expr_stmt|;
name|len1
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|len1
operator|>
literal|0
condition|)
break|break;
if|if
condition|(
operator|!
name|termEnum1
operator|.
name|next
argument_list|()
condition|)
break|break;
block|}
comment|// iterate until we get some docs
name|int
name|len2
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|len2
operator|=
literal|0
expr_stmt|;
name|term2
operator|=
name|termEnum2
operator|.
name|term
argument_list|()
expr_stmt|;
if|if
condition|(
name|term2
operator|==
literal|null
condition|)
break|break;
name|termDocs2
operator|.
name|seek
argument_list|(
name|termEnum2
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs2
operator|.
name|next
argument_list|()
condition|)
block|{
name|int
name|d2
init|=
name|termDocs2
operator|.
name|doc
argument_list|()
decl_stmt|;
name|int
name|f2
init|=
name|termDocs2
operator|.
name|freq
argument_list|()
decl_stmt|;
name|info2
index|[
name|len2
index|]
operator|=
operator|(
operator|(
operator|(
name|long
operator|)
name|r2r1
index|[
name|d2
index|]
operator|)
operator|<<
literal|32
operator|)
operator||
name|f2
expr_stmt|;
name|len2
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|len2
operator|>
literal|0
condition|)
break|break;
if|if
condition|(
operator|!
name|termEnum2
operator|.
name|next
argument_list|()
condition|)
break|break;
block|}
if|if
condition|(
operator|!
name|hasDeletes
condition|)
name|assertEquals
argument_list|(
name|termEnum1
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termEnum2
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|len1
argument_list|,
name|len2
argument_list|)
expr_stmt|;
if|if
condition|(
name|len1
operator|==
literal|0
condition|)
break|break;
comment|// no more terms
name|assertEquals
argument_list|(
name|term1
argument_list|,
name|term2
argument_list|)
expr_stmt|;
comment|// sort info2 to get it into ascending docid
name|Arrays
operator|.
name|sort
argument_list|(
name|info2
argument_list|,
literal|0
argument_list|,
name|len2
argument_list|)
expr_stmt|;
comment|// now compare
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len1
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|info1
index|[
name|i
index|]
argument_list|,
name|info2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|termEnum1
operator|.
name|next
argument_list|()
expr_stmt|;
name|termEnum2
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|verifyEquals
specifier|public
specifier|static
name|void
name|verifyEquals
parameter_list|(
name|Document
name|d1
parameter_list|,
name|Document
name|d2
parameter_list|)
block|{
name|List
name|ff1
init|=
name|d1
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|List
name|ff2
init|=
name|d2
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|ff1
argument_list|,
name|fieldNameComparator
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|ff2
argument_list|,
name|fieldNameComparator
argument_list|)
expr_stmt|;
if|if
condition|(
name|ff1
operator|.
name|size
argument_list|()
operator|!=
name|ff2
operator|.
name|size
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ff1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ff2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ff1
operator|.
name|size
argument_list|()
argument_list|,
name|ff2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ff1
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Fieldable
name|f1
init|=
operator|(
name|Fieldable
operator|)
name|ff1
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Fieldable
name|f2
init|=
operator|(
name|Fieldable
operator|)
name|ff2
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|f1
operator|.
name|isBinary
argument_list|()
condition|)
block|{
assert|assert
operator|(
name|f2
operator|.
name|isBinary
argument_list|()
operator|)
assert|;
comment|//TODO
block|}
else|else
block|{
name|String
name|s1
init|=
name|f1
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|String
name|s2
init|=
name|f2
operator|.
name|stringValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|s1
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
condition|)
block|{
comment|// print out whole doc on error
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ff1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ff2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|IndexingThread
specifier|private
specifier|static
class|class
name|IndexingThread
extends|extends
name|Thread
block|{
DECL|field|w
name|IndexWriter
name|w
decl_stmt|;
DECL|field|base
name|int
name|base
decl_stmt|;
DECL|field|range
name|int
name|range
decl_stmt|;
DECL|field|iterations
name|int
name|iterations
decl_stmt|;
DECL|field|docs
name|Map
name|docs
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|// Map<String,Document>
DECL|field|r
name|Random
name|r
decl_stmt|;
DECL|method|nextInt
specifier|public
name|int
name|nextInt
parameter_list|(
name|int
name|lim
parameter_list|)
block|{
return|return
name|r
operator|.
name|nextInt
argument_list|(
name|lim
argument_list|)
return|;
block|}
DECL|method|getString
specifier|public
name|String
name|getString
parameter_list|(
name|int
name|nTokens
parameter_list|)
block|{
name|nTokens
operator|=
name|nTokens
operator|!=
literal|0
condition|?
name|nTokens
else|:
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|+
literal|1
expr_stmt|;
comment|// avoid StringBuffer because it adds extra synchronization.
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|nTokens
operator|*
literal|2
index|]
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
name|nTokens
condition|;
name|i
operator|++
control|)
block|{
name|arr
index|[
name|i
operator|*
literal|2
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|'A'
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|arr
index|[
name|i
operator|*
literal|2
operator|+
literal|1
index|]
operator|=
literal|' '
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|arr
argument_list|)
return|;
block|}
DECL|method|indexDoc
specifier|public
name|void
name|indexDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|ArrayList
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|int
name|id
init|=
name|base
operator|+
name|nextInt
argument_list|(
name|range
argument_list|)
decl_stmt|;
name|String
name|idString
init|=
literal|""
operator|+
name|id
decl_stmt|;
name|Field
name|idField
init|=
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|idString
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
name|NO_NORMS
argument_list|)
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|int
name|nFields
init|=
name|nextInt
argument_list|(
name|maxFields
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
name|nFields
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|nextInt
argument_list|(
literal|4
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|fields
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f0"
argument_list|,
name|getString
argument_list|(
literal|1
argument_list|)
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
name|NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|fields
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f1"
argument_list|,
name|getString
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|fields
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f2"
argument_list|,
name|getString
argument_list|(
literal|0
argument_list|)
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
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|fields
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f3"
argument_list|,
name|getString
argument_list|(
name|bigFieldSize
argument_list|)
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|sameFieldOrder
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|fields
argument_list|,
name|fieldNameComparator
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// random placement of id field also
name|Collections
operator|.
name|swap
argument_list|(
name|fields
argument_list|,
name|nextInt
argument_list|(
name|fields
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|d
operator|.
name|add
argument_list|(
operator|(
name|Fieldable
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|updateDocument
argument_list|(
name|idTerm
operator|.
name|createTerm
argument_list|(
name|idString
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
comment|// System.out.println("indexing "+d);
name|docs
operator|.
name|put
argument_list|(
name|idString
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|r
operator|=
operator|new
name|Random
argument_list|(
name|base
operator|+
name|range
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|indexDoc
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|TestCase
operator|.
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|docs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
