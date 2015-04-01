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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Nightly
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
operator|.
name|SuppressCodecs
import|;
end_import
begin_comment
comment|/**  * Just like TestDuelingCodecs, only with a lot more documents.  */
end_comment
begin_class
annotation|@
name|Nightly
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Memory"
block|,
literal|"Direct"
block|}
argument_list|)
comment|// it can be too much for these codecs
DECL|class|TestDuelingCodecsAtNight
specifier|public
class|class
name|TestDuelingCodecsAtNight
extends|extends
name|TestDuelingCodecs
block|{
comment|// use a big number of documents
DECL|method|testBigEquals
specifier|public
name|void
name|testBigEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numdocs
init|=
name|atLeast
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|createRandomIndex
argument_list|(
name|numdocs
argument_list|,
name|leftWriter
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|createRandomIndex
argument_list|(
name|numdocs
argument_list|,
name|rightWriter
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|leftReader
operator|=
name|leftWriter
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|rightReader
operator|=
name|rightWriter
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|assertReaderEquals
argument_list|(
name|info
argument_list|,
name|leftReader
argument_list|,
name|rightReader
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
