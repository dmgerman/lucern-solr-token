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
name|util
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_class
DECL|class|TestMultiFields
specifier|public
class|class
name|TestMultiFields
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
name|newRandom
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
literal|2
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
condition|;
name|iter
operator|++
control|)
block|{
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|COMPOUND_FILES
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|docs
init|=
operator|new
name|HashMap
argument_list|<
name|BytesRef
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|deleted
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BytesRef
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|numDocs
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
literal|100
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|""
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
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|Field
name|id
init|=
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|""
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
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|boolean
name|onlyUniqueTerms
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|uniqueTerms
init|=
operator|new
name|HashSet
argument_list|<
name|BytesRef
argument_list|>
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|onlyUniqueTerms
operator|&&
name|r
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|terms
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// re-use existing term
name|BytesRef
name|term
init|=
name|terms
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|docs
operator|.
name|get
argument_list|(
name|term
argument_list|)
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|f
operator|.
name|setValue
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|r
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|BytesRef
name|term
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
name|docs
operator|.
name|containsKey
argument_list|(
name|term
argument_list|)
condition|)
block|{
name|docs
operator|.
name|put
argument_list|(
name|term
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|docs
operator|.
name|get
argument_list|(
name|term
argument_list|)
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|uniqueTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|f
operator|.
name|setValue
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|id
operator|.
name|setValue
argument_list|(
literal|""
operator|+
name|i
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
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|1
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|1
condition|)
block|{
name|int
name|delID
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|deleted
operator|.
name|add
argument_list|(
name|delID
argument_list|)
expr_stmt|;
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
name|delID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|List
argument_list|<
name|BytesRef
argument_list|>
name|termsList
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|(
name|uniqueTerms
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|termsList
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"UTF16 order:"
argument_list|)
expr_stmt|;
for|for
control|(
name|BytesRef
name|b
range|:
name|termsList
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|b
operator|.
name|utf8ToString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// nocommit
name|IndexReader
name|reader
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
comment|//System.out.println("TEST reader=" + reader);
name|Bits
name|delDocs
init|=
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|delDoc
range|:
name|deleted
control|)
block|{
name|assertTrue
argument_list|(
name|delDocs
operator|.
name|get
argument_list|(
name|delDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Terms
name|terms2
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
literal|"field"
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|term
init|=
name|terms
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
name|terms2
operator|.
name|docs
argument_list|(
name|delDocs
argument_list|,
name|term
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|docsEnum
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|docID
range|:
name|docs
operator|.
name|get
argument_list|(
name|term
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|deleted
operator|.
name|contains
argument_list|(
name|docID
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|docID
argument_list|,
name|docsEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|docsEnum
operator|.
name|NO_MORE_DOCS
argument_list|,
name|docsEnum
operator|.
name|nextDoc
argument_list|()
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
block|}
comment|/*   private void verify(IndexReader r, String term, List<Integer> expected) throws Exception {     DocsEnum docs = MultiFields.getTermDocsEnum(r,                                                 MultiFields.getDeletedDocs(r),                                                 "field",                                                 new BytesRef(term));      for(int docID : expected) {       assertEquals(docID, docs.nextDoc());     }     assertEquals(docs.NO_MORE_DOCS, docs.nextDoc());   }   */
DECL|method|testSeparateEnums
specifier|public
name|void
name|testSeparateEnums
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
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
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f"
argument_list|,
literal|"j"
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
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
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
name|DocsEnum
name|d1
init|=
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
literal|"f"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"j"
argument_list|)
argument_list|)
decl_stmt|;
name|DocsEnum
name|d2
init|=
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
literal|"f"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"j"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|d1
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|d2
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
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
