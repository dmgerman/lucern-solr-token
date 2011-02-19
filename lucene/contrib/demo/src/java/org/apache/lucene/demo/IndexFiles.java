begin_unit
begin_package
DECL|package|org.apache.lucene.demo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|NumericField
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|index
operator|.
name|IndexWriterConfig
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
name|index
operator|.
name|Term
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FSDirectory
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
name|Version
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
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_comment
comment|/** Index all text files under a directory. See http://lucene.apache.org/java/4_0/demo.html. */
end_comment
begin_class
DECL|class|IndexFiles
specifier|public
class|class
name|IndexFiles
block|{
DECL|method|IndexFiles
specifier|private
name|IndexFiles
parameter_list|()
block|{}
comment|/** Index all text files under a directory. */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|usage
init|=
literal|"java org.apache.lucene.demo.IndexFiles<root_directory>"
operator|+
literal|" [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
comment|// TODO: Change the link with every release (or: fill in some less error-prone alternative here...)
operator|+
literal|"See http://lucene.apache.org/java/4_0/demo.html for details."
decl_stmt|;
name|String
name|indexPath
init|=
literal|"index"
decl_stmt|;
name|String
name|docsPath
init|=
literal|null
decl_stmt|;
name|boolean
name|create
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"-index"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|indexPath
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-docs"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|docsPath
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-update"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|create
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|docsPath
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: "
operator|+
name|usage
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|File
name|docDir
init|=
operator|new
name|File
argument_list|(
name|docsPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|docDir
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|docDir
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Document directory '"
operator|+
name|docDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"' does not exist or is not readable, please check the path"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|Date
name|start
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Indexing to directory '"
operator|+
name|indexPath
operator|+
literal|"'..."
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|indexPath
argument_list|)
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
condition|)
block|{
comment|// Create a new index in the directory, removing any
comment|// previously indexed documents:
name|iwc
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Add new documents to an existing index:
name|iwc
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|)
expr_stmt|;
block|}
comment|// Optional: for better indexing performance, if you
comment|// are indexing many documents, increase the RAM
comment|// buffer.  But if you do this, increase the max heap
comment|// size to the JVM (eg add -Xmx512m or -Xmx1g):
comment|//
comment|// iwc.setRAMBufferSizeMB(256.0);
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|indexDocs
argument_list|(
name|writer
argument_list|,
name|docDir
argument_list|)
expr_stmt|;
comment|// NOTE: if you want to maximize search performance,
comment|// you can optionally call optimize here.  This can be
comment|// a costly operation, so generally it's only worth
comment|// it when your index is relatively static (ie you're
comment|// done adding documents to it):
comment|//
comment|// writer.optimize();
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Date
name|end
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
operator|+
literal|" total milliseconds"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" caught a "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|+
literal|"\n with message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Indexes the given file using the given writer, or if a directory is given,    * recurses over files and directories found under the given directory.    *     * NOTE: This method indexes one document per input file.  This is slow.  For good    * throughput, put multiple documents into your input file(s).  An example of this is    * in the benchmark module, which can create "line doc" files, one document per line,    * using the    *<a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"    *>WriteLineDocTask</a>.    *      * @param writer Writer to the index where the given file/dir info will be stored    * @param file The file to index, or the directory to recurse into to find files to index    * @throws IOException    */
DECL|method|indexDocs
specifier|static
name|void
name|indexDocs
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
comment|// do not try to index files that cannot be read
if|if
condition|(
name|file
operator|.
name|canRead
argument_list|()
condition|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|String
index|[]
name|files
init|=
name|file
operator|.
name|list
argument_list|()
decl_stmt|;
comment|// an IO error could occur
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexDocs
argument_list|(
name|writer
argument_list|,
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|files
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|FileInputStream
name|fis
decl_stmt|;
try|try
block|{
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
comment|// at least on windows, some temporary files raise this exception with an "access denied" message
comment|// checking if the file can be read doesn't help
return|return;
block|}
try|try
block|{
comment|// make a new, empty document
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Add the path of the file as a field named "path".  Use a
comment|// field that is indexed (i.e. searchable), but don't tokenize
comment|// the field into separate words and don't index term frequency
comment|// or positional information:
name|Field
name|pathField
init|=
operator|new
name|Field
argument_list|(
literal|"path"
argument_list|,
name|file
operator|.
name|getPath
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|)
decl_stmt|;
name|pathField
operator|.
name|setOmitTermFreqAndPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|pathField
argument_list|)
expr_stmt|;
comment|// Add the last modified date of the file a field named "modified".
comment|// Use a NumericField that is indexed (i.e. efficiently filterable with
comment|// NumericRangeFilter).  This indexes to milli-second resolution, which
comment|// is often too fine.  You could instead create a number based on
comment|// year/month/day/hour/minutes/seconds, down the resolution you require.
comment|// For example the long value 2011021714 would mean
comment|// February 17, 2011, 2-3 PM.
name|NumericField
name|modifiedField
init|=
operator|new
name|NumericField
argument_list|(
literal|"modified"
argument_list|)
decl_stmt|;
name|modifiedField
operator|.
name|setLongValue
argument_list|(
name|file
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|modifiedField
argument_list|)
expr_stmt|;
comment|// Add the contents of the file to a field named "contents".  Specify a Reader,
comment|// so that the text of the file is tokenized and indexed, but not stored.
comment|// Note that FileReader expects the file to be in UTF-8 encoding.
comment|// If that's not the case searching for special characters will fail.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getOpenMode
argument_list|()
operator|==
name|OpenMode
operator|.
name|CREATE
condition|)
block|{
comment|// New index, so we just add the document (no old document can be there):
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"adding "
operator|+
name|file
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Existing index (an old copy of this document may have been indexed) so
comment|// we use updateDocument instead to replace the old one matching the exact
comment|// path, if present:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"updating "
operator|+
name|file
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"path"
argument_list|,
name|file
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
