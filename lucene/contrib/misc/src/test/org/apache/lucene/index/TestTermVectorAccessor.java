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
name|MockRAMDirectory
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
name|Collections
import|;
end_import
begin_comment
comment|/*  *  Licensed under the Apache License, Version 2.0 (the "License");  *  you may not use this file except in compliance with the License.  *  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  *  */
end_comment
begin_class
DECL|class|TestTermVectorAccessor
specifier|public
class|class
name|TestTermVectorAccessor
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
operator|new
name|MockRAMDirectory
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
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
decl_stmt|;
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
name|Field
argument_list|(
literal|"a"
argument_list|,
literal|"a b a c a d a e a f a g a h a"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"b"
argument_list|,
literal|"a b c b d b e b f b g b h b"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"c"
argument_list|,
literal|"a c b c d c e c f c g c h c"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
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
name|Field
argument_list|(
literal|"a"
argument_list|,
literal|"a b a c a d a e a f a g a h a"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"b"
argument_list|,
literal|"a b c b d b e b f b g b h b"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"c"
argument_list|,
literal|"a c b c d c e c f c g c h c"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS
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
name|Field
argument_list|(
literal|"a"
argument_list|,
literal|"a b a c a d a e a f a g a h a"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"b"
argument_list|,
literal|"a b c b d b e b f b g b h b"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"c"
argument_list|,
literal|"a c b c d c e c f c g c h c"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|YES
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
name|Field
argument_list|(
literal|"a"
argument_list|,
literal|"a b a c a d a e a f a g a h a"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"b"
argument_list|,
literal|"a b c b d b e b f b g b h b"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"c"
argument_list|,
literal|"a c b c d c e c f c g c h c"
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
argument_list|,
name|Field
operator|.
name|TermVector
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
name|Field
argument_list|(
literal|"a"
argument_list|,
literal|"a b a c a d a e a f a g a h a"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"b"
argument_list|,
literal|"a b c b d b e b f b g b h b"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"c"
argument_list|,
literal|"a c b c d c e c f c g c h c"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|YES
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
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TermVectorAccessor
name|accessor
init|=
operator|new
name|TermVectorAccessor
argument_list|()
decl_stmt|;
name|ParallelArrayTermVectorMapper
name|mapper
decl_stmt|;
name|TermFreqVector
name|tfv
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
name|ir
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|mapper
operator|=
operator|new
name|ParallelArrayTermVectorMapper
argument_list|()
expr_stmt|;
name|accessor
operator|.
name|accept
argument_list|(
name|ir
argument_list|,
name|i
argument_list|,
literal|"a"
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
name|tfv
operator|=
name|mapper
operator|.
name|materializeVector
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
literal|"a"
argument_list|,
name|tfv
operator|.
name|getTerms
argument_list|()
index|[
literal|0
index|]
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
literal|8
argument_list|,
name|tfv
operator|.
name|getTermFrequencies
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|mapper
operator|=
operator|new
name|ParallelArrayTermVectorMapper
argument_list|()
expr_stmt|;
name|accessor
operator|.
name|accept
argument_list|(
name|ir
argument_list|,
name|i
argument_list|,
literal|"b"
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
name|tfv
operator|=
name|mapper
operator|.
name|materializeVector
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
literal|8
argument_list|,
name|tfv
operator|.
name|getTermFrequencies
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
literal|"b"
argument_list|,
name|tfv
operator|.
name|getTerms
argument_list|()
index|[
literal|1
index|]
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
literal|7
argument_list|,
name|tfv
operator|.
name|getTermFrequencies
argument_list|()
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|mapper
operator|=
operator|new
name|ParallelArrayTermVectorMapper
argument_list|()
expr_stmt|;
name|accessor
operator|.
name|accept
argument_list|(
name|ir
argument_list|,
name|i
argument_list|,
literal|"c"
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
name|tfv
operator|=
name|mapper
operator|.
name|materializeVector
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
literal|8
argument_list|,
name|tfv
operator|.
name|getTermFrequencies
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
literal|"c"
argument_list|,
name|tfv
operator|.
name|getTerms
argument_list|()
index|[
literal|2
index|]
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
literal|7
argument_list|,
name|tfv
operator|.
name|getTermFrequencies
argument_list|()
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|mapper
operator|=
operator|new
name|ParallelArrayTermVectorMapper
argument_list|()
expr_stmt|;
name|accessor
operator|.
name|accept
argument_list|(
name|ir
argument_list|,
name|i
argument_list|,
literal|"q"
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
name|tfv
operator|=
name|mapper
operator|.
name|materializeVector
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"doc "
operator|+
name|i
argument_list|,
name|tfv
argument_list|)
expr_stmt|;
block|}
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
block|}
block|}
end_class
end_unit
