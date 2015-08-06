begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Random
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
name|SortedDocValuesField
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
name|TestUtil
import|;
end_import
begin_class
DECL|class|TestReaderWrapperDVTypeCheck
specifier|public
class|class
name|TestReaderWrapperDVTypeCheck
extends|extends
name|LuceneTestCase
block|{
DECL|method|testNoDVFieldOnSegment
specifier|public
name|void
name|testNoDVFieldOnSegment
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
name|IndexWriterConfig
name|cfg
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|alwaysDocValuesFormat
argument_list|(
name|TestUtil
operator|.
name|getDefaultDocValuesFormat
argument_list|()
argument_list|)
argument_list|)
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
name|dir
argument_list|,
name|cfg
argument_list|)
decl_stmt|;
name|boolean
name|sdvExist
init|=
literal|false
decl_stmt|;
name|boolean
name|ssdvExist
init|=
literal|false
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
block|{
specifier|final
name|Random
name|indexRandom
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
specifier|final
name|int
name|docs
decl_stmt|;
name|docs
operator|=
name|indexRandom
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// System.out.println("docs:"+docs);
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|(
name|indexRandom
argument_list|)
condition|)
block|{
comment|// System.out.println("on:"+i+" rarely: true");
name|d
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"sdv"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|""
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sdvExist
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// System.out.println("on:"+i+" rarely: false");
block|}
specifier|final
name|int
name|numSortedSet
init|=
name|indexRandom
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|-
literal|3
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numSortedSet
condition|;
operator|++
name|j
control|)
block|{
comment|// System.out.println("on:"+i+" add ssdv:"+j);
name|d
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"ssdv"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|""
operator|+
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ssdvExist
operator|=
literal|true
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|DirectoryReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|// System.out.println("sdv:"+ sdvExist+ " ssdv:"+ssdvExist+", segs: "+reader.leaves().size() +", "+reader.leaves());
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|LeafReader
name|wrapper
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
decl_stmt|;
block|{
comment|//final Random indexRandom = new Random(seed);
specifier|final
name|SortedDocValues
name|sdv
init|=
name|wrapper
operator|.
name|getSortedDocValues
argument_list|(
literal|"sdv"
argument_list|)
decl_stmt|;
specifier|final
name|SortedSetDocValues
name|ssdv
init|=
name|wrapper
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"ssdv"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"confusing DV type"
argument_list|,
name|wrapper
operator|.
name|getSortedDocValues
argument_list|(
literal|"ssdv"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"confusing DV type"
argument_list|,
name|wrapper
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"sdv"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"absent field"
argument_list|,
name|wrapper
operator|.
name|getSortedDocValues
argument_list|(
literal|"NOssdv"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"absent field"
argument_list|,
name|wrapper
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"NOsdv"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"optional sdv field"
argument_list|,
name|sdvExist
operator|==
operator|(
name|sdv
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"optional ssdv field"
argument_list|,
name|ssdvExist
operator|==
operator|(
name|ssdv
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
block|}
name|reader
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