begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
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
begin_comment
comment|/** Simple tests for {@link LatLonPoint} */
end_comment
begin_class
DECL|class|TestLatLonPoint
specifier|public
class|class
name|TestLatLonPoint
extends|extends
name|LuceneTestCase
block|{
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
throws|throws
name|Exception
block|{
comment|// looks crazy due to lossiness
name|assertEquals
argument_list|(
literal|"LatLonPoint<field:18.313693958334625,-65.22744401358068>"
argument_list|,
operator|(
operator|new
name|LatLonPoint
argument_list|(
literal|"field"
argument_list|,
literal|18.313694
argument_list|,
operator|-
literal|65.227444
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// looks crazy due to lossiness
name|assertEquals
argument_list|(
literal|"field:[18.000000016763806 TO 18.999999999068677],[-65.9999999217689 TO -65.00000006519258]"
argument_list|,
name|LatLonPoint
operator|.
name|newBoxQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
operator|-
literal|66
argument_list|,
operator|-
literal|65
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// distance query does not quantize inputs
name|assertEquals
argument_list|(
literal|"field:18.0,19.0 +/- 25.0 meters"
argument_list|,
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
literal|25
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// sort field
name|assertEquals
argument_list|(
literal|"<distance:\"field\" latitude=18.0 longitude=19.0>"
argument_list|,
name|LatLonPoint
operator|.
name|newDistanceSort
argument_list|(
literal|"field"
argument_list|,
literal|18.0
argument_list|,
literal|19.0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * step through some integers, ensuring they decode to their expected double values.    * double values start at -90 and increase by LATITUDE_DECODE for each integer.    * check edge cases within the double range and random doubles within the range too.    */
DECL|method|testLatitudeQuantization
specifier|public
name|void
name|testLatitudeQuantization
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|int
name|encoded
init|=
name|random
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|double
name|min
init|=
operator|-
literal|90.0
operator|+
operator|(
name|encoded
operator|-
operator|(
name|long
operator|)
name|Integer
operator|.
name|MIN_VALUE
operator|)
operator|*
name|LatLonPoint
operator|.
name|LATITUDE_DECODE
decl_stmt|;
name|double
name|decoded
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
comment|// should exactly equal expected value
name|assertEquals
argument_list|(
name|min
argument_list|,
name|decoded
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
comment|// should round-trip
name|assertEquals
argument_list|(
name|encoded
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|decoded
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitudeCeil
argument_list|(
name|decoded
argument_list|)
argument_list|)
expr_stmt|;
comment|// test within the range
if|if
condition|(
name|i
operator|!=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
comment|// this is the next representable value
comment|// all double values between [min .. max) should encode to the current integer
comment|// all double values between (min .. max] should encodeCeil to the next integer.
name|double
name|max
init|=
name|min
operator|+
name|LatLonPoint
operator|.
name|LATITUDE_DECODE
decl_stmt|;
name|assertEquals
argument_list|(
name|max
argument_list|,
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|encoded
operator|+
literal|1
argument_list|)
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
operator|+
literal|1
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
operator|+
literal|1
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitudeCeil
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
comment|// first and last doubles in range that will be quantized
name|double
name|minEdge
init|=
name|Math
operator|.
name|nextUp
argument_list|(
name|min
argument_list|)
decl_stmt|;
name|double
name|maxEdge
init|=
name|Math
operator|.
name|nextDown
argument_list|(
name|max
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|encoded
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|minEdge
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
operator|+
literal|1
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitudeCeil
argument_list|(
name|minEdge
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|maxEdge
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
operator|+
literal|1
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitudeCeil
argument_list|(
name|maxEdge
argument_list|)
argument_list|)
expr_stmt|;
comment|// check random values within the double range
name|long
name|minBits
init|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|minEdge
argument_list|)
decl_stmt|;
name|long
name|maxBits
init|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|maxEdge
argument_list|)
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
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|double
name|value
init|=
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|,
name|minBits
argument_list|,
name|maxBits
argument_list|)
argument_list|)
decl_stmt|;
comment|// round down
name|assertEquals
argument_list|(
name|encoded
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
comment|// round up
name|assertEquals
argument_list|(
name|encoded
operator|+
literal|1
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitudeCeil
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**     * step through some integers, ensuring they decode to their expected double values.    * double values start at -180 and increase by LONGITUDE_DECODE for each integer.    * check edge cases within the double range and a random doubles within the range too.    */
DECL|method|testLongitudeQuantization
specifier|public
name|void
name|testLongitudeQuantization
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|int
name|encoded
init|=
name|random
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|double
name|min
init|=
operator|-
literal|180.0
operator|+
operator|(
name|encoded
operator|-
operator|(
name|long
operator|)
name|Integer
operator|.
name|MIN_VALUE
operator|)
operator|*
name|LatLonPoint
operator|.
name|LONGITUDE_DECODE
decl_stmt|;
name|double
name|decoded
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
comment|// should exactly equal expected value
name|assertEquals
argument_list|(
name|min
argument_list|,
name|decoded
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
comment|// should round-trip
name|assertEquals
argument_list|(
name|encoded
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|decoded
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitudeCeil
argument_list|(
name|decoded
argument_list|)
argument_list|)
expr_stmt|;
comment|// test within the range
if|if
condition|(
name|i
operator|!=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
comment|// this is the next representable value
comment|// all double values between [min .. max) should encode to the current integer
comment|// all double values between (min .. max] should encodeCeil to the next integer.
name|double
name|max
init|=
name|min
operator|+
name|LatLonPoint
operator|.
name|LONGITUDE_DECODE
decl_stmt|;
name|assertEquals
argument_list|(
name|max
argument_list|,
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|encoded
operator|+
literal|1
argument_list|)
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
operator|+
literal|1
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
operator|+
literal|1
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitudeCeil
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
comment|// first and last doubles in range that will be quantized
name|double
name|minEdge
init|=
name|Math
operator|.
name|nextUp
argument_list|(
name|min
argument_list|)
decl_stmt|;
name|double
name|maxEdge
init|=
name|Math
operator|.
name|nextDown
argument_list|(
name|max
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|encoded
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|minEdge
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
operator|+
literal|1
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitudeCeil
argument_list|(
name|minEdge
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|maxEdge
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encoded
operator|+
literal|1
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitudeCeil
argument_list|(
name|maxEdge
argument_list|)
argument_list|)
expr_stmt|;
comment|// check random values within the double range
name|long
name|minBits
init|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|minEdge
argument_list|)
decl_stmt|;
name|long
name|maxBits
init|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|maxEdge
argument_list|)
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
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|double
name|value
init|=
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|,
name|minBits
argument_list|,
name|maxBits
argument_list|)
argument_list|)
decl_stmt|;
comment|// round down
name|assertEquals
argument_list|(
name|encoded
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
comment|// round up
name|assertEquals
argument_list|(
name|encoded
operator|+
literal|1
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitudeCeil
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// check edge/interesting cases explicitly
DECL|method|testEncodeEdgeCases
specifier|public
name|void
name|testEncodeEdgeCases
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
operator|-
literal|90.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitudeCeil
argument_list|(
operator|-
literal|90.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
literal|90.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|LatLonPoint
operator|.
name|encodeLatitudeCeil
argument_list|(
literal|90.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
operator|-
literal|180.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitudeCeil
argument_list|(
operator|-
literal|180.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
literal|180.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|LatLonPoint
operator|.
name|encodeLongitudeCeil
argument_list|(
literal|180.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
