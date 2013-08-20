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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|// TODO: maybe we should test this with mocks, but its easy
end_comment
begin_comment
comment|// enough to test the basics via Codec
end_comment
begin_class
DECL|class|TestNamedSPILoader
specifier|public
class|class
name|TestNamedSPILoader
extends|extends
name|LuceneTestCase
block|{
DECL|method|testLookup
specifier|public
name|void
name|testLookup
parameter_list|()
block|{
name|Codec
name|codec
init|=
name|Codec
operator|.
name|forName
argument_list|(
literal|"Lucene45"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Lucene45"
argument_list|,
name|codec
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// we want an exception if its not found.
DECL|method|testBogusLookup
specifier|public
name|void
name|testBogusLookup
parameter_list|()
block|{
try|try
block|{
name|Codec
name|codec
init|=
name|Codec
operator|.
name|forName
argument_list|(
literal|"dskfdskfsdfksdfdsf"
argument_list|)
decl_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{}
block|}
DECL|method|testAvailableServices
specifier|public
name|void
name|testAvailableServices
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|codecs
init|=
name|Codec
operator|.
name|availableCodecs
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|codecs
operator|.
name|contains
argument_list|(
literal|"Lucene45"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
