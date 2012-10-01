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
name|util
operator|.
name|Collections
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
name|LinkedList
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
name|LuceneTestCase
operator|.
name|SuppressCodecs
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
comment|/**  * Simple test that adds numeric terms, where each term has the   * docFreq of its integer value, and checks that the docFreq is correct.   */
end_comment
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Direct"
block|,
literal|"Memory"
block|}
argument_list|)
comment|// at night this makes like 200k/300k docs and will make Direct's heart beat!
DECL|class|TestBagOfPostings
specifier|public
class|class
name|TestBagOfPostings
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
name|LinkedList
argument_list|<
name|String
argument_list|>
name|postings
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|numTerms
init|=
name|atLeast
argument_list|(
literal|300
argument_list|)
decl_stmt|;
name|int
name|maxTermsPerDoc
init|=
literal|10
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
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
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
name|i
condition|;
name|j
operator|++
control|)
block|{
name|postings
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|postings
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"bagofpostings"
argument_list|)
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
name|newTextField
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
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|postings
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|visited
init|=
operator|new
name|HashSet
argument_list|<
name|String
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
name|maxTermsPerDoc
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|postings
operator|.
name|isEmpty
argument_list|()
operator|||
name|visited
operator|.
name|contains
argument_list|(
name|postings
operator|.
name|peek
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
name|String
name|element
init|=
name|postings
operator|.
name|remove
argument_list|()
decl_stmt|;
name|text
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|text
operator|.
name|append
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|visited
operator|.
name|add
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
name|field
operator|.
name|setStringValue
argument_list|(
name|text
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ir
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|AtomicReader
name|air
init|=
name|ir
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
name|Terms
name|terms
init|=
name|air
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|int
name|value
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
comment|// don't really need to check more than this, as CheckIndex
comment|// will verify that docFreq == actual number of documents seen
comment|// from a docsAndPositionsEnum.
block|}
name|ir
operator|.
name|close
argument_list|()
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
