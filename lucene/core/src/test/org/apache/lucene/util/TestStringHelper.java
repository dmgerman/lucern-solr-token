begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestStringHelper
specifier|public
class|class
name|TestStringHelper
extends|extends
name|LuceneTestCase
block|{
DECL|method|testMurmurHash3
specifier|public
name|void
name|testMurmurHash3
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Hashes computed using murmur3_32 from https://code.google.com/p/pyfasthash
name|assertEquals
argument_list|(
literal|0xf6a5c420
argument_list|,
name|StringHelper
operator|.
name|murmurhash3_x86_32
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0xcd018ef6
argument_list|,
name|StringHelper
operator|.
name|murmurhash3_x86_32
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x111e7435
argument_list|,
name|StringHelper
operator|.
name|murmurhash3_x86_32
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"You want weapons? We're in a library! Books! The best weapons in the world!"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x2c628cd0
argument_list|,
name|StringHelper
operator|.
name|murmurhash3_x86_32
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"You want weapons? We're in a library! Books! The best weapons in the world!"
argument_list|)
argument_list|,
literal|3476
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortKeyLength
specifier|public
name|void
name|testSortKeyLength
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|StringHelper
operator|.
name|sortKeyLength
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"for"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|StringHelper
operator|.
name|sortKeyLength
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo1234"
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"for1234"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|StringHelper
operator|.
name|sortKeyLength
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"fz"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|StringHelper
operator|.
name|sortKeyLength
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"g"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|StringHelper
operator|.
name|sortKeyLength
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"food"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
