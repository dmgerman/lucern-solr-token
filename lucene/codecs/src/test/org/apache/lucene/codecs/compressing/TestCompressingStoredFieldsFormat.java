begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|codecs
operator|.
name|lucene41
operator|.
name|Lucene41Codec
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
name|DoubleField
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
name|Field
operator|.
name|Store
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
name|FloatField
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
name|IntField
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
name|LongField
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
name|StringField
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
name|RandomIndexWriter
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
name|StorableField
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
name|StoredDocument
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
name|Term
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
name|NumericRangeQuery
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
name|Query
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
name|TermQuery
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
name|TopDocs
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
name|MockDirectoryWrapper
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
name|MockDirectoryWrapper
operator|.
name|Throttling
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
name|IOUtils
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
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
import|;
end_import
begin_class
DECL|class|TestCompressingStoredFieldsFormat
specifier|public
class|class
name|TestCompressingStoredFieldsFormat
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|iwConf
name|IndexWriterConfig
name|iwConf
decl_stmt|;
DECL|field|iw
specifier|private
name|RandomIndexWriter
name|iw
decl_stmt|;
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|iwConf
operator|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iwConf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|iwConf
operator|.
name|setCodec
argument_list|(
name|CompressingCodec
operator|.
name|randomInstance
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwConf
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|iw
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|iw
operator|=
literal|null
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|randomByteArray
specifier|private
name|byte
index|[]
name|randomByteArray
parameter_list|(
name|int
name|length
parameter_list|,
name|int
name|max
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|length
index|]
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
operator|++
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|testWriteReadMerge
specifier|public
name|void
name|testWriteReadMerge
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|docCount
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
index|[]
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|docCount
index|]
index|[]
index|[]
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
name|docCount
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|fieldCount
init|=
name|rarely
argument_list|()
condition|?
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|)
else|:
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|data
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|fieldCount
index|]
index|[]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|fieldCount
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|int
name|length
init|=
name|rarely
argument_list|()
condition|?
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|rarely
argument_list|()
condition|?
literal|256
else|:
literal|2
decl_stmt|;
name|data
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|randomByteArray
argument_list|(
name|length
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|(
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|type
operator|.
name|setIndexed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|type
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|IntField
name|id
init|=
operator|new
name|IntField
argument_list|(
literal|"id"
argument_list|,
literal|0
argument_list|,
name|Store
operator|.
name|YES
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
name|data
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
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
name|id
argument_list|)
expr_stmt|;
name|id
operator|.
name|setIntValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|data
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
literal|"bytes"
operator|+
name|j
argument_list|,
name|data
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|&&
operator|(
name|i
operator|%
operator|(
name|data
operator|.
name|length
operator|/
literal|10
operator|)
operator|==
literal|0
operator|)
condition|)
block|{
name|iw
operator|.
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// switch codecs
if|if
condition|(
name|iwConf
operator|.
name|getCodec
argument_list|()
operator|instanceof
name|Lucene41Codec
condition|)
block|{
name|iwConf
operator|.
name|setCodec
argument_list|(
name|CompressingCodec
operator|.
name|randomInstance
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|iwConf
operator|.
name|setCodec
argument_list|(
operator|new
name|Lucene41Codec
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|iw
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwConf
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|min
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|data
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|min
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|iw
operator|.
name|deleteDocuments
argument_list|(
name|NumericRangeQuery
operator|.
name|newIntRange
argument_list|(
literal|"id"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|forceMerge
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// force merges with deletions
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
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
name|assertTrue
argument_list|(
name|ir
operator|.
name|numDocs
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
literal|0
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
operator|++
name|i
control|)
block|{
specifier|final
name|StoredDocument
name|doc
init|=
name|ir
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
operator|++
name|numDocs
expr_stmt|;
specifier|final
name|int
name|docId
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|data
index|[
name|docId
index|]
operator|.
name|length
operator|+
literal|1
argument_list|,
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|data
index|[
name|docId
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|byte
index|[]
name|arr
init|=
name|data
index|[
name|docId
index|]
index|[
name|j
index|]
decl_stmt|;
specifier|final
name|BytesRef
name|arr2Ref
init|=
name|doc
operator|.
name|getBinaryValue
argument_list|(
literal|"bytes"
operator|+
name|j
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|arr2
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|arr2Ref
operator|.
name|bytes
argument_list|,
name|arr2Ref
operator|.
name|offset
argument_list|,
name|arr2Ref
operator|.
name|offset
operator|+
name|arr2Ref
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|arr
argument_list|,
name|arr2
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|ir
operator|.
name|numDocs
argument_list|()
operator|<=
name|numDocs
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testReadSkip
specifier|public
name|void
name|testReadSkip
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|freeze
argument_list|()
expr_stmt|;
specifier|final
name|String
name|string
init|=
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|50
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
name|string
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|l
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|42
argument_list|)
else|:
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|i
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|42
argument_list|)
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
specifier|final
name|float
name|f
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
decl_stmt|;
specifier|final
name|double
name|d
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Field
argument_list|>
name|fields
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Field
argument_list|(
literal|"bytes"
argument_list|,
name|bytes
argument_list|,
name|ft
argument_list|)
argument_list|,
operator|new
name|Field
argument_list|(
literal|"string"
argument_list|,
name|string
argument_list|,
name|ft
argument_list|)
argument_list|,
operator|new
name|LongField
argument_list|(
literal|"long"
argument_list|,
name|l
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|,
operator|new
name|IntField
argument_list|(
literal|"int"
argument_list|,
name|i
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|,
operator|new
name|FloatField
argument_list|(
literal|"float"
argument_list|,
name|f
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|,
operator|new
name|DoubleField
argument_list|(
literal|"double"
argument_list|,
name|d
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
operator|++
name|k
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|fld
range|:
name|fields
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|fld
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|int
name|docID
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|Field
name|fld
range|:
name|fields
control|)
block|{
name|String
name|fldName
init|=
name|fld
operator|.
name|name
argument_list|()
decl_stmt|;
specifier|final
name|StoredDocument
name|sDoc
init|=
name|reader
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|fldName
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|StorableField
name|sField
init|=
name|sDoc
operator|.
name|getField
argument_list|(
name|fldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|Field
operator|.
name|class
operator|.
name|equals
argument_list|(
name|fld
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|fld
operator|.
name|binaryValue
argument_list|()
argument_list|,
name|sField
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fld
operator|.
name|stringValue
argument_list|()
argument_list|,
name|sField
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|fld
operator|.
name|numericValue
argument_list|()
argument_list|,
name|sField
operator|.
name|numericValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testEmptyDocs
specifier|public
name|void
name|testEmptyDocs
parameter_list|()
throws|throws
name|IOException
block|{
comment|// make sure that the fact that documents might be empty is not a problem
specifier|final
name|Document
name|emptyDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|1
else|:
name|atLeast
argument_list|(
literal|1000
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|emptyDoc
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryReader
name|rd
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|StoredDocument
name|doc
init|=
name|rd
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testConcurrentReads
specifier|public
name|void
name|testConcurrentReads
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make sure the readers are properly cloned
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|Field
name|field
init|=
operator|new
name|StringField
argument_list|(
literal|"fld"
argument_list|,
literal|""
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|1000
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|field
operator|.
name|setStringValue
argument_list|(
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryReader
name|rd
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|rd
argument_list|)
decl_stmt|;
specifier|final
name|int
name|concurrentReads
init|=
name|atLeast
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|int
name|readsPerThread
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Thread
argument_list|>
name|readThreads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|ex
init|=
operator|new
name|AtomicReference
argument_list|<
name|Exception
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
name|concurrentReads
condition|;
operator|++
name|i
control|)
block|{
name|readThreads
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
name|int
index|[]
name|queries
decl_stmt|;
block|{
name|queries
operator|=
operator|new
name|int
index|[
name|readsPerThread
index|]
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
name|queries
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|queries
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|q
range|:
name|queries
control|)
block|{
specifier|final
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"fld"
argument_list|,
literal|""
operator|+
name|q
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|topDocs
operator|.
name|totalHits
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Expected 1 hit, got "
operator|+
name|topDocs
operator|.
name|totalHits
argument_list|)
throw|;
block|}
specifier|final
name|StoredDocument
name|sdoc
init|=
name|rd
operator|.
name|document
argument_list|(
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|sdoc
operator|==
literal|null
operator|||
name|sdoc
operator|.
name|get
argument_list|(
literal|"fld"
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Could not find document "
operator|+
name|q
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|Integer
operator|.
name|toString
argument_list|(
name|q
argument_list|)
operator|.
name|equals
argument_list|(
name|sdoc
operator|.
name|get
argument_list|(
literal|"fld"
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Expected "
operator|+
name|q
operator|+
literal|", but got "
operator|+
name|sdoc
operator|.
name|get
argument_list|(
literal|"fld"
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ex
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|readThreads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|readThreads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|rd
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|ex
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ex
operator|.
name|get
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Nightly
DECL|method|testBigDocuments
specifier|public
name|void
name|testBigDocuments
parameter_list|()
throws|throws
name|IOException
block|{
comment|// "big" as "much bigger than the chunk size"
comment|// for this test we force a FS dir
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
name|dir
operator|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwConf
argument_list|)
expr_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setThrottling
argument_list|(
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Document
name|emptyDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// emptyDoc
specifier|final
name|Document
name|bigDoc1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// lot of small fields
specifier|final
name|Document
name|bigDoc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// 1 very big field
specifier|final
name|Field
name|idField
init|=
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|emptyDoc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|bigDoc1
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|bigDoc2
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
specifier|final
name|FieldType
name|onlyStored
init|=
operator|new
name|FieldType
argument_list|(
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|onlyStored
operator|.
name|setIndexed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Field
name|smallField
init|=
operator|new
name|Field
argument_list|(
literal|"fld"
argument_list|,
name|randomByteArray
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|256
argument_list|)
argument_list|,
name|onlyStored
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numFields
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|500000
argument_list|,
literal|1000000
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
name|numFields
condition|;
operator|++
name|i
control|)
block|{
name|bigDoc1
operator|.
name|add
argument_list|(
name|smallField
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Field
name|bigField
init|=
operator|new
name|Field
argument_list|(
literal|"fld"
argument_list|,
name|randomByteArray
argument_list|(
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1000000
argument_list|,
literal|5000000
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
name|onlyStored
argument_list|)
decl_stmt|;
name|bigDoc2
operator|.
name|add
argument_list|(
name|bigField
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|Document
index|[]
name|docs
init|=
operator|new
name|Document
index|[
name|numDocs
index|]
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
operator|++
name|i
control|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|emptyDoc
argument_list|,
name|bigDoc1
argument_list|,
name|bigDoc2
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
operator|++
name|i
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|docs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// look at what happens when big docs are merged
specifier|final
name|DirectoryReader
name|rd
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|rd
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|i
argument_list|,
literal|1
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|StoredDocument
name|doc
init|=
name|rd
operator|.
name|document
argument_list|(
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|StorableField
index|[]
name|fieldValues
init|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"fld"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|docs
index|[
name|i
index|]
operator|.
name|getFields
argument_list|(
literal|"fld"
argument_list|)
operator|.
name|length
argument_list|,
name|fieldValues
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldValues
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|docs
index|[
name|i
index|]
operator|.
name|getFields
argument_list|(
literal|"fld"
argument_list|)
index|[
literal|0
index|]
operator|.
name|binaryValue
argument_list|()
argument_list|,
name|fieldValues
index|[
literal|0
index|]
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|rd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
