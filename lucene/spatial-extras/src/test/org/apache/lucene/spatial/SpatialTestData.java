begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
package|;
end_package
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Shape
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|// This class is modelled after SpatialTestQuery.
end_comment
begin_comment
comment|// Before Lucene 4.7, this was a bit different in Spatial4j as SampleData& SampleDataReader.
end_comment
begin_class
DECL|class|SpatialTestData
specifier|public
class|class
name|SpatialTestData
block|{
DECL|field|id
specifier|public
name|String
name|id
decl_stmt|;
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|field|shape
specifier|public
name|Shape
name|shape
decl_stmt|;
comment|/** Reads the stream, consuming a format that is a tab-separated values of 3 columns:    * an "id", a "name" and the "shape".  Empty lines and lines starting with a '#' are skipped.    * The stream is closed.    */
DECL|method|getTestData
specifier|public
specifier|static
name|Iterator
argument_list|<
name|SpatialTestData
argument_list|>
name|getTestData
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|SpatialTestData
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|BufferedReader
name|bufInput
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|bufInput
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|line
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'#'
condition|)
continue|continue;
name|SpatialTestData
name|data
init|=
operator|new
name|SpatialTestData
argument_list|()
decl_stmt|;
name|String
index|[]
name|vals
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|.
name|length
operator|!=
literal|3
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"bad format; expecting 3 tab-separated values for line: "
operator|+
name|line
argument_list|)
throw|;
name|data
operator|.
name|id
operator|=
name|vals
index|[
literal|0
index|]
expr_stmt|;
name|data
operator|.
name|name
operator|=
name|vals
index|[
literal|1
index|]
expr_stmt|;
try|try
block|{
name|data
operator|.
name|shape
operator|=
name|ctx
operator|.
name|readShapeFromWkt
argument_list|(
name|vals
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|results
operator|.
name|add
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|bufInput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|results
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class
end_unit
