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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|WhitespaceAnalyzer
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
name|search
operator|.
name|Similarity
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
name|FSDirectory
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
name|_TestUtil
import|;
end_import
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
name|*
import|;
end_import
begin_class
DECL|class|TestFieldsReader
specifier|public
class|class
name|TestFieldsReader
extends|extends
name|TestCase
block|{
DECL|field|dir
specifier|private
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|field|testDoc
specifier|private
name|Document
name|testDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
init|=
literal|null
decl_stmt|;
DECL|method|TestFieldsReader
specifier|public
name|TestFieldsReader
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|()
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|fieldInfos
operator|.
name|add
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|DocumentWriter
name|writer
init|=
operator|new
name|DocumentWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
name|Similarity
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
literal|"test"
argument_list|,
name|testDoc
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldInfos
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|FieldsReader
name|reader
init|=
operator|new
name|FieldsReader
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|reader
operator|.
name|doc
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_1_KEY
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Fieldable
name|field
init|=
name|doc
operator|.
name|getField
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|isTermVectorStored
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|isStoreOffsetWithTermVector
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|isStorePositionWithTermVector
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|getOmitNorms
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|field
operator|=
name|doc
operator|.
name|getField
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_3_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|isTermVectorStored
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|isStoreOffsetWithTermVector
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|isStorePositionWithTermVector
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|getOmitNorms
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testLazyFields
specifier|public
name|void
name|testLazyFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldInfos
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|FieldsReader
name|reader
init|=
operator|new
name|FieldsReader
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Set
name|loadFieldNames
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|loadFieldNames
operator|.
name|add
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_1_KEY
argument_list|)
expr_stmt|;
name|loadFieldNames
operator|.
name|add
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_UTF1_KEY
argument_list|)
expr_stmt|;
name|Set
name|lazyFieldNames
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|//new String[]{DocHelper.LARGE_LAZY_FIELD_KEY, DocHelper.LAZY_FIELD_KEY, DocHelper.LAZY_FIELD_BINARY_KEY};
name|lazyFieldNames
operator|.
name|add
argument_list|(
name|DocHelper
operator|.
name|LARGE_LAZY_FIELD_KEY
argument_list|)
expr_stmt|;
name|lazyFieldNames
operator|.
name|add
argument_list|(
name|DocHelper
operator|.
name|LAZY_FIELD_KEY
argument_list|)
expr_stmt|;
name|lazyFieldNames
operator|.
name|add
argument_list|(
name|DocHelper
operator|.
name|LAZY_FIELD_BINARY_KEY
argument_list|)
expr_stmt|;
name|lazyFieldNames
operator|.
name|add
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_UTF2_KEY
argument_list|)
expr_stmt|;
name|lazyFieldNames
operator|.
name|add
argument_list|(
name|DocHelper
operator|.
name|COMPRESSED_TEXT_FIELD_2_KEY
argument_list|)
expr_stmt|;
name|SetBasedFieldSelector
name|fieldSelector
init|=
operator|new
name|SetBasedFieldSelector
argument_list|(
name|loadFieldNames
argument_list|,
name|lazyFieldNames
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|reader
operator|.
name|doc
argument_list|(
literal|0
argument_list|,
name|fieldSelector
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"doc is null and it shouldn't be"
argument_list|,
name|doc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Fieldable
name|field
init|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|LAZY_FIELD_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"field is null and it shouldn't be"
argument_list|,
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"field is not lazy and it should be"
argument_list|,
name|field
operator|.
name|isLazy
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|field
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"value is null and it shouldn't be"
argument_list|,
name|value
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|value
operator|+
literal|" is not equal to "
operator|+
name|DocHelper
operator|.
name|LAZY_FIELD_TEXT
argument_list|,
name|value
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|LAZY_FIELD_TEXT
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|field
operator|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|COMPRESSED_TEXT_FIELD_2_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"field is null and it shouldn't be"
argument_list|,
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"field is not lazy and it should be"
argument_list|,
name|field
operator|.
name|isLazy
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|=
name|field
operator|.
name|stringValue
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"value is null and it shouldn't be"
argument_list|,
name|value
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|value
operator|+
literal|" is not equal to "
operator|+
name|DocHelper
operator|.
name|FIELD_2_COMPRESSED_TEXT
argument_list|,
name|value
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|FIELD_2_COMPRESSED_TEXT
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|field
operator|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_1_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"field is null and it shouldn't be"
argument_list|,
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Field is lazy and it should not be"
argument_list|,
name|field
operator|.
name|isLazy
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|field
operator|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_UTF1_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"field is null and it shouldn't be"
argument_list|,
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Field is lazy and it should not be"
argument_list|,
name|field
operator|.
name|isLazy
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|DocHelper
operator|.
name|FIELD_UTF1_TEXT
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|FIELD_UTF1_TEXT
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|field
operator|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_UTF2_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"field is null and it shouldn't be"
argument_list|,
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Field is lazy and it should not be"
argument_list|,
name|field
operator|.
name|isLazy
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|DocHelper
operator|.
name|FIELD_UTF2_TEXT
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|FIELD_UTF2_TEXT
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|field
operator|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|LAZY_FIELD_BINARY_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"field is null and it shouldn't be"
argument_list|,
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|field
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"bytes is null and it shouldn't be"
argument_list|,
name|bytes
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|""
argument_list|,
name|DocHelper
operator|.
name|LAZY_FIELD_BINARY_BYTES
operator|.
name|length
operator|==
name|bytes
operator|.
name|length
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"byte["
operator|+
name|i
operator|+
literal|"] is mismatched"
argument_list|,
name|bytes
index|[
name|i
index|]
operator|==
name|DocHelper
operator|.
name|LAZY_FIELD_BINARY_BYTES
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLoadFirst
specifier|public
name|void
name|testLoadFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldInfos
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|FieldsReader
name|reader
init|=
operator|new
name|FieldsReader
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|LoadFirstFieldSelector
name|fieldSelector
init|=
operator|new
name|LoadFirstFieldSelector
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|reader
operator|.
name|doc
argument_list|(
literal|0
argument_list|,
name|fieldSelector
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"doc is null and it shouldn't be"
argument_list|,
name|doc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|List
name|l
init|=
name|doc
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|l
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"field is null and it shouldn't be"
argument_list|,
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|String
name|sv
init|=
name|field
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sv is null and it shouldn't be"
argument_list|,
name|sv
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|count
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|count
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Not really a test per se, but we should have some way of assessing whether this is worthwhile.    *<p/>    * Must test using a File based directory    *    * @throws Exception    */
DECL|method|testLazyPerformance
specifier|public
name|void
name|testLazyPerformance
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|tmpIODir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|tmpIODir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"lazyDir"
operator|+
name|userName
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|FSDirectory
name|tmpDir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tmpDir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|DocumentWriter
name|writer
init|=
operator|new
name|DocumentWriter
argument_list|(
name|tmpDir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
name|Similarity
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
literal|"test"
argument_list|,
name|testDoc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldInfos
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|FieldsReader
name|reader
decl_stmt|;
name|long
name|lazyTime
init|=
literal|0
decl_stmt|;
name|long
name|regularTime
init|=
literal|0
decl_stmt|;
name|int
name|length
init|=
literal|50
decl_stmt|;
name|Set
name|lazyFieldNames
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|lazyFieldNames
operator|.
name|add
argument_list|(
name|DocHelper
operator|.
name|LARGE_LAZY_FIELD_KEY
argument_list|)
expr_stmt|;
name|SetBasedFieldSelector
name|fieldSelector
init|=
operator|new
name|SetBasedFieldSelector
argument_list|(
name|Collections
operator|.
name|EMPTY_SET
argument_list|,
name|lazyFieldNames
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|reader
operator|=
operator|new
name|FieldsReader
argument_list|(
name|tmpDir
argument_list|,
literal|"test"
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Document
name|doc
decl_stmt|;
name|doc
operator|=
name|reader
operator|.
name|doc
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//Load all of them
name|assertTrue
argument_list|(
literal|"doc is null and it shouldn't be"
argument_list|,
name|doc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Fieldable
name|field
init|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|LARGE_LAZY_FIELD_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"field is lazy"
argument_list|,
name|field
operator|.
name|isLazy
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
name|String
name|value
decl_stmt|;
name|long
name|start
decl_stmt|;
name|long
name|finish
decl_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
comment|//On my machine this was always 0ms.
name|value
operator|=
name|field
operator|.
name|stringValue
argument_list|()
expr_stmt|;
name|finish
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"value is null and it shouldn't be"
argument_list|,
name|value
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"field is null and it shouldn't be"
argument_list|,
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|regularTime
operator|+=
operator|(
name|finish
operator|-
name|start
operator|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|doc
operator|=
literal|null
expr_stmt|;
comment|//Hmmm, are we still in cache???
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|reader
operator|=
operator|new
name|FieldsReader
argument_list|(
name|tmpDir
argument_list|,
literal|"test"
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|doc
operator|=
name|reader
operator|.
name|doc
argument_list|(
literal|0
argument_list|,
name|fieldSelector
argument_list|)
expr_stmt|;
name|field
operator|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|LARGE_LAZY_FIELD_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"field is not lazy"
argument_list|,
name|field
operator|.
name|isLazy
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
comment|//On my machine this took around 50 - 70ms
name|value
operator|=
name|field
operator|.
name|stringValue
argument_list|()
expr_stmt|;
name|finish
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"value is null and it shouldn't be"
argument_list|,
name|value
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|lazyTime
operator|+=
operator|(
name|finish
operator|-
name|start
operator|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Average Non-lazy time (should be very close to zero): "
operator|+
name|regularTime
operator|/
name|length
operator|+
literal|" ms for "
operator|+
name|length
operator|+
literal|" reads"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Average Lazy Time (should be greater than zero): "
operator|+
name|lazyTime
operator|/
name|length
operator|+
literal|" ms for "
operator|+
name|length
operator|+
literal|" reads"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLoadSize
specifier|public
name|void
name|testLoadSize
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldsReader
name|reader
init|=
operator|new
name|FieldsReader
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|Document
name|doc
decl_stmt|;
name|doc
operator|=
name|reader
operator|.
name|doc
argument_list|(
literal|0
argument_list|,
operator|new
name|FieldSelector
argument_list|()
block|{
specifier|public
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_1_KEY
argument_list|)
operator|||
name|fieldName
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|COMPRESSED_TEXT_FIELD_2_KEY
argument_list|)
operator|||
name|fieldName
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|LAZY_FIELD_BINARY_KEY
argument_list|)
condition|)
return|return
name|FieldSelectorResult
operator|.
name|SIZE
return|;
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_3_KEY
argument_list|)
condition|)
return|return
name|FieldSelectorResult
operator|.
name|LOAD
return|;
else|else
return|return
name|FieldSelectorResult
operator|.
name|NO_LOAD
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Fieldable
name|f1
init|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_1_KEY
argument_list|)
decl_stmt|;
name|Fieldable
name|f3
init|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_3_KEY
argument_list|)
decl_stmt|;
name|Fieldable
name|fb
init|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|DocHelper
operator|.
name|LAZY_FIELD_BINARY_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f1
operator|.
name|isBinary
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|f3
operator|.
name|isBinary
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fb
operator|.
name|isBinary
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizeEquals
argument_list|(
literal|2
operator|*
name|DocHelper
operator|.
name|FIELD_1_TEXT
operator|.
name|length
argument_list|()
argument_list|,
name|f1
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocHelper
operator|.
name|FIELD_3_TEXT
argument_list|,
name|f3
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizeEquals
argument_list|(
name|DocHelper
operator|.
name|LAZY_FIELD_BINARY_BYTES
operator|.
name|length
argument_list|,
name|fb
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertSizeEquals
specifier|private
name|void
name|assertSizeEquals
parameter_list|(
name|int
name|size
parameter_list|,
name|byte
index|[]
name|sizebytes
parameter_list|)
block|{
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|size
operator|>>>
literal|24
argument_list|)
argument_list|,
name|sizebytes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|size
operator|>>>
literal|16
argument_list|)
argument_list|,
name|sizebytes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|size
operator|>>>
literal|8
argument_list|)
argument_list|,
name|sizebytes
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|size
argument_list|,
name|sizebytes
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
