begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|codecs
operator|.
name|lucene50
operator|.
name|Lucene50NormsConsumer
operator|.
name|NormMap
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
name|BaseNormsFormatTestCase
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
begin_comment
comment|/**  * Tests Lucene50NormsFormat  */
end_comment
begin_class
DECL|class|TestLucene50NormsFormat
specifier|public
class|class
name|TestLucene50NormsFormat
extends|extends
name|BaseNormsFormatTestCase
block|{
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
init|=
operator|new
name|Lucene50RWCodec
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
comment|// NormMap is rather complicated, doing domain encoding / tracking frequencies etc.
comment|// test it directly some here...
DECL|method|testNormMapSimple
specifier|public
name|void
name|testNormMapSimple
parameter_list|()
block|{
name|NormMap
name|map
init|=
operator|new
name|NormMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
operator|(
name|byte
operator|)
literal|10
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
operator|(
name|byte
operator|)
literal|5
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
operator|(
name|byte
operator|)
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map
operator|.
name|size
argument_list|)
expr_stmt|;
comment|// first come, first serve ord assignment
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map
operator|.
name|ord
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|ord
argument_list|(
operator|(
name|byte
operator|)
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|ord
argument_list|(
operator|(
name|byte
operator|)
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|map
operator|.
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|map
operator|.
name|values
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|map
operator|.
name|values
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|freqs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|freqs
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|freqs
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// optimizing reorders the ordinals
name|map
operator|.
name|optimizeOrdinals
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map
operator|.
name|ord
argument_list|(
operator|(
name|byte
operator|)
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|ord
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|ord
argument_list|(
operator|(
name|byte
operator|)
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|map
operator|.
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|map
operator|.
name|values
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|map
operator|.
name|values
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|freqs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|freqs
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|freqs
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testNormMapRandom
specifier|public
name|void
name|testNormMapRandom
parameter_list|()
block|{
name|Set
argument_list|<
name|Byte
argument_list|>
name|uniqueValuesSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numUniqValues
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|256
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
name|numUniqValues
condition|;
name|i
operator|++
control|)
block|{
name|uniqueValuesSet
operator|.
name|add
argument_list|(
name|Byte
operator|.
name|valueOf
argument_list|(
operator|(
name|byte
operator|)
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Byte
name|uniqueValues
index|[]
init|=
name|uniqueValuesSet
operator|.
name|toArray
argument_list|(
operator|new
name|Byte
index|[
name|uniqueValuesSet
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Byte
argument_list|,
name|Integer
argument_list|>
name|freqs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|NormMap
name|map
init|=
operator|new
name|NormMap
argument_list|()
decl_stmt|;
name|int
name|numdocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100000
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
name|numdocs
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|value
init|=
name|uniqueValues
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|uniqueValues
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
comment|// now add to both expected and actual
name|map
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|freqs
operator|.
name|containsKey
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|freqs
operator|.
name|put
argument_list|(
name|value
argument_list|,
name|freqs
operator|.
name|get
argument_list|(
name|value
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|freqs
operator|.
name|put
argument_list|(
name|value
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|freqs
operator|.
name|size
argument_list|()
argument_list|,
name|map
operator|.
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Byte
argument_list|,
name|Integer
argument_list|>
name|kv
range|:
name|freqs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|byte
name|value
init|=
name|kv
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|freq
init|=
name|kv
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|ord
init|=
name|map
operator|.
name|ord
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|freq
argument_list|,
name|map
operator|.
name|freqs
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|map
operator|.
name|values
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
block|}
comment|// optimizing should reorder ordinals from greatest to least frequency
name|map
operator|.
name|optimizeOrdinals
argument_list|()
expr_stmt|;
comment|// recheck consistency
name|assertEquals
argument_list|(
name|freqs
operator|.
name|size
argument_list|()
argument_list|,
name|map
operator|.
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Byte
argument_list|,
name|Integer
argument_list|>
name|kv
range|:
name|freqs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|byte
name|value
init|=
name|kv
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|freq
init|=
name|kv
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|ord
init|=
name|map
operator|.
name|ord
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|freq
argument_list|,
name|map
operator|.
name|freqs
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|map
operator|.
name|values
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
block|}
comment|// also check descending freq
name|int
name|prevFreq
init|=
name|map
operator|.
name|freqs
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|map
operator|.
name|size
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|prevFreq
operator|>=
name|map
operator|.
name|freqs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|prevFreq
operator|=
name|map
operator|.
name|freqs
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
