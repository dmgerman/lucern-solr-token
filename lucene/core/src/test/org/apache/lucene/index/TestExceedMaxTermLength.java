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
name|TestUtil
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
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_comment
comment|/**  * Tests that a useful exception is thrown when attempting to index a term that is   * too large  *  * @see IndexWriter#MAX_TERM_LENGTH  */
end_comment
begin_class
DECL|class|TestExceedMaxTermLength
specifier|public
class|class
name|TestExceedMaxTermLength
extends|extends
name|LuceneTestCase
block|{
DECL|field|minTestTermLength
specifier|private
specifier|final
specifier|static
name|int
name|minTestTermLength
init|=
name|IndexWriter
operator|.
name|MAX_TERM_LENGTH
operator|+
literal|1
decl_stmt|;
DECL|field|maxTestTermLegnth
specifier|private
specifier|final
specifier|static
name|int
name|maxTestTermLegnth
init|=
name|IndexWriter
operator|.
name|MAX_TERM_LENGTH
operator|*
literal|2
decl_stmt|;
DECL|field|dir
name|Directory
name|dir
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|createDir
specifier|public
name|void
name|createDir
parameter_list|()
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|destroyDir
specifier|public
name|void
name|destroyDir
parameter_list|()
throws|throws
name|IOException
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStored
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|ft
operator|.
name|freeze
argument_list|()
expr_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// totally ok short field value
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// problematic field
specifier|final
name|String
name|name
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
specifier|final
name|String
name|value
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
name|minTestTermLength
argument_list|,
name|maxTestTermLegnth
argument_list|)
decl_stmt|;
specifier|final
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|ft
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// totally ok short field value
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
try|try
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not get an exception from adding a monster term"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|maxLengthMsg
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|IndexWriter
operator|.
name|MAX_TERM_LENGTH
argument_list|)
decl_stmt|;
specifier|final
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"IllegalArgumentException didn't mention 'immense term': "
operator|+
name|msg
argument_list|,
name|msg
operator|.
name|contains
argument_list|(
literal|"immense term"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"IllegalArgumentException didn't mention max length ("
operator|+
name|maxLengthMsg
operator|+
literal|"): "
operator|+
name|msg
argument_list|,
name|msg
operator|.
name|contains
argument_list|(
name|maxLengthMsg
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"IllegalArgumentException didn't mention field name ("
operator|+
name|name
operator|+
literal|"): "
operator|+
name|msg
argument_list|,
name|msg
operator|.
name|contains
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|w
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
