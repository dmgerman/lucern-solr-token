begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|benchmark
operator|.
name|BenchmarkTestCase
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
import|;
end_import
begin_comment
comment|/** Tests the functionality of {@link CreateIndexTask}. */
end_comment
begin_class
DECL|class|CreateIndexTaskTest
specifier|public
class|class
name|CreateIndexTaskTest
extends|extends
name|BenchmarkTestCase
block|{
DECL|method|createPerfRunData
specifier|private
name|PerfRunData
name|createPerfRunData
parameter_list|(
name|String
name|infoStreamValue
parameter_list|)
throws|throws
name|Exception
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"print.props"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// don't print anything
name|props
operator|.
name|setProperty
argument_list|(
literal|"directory"
argument_list|,
literal|"RAMDirectory"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"writer.info.stream"
argument_list|,
name|infoStreamValue
argument_list|)
expr_stmt|;
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
name|props
argument_list|)
decl_stmt|;
return|return
operator|new
name|PerfRunData
argument_list|(
name|config
argument_list|)
return|;
block|}
DECL|method|testInfoStream_SystemOutErr
specifier|public
name|void
name|testInfoStream_SystemOutErr
parameter_list|()
throws|throws
name|Exception
block|{
name|PrintStream
name|curOut
init|=
name|System
operator|.
name|out
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
literal|"SystemOut"
argument_list|)
decl_stmt|;
name|CreateIndexTask
name|cit
init|=
operator|new
name|CreateIndexTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|cit
operator|.
name|doLogic
argument_list|()
expr_stmt|;
operator|new
name|CloseIndexTask
argument_list|(
name|runData
argument_list|)
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|baos
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|curOut
argument_list|)
expr_stmt|;
block|}
name|PrintStream
name|curErr
init|=
name|System
operator|.
name|err
decl_stmt|;
name|baos
operator|.
name|reset
argument_list|()
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
literal|"SystemErr"
argument_list|)
decl_stmt|;
name|CreateIndexTask
name|cit
init|=
operator|new
name|CreateIndexTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|cit
operator|.
name|doLogic
argument_list|()
expr_stmt|;
operator|new
name|CloseIndexTask
argument_list|(
name|runData
argument_list|)
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|baos
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setErr
argument_list|(
name|curErr
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInfoStream_File
specifier|public
name|void
name|testInfoStream_File
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|outFile
init|=
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
literal|"infoStreamTest"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|outFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|CreateIndexTask
argument_list|(
name|runData
argument_list|)
operator|.
name|doLogic
argument_list|()
expr_stmt|;
operator|new
name|CloseIndexTask
argument_list|(
name|runData
argument_list|)
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|outFile
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
