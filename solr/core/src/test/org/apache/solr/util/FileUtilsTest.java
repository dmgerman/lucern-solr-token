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
name|IOException
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
begin_class
DECL|class|FileUtilsTest
specifier|public
class|class
name|FileUtilsTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testResolve
specifier|public
name|void
name|testResolve
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|cwd
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
literal|"conf/data"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|resolvePath
argument_list|(
operator|new
name|File
argument_list|(
literal|"conf"
argument_list|)
argument_list|,
literal|"data"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
name|cwd
operator|+
literal|"/conf/data"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|resolvePath
argument_list|(
operator|new
name|File
argument_list|(
name|cwd
operator|+
literal|"/conf"
argument_list|)
argument_list|,
literal|"data"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
name|cwd
operator|+
literal|"/data"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|resolvePath
argument_list|(
operator|new
name|File
argument_list|(
literal|"conf"
argument_list|)
argument_list|,
name|cwd
operator|+
literal|"/data"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
