begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|File
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
name|index
operator|.
name|DirectoryReader
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
name|IndexWriter
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
name|search
operator|.
name|IndexSearcher
import|;
end_import
begin_class
DECL|class|TestWindowsMMap
specifier|public
class|class
name|TestWindowsMMap
extends|extends
name|LuceneTestCase
block|{
DECL|field|alphabet
specifier|private
specifier|final
specifier|static
name|String
name|alphabet
init|=
literal|"abcdefghijklmnopqrstuvwzyz"
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|randomToken
specifier|private
name|String
name|randomToken
parameter_list|()
block|{
name|int
name|tl
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|cx
init|=
literal|0
init|;
name|cx
operator|<
name|tl
condition|;
name|cx
operator|++
control|)
block|{
name|int
name|c
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|alphabet
operator|.
name|substring
argument_list|(
name|c
argument_list|,
name|c
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|randomField
specifier|private
name|String
name|randomField
parameter_list|()
block|{
name|int
name|fl
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|StringBuilder
name|fb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|fx
init|=
literal|0
init|;
name|fx
operator|<
name|fl
condition|;
name|fx
operator|++
control|)
block|{
name|fb
operator|.
name|append
argument_list|(
name|randomToken
argument_list|()
argument_list|)
expr_stmt|;
name|fb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
return|return
name|fb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|testMmapIndex
specifier|public
name|void
name|testMmapIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// sometimes the directory is not cleaned by rmDir, because on Windows it
comment|// may take some time until the files are finally dereferenced. So clean the
comment|// directory up front, or otherwise new IndexWriter will fail.
name|File
name|dirPath
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"testLuceneMmap"
argument_list|)
decl_stmt|;
name|rmDir
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
name|MMapDirectory
name|dir
init|=
operator|new
name|MMapDirectory
argument_list|(
name|dirPath
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// plan to add a set of useful stopwords, consider changing some of the
comment|// interior filters.
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO: something about lock timeouts and leftover locks.
name|IndexWriter
name|writer
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
name|analyzer
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|dx
init|=
literal|0
init|;
name|dx
operator|<
name|num
condition|;
name|dx
operator|++
control|)
block|{
name|String
name|f
init|=
name|randomField
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"data"
argument_list|,
name|f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|rmDir
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
block|}
DECL|method|rmDir
specifier|private
name|void
name|rmDir
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|File
name|file
range|:
name|dir
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
