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
name|Collections
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
name|LineFileDocs
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
DECL|class|TestTermsEnum
specifier|public
class|class
name|TestTermsEnum
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
specifier|final
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|)
decl_stmt|;
specifier|final
name|Directory
name|d
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|d
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docCount
init|=
literal|0
init|;
name|docCount
operator|<
name|numDocs
condition|;
name|docCount
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|docs
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
specifier|final
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
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"body"
argument_list|)
operator|.
name|iterator
argument_list|()
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
name|terms
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
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
literal|"TEST: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" terms"
argument_list|)
expr_stmt|;
block|}
name|int
name|upto
init|=
operator|-
literal|1
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
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
specifier|final
name|boolean
name|isEnd
decl_stmt|;
if|if
condition|(
name|upto
operator|!=
operator|-
literal|1
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// next
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
literal|"TEST: iter next"
argument_list|)
expr_stmt|;
block|}
name|isEnd
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|==
literal|null
expr_stmt|;
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|isEnd
condition|)
block|{
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
literal|"  end"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|upto
argument_list|,
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|upto
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
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
literal|"  got term="
operator|+
name|termsEnum
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" expected="
operator|+
name|terms
operator|.
name|get
argument_list|(
name|upto
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|upto
operator|<
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|upto
argument_list|)
argument_list|,
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
specifier|final
name|BytesRef
name|target
decl_stmt|;
specifier|final
name|String
name|exists
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// likely fake term
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|target
operator|=
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|target
operator|=
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|exists
operator|=
literal|"likely not"
expr_stmt|;
block|}
else|else
block|{
comment|// real term
name|target
operator|=
name|terms
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|exists
operator|=
literal|"yes"
expr_stmt|;
block|}
name|upto
operator|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|terms
argument_list|,
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
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
literal|"TEST: iter seekCeil target="
operator|+
name|target
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" exists="
operator|+
name|exists
argument_list|)
expr_stmt|;
block|}
comment|// seekCeil
specifier|final
name|TermsEnum
operator|.
name|SeekStatus
name|status
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|target
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
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
literal|"  got "
operator|+
name|status
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|upto
operator|<
literal|0
condition|)
block|{
name|upto
operator|=
operator|-
operator|(
name|upto
operator|+
literal|1
operator|)
expr_stmt|;
if|if
condition|(
name|upto
operator|>=
name|terms
operator|.
name|size
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|upto
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|upto
argument_list|)
argument_list|,
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|upto
argument_list|)
argument_list|,
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
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
literal|"TEST: iter seekExact target="
operator|+
name|target
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" exists="
operator|+
name|exists
argument_list|)
expr_stmt|;
block|}
comment|// seekExact
specifier|final
name|boolean
name|result
init|=
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|target
argument_list|,
literal|false
argument_list|)
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
literal|"  got "
operator|+
name|result
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|upto
operator|<
literal|0
condition|)
block|{
name|assertFalse
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|upto
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|target
argument_list|,
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
