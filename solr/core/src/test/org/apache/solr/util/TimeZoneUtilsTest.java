begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
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
name|util
operator|.
name|Set
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
name|TimeZone
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_class
DECL|class|TimeZoneUtilsTest
specifier|public
class|class
name|TimeZoneUtilsTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|assertSameRules
specifier|private
specifier|static
name|void
name|assertSameRules
parameter_list|(
specifier|final
name|String
name|label
parameter_list|,
specifier|final
name|TimeZone
name|expected
parameter_list|,
specifier|final
name|TimeZone
name|actual
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|expected
operator|&&
literal|null
operator|==
name|actual
condition|)
return|return;
name|assertNotNull
argument_list|(
name|label
operator|+
literal|": expected is null"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|label
operator|+
literal|": actual is null"
argument_list|,
name|actual
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|same
init|=
name|expected
operator|.
name|hasSameRules
argument_list|(
name|actual
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|label
operator|+
literal|": "
operator|+
name|expected
operator|.
name|toString
argument_list|()
operator|+
literal|" [[NOT SAME RULES]] "
operator|+
name|actual
operator|.
name|toString
argument_list|()
argument_list|,
name|same
argument_list|)
expr_stmt|;
block|}
DECL|method|testValidIds
specifier|public
name|void
name|testValidIds
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|idsTested
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// brain dead: anything the JVM supports, should work
for|for
control|(
name|String
name|validId
range|:
name|TimeZone
operator|.
name|getAvailableIDs
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|validId
operator|+
literal|" not found in list of known ids"
argument_list|,
name|TimeZoneUtils
operator|.
name|KNOWN_TIMEZONE_IDS
operator|.
name|contains
argument_list|(
name|validId
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|TimeZone
name|expected
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|validId
argument_list|)
decl_stmt|;
specifier|final
name|TimeZone
name|actual
init|=
name|TimeZoneUtils
operator|.
name|getTimeZone
argument_list|(
name|validId
argument_list|)
decl_stmt|;
name|assertSameRules
argument_list|(
name|validId
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|idsTested
operator|.
name|add
argument_list|(
name|validId
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"TimeZone.getAvailableIDs vs TimeZoneUtils.KNOWN_TIMEZONE_IDS"
argument_list|,
name|TimeZoneUtils
operator|.
name|KNOWN_TIMEZONE_IDS
operator|.
name|size
argument_list|()
argument_list|,
name|idsTested
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustom
specifier|public
name|void
name|testCustom
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|input
range|:
operator|new
name|String
index|[]
block|{
literal|"GMT-00"
block|,
literal|"GMT+00"
block|,
literal|"GMT-0"
block|,
literal|"GMT+0"
block|,
literal|"GMT+08"
block|,
literal|"GMT+8"
block|,
literal|"GMT-08"
block|,
literal|"GMT-8"
block|,
literal|"GMT+0800"
block|,
literal|"GMT+08:00"
block|,
literal|"GMT-0800"
block|,
literal|"GMT-08:00"
block|,
literal|"GMT+23"
block|,
literal|"GMT+2300"
block|,
literal|"GMT-23"
block|,
literal|"GMT-2300"
block|}
control|)
block|{
name|assertSameRules
argument_list|(
name|input
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|input
argument_list|)
argument_list|,
name|TimeZoneUtils
operator|.
name|getTimeZone
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testStupidIKnowButIDontTrustTheJVM
specifier|public
name|void
name|testStupidIKnowButIDontTrustTheJVM
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|input
range|:
operator|new
name|String
index|[]
block|{
literal|"GMT-00"
block|,
literal|"GMT+00"
block|,
literal|"GMT-0"
block|,
literal|"GMT+0"
block|,
literal|"GMT+08"
block|,
literal|"GMT+8"
block|,
literal|"GMT-08"
block|,
literal|"GMT-8"
block|,
literal|"GMT+0800"
block|,
literal|"GMT+08:00"
block|,
literal|"GMT-0800"
block|,
literal|"GMT-08:00"
block|,
literal|"GMT+23"
block|,
literal|"GMT+2300"
block|,
literal|"GMT-23"
block|,
literal|"GMT-2300"
block|}
control|)
block|{
name|assertSameRules
argument_list|(
name|input
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|input
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidInput
specifier|public
name|void
name|testInvalidInput
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|giberish
init|=
literal|"giberish"
decl_stmt|;
name|assumeFalse
argument_list|(
literal|"This test assumes that "
operator|+
name|giberish
operator|+
literal|" is not a valid tz id"
argument_list|,
name|TimeZoneUtils
operator|.
name|KNOWN_TIMEZONE_IDS
operator|.
name|contains
argument_list|(
name|giberish
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|giberish
argument_list|,
name|TimeZoneUtils
operator|.
name|getTimeZone
argument_list|(
name|giberish
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|malformed
range|:
operator|new
name|String
index|[]
block|{
literal|"GMT+72"
block|,
literal|"GMT0800"
block|,
literal|"GMT+2400"
block|,
literal|"GMT+24:00"
block|,
literal|"GMT+11-30"
block|,
literal|"GMT+11:-30"
block|,
literal|"GMT+0080"
block|,
literal|"GMT+00:80"
block|}
control|)
block|{
name|assertNull
argument_list|(
name|malformed
argument_list|,
name|TimeZoneUtils
operator|.
name|getTimeZone
argument_list|(
name|malformed
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|ONE_DIGIT
init|=
literal|"%1d"
decl_stmt|;
specifier|final
name|String
name|TWO_DIGIT
init|=
literal|"%02d"
decl_stmt|;
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
name|r
argument_list|,
literal|50
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
operator|<=
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|int
name|hour
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0
argument_list|,
literal|23
argument_list|)
decl_stmt|;
name|int
name|min
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0
argument_list|,
literal|59
argument_list|)
decl_stmt|;
name|String
name|hours
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
operator|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
name|ONE_DIGIT
else|:
name|TWO_DIGIT
operator|)
argument_list|,
name|hour
argument_list|)
decl_stmt|;
name|String
name|mins
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|TWO_DIGIT
argument_list|,
name|min
argument_list|)
decl_stmt|;
name|String
name|input
init|=
literal|"GMT"
operator|+
operator|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"+"
else|:
literal|"-"
operator|)
operator|+
name|hours
operator|+
operator|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|""
else|:
operator|(
operator|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|":"
else|:
literal|""
operator|)
operator|+
name|mins
operator|)
operator|)
decl_stmt|;
name|assertSameRules
argument_list|(
name|input
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|input
argument_list|)
argument_list|,
name|TimeZoneUtils
operator|.
name|getTimeZone
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
