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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|codecs
operator|.
name|Codec
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
name|document
operator|.
name|StoredField
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
name|store
operator|.
name|IOContext
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
name|StringHelper
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
name|util
operator|.
name|Version
import|;
end_import
begin_comment
comment|/**  * Abstract class to do basic tests for fis format.  * NOTE: This test focuses on the fis impl, nothing else.  * The [stretch] goal is for this test to be  * so thorough in testing a new fis format that if this  * test passes, then all Lucene/Solr tests should also pass.  Ie,  * if there is some bug in a given fis Format that this  * test fails to catch then this test needs to be improved! */
end_comment
begin_class
DECL|class|BaseFieldInfoFormatTestCase
specifier|public
specifier|abstract
class|class
name|BaseFieldInfoFormatTestCase
extends|extends
name|BaseIndexFileFormatTestCase
block|{
comment|/** Test field infos read/write with a single field */
DECL|method|testOneField
specifier|public
name|void
name|testOneField
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
name|Codec
name|codec
init|=
name|getCodec
argument_list|()
decl_stmt|;
name|SegmentInfo
name|segmentInfo
init|=
name|newSegmentInfo
argument_list|(
name|dir
argument_list|,
literal|"_123"
argument_list|)
decl_stmt|;
name|FieldInfos
operator|.
name|Builder
name|builder
init|=
operator|new
name|FieldInfos
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|FieldInfo
name|fi
init|=
name|builder
operator|.
name|getOrAdd
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|fi
operator|.
name|setIndexOptions
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
operator|.
name|indexOptions
argument_list|()
argument_list|)
expr_stmt|;
name|addAttributes
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|FieldInfos
name|infos
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
name|codec
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|write
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
literal|""
argument_list|,
name|infos
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|FieldInfos
name|infos2
init|=
name|codec
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|read
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
literal|""
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|infos2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|infos2
operator|.
name|fieldInfo
argument_list|(
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|infos2
operator|.
name|fieldInfo
argument_list|(
literal|"field"
argument_list|)
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|infos2
operator|.
name|fieldInfo
argument_list|(
literal|"field"
argument_list|)
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|infos2
operator|.
name|fieldInfo
argument_list|(
literal|"field"
argument_list|)
operator|.
name|omitsNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|infos2
operator|.
name|fieldInfo
argument_list|(
literal|"field"
argument_list|)
operator|.
name|hasPayloads
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|infos2
operator|.
name|fieldInfo
argument_list|(
literal|"field"
argument_list|)
operator|.
name|hasVectors
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// TODO: more tests
comment|/** Test field infos read/write with random fields, with different values. */
DECL|method|testRandom
specifier|public
name|void
name|testRandom
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
name|Codec
name|codec
init|=
name|getCodec
argument_list|()
decl_stmt|;
name|SegmentInfo
name|segmentInfo
init|=
name|newSegmentInfo
argument_list|(
name|dir
argument_list|,
literal|"_123"
argument_list|)
decl_stmt|;
comment|// generate a bunch of fields
name|int
name|numFields
init|=
name|atLeast
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|HashSet
argument_list|<>
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|fieldNames
operator|.
name|add
argument_list|(
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FieldInfos
operator|.
name|Builder
name|builder
init|=
operator|new
name|FieldInfos
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fieldNames
control|)
block|{
name|IndexableFieldType
name|fieldType
init|=
name|randomFieldType
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|FieldInfo
name|fi
init|=
name|builder
operator|.
name|getOrAdd
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|IndexOptions
name|indexOptions
init|=
name|fieldType
operator|.
name|indexOptions
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
name|fi
operator|.
name|setIndexOptions
argument_list|(
name|indexOptions
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldType
operator|.
name|omitNorms
argument_list|()
condition|)
block|{
name|fi
operator|.
name|setOmitsNorms
argument_list|()
expr_stmt|;
block|}
block|}
name|fi
operator|.
name|setDocValuesType
argument_list|(
name|fieldType
operator|.
name|docValuesType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldType
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|&&
name|fieldType
operator|.
name|indexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|fi
operator|.
name|setStorePayloads
argument_list|()
expr_stmt|;
block|}
block|}
name|addAttributes
argument_list|(
name|fi
argument_list|)
expr_stmt|;
block|}
name|FieldInfos
name|infos
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
name|codec
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|write
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
literal|""
argument_list|,
name|infos
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|FieldInfos
name|infos2
init|=
name|codec
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|read
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
literal|""
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|infos
argument_list|,
name|infos2
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|randomFieldType
specifier|private
specifier|final
name|IndexableFieldType
name|randomFieldType
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|IndexOptions
name|values
index|[]
init|=
name|IndexOptions
operator|.
name|values
argument_list|()
decl_stmt|;
name|type
operator|.
name|setIndexOptions
argument_list|(
name|values
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|values
operator|.
name|length
argument_list|)
index|]
argument_list|)
expr_stmt|;
name|type
operator|.
name|setOmitNorms
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|type
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|indexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|type
operator|.
name|setStoreTermVectorPositions
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|type
operator|.
name|setStoreTermVectorOffsets
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|storeTermVectorPositions
argument_list|()
condition|)
block|{
name|type
operator|.
name|setStoreTermVectorPayloads
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|DocValuesType
name|values
index|[]
init|=
name|getDocValuesTypes
argument_list|()
decl_stmt|;
name|type
operator|.
name|setDocValuesType
argument_list|(
name|values
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|values
operator|.
name|length
argument_list|)
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
comment|/**     * Hook to add any codec attributes to fieldinfo    * instances added in this test.    */
DECL|method|addAttributes
specifier|protected
name|void
name|addAttributes
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{   }
comment|/**     * Docvalues types to test.     * @deprecated only for Only available to ancient codecs can     * limit this to the subset of types they support.    */
annotation|@
name|Deprecated
DECL|method|getDocValuesTypes
specifier|protected
name|DocValuesType
index|[]
name|getDocValuesTypes
parameter_list|()
block|{
return|return
name|DocValuesType
operator|.
name|values
argument_list|()
return|;
block|}
comment|/** equality for entirety of fieldinfos */
DECL|method|assertEquals
specifier|protected
name|void
name|assertEquals
parameter_list|(
name|FieldInfos
name|expected
parameter_list|,
name|FieldInfos
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|,
name|actual
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldInfo
name|expectedField
range|:
name|expected
control|)
block|{
name|FieldInfo
name|actualField
init|=
name|actual
operator|.
name|fieldInfo
argument_list|(
name|expectedField
operator|.
name|number
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|actualField
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedField
argument_list|,
name|actualField
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** equality for two individual fieldinfo objects */
DECL|method|assertEquals
specifier|protected
name|void
name|assertEquals
parameter_list|(
name|FieldInfo
name|expected
parameter_list|,
name|FieldInfo
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|number
argument_list|,
name|actual
operator|.
name|number
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|name
argument_list|,
name|actual
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getDocValuesType
argument_list|()
argument_list|,
name|actual
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getIndexOptions
argument_list|()
argument_list|,
name|actual
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|hasNorms
argument_list|()
argument_list|,
name|actual
operator|.
name|hasNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|hasPayloads
argument_list|()
argument_list|,
name|actual
operator|.
name|hasPayloads
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|hasVectors
argument_list|()
argument_list|,
name|actual
operator|.
name|hasVectors
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|omitsNorms
argument_list|()
argument_list|,
name|actual
operator|.
name|omitsNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getDocValuesGen
argument_list|()
argument_list|,
name|actual
operator|.
name|getDocValuesGen
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a new fake segment */
DECL|method|newSegmentInfo
specifier|protected
specifier|static
name|SegmentInfo
name|newSegmentInfo
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SegmentInfo
argument_list|(
name|dir
argument_list|,
name|Version
operator|.
name|LATEST
argument_list|,
name|name
argument_list|,
literal|10000
argument_list|,
literal|false
argument_list|,
name|Codec
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|null
argument_list|,
name|StringHelper
operator|.
name|randomId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addRandomFields
specifier|protected
name|void
name|addRandomFields
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"foobar"
argument_list|,
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testRamBytesUsed
specifier|public
name|void
name|testRamBytesUsed
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
literal|"not applicable for this format"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit