begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Assert
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|JUnitCore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Result
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|Failure
import|;
end_import
begin_comment
comment|/**  * Check that uncaught exceptions result in seed info being dumped to  * console.   */
end_comment
begin_class
DECL|class|TestSeedFromUncaught
specifier|public
class|class
name|TestSeedFromUncaught
extends|extends
name|WithNestedTests
block|{
DECL|class|ThrowInUncaught
specifier|public
specifier|static
class|class
name|ThrowInUncaught
extends|extends
name|AbstractNestedTest
block|{
annotation|@
name|Test
DECL|method|testFoo
specifier|public
name|void
name|testFoo
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"foobar"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|TestSeedFromUncaught
specifier|public
name|TestSeedFromUncaught
parameter_list|()
block|{
name|super
argument_list|(
comment|/* suppress normal output. */
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify super method calls on {@link LuceneTestCase#setUp()}.    */
annotation|@
name|Test
DECL|method|testUncaughtDumpsSeed
specifier|public
name|void
name|testUncaughtDumpsSeed
parameter_list|()
block|{
name|Result
name|result
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|ThrowInUncaught
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertFailureCount
argument_list|(
literal|1
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|Failure
name|f
init|=
name|result
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|trace
init|=
name|f
operator|.
name|getTrace
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|trace
operator|.
name|contains
argument_list|(
literal|"SeedInfo.seed("
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|trace
operator|.
name|contains
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
