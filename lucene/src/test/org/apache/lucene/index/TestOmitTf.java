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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|TermContext
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
name|index
operator|.
name|FieldInfo
operator|.
name|IndexOptions
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
operator|.
name|AtomicReaderContext
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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
begin_class
DECL|class|TestOmitTf
specifier|public
class|class
name|TestOmitTf
extends|extends
name|LuceneTestCase
block|{
DECL|class|SimpleSimilarityProvider
specifier|public
specifier|static
class|class
name|SimpleSimilarityProvider
implements|implements
name|SimilarityProvider
block|{
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
DECL|method|get
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|TFIDFSimilarity
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|byte
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|encodeNormValue
argument_list|(
name|state
operator|.
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
return|return
name|freq
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|2.0f
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|idf
parameter_list|(
name|int
name|docFreq
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|idfExplain
parameter_list|(
name|TermContext
index|[]
name|terms
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Explanation
argument_list|(
literal|1.0f
argument_list|,
literal|"Inexplicable"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|scorePayload
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
block|}
return|;
block|}
block|}
comment|// Tests whether the DocumentWriter correctly enable the
comment|// omitTermFreqAndPositions bit in the FieldInfo
DECL|method|testOmitTermFreqAndPositions
specifier|public
name|void
name|testOmitTermFreqAndPositions
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|ram
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ram
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// this field will have Tf
name|Field
name|f1
init|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has term freqs"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
comment|// this field will NOT have Tf
name|Field
name|f2
init|=
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"This field has NO Tf in all docs"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|f2
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// now we add another document which has term freq for field f2 and not for f1 and verify if the SegmentMerger
comment|// keep things constant
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
comment|// Reverse
name|f1
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|f2
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// force merge
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|getOnlySegmentReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|ram
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfos
name|fi
init|=
name|reader
operator|.
name|fieldInfos
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"OmitTermFreqAndPositions field bit should be set."
argument_list|,
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|indexOptions
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OmitTermFreqAndPositions field bit should be set."
argument_list|,
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f2"
argument_list|)
operator|.
name|indexOptions
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ram
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Tests whether merging of docs that have different
comment|// omitTermFreqAndPositions for the same field works
DECL|method|testMixedMerge
specifier|public
name|void
name|testMixedMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|ram
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ram
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|3
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
name|setInfoStream
argument_list|(
name|VERBOSE
condition|?
name|System
operator|.
name|out
else|:
literal|null
argument_list|)
expr_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// this field will have Tf
name|Field
name|f1
init|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has term freqs"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
comment|// this field will NOT have Tf
name|Field
name|f2
init|=
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"This field has NO Tf in all docs"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|f2
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
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
literal|30
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// now we add another document which has term freq for field f2 and not for f1 and verify if the SegmentMerger
comment|// keep things constant
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
comment|// Reverese
name|f1
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|f2
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
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
literal|30
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// force merge
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|getOnlySegmentReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|ram
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfos
name|fi
init|=
name|reader
operator|.
name|fieldInfos
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"OmitTermFreqAndPositions field bit should be set."
argument_list|,
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|indexOptions
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OmitTermFreqAndPositions field bit should be set."
argument_list|,
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f2"
argument_list|)
operator|.
name|indexOptions
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ram
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Make sure first adding docs that do not omitTermFreqAndPositions for
comment|// field X, then adding docs that do omitTermFreqAndPositions for that same
comment|// field,
DECL|method|testMixedRAM
specifier|public
name|void
name|testMixedRAM
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|ram
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ram
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
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
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// this field will have Tf
name|Field
name|f1
init|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has term freqs"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
comment|// this field will NOT have Tf
name|Field
name|f2
init|=
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"This field has NO Tf in all docs"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
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
literal|5
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|f2
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
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
literal|20
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// force merge
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|getOnlySegmentReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|ram
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfos
name|fi
init|=
name|reader
operator|.
name|fieldInfos
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"OmitTermFreqAndPositions field bit should not be set."
argument_list|,
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|indexOptions
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OmitTermFreqAndPositions field bit should be set."
argument_list|,
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f2"
argument_list|)
operator|.
name|indexOptions
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ram
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertNoPrx
specifier|private
name|void
name|assertNoPrx
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|Throwable
block|{
specifier|final
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|listAll
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|files
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
literal|".prx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|files
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
literal|".pos"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Verifies no *.prx exists when all fields omit term freq:
DECL|method|testNoPrxFile
specifier|public
name|void
name|testNoPrxFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|ram
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ram
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|3
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMergeFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|f1
init|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has no term freqs"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|f1
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
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
literal|30
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNoPrx
argument_list|(
name|ram
argument_list|)
expr_stmt|;
comment|// now add some documents with positions, and check there is no prox after optimization
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|f1
operator|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has positions"
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
name|ANALYZED
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
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
literal|30
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// force merge
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNoPrx
argument_list|(
name|ram
argument_list|)
expr_stmt|;
name|ram
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Test scores with one field with Term Freqs and one without, otherwise with equal content
DECL|method|testBasic
specifier|public
name|void
name|testBasic
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
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
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
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setSimilarityProvider
argument_list|(
operator|new
name|SimpleSimilarityProvider
argument_list|()
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
name|setInfoStream
argument_list|(
name|VERBOSE
condition|?
name|System
operator|.
name|out
else|:
literal|null
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|265
argument_list|)
decl_stmt|;
name|String
name|term
init|=
literal|"term"
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
literal|30
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
name|sb
operator|.
name|append
argument_list|(
name|term
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Field
name|noTf
init|=
name|newField
argument_list|(
literal|"noTf"
argument_list|,
name|content
operator|+
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|?
literal|""
else|:
literal|" notf"
operator|)
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|noTf
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|noTf
argument_list|)
expr_stmt|;
name|Field
name|tf
init|=
name|newField
argument_list|(
literal|"tf"
argument_list|,
name|content
operator|+
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|?
literal|" tf"
else|:
literal|""
operator|)
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|tf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|//System.out.println(d);
block|}
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|/*      * Verify the index      */
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|setSimilarityProvider
argument_list|(
operator|new
name|SimpleSimilarityProvider
argument_list|()
argument_list|)
expr_stmt|;
name|Term
name|a
init|=
operator|new
name|Term
argument_list|(
literal|"noTf"
argument_list|,
name|term
argument_list|)
decl_stmt|;
name|Term
name|b
init|=
operator|new
name|Term
argument_list|(
literal|"tf"
argument_list|,
name|term
argument_list|)
decl_stmt|;
name|Term
name|c
init|=
operator|new
name|Term
argument_list|(
literal|"noTf"
argument_list|,
literal|"notf"
argument_list|)
decl_stmt|;
name|Term
name|d
init|=
operator|new
name|Term
argument_list|(
literal|"tf"
argument_list|,
literal|"tf"
argument_list|)
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|TermQuery
name|q3
init|=
operator|new
name|TermQuery
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|TermQuery
name|q4
init|=
operator|new
name|TermQuery
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|pq
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
try|try
block|{
name|searcher
operator|.
name|search
argument_list|(
name|pq
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
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
name|searcher
operator|.
name|search
argument_list|(
name|q1
argument_list|,
operator|new
name|CountingHitCollector
argument_list|()
block|{
specifier|private
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("Q1: Doc=" + doc + " score=" + score);
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|score
operator|==
literal|1.0f
argument_list|)
expr_stmt|;
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|//System.out.println(CountingHitCollector.getCount());
name|searcher
operator|.
name|search
argument_list|(
name|q2
argument_list|,
operator|new
name|CountingHitCollector
argument_list|()
block|{
specifier|private
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("Q2: Doc=" + doc + " score=" + score);
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.0f
operator|+
name|doc
argument_list|,
name|score
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|//System.out.println(CountingHitCollector.getCount());
name|searcher
operator|.
name|search
argument_list|(
name|q3
argument_list|,
operator|new
name|CountingHitCollector
argument_list|()
block|{
specifier|private
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("Q1: Doc=" + doc + " score=" + score);
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|score
operator|==
literal|1.0f
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|%
literal|2
operator|==
literal|0
argument_list|)
expr_stmt|;
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|//System.out.println(CountingHitCollector.getCount());
name|searcher
operator|.
name|search
argument_list|(
name|q4
argument_list|,
operator|new
name|CountingHitCollector
argument_list|()
block|{
specifier|private
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
comment|//System.out.println("Q1: Doc=" + doc + " score=" + score);
name|assertTrue
argument_list|(
name|score
operator|==
literal|1.0f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|%
literal|2
operator|==
literal|0
argument_list|)
expr_stmt|;
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|//System.out.println(CountingHitCollector.getCount());
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|q4
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|bq
argument_list|,
operator|new
name|CountingHitCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("BQ: Doc=" + doc + " score=" + score);
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|CountingHitCollector
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
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
DECL|class|CountingHitCollector
specifier|public
specifier|static
class|class
name|CountingHitCollector
extends|extends
name|Collector
block|{
DECL|field|count
specifier|static
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|sum
specifier|static
name|int
name|sum
init|=
literal|0
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|CountingHitCollector
name|CountingHitCollector
parameter_list|()
block|{
name|count
operator|=
literal|0
expr_stmt|;
name|sum
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|count
operator|++
expr_stmt|;
name|sum
operator|+=
name|doc
operator|+
name|docBase
expr_stmt|;
comment|// use it to avoid any possibility of being optimized away
block|}
DECL|method|getCount
specifier|public
specifier|static
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|getSum
specifier|public
specifier|static
name|int
name|getSum
parameter_list|()
block|{
return|return
name|sum
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_class
end_unit
