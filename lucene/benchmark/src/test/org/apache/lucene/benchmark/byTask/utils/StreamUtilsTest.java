begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.utils
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
name|utils
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|BufferedWriter
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
name|FileOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|io
operator|.
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|compress
operator|.
name|compressors
operator|.
name|CompressorStreamFactory
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
name|utils
operator|.
name|StreamUtils
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
name|IOUtils
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
name|_TestUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|StreamUtilsTest
specifier|public
class|class
name|StreamUtilsTest
extends|extends
name|BenchmarkTestCase
block|{
DECL|field|TEXT
specifier|private
specifier|static
specifier|final
name|String
name|TEXT
init|=
literal|"Some-Text..."
decl_stmt|;
DECL|field|testDir
specifier|private
name|File
name|testDir
decl_stmt|;
annotation|@
name|Test
DECL|method|testGetInputStreamPlainText
specifier|public
name|void
name|testGetInputStreamPlainText
parameter_list|()
throws|throws
name|Exception
block|{
name|assertReadText
argument_list|(
name|rawTextFile
argument_list|(
literal|"txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|rawTextFile
argument_list|(
literal|"TXT"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetInputStreamGzip
specifier|public
name|void
name|testGetInputStreamGzip
parameter_list|()
throws|throws
name|Exception
block|{
name|assertReadText
argument_list|(
name|rawGzipFile
argument_list|(
literal|"gz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|rawGzipFile
argument_list|(
literal|"gzip"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|rawGzipFile
argument_list|(
literal|"GZ"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|rawGzipFile
argument_list|(
literal|"GZIP"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetInputStreamBzip2
specifier|public
name|void
name|testGetInputStreamBzip2
parameter_list|()
throws|throws
name|Exception
block|{
name|assertReadText
argument_list|(
name|rawBzip2File
argument_list|(
literal|"bz2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|rawBzip2File
argument_list|(
literal|"bzip"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|rawBzip2File
argument_list|(
literal|"BZ2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|rawBzip2File
argument_list|(
literal|"BZIP"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetOutputStreamBzip2
specifier|public
name|void
name|testGetOutputStreamBzip2
parameter_list|()
throws|throws
name|Exception
block|{
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"bz2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"bzip"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"BZ2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"BZIP"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetOutputStreamGzip
specifier|public
name|void
name|testGetOutputStreamGzip
parameter_list|()
throws|throws
name|Exception
block|{
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"gz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"gzip"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"GZ"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"GZIP"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetOutputStreamPlain
specifier|public
name|void
name|testGetOutputStreamPlain
parameter_list|()
throws|throws
name|Exception
block|{
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"TXT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadText
argument_list|(
name|autoOutFile
argument_list|(
literal|"TEXT"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|rawTextFile
specifier|private
name|File
name|rawTextFile
parameter_list|(
name|String
name|ext
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"testfile."
operator|+
name|ext
argument_list|)
decl_stmt|;
name|BufferedWriter
name|w
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|w
operator|.
name|write
argument_list|(
name|TEXT
argument_list|)
expr_stmt|;
name|w
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|f
return|;
block|}
DECL|method|rawGzipFile
specifier|private
name|File
name|rawGzipFile
parameter_list|(
name|String
name|ext
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"testfile."
operator|+
name|ext
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|CompressorStreamFactory
argument_list|()
operator|.
name|createCompressorOutputStream
argument_list|(
name|CompressorStreamFactory
operator|.
name|GZIP
argument_list|,
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
name|writeText
argument_list|(
name|os
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
DECL|method|rawBzip2File
specifier|private
name|File
name|rawBzip2File
parameter_list|(
name|String
name|ext
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"testfile."
operator|+
name|ext
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|CompressorStreamFactory
argument_list|()
operator|.
name|createCompressorOutputStream
argument_list|(
name|CompressorStreamFactory
operator|.
name|BZIP2
argument_list|,
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
name|writeText
argument_list|(
name|os
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
DECL|method|autoOutFile
specifier|private
name|File
name|autoOutFile
parameter_list|(
name|String
name|ext
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"testfile."
operator|+
name|ext
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
name|StreamUtils
operator|.
name|outputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|writeText
argument_list|(
name|os
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
DECL|method|writeText
specifier|private
name|void
name|writeText
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedWriter
name|w
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|w
operator|.
name|write
argument_list|(
name|TEXT
argument_list|)
expr_stmt|;
name|w
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertReadText
specifier|private
name|void
name|assertReadText
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|ir
init|=
name|StreamUtils
operator|.
name|inputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|InputStreamReader
name|in
init|=
operator|new
name|InputStreamReader
argument_list|(
name|ir
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
decl_stmt|;
name|BufferedReader
name|r
init|=
operator|new
name|BufferedReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|r
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong text found in "
operator|+
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|TEXT
argument_list|,
name|line
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|testDir
operator|=
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
literal|"ContentSourceTest"
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
