begin_unit
begin_package
DECL|package|org.apache.lucene.uninverting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|uninverting
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
name|index
operator|.
name|SortedSetDocValues
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
name|uninverting
operator|.
name|UninvertingReader
operator|.
name|Type
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
name|NumericUtils
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
begin_class
DECL|class|TestUninvertingReader
specifier|public
class|class
name|TestUninvertingReader
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSortedSetInteger
specifier|public
name|void
name|testSortedSetInteger
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
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
name|IntField
argument_list|(
literal|"foo"
argument_list|,
literal|5
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
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
name|IntField
argument_list|(
literal|"foo"
argument_list|,
literal|5
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntField
argument_list|(
literal|"foo"
argument_list|,
operator|-
literal|3
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|UninvertingReader
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"foo"
argument_list|,
name|Type
operator|.
name|SORTED_SET_INTEGER
argument_list|)
argument_list|)
decl_stmt|;
name|AtomicReader
name|ar
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
name|SortedSetDocValues
name|v
init|=
name|ar
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|v
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|setDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|setDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|value
init|=
name|v
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|3
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|=
name|v
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|checkReader
argument_list|(
name|ir
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
DECL|method|testSortedSetFloat
specifier|public
name|void
name|testSortedSetFloat
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
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
name|IntField
argument_list|(
literal|"foo"
argument_list|,
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
literal|5f
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
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
name|IntField
argument_list|(
literal|"foo"
argument_list|,
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
literal|5f
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntField
argument_list|(
literal|"foo"
argument_list|,
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
operator|-
literal|3f
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|UninvertingReader
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"foo"
argument_list|,
name|Type
operator|.
name|SORTED_SET_FLOAT
argument_list|)
argument_list|)
decl_stmt|;
name|AtomicReader
name|ar
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
name|SortedSetDocValues
name|v
init|=
name|ar
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|v
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|setDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|setDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|value
init|=
name|v
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
operator|-
literal|3f
argument_list|)
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|=
name|v
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
literal|5f
argument_list|)
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|checkReader
argument_list|(
name|ir
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
DECL|method|testSortedSetLong
specifier|public
name|void
name|testSortedSetLong
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
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
name|LongField
argument_list|(
literal|"foo"
argument_list|,
literal|5
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
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
name|LongField
argument_list|(
literal|"foo"
argument_list|,
literal|5
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LongField
argument_list|(
literal|"foo"
argument_list|,
operator|-
literal|3
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|UninvertingReader
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"foo"
argument_list|,
name|Type
operator|.
name|SORTED_SET_LONG
argument_list|)
argument_list|)
decl_stmt|;
name|AtomicReader
name|ar
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
name|SortedSetDocValues
name|v
init|=
name|ar
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|v
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|setDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|setDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|value
init|=
name|v
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|3
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|=
name|v
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|checkReader
argument_list|(
name|ir
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
DECL|method|testSortedSetDouble
specifier|public
name|void
name|testSortedSetDouble
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
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
name|LongField
argument_list|(
literal|"foo"
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
literal|5d
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
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
name|LongField
argument_list|(
literal|"foo"
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
literal|5d
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LongField
argument_list|(
literal|"foo"
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
operator|-
literal|3d
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|UninvertingReader
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"foo"
argument_list|,
name|Type
operator|.
name|SORTED_SET_DOUBLE
argument_list|)
argument_list|)
decl_stmt|;
name|AtomicReader
name|ar
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
name|SortedSetDocValues
name|v
init|=
name|ar
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|v
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|setDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|setDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
argument_list|,
name|v
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|value
init|=
name|v
operator|.
name|lookupOrd
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
operator|-
literal|3d
argument_list|)
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|=
name|v
operator|.
name|lookupOrd
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
literal|5d
argument_list|)
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|checkReader
argument_list|(
name|ir
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
