begin_unit
begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
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
name|SortedSetDocValuesField
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
name|AtomicReader
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|SortedSetFieldSource
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
begin_class
DECL|class|TestSortedSetFieldSource
specifier|public
class|class
name|TestSortedSetFieldSource
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
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
literal|null
argument_list|)
argument_list|)
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
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"value"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"baz"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|"2"
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
name|SortedSetDocValuesField
argument_list|(
literal|"value"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"value"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
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
name|DirectoryReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|AtomicReader
name|ar
init|=
name|getOnlySegmentReader
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|ValueSource
name|vs
init|=
operator|new
name|SortedSetFieldSource
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
name|FunctionValues
name|values
init|=
name|vs
operator|.
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|ar
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"baz"
argument_list|,
name|values
operator|.
name|strVal
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|values
operator|.
name|strVal
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
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
