begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.internal.csv.writer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
operator|.
name|writer
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_comment
comment|/**  * Tests for the config guesser.  */
end_comment
begin_class
DECL|class|CSVConfigGuesserTest
specifier|public
class|class
name|CSVConfigGuesserTest
extends|extends
name|TestCase
block|{
DECL|method|testSetters
specifier|public
name|void
name|testSetters
parameter_list|()
throws|throws
name|Exception
block|{
name|CSVConfigGuesser
name|guesser
init|=
operator|new
name|CSVConfigGuesser
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|guesser
operator|.
name|setInputStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|in
argument_list|,
name|guesser
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
name|guesser
operator|=
operator|new
name|CSVConfigGuesser
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|in
argument_list|,
name|guesser
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|guesser
operator|.
name|hasFieldHeader
argument_list|()
argument_list|)
expr_stmt|;
name|guesser
operator|.
name|setHasFieldHeader
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|guesser
operator|.
name|hasFieldHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test a format like      *  1234 ; abcd ; 1234 ;      *      */
DECL|method|testConfigGuess1
specifier|public
name|void
name|testConfigGuess1
parameter_list|()
throws|throws
name|Exception
block|{
name|CSVConfig
name|expected
init|=
operator|new
name|CSVConfig
argument_list|()
decl_stmt|;
name|expected
operator|.
name|setDelimiter
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setValueDelimiter
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setFill
argument_list|(
name|CSVConfig
operator|.
name|FILLRIGHT
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setIgnoreValueDelimiter
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setFixedWidth
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|CSVField
name|field
init|=
operator|new
name|CSVField
argument_list|()
decl_stmt|;
name|field
operator|.
name|setSize
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|expected
operator|.
name|addField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|expected
operator|.
name|addField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"1234;abcd;1234\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"abcd;1234;abcd"
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|CSVConfigGuesser
name|guesser
init|=
operator|new
name|CSVConfigGuesser
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|CSVConfig
name|guessed
init|=
name|guesser
operator|.
name|guess
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|isFixedWidth
argument_list|()
argument_list|,
name|guessed
operator|.
name|isFixedWidth
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getFields
argument_list|()
operator|.
name|length
argument_list|,
name|guessed
operator|.
name|getFields
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getFields
argument_list|()
index|[
literal|0
index|]
operator|.
name|getSize
argument_list|()
argument_list|,
name|guessed
operator|.
name|getFields
argument_list|()
index|[
literal|0
index|]
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test a format like      *  1234,123123,12312312,213123      *  1,2,3,4      *      */
DECL|method|testConfigGuess2
specifier|public
name|void
name|testConfigGuess2
parameter_list|()
throws|throws
name|Exception
block|{
name|CSVConfig
name|expected
init|=
operator|new
name|CSVConfig
argument_list|()
decl_stmt|;
name|expected
operator|.
name|setDelimiter
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setValueDelimiter
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setFill
argument_list|(
name|CSVConfig
operator|.
name|FILLRIGHT
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setIgnoreValueDelimiter
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//        expected.setFixedWidth(false);
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"1,2,3,4\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"abcd,1234,abcd,1234"
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|CSVConfigGuesser
name|guesser
init|=
operator|new
name|CSVConfigGuesser
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|CSVConfig
name|guessed
init|=
name|guesser
operator|.
name|guess
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|isFixedWidth
argument_list|()
argument_list|,
name|guessed
operator|.
name|isFixedWidth
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
