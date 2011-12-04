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
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|Random
import|;
end_import
begin_class
DECL|class|TestIndexWriterMerging
specifier|public
class|class
name|TestIndexWriterMerging
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Tests that index merging (specifically addIndexes(Directory...)) doesn't    * change the index order of documents.    */
DECL|method|testLucene
specifier|public
name|void
name|testLucene
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|num
init|=
literal|100
decl_stmt|;
name|Directory
name|indexA
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|indexB
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|fillIndex
argument_list|(
name|random
argument_list|,
name|indexA
argument_list|,
literal|0
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|boolean
name|fail
init|=
name|verifyIndex
argument_list|(
name|indexA
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|fail
condition|)
block|{
name|fail
argument_list|(
literal|"Index a is invalid"
argument_list|)
expr_stmt|;
block|}
name|fillIndex
argument_list|(
name|random
argument_list|,
name|indexB
argument_list|,
name|num
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|fail
operator|=
name|verifyIndex
argument_list|(
name|indexB
argument_list|,
name|num
argument_list|)
expr_stmt|;
if|if
condition|(
name|fail
condition|)
block|{
name|fail
argument_list|(
literal|"Index b is invalid"
argument_list|)
expr_stmt|;
block|}
name|Directory
name|merged
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
name|merged
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addIndexes
argument_list|(
name|indexA
argument_list|,
name|indexB
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
operator|=
name|verifyIndex
argument_list|(
name|merged
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"The merged index is invalid"
argument_list|,
name|fail
argument_list|)
expr_stmt|;
name|indexA
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexB
operator|.
name|close
argument_list|()
expr_stmt|;
name|merged
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyIndex
specifier|private
name|boolean
name|verifyIndex
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|int
name|startAt
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|max
init|=
name|reader
operator|.
name|maxDoc
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
name|max
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|temp
init|=
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//System.out.println("doc "+i+"="+temp.getField("count").stringValue());
comment|//compare the index doc number to the value that it should be
if|if
condition|(
operator|!
name|temp
operator|.
name|getField
argument_list|(
literal|"count"
argument_list|)
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
name|i
operator|+
name|startAt
operator|)
operator|+
literal|""
argument_list|)
condition|)
block|{
name|fail
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Document "
operator|+
operator|(
name|i
operator|+
name|startAt
operator|)
operator|+
literal|" is returning document "
operator|+
name|temp
operator|.
name|getField
argument_list|(
literal|"count"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|fail
return|;
block|}
DECL|method|fillIndex
specifier|private
name|void
name|fillIndex
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
operator|(
name|start
operator|+
name|numDocs
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|temp
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|temp
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"count"
argument_list|,
operator|(
literal|""
operator|+
name|i
operator|)
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/* nocommit: Fix tests to use an id and delete by term   // LUCENE-325: test forceMergeDeletes, when 2 singular merges   // are required   public void testForceMergeDeletes() throws IOException {     Directory dir = newDirectory();     IndexWriter writer = new IndexWriter(dir, newIndexWriterConfig(         TEST_VERSION_CURRENT, new MockAnalyzer(random))         .setMaxBufferedDocs(2).setRAMBufferSizeMB(                                                   IndexWriterConfig.DISABLE_AUTO_FLUSH));     Document document = new Document();      FieldType customType = new FieldType();     customType.setStored(true);      FieldType customType1 = new FieldType(TextField.TYPE_UNSTORED);     customType1.setTokenized(false);     customType1.setStoreTermVectors(true);     customType1.setStoreTermVectorPositions(true);     customType1.setStoreTermVectorOffsets(true);          Field storedField = newField("stored", "stored", customType);     document.add(storedField);     Field termVectorField = newField("termVector", "termVector", customType1);     document.add(termVectorField);     for(int i=0;i<10;i++)       writer.addDocument(document);     writer.close();      IndexReader ir = IndexReader.open(dir, false);     assertEquals(10, ir.maxDoc());     assertEquals(10, ir.numDocs());     ir.deleteDocument(0);     ir.deleteDocument(7);     assertEquals(8, ir.numDocs());     ir.close();      writer = new IndexWriter(dir, newIndexWriterConfig( TEST_VERSION_CURRENT, new MockAnalyzer(random)).setMergePolicy(newLogMergePolicy()));     assertEquals(8, writer.numDocs());     assertEquals(10, writer.maxDoc());     writer.forceMergeDeletes();     assertEquals(8, writer.numDocs());     writer.close();     ir = IndexReader.open(dir, true);     assertEquals(8, ir.maxDoc());     assertEquals(8, ir.numDocs());     ir.close();     dir.close();   }    // LUCENE-325: test forceMergeDeletes, when many adjacent merges are required   public void testForceMergeDeletes2() throws IOException {     Directory dir = newDirectory();     IndexWriter writer = new IndexWriter(         dir,         newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer(random)).             setMaxBufferedDocs(2).             setRAMBufferSizeMB(IndexWriterConfig.DISABLE_AUTO_FLUSH).             setMergePolicy(newLogMergePolicy(50))     );      Document document = new Document();      FieldType customType = new FieldType();     customType.setStored(true);      FieldType customType1 = new FieldType(TextField.TYPE_UNSTORED);     customType1.setTokenized(false);     customType1.setStoreTermVectors(true);     customType1.setStoreTermVectorPositions(true);     customType1.setStoreTermVectorOffsets(true);          Field storedField = newField("stored", "stored", customType);     document.add(storedField);     Field termVectorField = newField("termVector", "termVector", customType1);     document.add(termVectorField);     for(int i=0;i<98;i++)       writer.addDocument(document);     writer.close();      IndexReader ir = IndexReader.open(dir, false);     assertEquals(98, ir.maxDoc());     assertEquals(98, ir.numDocs());     for(int i=0;i<98;i+=2)       ir.deleteDocument(i);     assertEquals(49, ir.numDocs());     ir.close();      writer = new IndexWriter(         dir,         newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer(random)).             setMergePolicy(newLogMergePolicy(3))     );     assertEquals(49, writer.numDocs());     writer.forceMergeDeletes();     writer.close();     ir = IndexReader.open(dir, true);     assertEquals(49, ir.maxDoc());     assertEquals(49, ir.numDocs());     ir.close();     dir.close();   }    // LUCENE-325: test forceMergeDeletes without waiting, when   // many adjacent merges are required   public void testForceMergeDeletes3() throws IOException {     Directory dir = newDirectory();     IndexWriter writer = new IndexWriter(         dir,         newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer(random)).             setMaxBufferedDocs(2).             setRAMBufferSizeMB(IndexWriterConfig.DISABLE_AUTO_FLUSH).             setMergePolicy(newLogMergePolicy(50))     );      FieldType customType = new FieldType();     customType.setStored(true);      FieldType customType1 = new FieldType(TextField.TYPE_UNSTORED);     customType1.setTokenized(false);     customType1.setStoreTermVectors(true);     customType1.setStoreTermVectorPositions(true);     customType1.setStoreTermVectorOffsets(true);          Document document = new Document();     Field storedField = newField("stored", "stored", customType);     document.add(storedField);     Field termVectorField = newField("termVector", "termVector", customType1);     document.add(termVectorField);     for(int i=0;i<98;i++)       writer.addDocument(document);     writer.close();      IndexReader ir = IndexReader.open(dir, false);     assertEquals(98, ir.maxDoc());     assertEquals(98, ir.numDocs());     for(int i=0;i<98;i+=2)       ir.deleteDocument(i);     assertEquals(49, ir.numDocs());     ir.close();      writer = new IndexWriter(         dir,         newIndexWriterConfig( TEST_VERSION_CURRENT, new MockAnalyzer(random)).             setMergePolicy(newLogMergePolicy(3))     );     writer.forceMergeDeletes(false);     writer.close();     ir = IndexReader.open(dir, true);     assertEquals(49, ir.maxDoc());     assertEquals(49, ir.numDocs());     ir.close();     dir.close();   }   */
comment|// Just intercepts all merges& verifies that we are never
comment|// merging a segment with>= 20 (maxMergeDocs) docs
DECL|class|MyMergeScheduler
specifier|private
class|class
name|MyMergeScheduler
extends|extends
name|MergeScheduler
block|{
annotation|@
name|Override
DECL|method|merge
specifier|synchronized
specifier|public
name|void
name|merge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|writer
operator|.
name|getNextMerge
argument_list|()
decl_stmt|;
if|if
condition|(
name|merge
operator|==
literal|null
condition|)
block|{
break|break;
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
name|merge
operator|.
name|segments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
assert|assert
name|merge
operator|.
name|segments
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|docCount
operator|<
literal|20
assert|;
block|}
name|writer
operator|.
name|merge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
block|}
comment|// LUCENE-1013
DECL|method|testSetMaxMergeDocs
specifier|public
name|void
name|testSetMaxMergeDocs
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
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|MyMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|conf
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeDocs
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setMergeFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"tvtest"
argument_list|,
literal|"a b c"
argument_list|,
name|customType
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
literal|177
condition|;
name|i
operator|++
control|)
name|iw
operator|.
name|addDocument
argument_list|(
name|document
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
end_class
end_unit
