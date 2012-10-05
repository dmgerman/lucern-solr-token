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
name|Date
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
name|ByteDocValuesField
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
name|DerefBytesDocValuesField
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
name|DoubleDocValuesField
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
name|FloatDocValuesField
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
name|IntDocValuesField
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
name|LongDocValuesField
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
name|PackedLongDocValuesField
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
name|ShortDocValuesField
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
name|SortedBytesDocValuesField
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
name|StraightBytesDocValuesField
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
name|AtomicReaderContext
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
name|DocValues
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|DateDocValuesFieldSource
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
name|NumericDocValuesFieldSource
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
name|StrDocValuesFieldSource
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
name|_TestUtil
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
name|packed
operator|.
name|PackedInts
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
begin_class
DECL|class|TestDocValuesFieldSources
specifier|public
class|class
name|TestDocValuesFieldSources
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|(
name|DocValues
operator|.
name|Type
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|d
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwConfig
init|=
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
decl_stmt|;
specifier|final
name|int
name|nDocs
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
decl_stmt|;
specifier|final
name|Field
name|id
init|=
operator|new
name|IntDocValuesField
argument_list|(
literal|"id"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Field
name|f
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
name|f
operator|=
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_SORTED
case|:
name|f
operator|=
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|f
operator|=
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_DEREF
case|:
name|f
operator|=
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_SORTED
case|:
name|f
operator|=
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|f
operator|=
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|f
operator|=
operator|new
name|ByteDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
name|f
operator|=
operator|new
name|ShortDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|f
operator|=
operator|new
name|IntDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|f
operator|=
operator|new
name|LongDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
break|break;
case|case
name|VAR_INTS
case|:
name|f
operator|=
operator|new
name|PackedLongDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|f
operator|=
operator|new
name|FloatDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|f
operator|=
operator|new
name|DoubleDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
specifier|final
name|Object
index|[]
name|vals
init|=
operator|new
name|Object
index|[
name|nDocs
index|]
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
name|d
argument_list|,
name|iwConfig
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
name|nDocs
condition|;
operator|++
name|i
control|)
block|{
name|id
operator|.
name|setIntValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|vals
index|[
name|i
index|]
operator|=
name|_TestUtil
operator|.
name|randomFixedByteLengthUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|f
operator|.
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|(
name|String
operator|)
name|vals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|vals
index|[
name|i
index|]
operator|=
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|f
operator|.
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|(
name|String
operator|)
name|vals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|vals
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
literal|256
argument_list|)
expr_stmt|;
name|f
operator|.
name|setByteValue
argument_list|(
operator|(
name|Byte
operator|)
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
name|vals
index|[
name|i
index|]
operator|=
operator|(
name|short
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1
operator|<<
literal|16
argument_list|)
expr_stmt|;
name|f
operator|.
name|setShortValue
argument_list|(
operator|(
name|Short
operator|)
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|vals
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|f
operator|.
name|setIntValue
argument_list|(
operator|(
name|Integer
operator|)
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
case|case
name|VAR_INTS
case|:
specifier|final
name|int
name|bitsPerValue
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|31
argument_list|)
decl_stmt|;
comment|// keep it an int
name|vals
index|[
name|i
index|]
operator|=
operator|(
name|long
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
operator|(
name|int
operator|)
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|bitsPerValue
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|setLongValue
argument_list|(
operator|(
name|Long
operator|)
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|vals
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
expr_stmt|;
name|f
operator|.
name|setFloatValue
argument_list|(
operator|(
name|Float
operator|)
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|vals
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
expr_stmt|;
name|f
operator|.
name|setDoubleValue
argument_list|(
operator|(
name|Double
operator|)
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|document
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
name|i
operator|%
literal|10
operator|==
literal|9
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
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|rd
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|leave
range|:
name|rd
operator|.
name|leaves
argument_list|()
control|)
block|{
specifier|final
name|FunctionValues
name|ids
init|=
operator|new
name|NumericDocValuesFieldSource
argument_list|(
literal|"id"
argument_list|,
literal|false
argument_list|)
operator|.
name|getValues
argument_list|(
literal|null
argument_list|,
name|leave
argument_list|)
decl_stmt|;
specifier|final
name|ValueSource
name|vs
decl_stmt|;
specifier|final
name|boolean
name|direct
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|vs
operator|=
operator|new
name|StrDocValuesFieldSource
argument_list|(
literal|"dv"
argument_list|,
name|direct
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
name|vs
operator|=
operator|new
name|NumericDocValuesFieldSource
argument_list|(
literal|"dv"
argument_list|,
name|direct
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
case|case
name|VAR_INTS
case|:
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|vs
operator|=
operator|new
name|NumericDocValuesFieldSource
argument_list|(
literal|"dv"
argument_list|,
name|direct
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vs
operator|=
operator|new
name|DateDocValuesFieldSource
argument_list|(
literal|"dv"
argument_list|,
name|direct
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
specifier|final
name|FunctionValues
name|values
init|=
name|vs
operator|.
name|getValues
argument_list|(
literal|null
argument_list|,
name|leave
argument_list|)
decl_stmt|;
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
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
name|leave
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|values
operator|.
name|exists
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|vs
operator|instanceof
name|StrDocValuesFieldSource
condition|)
block|{
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|String
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|vs
operator|instanceof
name|NumericDocValuesFieldSource
condition|)
block|{
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|Number
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FIXED_INTS_8
case|:
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|Byte
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|bytesVal
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|Short
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|bytesVal
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|Integer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|bytesVal
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
case|case
name|VAR_INTS
case|:
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|Long
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|bytesVal
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|Float
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|bytesVal
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|Double
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|bytesVal
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|vs
operator|instanceof
name|DateDocValuesFieldSource
condition|)
block|{
name|assertTrue
argument_list|(
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
operator|instanceof
name|Date
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|Object
name|expected
init|=
name|vals
index|[
name|ids
operator|.
name|intVal
argument_list|(
name|i
argument_list|)
index|]
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
name|values
operator|.
name|ordVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// no exception
name|assertTrue
argument_list|(
name|values
operator|.
name|numOrd
argument_list|()
operator|>=
literal|1
argument_list|)
expr_stmt|;
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|values
operator|.
name|strVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|values
operator|.
name|objectVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|values
operator|.
name|strVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|bytesVal
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|(
name|String
operator|)
name|expected
argument_list|)
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|assertEquals
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|expected
operator|)
operator|.
name|floatValue
argument_list|()
argument_list|,
name|values
operator|.
name|floatVal
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|assertEquals
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|expected
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|values
operator|.
name|doubleVal
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.001d
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|VAR_INTS
case|:
name|assertEquals
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|expected
operator|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|values
operator|.
name|longVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|rd
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
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|DocValues
operator|.
name|Type
name|type
range|:
name|DocValues
operator|.
name|Type
operator|.
name|values
argument_list|()
control|)
block|{
name|test
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
