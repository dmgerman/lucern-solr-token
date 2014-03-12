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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|search
operator|.
name|DocIdSetIterator
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
name|store
operator|.
name|MockDirectoryWrapper
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
name|RAMDirectory
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
name|ArrayUtil
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
name|Bits
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
begin_class
DECL|class|TestPerSegmentDeletes
specifier|public
class|class
name|TestPerSegmentDeletes
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDeletes1
specifier|public
name|void
name|testDeletes1
parameter_list|()
throws|throws
name|Exception
block|{
comment|//IndexWriter.debug2 = System.out;
name|Directory
name|dir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
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
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|RangeMergePolicy
name|fsmp
init|=
operator|new
name|RangeMergePolicy
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|fsmp
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
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
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|5
condition|;
name|x
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|DocHelper
operator|.
name|createDocument
argument_list|(
name|x
argument_list|,
literal|"1"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println("numRamDocs(" + x + ")" + writer.numRamDocs());
block|}
comment|//System.out.println("commit1");
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|writer
operator|.
name|segmentInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|5
init|;
name|x
operator|<
literal|10
condition|;
name|x
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|DocHelper
operator|.
name|createDocument
argument_list|(
name|x
argument_list|,
literal|"2"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println("numRamDocs(" + x + ")" + writer.numRamDocs());
block|}
comment|//System.out.println("commit2");
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|writer
operator|.
name|segmentInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|10
init|;
name|x
operator|<
literal|15
condition|;
name|x
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|DocHelper
operator|.
name|createDocument
argument_list|(
name|x
argument_list|,
literal|"3"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println("numRamDocs(" + x + ")" + writer.numRamDocs());
block|}
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|)
argument_list|)
expr_stmt|;
comment|// flushing without applying deletes means
comment|// there will still be deletes in the segment infos
name|writer
operator|.
name|flush
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|.
name|bufferedUpdatesStream
operator|.
name|any
argument_list|()
argument_list|)
expr_stmt|;
comment|// get reader flushes pending deletes
comment|// so there should not be anymore
name|IndexReader
name|r1
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|bufferedUpdatesStream
operator|.
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// delete id:2 from the first segment
comment|// merge segments 0 and 1
comment|// which should apply the delete id:2
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fsmp
operator|=
operator|(
name|RangeMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
expr_stmt|;
name|fsmp
operator|.
name|doMerge
operator|=
literal|true
expr_stmt|;
name|fsmp
operator|.
name|start
operator|=
literal|0
expr_stmt|;
name|fsmp
operator|.
name|length
operator|=
literal|2
expr_stmt|;
name|writer
operator|.
name|maybeMerge
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|writer
operator|.
name|segmentInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// id:2 shouldn't exist anymore because
comment|// it's been applied in the merge and now it's gone
name|IndexReader
name|r2
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|int
index|[]
name|id2docs
init|=
name|toDocsArray
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|r2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|id2docs
operator|==
literal|null
argument_list|)
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
comment|/**     // added docs are in the ram buffer     for (int x = 15; x< 20; x++) {       writer.addDocument(TestIndexWriterReader.createDocument(x, "4", 2));       System.out.println("numRamDocs(" + x + ")" + writer.numRamDocs());     }     assertTrue(writer.numRamDocs()> 0);     // delete from the ram buffer     writer.deleteDocuments(new Term("id", Integer.toString(13)));      Term id3 = new Term("id", Integer.toString(3));      // delete from the 1st segment     writer.deleteDocuments(id3);      assertTrue(writer.numRamDocs()> 0);      //System.out     //    .println("segdels1:" + writer.docWriter.deletesToString());      //assertTrue(writer.docWriter.segmentDeletes.size()> 0);      // we cause a merge to happen     fsmp.doMerge = true;     fsmp.start = 0;     fsmp.length = 2;     System.out.println("maybeMerge "+writer.segmentInfos);      SegmentInfo info0 = writer.segmentInfos.info(0);     SegmentInfo info1 = writer.segmentInfos.info(1);      writer.maybeMerge();     System.out.println("maybeMerge after "+writer.segmentInfos);     // there should be docs in RAM     assertTrue(writer.numRamDocs()> 0);      // assert we've merged the 1 and 2 segments     // and still have a segment leftover == 2     assertEquals(2, writer.segmentInfos.size());     assertFalse(segThere(info0, writer.segmentInfos));     assertFalse(segThere(info1, writer.segmentInfos));      //System.out.println("segdels2:" + writer.docWriter.deletesToString());      //assertTrue(writer.docWriter.segmentDeletes.size()> 0);      IndexReader r = writer.getReader();     IndexReader r1 = r.getSequentialSubReaders()[0];     printDelDocs(r1.getLiveDocs());     int[] docs = toDocsArray(id3, null, r);     System.out.println("id3 docs:"+Arrays.toString(docs));     // there shouldn't be any docs for id:3     assertTrue(docs == null);     r.close();      part2(writer, fsmp);     **/
comment|// System.out.println("segdels2:"+writer.docWriter.segmentDeletes.toString());
comment|//System.out.println("close");
name|writer
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
comment|/**   static boolean hasPendingDeletes(SegmentInfos infos) {     for (SegmentInfo info : infos) {       if (info.deletes.any()) {         return true;       }     }     return false;   }   **/
DECL|method|part2
name|void
name|part2
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|RangeMergePolicy
name|fsmp
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|x
init|=
literal|20
init|;
name|x
operator|<
literal|25
condition|;
name|x
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|DocHelper
operator|.
name|createDocument
argument_list|(
name|x
argument_list|,
literal|"5"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println("numRamDocs(" + x + ")" + writer.numRamDocs());
block|}
name|writer
operator|.
name|flush
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|25
init|;
name|x
operator|<
literal|30
condition|;
name|x
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|DocHelper
operator|.
name|createDocument
argument_list|(
name|x
argument_list|,
literal|"5"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println("numRamDocs(" + x + ")" + writer.numRamDocs());
block|}
name|writer
operator|.
name|flush
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//System.out.println("infos3:"+writer.segmentInfos);
name|Term
name|delterm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|delterm
argument_list|)
expr_stmt|;
comment|//System.out.println("segdels3:" + writer.docWriter.deletesToString());
name|fsmp
operator|.
name|doMerge
operator|=
literal|true
expr_stmt|;
name|fsmp
operator|.
name|start
operator|=
literal|1
expr_stmt|;
name|fsmp
operator|.
name|length
operator|=
literal|2
expr_stmt|;
name|writer
operator|.
name|maybeMerge
argument_list|()
expr_stmt|;
comment|// deletes for info1, the newly created segment from the
comment|// merge should have no deletes because they were applied in
comment|// the merge
comment|//SegmentInfo info1 = writer.segmentInfos.info(1);
comment|//assertFalse(exists(info1, writer.docWriter.segmentDeletes));
comment|//System.out.println("infos4:"+writer.segmentInfos);
comment|//System.out.println("segdels4:" + writer.docWriter.deletesToString());
block|}
DECL|method|segThere
name|boolean
name|segThere
parameter_list|(
name|SegmentCommitInfo
name|info
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|)
block|{
for|for
control|(
name|SegmentCommitInfo
name|si
range|:
name|infos
control|)
block|{
if|if
condition|(
name|si
operator|.
name|info
operator|.
name|name
operator|.
name|equals
argument_list|(
name|info
operator|.
name|info
operator|.
name|name
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|printDelDocs
specifier|public
specifier|static
name|void
name|printDelDocs
parameter_list|(
name|Bits
name|bits
parameter_list|)
block|{
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
return|return;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|bits
operator|.
name|length
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|x
operator|+
literal|":"
operator|+
name|bits
operator|.
name|get
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toDocsArray
specifier|public
name|int
index|[]
name|toDocsArray
parameter_list|(
name|Term
name|term
parameter_list|,
name|Bits
name|bits
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|fields
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Terms
name|cterms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|)
decl_stmt|;
name|TermsEnum
name|ctermsEnum
init|=
name|cterms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctermsEnum
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|DocsEnum
name|docsEnum
init|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|ctermsEnum
argument_list|,
name|bits
argument_list|,
literal|null
argument_list|,
name|DocsEnum
operator|.
name|FLAG_NONE
argument_list|)
decl_stmt|;
return|return
name|toArray
argument_list|(
name|docsEnum
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|toArray
specifier|public
specifier|static
name|int
index|[]
name|toArray
parameter_list|(
name|DocsEnum
name|docsEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|docsEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|int
name|docID
init|=
name|docsEnum
operator|.
name|docID
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
return|return
name|ArrayUtil
operator|.
name|toIntArray
argument_list|(
name|docs
argument_list|)
return|;
block|}
DECL|class|RangeMergePolicy
specifier|public
class|class
name|RangeMergePolicy
extends|extends
name|MergePolicy
block|{
DECL|field|doMerge
name|boolean
name|doMerge
init|=
literal|false
decl_stmt|;
DECL|field|start
name|int
name|start
decl_stmt|;
DECL|field|length
name|int
name|length
decl_stmt|;
DECL|field|useCompoundFile
specifier|private
specifier|final
name|boolean
name|useCompoundFile
decl_stmt|;
DECL|method|RangeMergePolicy
specifier|private
name|RangeMergePolicy
parameter_list|(
name|boolean
name|useCompoundFile
parameter_list|)
block|{
name|this
operator|.
name|useCompoundFile
operator|=
name|useCompoundFile
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|findMerges
specifier|public
name|MergeSpecification
name|findMerges
parameter_list|(
name|MergeTrigger
name|mergeTrigger
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|IOException
block|{
name|MergeSpecification
name|ms
init|=
operator|new
name|MergeSpecification
argument_list|()
decl_stmt|;
if|if
condition|(
name|doMerge
condition|)
block|{
name|OneMerge
name|om
init|=
operator|new
name|OneMerge
argument_list|(
name|segmentInfos
operator|.
name|asList
argument_list|()
operator|.
name|subList
argument_list|(
name|start
argument_list|,
name|start
operator|+
name|length
argument_list|)
argument_list|)
decl_stmt|;
name|ms
operator|.
name|add
argument_list|(
name|om
argument_list|)
expr_stmt|;
name|doMerge
operator|=
literal|false
expr_stmt|;
return|return
name|ms
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|findForcedMerges
specifier|public
name|MergeSpecification
name|findForcedMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|int
name|maxSegmentCount
parameter_list|,
name|Map
argument_list|<
name|SegmentCommitInfo
argument_list|,
name|Boolean
argument_list|>
name|segmentsToMerge
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|findForcedDeletesMerges
specifier|public
name|MergeSpecification
name|findForcedDeletesMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|useCompoundFile
specifier|public
name|boolean
name|useCompoundFile
parameter_list|(
name|SegmentInfos
name|segments
parameter_list|,
name|SegmentCommitInfo
name|newSegment
parameter_list|)
block|{
return|return
name|useCompoundFile
return|;
block|}
block|}
block|}
end_class
end_unit
