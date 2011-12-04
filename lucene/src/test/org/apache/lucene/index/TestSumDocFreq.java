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
begin_comment
comment|/**  * Tests {@link Terms#getSumDocFreq()}  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TestSumDocFreq
specifier|public
class|class
name|TestSumDocFreq
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSumDocFreq
specifier|public
name|void
name|testSumDocFreq
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field1
init|=
name|newField
argument_list|(
literal|"foo"
argument_list|,
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|Field
name|field2
init|=
name|newField
argument_list|(
literal|"bar"
argument_list|,
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field2
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch1
init|=
operator|(
name|char
operator|)
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
decl_stmt|;
name|char
name|ch2
init|=
operator|(
name|char
operator|)
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
decl_stmt|;
name|field1
operator|.
name|setValue
argument_list|(
literal|""
operator|+
name|ch1
operator|+
literal|" "
operator|+
name|ch2
argument_list|)
expr_stmt|;
name|ch1
operator|=
operator|(
name|char
operator|)
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
expr_stmt|;
name|ch2
operator|=
operator|(
name|char
operator|)
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
expr_stmt|;
name|field2
operator|.
name|setValue
argument_list|(
literal|""
operator|+
name|ch1
operator|+
literal|" "
operator|+
name|ch2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|ir
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
name|assertSumDocFreq
argument_list|(
name|ir
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|/* nocommit: fix this to use IW to delete documents     ir = IndexReader.open(dir, false);     int numDeletions = atLeast(20);     for (int i = 0; i< numDeletions; i++) {       ir.deleteDocument(random.nextInt(ir.maxDoc()));     }     ir.close();          IndexWriter w = new IndexWriter(dir, newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer(random)));     w.forceMerge(1);     w.close();          ir = IndexReader.open(dir, true);     assertSumDocFreq(ir);     ir.close();     */
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertSumDocFreq
specifier|private
name|void
name|assertSumDocFreq
parameter_list|(
name|IndexReader
name|ir
parameter_list|)
throws|throws
name|Exception
block|{
comment|// compute sumDocFreq across all fields
name|Fields
name|fields
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FieldsEnum
name|fieldEnum
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|f
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|fieldEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|long
name|sumDocFreq
init|=
name|terms
operator|.
name|getSumDocFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|sumDocFreq
operator|==
operator|-
literal|1
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
literal|"skipping field: "
operator|+
name|f
operator|+
literal|", codec does not support sumDocFreq"
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
name|long
name|computedSumDocFreq
init|=
literal|0
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
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|computedSumDocFreq
operator|+=
name|termsEnum
operator|.
name|docFreq
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|computedSumDocFreq
argument_list|,
name|sumDocFreq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
