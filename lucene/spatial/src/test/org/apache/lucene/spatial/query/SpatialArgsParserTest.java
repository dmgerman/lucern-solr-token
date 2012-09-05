begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|query
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
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
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|SpatialArgsParserTest
specifier|public
class|class
name|SpatialArgsParserTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|ctx
specifier|private
name|SpatialContext
name|ctx
init|=
name|SpatialContext
operator|.
name|GEO
decl_stmt|;
comment|//The args parser is only dependent on the ctx for IO so I don't care to test
comment|// with other implementations.
annotation|@
name|Test
DECL|method|testArgsParser
specifier|public
name|void
name|testArgsParser
parameter_list|()
throws|throws
name|Exception
block|{
name|SpatialArgsParser
name|parser
init|=
operator|new
name|SpatialArgsParser
argument_list|()
decl_stmt|;
name|String
name|arg
init|=
name|SpatialOperation
operator|.
name|IsWithin
operator|+
literal|"(-10 -20 10 20)"
decl_stmt|;
name|SpatialArgs
name|out
init|=
name|parser
operator|.
name|parse
argument_list|(
name|arg
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SpatialOperation
operator|.
name|IsWithin
argument_list|,
name|out
operator|.
name|getOperation
argument_list|()
argument_list|)
expr_stmt|;
name|Rectangle
name|bounds
init|=
operator|(
name|Rectangle
operator|)
name|out
operator|.
name|getShape
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|10.0
argument_list|,
name|bounds
operator|.
name|getMinX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10.0
argument_list|,
name|bounds
operator|.
name|getMaxX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
comment|// Disjoint should not be scored
name|arg
operator|=
name|SpatialOperation
operator|.
name|IsDisjointTo
operator|+
literal|" (-10 10 -20 20)"
expr_stmt|;
name|out
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|arg
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SpatialOperation
operator|.
name|IsDisjointTo
argument_list|,
name|out
operator|.
name|getOperation
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|SpatialOperation
operator|.
name|IsDisjointTo
operator|+
literal|"[ ]"
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"spatial operations need args"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
literal|"XXXX(-10 10 -20 20)"
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"unknown operation!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|//expected
block|}
block|}
block|}
end_class
end_unit
